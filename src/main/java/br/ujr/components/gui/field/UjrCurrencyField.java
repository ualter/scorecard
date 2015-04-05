package br.ujr.components.gui.field;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import br.ujr.components.gui.Commons;

public class UjrCurrencyField extends AbstractUjrField {
	
	public UjrCurrencyField() {
		super();
		this.setHorizontalAlignment(JTextField.RIGHT);
	}
	public UjrCurrencyField(int x, int y) {
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
		StringBuffer result = new StringBuffer("");
		String number = this.getText().replaceAll(",", "").replaceAll("\\.", "");
		int decimais = 2;
		int demais = 0;
		int tam    = number.length();
		for(int i = (tam-1); i >= 0; i--) {
			result.insert(0, number.substring(i,i+1));
	 		if((decimais <= 0) && (++demais   == 3)){result.insert(0, "."); demais=0;}
	 		if((decimais >  0) && (--decimais == 0)){result.insert(0, ",");}
	 	}
		if ( result.length() > 0 ) {
			if(result.charAt(0) == '.') {
				result = result.delete(0, 1);
			}
		}
		this.setText(result.toString());
	}
}

