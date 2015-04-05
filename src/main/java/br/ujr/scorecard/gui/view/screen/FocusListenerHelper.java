package br.ujr.scorecard.gui.view.screen;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class FocusListenerHelper implements FocusListener {

	public JDialog parent = null; 
	
	public FocusListenerHelper(JDialog parent) {
		this.parent = parent;
		
		for (int i = 0;i < this.parent.getContentPane().getComponentCount(); i++) {
			Component c = this.parent.getContentPane().getComponents()[i];
			if ( c instanceof JButton ) {
				((JButton)c).addFocusListener(this);
			} else
			if ( c instanceof JPanel ) {	
				this.addFocusListenerToButton((JPanel)c);
			}
		}
	}
	
	private void addFocusListenerToButton(JPanel panel) {
		for (int i = 0; i < panel.getComponentCount(); i++) {
			Component c = panel.getComponents()[i];
			if ( c instanceof JButton ) {
				((JButton)c).addFocusListener(this);
			} else
			if ( c instanceof JPanel ) {	
				this.addFocusListenerToButton((JPanel)c);
			}
		}
	}
	
	public void focusGained(FocusEvent e) {
		if ( e.getSource() instanceof JButton ) {
			parent.getRootPane().setDefaultButton((JButton)e.getSource());
		}
	}

	public void focusLost(FocusEvent e) {
		if ( e.getSource() instanceof JButton ) {
			parent.getRootPane().setDefaultButton(null);
		}
	}

}
