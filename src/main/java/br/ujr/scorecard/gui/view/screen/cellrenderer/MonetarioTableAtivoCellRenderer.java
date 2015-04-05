package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang.StringUtils;

public class MonetarioTableAtivoCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	
	
	public MonetarioTableAtivoCellRenderer() {
	}
	
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		UtilTableCells.setUpTableAtivoCellRendererComponent(this,table, value, isSelected, hasFocus, row, column);
		String valor = String.valueOf(value);
		valor        = StringUtils.leftPad(valor, 10);
		valor        = " R$ " + valor + " ";
		this.setValue(valor);
		return this;
	}

}
