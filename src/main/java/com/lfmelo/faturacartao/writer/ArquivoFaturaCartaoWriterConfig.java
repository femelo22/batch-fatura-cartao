package com.lfmelo.faturacartao.writer;

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
				.build();
	}
	
	
	private LineAggregator<FaturaCartao> geradorConteudoArquivo() {
		return new LineAggregator<FaturaCartao>() {
			
			@Override
			public String aggregate(FaturaCartao faturaCartao) {
				StringBuilder writer = new StringBuilder();
				writer.append(String.format("Nome: %s\n", faturaCartao.getCliente().getNome()));
				writer.append(String.format("Endereço: %s\n\n\n", faturaCartao.getCliente().getEndereco()));
				//TODO: continuar
				return null;
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
