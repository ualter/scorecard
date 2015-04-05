package br.ujr.components.gui.combo;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.plaf.metal.MetalComboBoxEditor;

public class UjrComboBoxEditor extends MetalComboBoxEditor implements KeyListener {
	
	protected char lastKeyChar;
	protected int lastKeyCode;
	protected ComboBoxModel model;
	protected List list = new ArrayList();
	
	public UjrComboBoxEditor(ComboBoxModel model) {
		super();
		this.editor.addFocusListener(this);
		this.editor.addKeyListener(this);
		this.model = model;
	}
	
	public void refreshList() {
		this.list = new ArrayList();
		for (int i = 0; i < this.model.getSize(); i++) {
			Object obj = this.model.getElementAt(i);
			list.add(obj);
		}
		Collections.sort(list);
	}
	
	public void focusGained(FocusEvent focusEvent) {
		this.editor.setBackground(Color.LIGHT_GRAY);
		this.editor.select(0, this.editor.getText().length());
	}
	public void focusLost(FocusEvent focusEvent) {
		this.editor.setBackground(Color.WHITE);
	}
	
	public void keyReleased(KeyEvent e) {
		if ( this.lastKeyCode == KeyEvent.VK_SHIFT || 
			 this.lastKeyCode == KeyEvent.VK_CAPS_LOCK ||
			 this.lastKeyCode == KeyEvent.VK_CONTROL) {
			 this.selectAll();
		} else
		if ( !this.isEditKey(this.lastKeyCode) ) {
			String word = this.editor.getText();
			for (Iterator i = this.list.iterator(); i.hasNext();) {
				Object object       = (Object) i.next();
				String description  = object.toString();
				if ( description.length() >= word.length() ) {
					String partValue = description.substring(0, word.length());
					if ( word.equalsIgnoreCase(partValue) ) {
						this.setItem(object);
						this.editor.select(word.length(), description.length());
					}
				}
			}
		}
	}

	public void keyTyped(KeyEvent e) {
	}
	
	protected char getLastCharacther() {
		if ( this.editor.getText() != null && this.editor.getText().length() > 0 )
			return this.editor.getText().charAt(this.editor.getText().length()-1);
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
		if ( keyCode == KeyEvent.VK_HOME ) {
			return true;
		} else
		if ( keyCode == KeyEvent.VK_END ) {
			return true;
		} else
		if ( keyCode == KeyEvent.VK_SHIFT ) {
			return true;
		}
		if ( keyCode == KeyEvent.VK_CONTROL ) {
			return true;
		}
		return false;
	}
	
	public void keyPressed(KeyEvent e) {
		this.lastKeyChar = e.getKeyChar();
		this.lastKeyCode = e.getKeyCode();
	}
	
	public void printList() {
		for (Iterator i = list.iterator(); i.hasNext();) {
			String str = (String) i.next();
			System.out.println(str);
		}
	}

}
