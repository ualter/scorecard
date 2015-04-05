package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class FaturaCartaoCheckBoxCellRenderer extends JCheckBox implements TableCellRenderer {
	
		private static final long serialVersionUID = 6594333725114220143L;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			boolean selected = ((Boolean)value).booleanValue();
			
			Color background = selected ? Color.YELLOW : Color.WHITE;
			if ( isSelected ) {
				background = Color.BLACK;
			}
			
			this.setForeground(table.getForeground());
			this.setBackground(background);
			this.setBorder(table.getBorder());
			this.setFont(table.getFont());
			this.setSelected(selected);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
			return this;
		}
	}