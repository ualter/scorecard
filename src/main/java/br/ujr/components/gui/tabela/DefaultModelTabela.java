package br.ujr.components.gui.tabela;

import javax.swing.table.DefaultTableModel;

public class DefaultModelTabela extends DefaultTableModel  {
	
	private static final long serialVersionUID = 5949818842699067816L;
	private int[] indexes;
	
	public DefaultModelTabela(Object[][] data, Object[] columnNames) {
		super(data,columnNames);
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int c) {
		return this.getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		return true;
	}
	
	public Object getValueAt(int row, int col) {
		int rowIndex = row;
		if (indexes != null) {
			rowIndex = indexes[row];
		}
		return super.getValueAt(rowIndex, col);
	}

	public void setValueAt(Object value, int row, int col) {
		int rowIndex = row;
		if (indexes != null) {
			rowIndex = indexes[row];
		}
		super.setValueAt(value, rowIndex, col);
	}

	public int[] getIndexes() {
		int n = getRowCount();
		if (indexes != null) {
			if (indexes.length == n) {
				return indexes;
			}
		}
		indexes = new int[n];
		for (int i = 0; i < n; i++) {
			indexes[i] = i;
		}
		return indexes;
	}

}
