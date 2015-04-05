package br.ujr.components.gui.field;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class AbstractUjrField extends JFormattedTextField implements KeyListener, FocusListener {
	
	protected char lastKeyChar;
	protected int lastKeyCode;

	public AbstractUjrField() {
		this.addKeyListener(this);
		this.addFocusListener(this);
		this.setFont(new Font("Courier New",Font.PLAIN,13));
	}
	
	public void keyPressed(KeyEvent e) {
		this.lastKeyChar = e.getKeyChar();
		this.lastKeyCode = e.getKeyCode();
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
	
	protected char getLastCharacther() {
		if ( this.getText() != null && this.getText().length() > 0 )
			return this.getText().charAt(this.getText().length()-1);
		return '\n';
	}
	
	protected boolean isEditKey(int keyCode) {
		if ( keyCode == KeyEvent.VK_BACK_SPACE ) {
			return true;
		} else
		if ( keyCode == KeyEvent.VK_DELETE ) {
			return true;
		} else
		if ( keyCode == KeyEvent.VK_TAB ) {
			return true;
		} else
		if ( keyCode == KeyEvent.VK_ENTER ) {
			return true;
		} else
		if ( keyCode == KeyEvent.VK_ESCAPE ) {
			return true;
		}
		return false;
	}

	public void focusGained(FocusEvent focusEvent) {
		this.setBackground(Color.LIGHT_GRAY);
		this.select(0, this.getText().length());
	}
	public void focusLost(FocusEvent focusEvent) {
		this.setBackground(Color.WHITE);
	}
	
	
}
