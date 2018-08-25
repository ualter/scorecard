package br.ujr.scorecard.config;

import org.springframework.context.support.GenericXmlApplicationContext;

public class ScorecardConfigUtil {
	
	private static GenericXmlApplicationContext context;
	
	// Genesis
	static 
	{
		//MetricRegistry metrics = new MetricRegistry();
		//Timer.Context timerSpringConfig = metrics.timer("springConfig").time();
		//ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
		
		context = new GenericXmlApplicationContext();
		context.load("spring.config.xml");
		context.refresh();
		
		//timerSpringConfig.stop();
		//reporter.start(8, TimeUnit.SECONDS);
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
