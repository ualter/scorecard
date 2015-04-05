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

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.scorecard.util.Util;

public class FaturaCartaoContaContabilCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener, FocusListener {

	UjrComboBox combo = new UjrComboBox();
	
	public FaturaCartaoContaContabilCellEditor(UjrComboBox cmb) {
		this.combo = cmb;
		this.combo.setName("conta");
		this.combo.addKeyListener(this);
		this.combo.addFocusListener(this);
		this.combo.setEditable(false);
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		combo.setFont(new Font("Courier New",Font.BOLD,12));
		combo.setSelectedItem(value);
		combo.setBorder(null);
		combo.addKeyListener(this);
		return combo;
	}

	public Object getCellEditorValue() {
		return combo.getSelectedItem();
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
