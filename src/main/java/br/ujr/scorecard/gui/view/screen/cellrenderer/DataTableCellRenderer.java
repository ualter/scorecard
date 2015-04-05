package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import br.ujr.components.gui.field.JDateChooser;
import br.ujr.scorecard.util.Util;

public class DataTableCellRenderer extends JDateChooser implements TableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	
	private int columnEfetivado;
	
	public int getColumnEfetivado() {
		return columnEfetivado;
	}

	public void setColumnEfetivado(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}

	public DataTableCellRenderer(int columnEfetivado) {
		super("dd/MM/yyyy","##/##/####",'_');
		this.columnEfetivado = columnEfetivado;
	}
	
	public DataTableCellRenderer() {
		super("dd/MM/yyyy","##/##/####",'_');
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		JFormattedTextField field = (JFormattedTextField)this.getDateEditor();
		UtilTableCells.setUpTableCellRendererComponent(field, this.columnEfetivado, table, value, isSelected, hasFocus, row, column);
		
		table.setRowHeight(row,20);
		field.setFont(new Font("Courier New",Font.PLAIN,13));
		Date date = Util.parseDate(value.toString());
		this.setDate(date);
		
		return this;
	}
	
}
