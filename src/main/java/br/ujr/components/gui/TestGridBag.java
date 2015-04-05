package br.ujr.components.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestGridBag extends JFrame {
	
	public TestGridBag() {
		this.setBounds(100, 100, 800, 500);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel panel = new JPanel();
		panel.setLayout(gridBagLayout);
		
		JButton button1 = new JButton("1"); 
		JButton button2 = new JButton("2");
		JButton button3 = new JButton("3"); 
		JButton button4 = new JButton("4");
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		panel.add(button1,c);
		
		//c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		panel.add(button2,c);
		
		
		c.gridy = 1;
 		//c.fill = GridBagConstraints.BOTH;
		c.weightx = 3;
		panel.add(button3,c);
		
		//c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		panel.add(button4,c);
		
		this.getContentPane().add(panel);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new TestGridBag();
	}

}
