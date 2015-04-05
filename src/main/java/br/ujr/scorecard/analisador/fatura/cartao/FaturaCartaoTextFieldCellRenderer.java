package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.lang.StringUtils;

public class FaturaCartaoTextFieldCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 2418901921193844216L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			boolean isChosen = Boolean.valueOf(table.getValueAt(row, 0).toString());
			
			Color colorFore = table.getForeground();
			Color colorBack = !isChosen ? table.getBackground() : Color.YELLOW;
			Font  font      = new Font("Courier New",Font.PLAIN,11);
			if ( isSelected ) {
				colorBack = !isChosen ? Color.BLACK : Color.BLACK;
				colorFore = !isChosen ? Color.WHITE : Color.YELLOW;
				font      = new Font("Courier New",Font.BOLD,11);
			} 
			this.setBackground(colorBack);
			this.setForeground(colorFore);
		
			
			this.setFont(font);
			this.setText(String.valueOf(value));
			this.setBorder(table.getBorder());
			
			switch (column) {
				case 2:{
					this.setText(" " + this.getText());
					break;
				}
				case 3:{
					layOutCell(value, colorFore, font, " R$ ");
					break;
				}
			}
			return this;
		}

		private void layOutCell(Object value, Color color, Font font, String curr) {
			String valor = String.valueOf(value);
			valor        = StringUtils.leftPad(valor, 9);
			valor        = curr + valor + " ";
			this.setValue(valor);
			this.setForeground(color);
			this.setFont(font);
		}

	}