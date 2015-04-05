package br.ujr.components.gui.combo;

import java.awt.Font;

import javax.swing.JComboBox;

public class UjrComboBox extends JComboBox {
	
	protected char lastKeyChar;
	protected int lastKeyCode;

	public UjrComboBox() {
		this.setEditable(true);
		this.setFont(new Font("Courier New",Font.PLAIN,13));
		this.setEditor(new UjrComboBoxEditor(this.getModel()));
		this.setMaximumRowCount(4);
	}
	public UjrComboBox(int x, int y, int width) {
		this.setBounds(x,y,width,20);
		this.setEditable(true);
		this.setFont(new Font("Courier New",Font.PLAIN,13));
		this.setEditor(new UjrComboBoxEditor(this.getModel()));
		this.setMaximumRowCount(4);
	}

	public void addItem(Object anObject) {
		super.addItem(anObject);
		((UjrComboBoxEditor)this.getEditor()).refreshList();
		/*System.out.println("****   LISTA");
		((UjrComboBoxEditor)this.getEditor()).printList();*/
	}
}
