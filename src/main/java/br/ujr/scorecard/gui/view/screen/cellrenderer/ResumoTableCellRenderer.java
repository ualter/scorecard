package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class ResumoTableCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2418901921193844216L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		this.setForeground(table.getForeground());
		this.setBackground(table.getBackground());
		this.setText(String.valueOf(value));
		this.setFont(new Font("Verdana",Font.PLAIN,11));
		this.setBorder(table.getBorder());
		
		table.setRowHeight(0,25);
		table.setRowHeight(7,25);
		
		table.setRowHeight(12,25);
		table.setRowHeight(13,25);
		table.setRowHeight(14,25);
		
		if ( row == 0 ) {
			this.setFont(new Font("Verdana",Font.BOLD,11));
			this.setBackground(new Color(255,255,153));
			this.setForeground(Color.BLUE);
		} else
		if ( row == 0 || row == 8 || row == 9 || row == 10 || row == 11) {
			this.setForeground(Color.BLUE);
		} else
		if ( row == 1 || row == 2 || row == 3 || row == 4 || row == 5 || row == 6) {
			this.setForeground(Color.RED);
		} else
		if ( row == 7 ) {
			this.setFont(new Font("Verdana",Font.BOLD,11));
			this.setBackground(new Color(255,255,153));
			this.setForeground(Color.RED);
		} else
		if ( row == 12 || row == 13 || row == 14 ) {
			this.setFont(new Font("Verdana",Font.BOLD,11));
			this.setBackground(new Color(255,255,153));
			this.setForeground(Color.BLACK);
		}
		return this;
	}

}
