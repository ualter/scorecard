package br.ujr.scorecard.config;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Conditional(ScorecardConditionalDev.class)
@ImportResource("classpath:spring.beans-dev.xml")
public class ScorecardConfigDev {

}
