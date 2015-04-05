package br.ujr.scorecard.model.reports;

public interface ReportManagerListener {
	
	public void reportStarted();
	public void reportFinished();
	public void reportMaxProgress(int value);
	public void reportProgress(String msg, int value);

}
