package br.ujr.scorecard.gui.view.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import br.ujr.components.gui.field.JDateChooser;
import br.ujr.scorecard.util.Util;

public class DataCellEditor extends AbstractCellEditor implements TableCellEditor {

	JDateChooser dataChooser = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
	
	public DataCellEditor() {
		
	}
	public DataCellEditor(Date date) {
		this.dataChooser.setDate(date);
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		dataChooser.setFont(new Font("Courier New",Font.PLAIN,13));
		dataChooser.setDate(Util.parseDate(value.toString()));
		dataChooser.setBorder(null);
		dataChooser.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		return dataChooser;
	}

	public Object getCellEditorValue() {
		return Util.formatDate(dataChooser.getDate());
	}

}
