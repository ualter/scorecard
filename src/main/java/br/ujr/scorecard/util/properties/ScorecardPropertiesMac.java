package br.ujr.scorecard.util.properties;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.ujr.scorecard.util.properties.condition.ScorecardConditionOSMac;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@Conditional(ScorecardConditionOSMac.class)
@PropertySource({"classpath:scorecard-mac.properties"})
public class ScorecardPropertiesMac extends AbstractScorecardProperties implements ScorecardProperties {


}
