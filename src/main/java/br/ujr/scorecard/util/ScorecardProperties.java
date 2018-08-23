package br.ujr.scorecard.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @Deprecated
 *
 * Substituir por br.ujr.scorecard.util.properties.ScorecardProperties (via Spring Bean)
 */
public class ScorecardProperties {
	
	private static Properties properties;
	
	static {
		properties = new Properties();
		 try {
			properties.load(Thread.currentThread().getClass().getResourceAsStream("/scorecard.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Arquivo scorecard.properties não encontrado.");
		}
	}
	
	private ScorecardProperties() {
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

}
