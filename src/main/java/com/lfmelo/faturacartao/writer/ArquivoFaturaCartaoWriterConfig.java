package com.lfmelo.faturacartao.writer;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.ResourceSuffixCreator;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.lfmelo.faturacartao.domain.FaturaCartao;
import com.lfmelo.faturacartao.domain.Transacao;

@Configuration
public class ArquivoFaturaCartaoWriterConfig {
	
	/**
	 * 
	 *  lineAggregator - Quando precisamos modificar a geração do arquivo, que não seja predefinida
	 * 	 
	 */
	
	@Bean
	public MultiResourceItemWriter<FaturaCartao> arquivosFaturaCartao() {
		return new MultiResourceItemWriterBuilder<FaturaCartao>()
				.name("arquivosFaturaCartao")
				.resource(new FileSystemResource("files/fatura"))
				.itemCountLimitPerResource(1)
				.resourceSuffixCreator(suffixCreator())
				.delegate(arquivoFaturaCartao())
				.build();
	}
	
	private FlatFileItemWriter<FaturaCartao> arquivoFaturaCartao() {
		return new FlatFileItemWriterBuilder<FaturaCartao>()
				.name("arquivoFaturaCartao")
				.resource(new FileSystemResource("files/fatura.txt"))
				.lineAggregator(geradorConteudoArquivo())
				.headerCallback(headerCallback()) //add header
				.footerCallback(footerCallback()) //add footer
				.build();
	}
	
	
	private FlatFileHeaderCallback headerCallback() {
		return new FlatFileHeaderCallback() {
			
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.append(String.format("%121s\n", "Cartão XPTO"));
				writer.append(String.format("%121s\n\n", "Rua Guaçui, 35"));
			}
		};
	}
	
	//Ele aguarda o momento antes da escrita, e totaliza os o valor das transações
	@Bean
	public FlatFileFooterCallback footerCallback() {
		return new TotalTransacoesFooterCallback();
	}

	private LineAggregator<FaturaCartao> geradorConteudoArquivo() {
		return new LineAggregator<FaturaCartao>() {
			
			@Override
			public String aggregate(FaturaCartao faturaCartao) {
				StringBuilder writer = new StringBuilder();
				writer.append(String.format("Nome: %s\n", faturaCartao.getCliente().getNome()));
				writer.append(String.format("Endereço: %s\n\n\n", faturaCartao.getCliente().getEndereco()));
				writer.append(String.format("Fatura completa do cartão %d\n", faturaCartao.getCartaoCredito().getNumeroCartao()));
				writer.append("-------------------------------------------------------------------------------------------------------------------\n");
				writer.append("DATA | DESCRIÇÃO | VALOR\n");
				writer.append("-------------------------------------------------------------------------------------------------------------------\n");
				
				for(Transacao transacao: faturaCartao.getTransacoes()) {
					writer.append(String.format("\n[%10s] %-80s - %s", 
							new SimpleDateFormat("dd/MM/yyyy").format(transacao.getData()),
							transacao.getDescricao(),
							NumberFormat.getCurrencyInstance().format(transacao.getValor())));
				}
				return writer.toString();
			}
		};
	}

	private ResourceSuffixCreator suffixCreator() {
		return new ResourceSuffixCreator() {
			
			@Override
			public String getSuffix(int index) {
				return index + ".txt";
			}
		};
	}


}
