package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.scorecard.util.Util;

public class FaturaCartaoDateCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {

	JDateChooser dataChooser = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
	
	public FaturaCartaoDateCellEditor() {
		
	}
	public FaturaCartaoDateCellEditor(Date date) {
		this.dataChooser.setDate(date);
		((JTextFieldDateEditor)this.dataChooser.getDateEditor()).addKeyListener(this);
		((JTextFieldDateEditor)this.dataChooser.getDateEditor()).setName("data");
		this.dataChooser.setName("data");
		this.dataChooser.addKeyListener(this);
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		dataChooser.setFont(new Font("Courier New",Font.BOLD,12));
		dataChooser.setDate(Util.parseDate(value.toString()));
		dataChooser.setAlignmentX(SwingConstants.CENTER);
		dataChooser.setBorder(null);
		return dataChooser;
	}

	public Object getCellEditorValue() {
		return Util.formatDate(dataChooser.getDate());
	}
	
	public void keyPressed(KeyEvent e) {
		System.out.println(e);
	}
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			this.fireEditingStopped();
			break;
		case KeyEvent.VK_ESCAPE:
			this.fireEditingCanceled();
			break;
		}
		System.out.println(keyCode);
	}
	public void keyTyped(KeyEvent e) {
		System.out.println(e);
	}

}
