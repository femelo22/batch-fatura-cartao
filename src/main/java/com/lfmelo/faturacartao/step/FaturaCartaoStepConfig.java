package com.lfmelo.faturacartao.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lfmelo.faturacartao.domain.FaturaCartao;
import com.lfmelo.faturacartao.writer.TotalTransacoesFooterCallback;

@Configuration
public class FaturaCartaoStepConfig {
	
	/**
	 * 
	 * 	ItemProcessor<I,O>::
	 *  <I> type of input item
	 * 	<O> type of output item
	 */
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step faturaCartaoStep(
			ItemReader<FaturaCartao> lerTransacoesReader,
			ItemProcessor<FaturaCartao, FaturaCartao> carregarDadosClienteProcessor,
			ItemWriter<FaturaCartao> escreveFaturaCartao,
			TotalTransacoesFooterCallback listener) {
		return stepBuilderFactory
				.get("faturaCartaoStep")
				.<FaturaCartao, FaturaCartao>chunk(1)
				.reader(lerTransacoesReader)
				.processor(carregarDadosClienteProcessor)
				.writer(escreveFaturaCartao)
				.listener(listener)
				.build();
	}

}
