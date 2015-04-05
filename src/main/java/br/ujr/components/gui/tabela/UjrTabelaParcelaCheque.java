package br.ujr.components.gui.tabela;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrDateField;
import br.ujr.components.gui.field.UjrNumberField;

public class UjrTabelaParcelaCheque extends UjrTabelaParcela {
	
	public UjrTabelaParcelaCheque(Component componentBefore, Component componentAfter) {
		super(componentBefore,componentAfter);
	}
	
	public UjrTabelaParcelaCheque(TableModel dm, Component componentBefore, Component componentAfter) {
		super(dm,componentBefore,componentAfter);
	}
	
	/* (non-Javadoc)
	 * @see br.ujr.components.gui.tabela.UjrTabelaParcela#setUpColumns()
	 */
	protected void setUpColumns() {
		if (this.getColumnModel().getColumnCount() > 0) {
			TableColumn vencimentoColumn = this.getColumnModel().getColumn(0);
			TableColumn valorColumn      = this.getColumnModel().getColumn(1);
			TableColumn numeroColumn     = this.getColumnModel().getColumn(2);

			vencimentoColumn.setPreferredWidth(20);
			valorColumn.setPreferredWidth(22);
			numeroColumn.setPreferredWidth(50);
			
			DefaultCellEditor vencimentoEditor = new DefaultCellEditor(new UjrDateField());
			DefaultCellEditor valorEditor      = new DefaultCellEditor(new UjrCurrencyField());
			DefaultCellEditor numeroEditor     = new DefaultCellEditor(new UjrNumberField());

			DefaultTableCellRenderer vencimentoRenderer = new DefaultTableCellRenderer();
			DefaultTableCellRenderer valorRenderer      = new DefaultTableCellRenderer();
			DefaultTableCellRenderer numeroRenderer     = new DefaultTableCellRenderer();

			vencimentoRenderer.setToolTipText("Data de Vencimento no formato DD/MM/YYYY");
			vencimentoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

			valorRenderer.setToolTipText("Valor da parcela");
			valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);

			numeroRenderer.setToolTipText("Número do Cheque");
			numeroRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

			vencimentoColumn.setCellRenderer(vencimentoRenderer);
			vencimentoColumn.setCellEditor(vencimentoEditor);

			valorColumn.setCellRenderer(valorRenderer);
			valorColumn.setCellEditor(valorEditor);

			numeroColumn.setCellRenderer(numeroRenderer);
			numeroColumn.setCellEditor(numeroEditor);
		}
	}

}
