package com.lfmelo.faturacartao.reader;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import com.lfmelo.faturacartao.domain.CartaoCredito;
import com.lfmelo.faturacartao.domain.Cliente;
import com.lfmelo.faturacartao.domain.Transacao;

@Configuration
public class LerTransacoesReaderConfig {
	
	
	Logger logger = LogManager.getLogger(LerTransacoesReaderConfig.class.getName());
	
	
	/**
	 * 
	 *  RowMapper<T> é usada pelo JdbcTemplate para mapear linhas de um ResultSet por linha.
	 *  As implementações dessa interface executam o trabalho real de mapear cada linha para um objeto de resultado.
	 */
	
	@Bean
	public JdbcCursorItemReader<Transacao> lerTransacoesReader(@Qualifier("appDataSource") DataSource dataSource) {
		return new JdbcCursorItemReaderBuilder<Transacao>()
				.name("lerTransacoesReader")
				.dataSource(dataSource)
				.sql("select * from transacao as t join cartao_credito as cc using (numero_cartao_credito) order by numero_cartao_credito")
				.rowMapper(rowMapperTransacao())
				.build();
	}

	private RowMapper<Transacao> rowMapperTransacao() {
		return new RowMapper<Transacao>() {
			
			@Override
			public Transacao mapRow(ResultSet rs, int rowNum) throws SQLException {
				CartaoCredito cartaoCredito = new CartaoCredito();
				cartaoCredito.setNumeroCartao(rs.getInt("numero_cartao_credito"));
				
				Cliente cliente = new Cliente();
				cliente.setId(rs.getInt("cliente"));
				cartaoCredito.setCliente(cliente);
				
				Transacao transacao = new Transacao();
				transacao.setCartaoCredito(cartaoCredito);
				transacao.setId(rs.getInt("id"));
				transacao.setDescricao(rs.getString("descricao"));
				transacao.setData(rs.getDate("data"));
				transacao.setValor(rs.getDouble("valor"));			
				
				return transacao;
			}
		};
	}

}
