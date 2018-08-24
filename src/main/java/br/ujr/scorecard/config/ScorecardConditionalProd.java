package br.ujr.scorecard.config;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ScorecardConditionalProd implements ConfigurationCondition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String[] profiles = context.getEnvironment().getActiveProfiles();
		
		boolean match = false;
		for (String profile : profiles) {
			if ( profile.equalsIgnoreCase("prod") ) {
				match = true;
				break;
			}
		}
		return match;
	}

	@Override
	public ConfigurationPhase getConfigurationPhase() {
		return ConfigurationPhase.PARSE_CONFIGURATION;
	}

}
