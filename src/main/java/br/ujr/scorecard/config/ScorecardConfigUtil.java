package br.ujr.scorecard.config;

import org.springframework.context.support.GenericXmlApplicationContext;

public class ScorecardConfigUtil {
	
	private static GenericXmlApplicationContext context;
	
	// Genesis
	static 
	{
		context = new GenericXmlApplicationContext();
		context.load("spring.config.xml");
		context.refresh();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T getBean(String bean) {
		return (T)context.getBean(bean);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T getBean(Class clazz) {
		return (T)context.getBean(clazz);
	}

}
