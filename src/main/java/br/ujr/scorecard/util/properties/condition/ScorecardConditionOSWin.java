package br.ujr.scorecard.util.properties.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ScorecardConditionOSWin extends ScorecardConditionOS {
	
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		 return StringUtils.equalsIgnoreCase(this.getOperationSystem(context), "win");
	}

}
