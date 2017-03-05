package br.ujr.scorecard.model.reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface Report {
	
	public String getJasperFileName();
	public HashMap getParameters();
	public String getTargetFile(String pathFileReport);
	public Collection execute();
	public void setCriteria(Object[] criteria);
	public void setListeners(List<ReportManagerListener> listener); 

}
