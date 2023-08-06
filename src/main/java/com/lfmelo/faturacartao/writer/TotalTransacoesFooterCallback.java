package com.lfmelo.faturacartao.writer;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.List;

import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.file.FlatFileFooterCallback;

import com.lfmelo.faturacartao.domain.FaturaCartao;

public class TotalTransacoesFooterCallback implements FlatFileFooterCallback {
	
	/**
	 * @BeforeWrite - Ele aguarda o momento antes da escrita
	 * 				  e executa a soma de todos os registros
	 * 
	 * @AfterChunk - Zera tudo para a proxima fatura
	 */
	
	private Double total =  0.0;

	@Override
	public void writeFooter(Writer writer) throws IOException {
		writer.append(String.format("\n%121s", "Total: " + NumberFormat.getCurrencyInstance().format(total)));
	}
	
	@BeforeWrite
	public void beforeWrite(List<FaturaCartao> faturas) {
		for(FaturaCartao fatura: faturas) {
			total += fatura.getTotal();
		}
	}
	
	@AfterChunk
	public void afterChunck(ChunkContext chunkContext) {
		total = 0.0;
	}

}
