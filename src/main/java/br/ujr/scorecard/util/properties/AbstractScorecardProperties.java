package br.ujr.scorecard.util.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

public class AbstractScorecardProperties {

	@Autowired
	private Environment env;
	
	@Value("${scorecard.version}")
	private String scorecardVersion;

	public AbstractScorecardProperties() {
		super();
	}

	public String getProperty(String key) {
		return env.getProperty(key);
	}

	
	public String getScorecardVersion() {
		return scorecardVersion;
	}
	

}