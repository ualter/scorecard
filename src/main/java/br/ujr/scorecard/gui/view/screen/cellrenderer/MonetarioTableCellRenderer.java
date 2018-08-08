package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.apache.commons.lang3.StringUtils;

public class MonetarioTableCellRenderer extends AbstractTableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	private int    columnEfetivado             = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
	
	public MonetarioTableCellRenderer() {
	}
	
	public MonetarioTableCellRenderer(Color foreground) {
		this.foregroundColor = foreground;
	}
	
	public MonetarioTableCellRenderer(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		UtilTableCells.setUpTableCellRendererComponent(this, this.columnEfetivado, table, value, isSelected, hasFocus, row, column, this.foregroundColor);
		String valor = String.valueOf(value);
		valor        = StringUtils.leftPad(valor, 10);
		valor        = " R$ " + valor + " ";
		this.setValue(valor);
		return this;
	}

}
