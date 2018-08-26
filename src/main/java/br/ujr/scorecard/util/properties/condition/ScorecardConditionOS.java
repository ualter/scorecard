package br.ujr.scorecard.util.properties.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;

public abstract class ScorecardConditionOS implements Condition {

	public ScorecardConditionOS() {
		super();
	}
	
	protected String getOperationSystem(ConditionContext context) {
		return context.getEnvironment().getProperty("scorecard.os");
	}

}