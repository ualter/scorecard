package br.ujr.components.gui.tabela;

import java.math.BigDecimal;
import java.util.Date;
import br.ujr.scorecard.util.Util;

public class DefaultOrdenadorTabela {
	
	protected DefaultModelTabela model;
	
	public DefaultOrdenadorTabela(DefaultModelTabela model) {
		this.model = model;
	}
	
	public void sortByColumn(int column, boolean isAscent) {
		int n = this.model.getRowCount();
		int[] indexes = this.model.getIndexes();
		
		for (int i = 0; i < n - 1; i++) {
			int k = i;
			for (int j = i + 1; j < n; j++) {
				if (isAscent) {
					if (compare(column, j, k) < 0) {
						k = j;
					}
				} else {
					if (compare(column, j, k) > 0) {
						k = j;
					}
				}
			}
			int tmp = indexes[i];
			indexes[i] = indexes[k];
			indexes[k] = tmp;
		}
		
		this.model.fireTableDataChanged();
	}
	
	public int compare(int column, int row1, int row2) {
		Object o1 = this.model.getValueAt(row1, column);
		Object o2 = this.model.getValueAt(row2, column);
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else {
			/**
			 * Datas
			 */
			if ( column == 0 ) {
				Date d1 = Util.parseDate(o1.toString());
				Date d2 = Util.parseDate(o2.toString());
				return d1.compareTo(d2);
			/**
			 * Valores
			 */
			} else
			if ( column == 1 ) {
				BigDecimal bd1 = Util.parseCurrency(o1.toString());
				BigDecimal bd2 = Util.parseCurrency(o2.toString());
				return bd1.compareTo(bd2);
			} else {
				Class type = this.model.getColumnClass(column);
				if (type == String.class) {
					return ((String) o1).compareTo((String) o2);
				} else if (type == Boolean.class) {
					return compare((Boolean) o1, (Boolean) o2);
				} else {
					return ((String) o1).compareTo((String) o2);
				}
			}
		}
	}
	
	public int compare(Number o1, Number o2) {
		double n1 = o1.doubleValue();
		double n2 = o2.doubleValue();
		if (n1 < n2) {
			return -1;
		} else if (n1 > n2) {
			return 1;
		} else {
			return 0;
		}
	}

	public int compare(Date o1, Date o2) {
		long n1 = o1.getTime();
		long n2 = o2.getTime();
		if (n1 < n2) {
			return -1;
		} else if (n1 > n2) {
			return 1;
		} else {
			return 0;
		}
	}

	public int compare(Boolean o1, Boolean o2) {
		boolean b1 = o1.booleanValue();
		boolean b2 = o2.booleanValue();
		if (b1 == b2) {
			return 0;
		} else if (b1) {
			return 1;
		} else {
			return -1;
		}
	}

}
