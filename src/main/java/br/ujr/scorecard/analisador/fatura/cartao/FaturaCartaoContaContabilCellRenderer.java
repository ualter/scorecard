package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import br.ujr.scorecard.model.conta.Conta;

public class FaturaCartaoContaContabilCellRenderer extends DefaultTableCellRenderer {
	
		private static final long serialVersionUID = 2418901921193844216L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			boolean isChosen = Boolean.valueOf(table.getValueAt(row, 0).toString());
			
			if (value instanceof Conta) {
				Conta conta = (Conta) value;
				conta.toStringMode = 1;	
			}
			
			Color colorFore = table.getForeground();
			Color colorBack = !isChosen ? table.getBackground() : Color.YELLOW;
			Font  font      = new Font("Courier New",Font.PLAIN,11);
			if ( isSelected ) {
				colorBack = !isChosen ? Color.BLACK : Color.BLACK;
				colorFore = !isChosen ? Color.WHITE : Color.YELLOW;
				font      = new Font("Courier New",Font.BOLD,11);
			} 
			
			this.setForeground(colorFore);
			this.setBackground(colorBack);
			
			this.setFont(font);
			this.setText(String.valueOf(value));
			this.setBorder(table.getBorder());
			
			return this;
		}

	

	}