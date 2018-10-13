package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public abstract class AbstractTableCellRenderer extends DefaultTableCellRenderer {
	
	protected Color foregroundColor;
	protected RendererRules rendererRules;

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	public RendererRules getRendererRules() {
		return rendererRules;
	}

	public void setRendererRules(RendererRules rendererRules) {
		this.rendererRules = rendererRules;
	}
	
	
	public void checkRendererRules(JTable table, int row) {
		if ( this.rendererRules != null ) { 
			 RendererRules.Format format = this.rendererRules.getPredicatedRenderer().checkFormat((DefaultTableModel)table.getModel(), row);
			
			 if ( format != null ) {
				 if ( format.getFont() != null ) {
					 this.setFont(format.getFont());
				 }
				 if ( format.getBackground() != null ) {
					 this.setBackground(format.getBackground());
				 }
				 if ( format.getForeground() != null ) {
					 this.setForeground(format.getForeground());
				 }
			 }
		}
	}
	
	

}
