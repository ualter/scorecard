package br.ujr.components.gui.tabela;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

public class UjrTabelaTabActionDecorator extends AbstractAction {
	
	private Action           action;
	private UjrTabelaParcela table;
	private JFrame           frame;
	
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
