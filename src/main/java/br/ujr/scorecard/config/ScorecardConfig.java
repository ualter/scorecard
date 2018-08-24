package br.ujr.scorecard.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
@ImportResource("classpath:spring.beans.xml")
public class ScorecardConfig {
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

}
