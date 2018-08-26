package br.ujr.scorecard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
@ImportResource("classpath:spring.beans.xml")
public class ScorecardConfig {
	
	/*@Autowired
	DataSource dataSource;*/
	
	/*@Bean
	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}*/
	

}
