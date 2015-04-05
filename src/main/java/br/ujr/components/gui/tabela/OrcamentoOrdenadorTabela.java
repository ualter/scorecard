package br.ujr.components.gui.tabela;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import br.ujr.scorecard.model.conta.ContaContabilNivelOrdenador;
import br.ujr.scorecard.util.Util;

public class OrcamentoOrdenadorTabela extends DefaultOrdenadorTabela {

	public OrcamentoOrdenadorTabela(DefaultModelTabela model) {
		super(model);
	}

	@Override
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
			 * Valores
			 */
			if ( column == 1 || column == 2 || column == 3) {
				BigDecimal bd1 = Util.parseCurrency(o1.toString());
				BigDecimal bd2 = Util.parseCurrency(o2.toString());
				return bd1.compareTo(bd2);
			} else
			if ( column == 4 ) {
				String nivel1 = this.extrairNivel(o1.toString());
				String nivel2 = this.extrairNivel(o2.toString());
				return ContaContabilNivelOrdenador.compare(nivel1, nivel2);
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

	private String extrairNivel(String str) {
		str = str.split(" ")[1];
		if ( StringUtils.isBlank(str) ) {
			throw new RuntimeException("Ordenação não efetuada, não foi encontrado o Nível no valor do campo a ser ordenado.");
		}
		return str.trim();
	}
	
	

}
