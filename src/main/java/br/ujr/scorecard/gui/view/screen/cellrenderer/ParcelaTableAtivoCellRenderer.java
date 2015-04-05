package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ParcelaTableAtivoCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	
	private String addToValue = "";
	private int    horizontalAlignment = SwingConstants.LEFT;
	
	public ParcelaTableAtivoCellRenderer() {
	}
	
	public ParcelaTableAtivoCellRenderer(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	public ParcelaTableAtivoCellRenderer(String addToValue,int horizontalAlignment) {
		this.addToValue = addToValue;
		this.horizontalAlignment = horizontalAlignment;
	}
	public ParcelaTableAtivoCellRenderer(String addToValue) {
		this.addToValue = addToValue;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		UtilTableCells.setUpTableAtivoCellRendererComponent(this, table, value, isSelected, hasFocus, row, column);
		this.setValue(addToValue + value);
		this.setHorizontalAlignment(this.horizontalAlignment);
		return this;
	}
}
