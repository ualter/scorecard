package br.ujr.scorecard.model.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.util.ScorecardProperties;

public abstract class AbstractReport implements Report {

	protected HashMap parameters = new HashMap();
	protected String  targetFile = null;
	protected Object[] criteria  = new Object[]{};
	protected ScorecardBusinessDelegate scorecardBusinessDelegate = ScorecardBusinessDelegate.getInstance();
	protected List<ReportManagerListener> listeners = new ArrayList<ReportManagerListener>();
	
	public HashMap getParameters() {
		return parameters;
	}

	public String getTargetFile() {
		if ( this.targetFile == null ) {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			this.targetFile = "/Users/ualter/Temp/" + sdf.format(calendar.getTime()) + ".pdf"; 
		}
		return this.targetFile;
	}
	
	public void setCriteria(Object[] criteria) {
		this.criteria = criteria;
	}
	
	public void setListeners(List<ReportManagerListener> listeners) {
		this.listeners = listeners;
	}
}
