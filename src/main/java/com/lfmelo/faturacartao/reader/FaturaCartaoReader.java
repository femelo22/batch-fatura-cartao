package com.lfmelo.faturacartao.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.context.annotation.Configuration;

import com.lfmelo.faturacartao.domain.FaturaCartao;
import com.lfmelo.faturacartao.domain.Transacao;

@Configuration
public class FaturaCartaoReader implements ItemStreamReader<FaturaCartao>{

	/**
	 *   Vamos usar um leitor do tipo ItemStream, utilizando o padrão Delegate
	 *   ItemStream - Abre e fecha recursos durante o cliclo de vida do leitor
	 *   
	 *   peek - Método para espiar a transação:
	 *   	Espie a proxima transação e se ela NÃO FOR nula ou for do mesmo cartão da fatura atual, adicione essa transação na fatura atual
	 */
	
	private ItemStreamReader<Transacao> delegate;
	private Transacao transacaoAtual;

	@Override
	public FaturaCartao read() throws Exception {
		if (transacaoAtual == null)
			transacaoAtual = delegate.read();
		
		FaturaCartao faturaCartaoCredito = null;
		Transacao transacao = transacaoAtual;
		transacaoAtual = null;
		
		if (transacao != null) {
			faturaCartaoCredito = new FaturaCartao();
			faturaCartaoCredito.setCartaoCredito(transacao.getCartaoCredito());
			faturaCartaoCredito.setCliente(transacao.getCartaoCredito().getCliente());
			faturaCartaoCredito.getTransacoes().add(transacao);
			
			while (isTransacaoRelacionada(transacao))
				faturaCartaoCredito.getTransacoes().add(transacaoAtual);
		}
		return faturaCartaoCredito;
	}

	private boolean isTransacaoRelacionada(Transacao transacao) throws Exception {
		return peek() != null && transacao.getCartaoCredito().getNumeroCartao() == transacaoAtual.getCartaoCredito().getNumeroCartao();
	}

	private Transacao peek() throws Exception {
		transacaoAtual = delegate.read();
		return transacaoAtual;
	}

	public FaturaCartaoReader(ItemStreamReader<Transacao> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		delegate.close();
	}
	
}
