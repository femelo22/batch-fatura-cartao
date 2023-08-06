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
		if(transacaoAtual == null) {
			transacaoAtual = delegate.read();
		}
		
		FaturaCartao faturaCartao = null;
		Transacao transacao = transacaoAtual;
		transacaoAtual = null;
		
		if(transacao != null) {
			faturaCartao = new FaturaCartao();
			faturaCartao.setCartaoCredito(transacao.getCartaoCredito());
			faturaCartao.setCliente(transacao.getCartaoCredito().getCliente());
			faturaCartao.getTransacoes().add(transacao);
			
			while(isTransacaoRelacionada(transacao)) {
				faturaCartao.getTransacoes().add(transacaoAtual);
			}
		}
		
		return faturaCartao;
	}
	
	private boolean isTransacaoRelacionada(Transacao transacao) throws Exception {
		return peek() != null && transacao.getCartaoCredito().getNumeroCartao() == transacaoAtual.getCartaoCredito().getNumeroCartao();
	}

	private Transacao peek() throws Exception {
		transacaoAtual = delegate.read();
		return transacaoAtual;
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
