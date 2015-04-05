package br.ujr.scorecard.util;


import java.util.HashMap;
import java.util.Map;

public class PerformanceChecker {
	
	private Map<String,Checker> checkers = new HashMap<String,Checker>();
	
	public PerformanceChecker() {
	}
	
	public void start(String tag) {
		Checker checker = new Checker(tag);
		checker.start();
		this.checkers.put(tag, checker);
	}
	
	public void finish(String tag) {
		this.checkers.get(tag).finish();
	}
	
	public void printResult(String tag) {
		this.checkers.get(tag).printResult();
	}
	
	private static class Checker {
		private long timeBefore;
		private long timeAfter;
		private String tag;
		
		public Checker(String tag) {
			this.tag = tag;
		}
		
		public void start() {
			this.timeBefore = System.currentTimeMillis();
		}
		
		public void finish() {
			this.timeAfter = System.currentTimeMillis();
			this.printResult();
		}
		
		public void printResult() {
			long diff = timeAfter - timeBefore;
			diff      = diff / 1000;
			System.out.println(diff + " seconds [" + this.tag + "]");
		}
		
	}
}
