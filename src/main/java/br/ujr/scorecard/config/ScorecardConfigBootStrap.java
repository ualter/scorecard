package br.ujr.scorecard.config;

import org.springframework.context.support.GenericXmlApplicationContext;

import br.com.ujr.utils.TimeTracker;

public class ScorecardConfigBootStrap {
	
	private static GenericXmlApplicationContext context;
	
	// Genesis
	static 
	{
		
		//System.out.println(TimeTracker.getInstance().startTracking("context.load(\"spring.config.xml\")"));
		context = new GenericXmlApplicationContext();
		context.load("spring.config.xml");
		context.refresh();
		//System.out.println(TimeTracker.getInstance().endTracking("context.load(\"spring.config.xml\")"));
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
