package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import br.ujr.components.gui.field.UjrTextField;

public class FaturaCartaoTextFieldCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {

		private static final long serialVersionUID = 2878765222057568025L;
		
		private UjrTextField field = new UjrTextField();
		
		public FaturaCartaoTextFieldCellEditor() {
			this.field.addKeyListener(this);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			this.field.setText(value.toString());
			this.field.setFont(new Font("Courier New",Font.BOLD,12));
			this.field.setBackground(Color.DARK_GRAY);
			this.field.setForeground(Color.BLACK);
			this.field.setBorder(null);
			this.field.selectAll();
			return this.field;
		}

		public Object getCellEditorValue() {
			return this.field.getText();
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