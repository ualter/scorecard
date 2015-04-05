package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.scorecard.util.Util;

public class FaturaCartaoMoneyCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener, FocusListener {

	UjrCurrencyField currencyField = new UjrCurrencyField();
	
	public FaturaCartaoMoneyCellEditor() {
		this.currencyField.setName("currencyField");
		this.currencyField.addKeyListener(this);
		this.currencyField.addFocusListener(this);
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		boolean isChosen = Boolean.valueOf(table.getValueAt(row, 0).toString());
		
		Color colorFore = table.getForeground();
		Color colorBack = !isChosen ? table.getBackground() : Color.YELLOW;
		Font  font      = new Font("Courier New",Font.PLAIN,11);
		if ( isSelected ) {
			colorBack = !isChosen ? Color.DARK_GRAY : Color.YELLOW;
			colorFore = !isChosen ? Color.BLACK     : Color.BLACK;
			font      = new Font("Courier New",Font.BOLD,11);
		} 
		currencyField.setBackground(colorBack);
		currencyField.setForeground(colorFore);
		
		currencyField.setFont(new Font("Courier New",Font.BOLD,12));
		currencyField.setValue( value.toString() );
		currencyField.setBorder(null);
		currencyField.addKeyListener(this);
		currencyField.selectAll();
		return currencyField;
	}

	public Object getCellEditorValue() {
		return currencyField.getText();
	}
	
	public void keyPressed(KeyEvent e) {
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
	}
	public void keyTyped(KeyEvent e) {
	}
	
	
	public void focusGained(FocusEvent e) {
		this.currencyField.selectAll();
	}
	public void focusLost(FocusEvent e) {
	}
	
	@Override
	public boolean isCellEditable(EventObject e) {
		if ( e instanceof MouseEvent ) {
			int clickCount = ((MouseEvent)e).getClickCount();
			if ( clickCount == 2 ) 
				return true;
		}
		return false;
	}

}
