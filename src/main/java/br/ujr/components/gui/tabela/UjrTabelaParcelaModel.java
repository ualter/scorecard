package br.ujr.components.gui.tabela;

import javax.swing.table.DefaultTableModel;

public class UjrTabelaParcelaModel extends DefaultTableModel {

	protected static final long serialVersionUID = 1L;

	public UjrTabelaParcelaModel() {
		super(null,
			  new String[]{"Vencimento","Valor","C/C","ID"});
	}
	
 	public UjrTabelaParcelaModel(Object data[][]) {
		super(data,
			  new String[]{"Vencimento","Valor","C/C","ID"});
	}
 	
	public UjrTabelaParcelaModel(Object[][] data, String[] columnNames) {
		super(data,columnNames);
	}

	public Class getColumnClass(int c) {
		return this.getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		return true;
	}
}
