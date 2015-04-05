package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class UtilTableCells {
	
	public final static int DEFAULT_COLUMN_EFETIVADO   = 4;
	public final static int DEFAULT_COLUMN_TEMP_SELECT = 5;
	
	public static Component setUpTableCellRendererComponent(JComponent component, int columnEfetivado, JTable table, 
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return 
			UtilTableCells.setUpTableCellRendererComponent(component, columnEfetivado, table, value, isSelected, hasFocus, row, column, null);
	}
	
	public static Component setUpTableCellRendererComponent(JComponent component, int columnEfetivado, JTable table, 
			Object value, boolean isSelected, boolean hasFocus, int row, int column, Color foregroundColor) {
		
		int COLUMN_EFETIVADO = DEFAULT_COLUMN_EFETIVADO;
		if ( columnEfetivado != -1 ) {
			COLUMN_EFETIVADO = columnEfetivado;
		}
		
		/**
		 * Seleção temporária, campo do Modelo só encontrado nas tabelas de Cartões de Crédito
		 * Utilizado no auxílio para conferência de fatura do cartão
		 */
		
		boolean selectedTemp = false;
		if ( table.getModel().getColumnCount() == 8 && table.getModel().getValueAt(row, 7) instanceof Boolean)  {
			selectedTemp = ((Boolean)table.getModel().getValueAt(row, 7)).booleanValue();
		}
		
		boolean isEfetivado          = false;
		Object  columnEfetivadoValue = table.getValueAt(row, COLUMN_EFETIVADO);
		if ( columnEfetivadoValue != null ) {
			isEfetivado = ((Boolean)table.getValueAt(row, COLUMN_EFETIVADO)).booleanValue();
		}
		
		Color  foreground = isEfetivado ? Color.LIGHT_GRAY : table.getForeground();
		Color  background = table.getBackground();
		Font   font       = new Font("Courier New",Font.PLAIN,11);
		Border border     = null;
		if ( isSelected ) {
			foreground = isEfetivado ? new Color(163,141,163) : table.getSelectionForeground();
			background = isEfetivado ? Color.LIGHT_GRAY       : table.getSelectionBackground();
			font = new Font("Courier New",Font.BOLD,12);
		}
		/*if ( hasFocus ) {
			border = BorderFactory.createLineBorder(Color.BLACK);
		}*/
		component.setForeground(foreground);
		component.setBackground(background);
		component.setBorder(border);
		component.setFont(font);
		
		if (component instanceof JLabel) {
			JLabel label = (JLabel) component;
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalAlignment(SwingConstants.CENTER);
		} else
		if (component instanceof JCheckBox) {
			JCheckBox checkbox = (JCheckBox) component;
			boolean selected = ((Boolean)value).booleanValue();
			checkbox.setSelected(selected);
			checkbox.setHorizontalAlignment(SwingConstants.CENTER);
			checkbox.setVerticalAlignment(SwingConstants.CENTER);
			checkbox.setEnabled(false);
		}
		
		if ( selectedTemp ) {
			component.setBackground(Color.YELLOW);
		}
		
		if ( foregroundColor != null ) {
			component.setForeground(foregroundColor);
		}
		
		return component;
	}
	
	public static Component setUpTableAtivoCellRendererComponent(JComponent component, JTable table, 
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Color  foreground = table.getForeground();
		Color  background = table.getBackground();
		Font   font       = new Font("Courier New",Font.PLAIN,11);
		Border border     = null;
		if ( isSelected ) {
			foreground = table.getSelectionForeground();
			background = table.getSelectionBackground();
			font = new Font("Courier New",Font.BOLD,12);
		}
		/*if ( hasFocus ) {
			border = BorderFactory.createLineBorder(Color.BLACK);
		}*/
		component.setForeground(foreground);
		component.setBackground(background);
		component.setBorder(border);
		component.setFont(font);
		
		if (component instanceof JLabel) {
			JLabel label = (JLabel) component;
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalAlignment(SwingConstants.CENTER);
		} else
		if (component instanceof JCheckBox) {
			JCheckBox checkbox = (JCheckBox) component;
			boolean selected = ((Boolean)value).booleanValue();
			checkbox.setSelected(selected);
			checkbox.setHorizontalAlignment(SwingConstants.CENTER);
			checkbox.setVerticalAlignment(SwingConstants.CENTER);
		}
		
		return component;
	}

}
