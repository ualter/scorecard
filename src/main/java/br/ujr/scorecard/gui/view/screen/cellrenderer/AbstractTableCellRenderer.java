package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;

import javax.swing.table.DefaultTableCellRenderer;

public abstract class AbstractTableCellRenderer extends DefaultTableCellRenderer {
	
	protected Color foregroundColor;

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	

}
