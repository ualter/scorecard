package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class EfetivadoTableCellRenderer extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	private Color foregroundColor;
	
	private int columnEfetivado;
	
	public EfetivadoTableCellRenderer() {
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
	}
	
	public EfetivadoTableCellRenderer(Color foreground) {
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
		this.foregroundColor = foreground;
	}
	
	public EfetivadoTableCellRenderer(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}

	
	public int getColumnEfetivado() {
		return columnEfetivado;
	}

	public void setColumnEfetivado(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		UtilTableCells.setUpTableCellRendererComponent(this, this.columnEfetivado, table, value, isSelected, hasFocus, row, column, this.foregroundColor);
		return this;
	}
	
}
