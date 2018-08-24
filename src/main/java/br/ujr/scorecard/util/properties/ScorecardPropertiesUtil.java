package br.ujr.scorecard.util.properties;

import br.ujr.scorecard.config.ScorecardConfigUtil;

/**
 * Temporary Object, until finish migration from Spring 1.0 to Spring 5.0
 *
 */
public class ScorecardPropertiesUtil {
	
	private static br.ujr.scorecard.util.properties.ScorecardProperties scorecardProperties;
	
	
	public static String getProperty(String key) {
		if ( scorecardProperties == null ) {
			scorecardProperties =  ScorecardConfigUtil.getBean(br.ujr.scorecard.util.properties.ScorecardProperties.class);
		}
		return scorecardProperties.getProperty(key);
	}
	
	

}
