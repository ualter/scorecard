package br.ujr.components.gui.tabela;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class UjrTabelaTabActionDecorator extends AbstractAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 183378858188422093L;
	private Action           action;
	private UjrTabelaParcela table;
	
	public UjrTabelaTabActionDecorator(UjrTabelaParcela table, Action action) {
		this.action = action;
		this.table  = table;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if ( this.table.getSelectedColumn() == (this.table.getColumnCount()-1) && this.table.getSelectedRow() == (this.table.getRowCount()-1) ) {
			this.table.getComponentAfter().requestFocus();	
		} else {
			this.action.actionPerformed(actionEvent);
		}
	}

}
