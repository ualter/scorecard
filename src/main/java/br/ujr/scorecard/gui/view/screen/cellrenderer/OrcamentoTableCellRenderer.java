package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang.StringUtils;


public class OrcamentoTableCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2418901921193844216L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if ( isSelected ) {
			this.setForeground(table.getSelectionForeground());
			this.setBackground(table.getSelectionBackground());
		} else {
			this.setForeground(table.getForeground());
			this.setBackground(table.getBackground());
		}
		
		this.setFont(new Font("Courier New",Font.PLAIN,11));
		this.setText(String.valueOf(value));
		this.setBorder(table.getBorder());
		
		boolean selectedTemp = false;
		if ( table.getModel().getColumnCount() == 7 && table.getModel().getValueAt(row, 6) instanceof Boolean)  {
			selectedTemp = ((Boolean)table.getModel().getValueAt(row, 6)).booleanValue();
		}
		
		if ( column == 1 ) {
			layOutCell(value, new Color(0, 128, 0));
		} else
		if ( column == 2 ) {
			layOutCell(value, Color.BLACK);
		} else
		if ( column == 3 ) {
			Color color = Color.BLUE;
			if ( StringUtils.contains(String.valueOf(value), "-") ) {
				color = Color.RED;
			}
			layOutCell(value, color);
		}
		
		if ( selectedTemp ) {
			if ( isSelected ) {
				this.setBackground(Color.GREEN);
			} else {
				this.setBackground(Color.YELLOW);
			}
		}
		
		return this;
	}

	private void layOutCell(Object value, Color color) {
		String valor = String.valueOf(value);
		valor        = StringUtils.leftPad(valor, 9);
		valor        = " R$ " + valor + " ";
		this.setValue(valor);
		this.setForeground(color);
		this.setFont(new Font("Courier New",Font.BOLD,11));
	}

}
