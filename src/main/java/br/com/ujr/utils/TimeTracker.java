package br.com.ujr.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeTracker {
	
	private Map<String,TimeTrack> times = new HashMap<String,TimeTrack>();

	public TimeTracker() {
	}
	
	public void startTracking(String description) {
		times.put(description, new TimeTrack());
	}
	
	private String descriptionTmpEnd;
	public TimeTracker endTracking(String description) {
		if ( !times.containsKey(description)) {
			throw new RuntimeException(description + " Not found");
		}
		times.get(description).stop();
		this.descriptionTmpEnd = description;
		return this;
	}
	
	public String formattedTime() {
		return times.get(this.descriptionTmpEnd).getTimeSpenFormatted();
	}
	
	public String formattedTimeLabel() {
		return String.format("### Time Spent (%s) --> %s ###",this.descriptionTmpEnd,times.get(this.descriptionTmpEnd).getTimeSpenFormatted());
	}
	
	public static class TimeTrack {
		private String description;
		private long startTime;
		private long endTime;
		private long timeSpent;
		private String timeSpenFormatted;
		
		public TimeTrack() {
			this.startTime = System.currentTimeMillis();
		}
		public void stop() {
			this.endTime = System.currentTimeMillis();
			this.timeSpent = this.endTime - this.startTime;
			this.timeSpenFormatted = this.formatMillis(this.timeSpent);
		}
		
		private String formatMillis(long timeSpent) {
			String resultTime = String.format("%02d:%02d:%02d:%04d", 
				TimeUnit.MILLISECONDS.toHours(timeSpent),
				
				TimeUnit.MILLISECONDS.toMinutes(timeSpent) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeSpent)),
				
				TimeUnit.MILLISECONDS.toSeconds(timeSpent) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSpent)),
				
				TimeUnit.MILLISECONDS.toMillis(timeSpent) -
				TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeSpent))
			);
			return resultTime;
		}
		
		public String getTimeSpenFormatted() {
			return timeSpenFormatted;
		}
		public void setTimeSpenFormatted(String timeSpenFormatted) {
			this.timeSpenFormatted = timeSpenFormatted;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		public long getEndTime() {
			return endTime;
		}
		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}
		public long getTimeSpent() {
			return timeSpent;
		}
		public void setTimeSpent(long timeSpent) {
			this.timeSpent = timeSpent;
		}
		
		
		
	}

}
