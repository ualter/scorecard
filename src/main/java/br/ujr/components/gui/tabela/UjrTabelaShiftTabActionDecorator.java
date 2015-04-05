package br.ujr.components.gui.tabela;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

public class UjrTabelaShiftTabActionDecorator extends AbstractAction {
	
	private Action           action;
	private UjrTabelaParcela table;
	private JFrame           frame;
	
	public UjrTabelaShiftTabActionDecorator(UjrTabelaParcela table, Action action) {
		this.action = action;
		this.table  = table;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if ( this.table.getSelectedColumn() == 0 && this.table.getSelectedRow() == 0 ) {
			this.table.getComponentBefore().requestFocus();
		} else {
			this.action.actionPerformed(actionEvent);
		}
	}

}
