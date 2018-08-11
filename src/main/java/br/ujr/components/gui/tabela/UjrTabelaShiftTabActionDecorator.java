package br.ujr.components.gui.tabela;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class UjrTabelaShiftTabActionDecorator extends AbstractAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7529111176219763195L;
	private Action           action;
	private UjrTabelaParcela table;
	
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
