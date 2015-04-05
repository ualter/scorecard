package br.ujr.components.gui.field;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import br.ujr.components.gui.Commons;

public class UjrNumberField extends AbstractUjrField {
	
	public UjrNumberField() {
		super();
		this.setHorizontalAlignment(JTextField.RIGHT);
	}
	public UjrNumberField(int x, int y) {
		super();
		this.setBounds(x,y,130,20);
		this.setHorizontalAlignment(JTextField.RIGHT);
	}
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
	}
	public void keyReleased(KeyEvent e) {
		if ( !isEditKey(this.lastKeyCode) && !Commons.isNumber(this.lastKeyChar) ) {
			e.consume();
		} else {
			this.format(e);
		}
	}
	public void keyTyped(KeyEvent e) {
		if ( !isEditKey(this.lastKeyCode) && !Commons.isNumber(this.lastKeyChar) ) {
			e.consume();
		}
	}
	
	/*public double getValue() {
		String number = this.getText().replaceAll(",", "").replaceAll("\\.", "");
		return Double.parseDouble(number);
	}*/
	
	private void format(KeyEvent e) {
		
	}
}

