package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;

public class ParcelaTableCellRenderer extends AbstractTableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	
	private String addToValue = "";
	private int    columnEfetivado     = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
	private int    horizontalAlignment = SwingConstants.LEFT;
	
	public ParcelaTableCellRenderer() {
	}
	
	public ParcelaTableCellRenderer(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	public ParcelaTableCellRenderer(int horizontalAlignment, Color foregroundColor) {
		this.horizontalAlignment = horizontalAlignment;
		this.foregroundColor     = foregroundColor;
	}
	public ParcelaTableCellRenderer(int columnEfetivado, int horizontalAlignment) {
		this.columnEfetivado = columnEfetivado;
		this.horizontalAlignment = horizontalAlignment;
	}
	public ParcelaTableCellRenderer(String addToValue, int columnEfetivado, int horizontalAlignment) {
		this.addToValue = addToValue;
		this.columnEfetivado = columnEfetivado;
		this.horizontalAlignment = horizontalAlignment;
	}
	public ParcelaTableCellRenderer(String addToValue) {
		this.addToValue = addToValue;
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
	}
	public ParcelaTableCellRenderer(String addToValue, Color foreground) {
		this.addToValue = addToValue;
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
		this.foregroundColor = foreground;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		UtilTableCells.setUpTableCellRendererComponent(this, this.columnEfetivado, table, value, isSelected, hasFocus, row, column, foregroundColor);
		this.setValue(addToValue + value);
		this.setHorizontalAlignment(this.horizontalAlignment);
		return this;
	}
	
	public int getColumnEfetivado() {
		return columnEfetivado;
	}
	public void setColumnEfetivado(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}

}
