package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class EfetivadoTableCellRenderer extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 2878765222057568025L;
	private Color foregroundColor;
	private RendererRules rendererRules;
	
	private int columnEfetivado;
	
	public EfetivadoTableCellRenderer() {
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
	}
	
	public EfetivadoTableCellRenderer(RendererRules rendererRules) {
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
		this.rendererRules   = rendererRules;
	}
	
	public EfetivadoTableCellRenderer(Color foreground) {
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
		this.foregroundColor = foreground;
	}
	
	public EfetivadoTableCellRenderer(Color foreground, RendererRules rendererRules) {
		this.columnEfetivado = UtilTableCells.DEFAULT_COLUMN_EFETIVADO;
		this.foregroundColor = foreground;
		this.rendererRules   = rendererRules;
	}
	
	public EfetivadoTableCellRenderer(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}
	
	public EfetivadoTableCellRenderer(int columnEfetivado, RendererRules rendererRules) {
		this.columnEfetivado = columnEfetivado;
		this.rendererRules   = rendererRules;
	}

	
	public int getColumnEfetivado() {
		return columnEfetivado;
	}

	public void setColumnEfetivado(int columnEfetivado) {
		this.columnEfetivado = columnEfetivado;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		UtilTableCells.setUpTableCellRendererComponent(this, this.columnEfetivado, table, value, isSelected, hasFocus, row, column, this.foregroundColor);
		this.checkRendererRules(table, row);
		return this;
	}
	
	public void checkRendererRules(JTable table, int row) {
		if ( this.rendererRules != null ) { 
			 RendererRules.Format format = this.rendererRules.getPredicatedRenderer().checkFormat((DefaultTableModel)table.getModel(), row);
			
			 if ( format != null ) {
				 if ( format.getFont() != null ) {
					 this.setFont(format.getFont());
				 }
				 if ( format.getBackground() != null ) {
					 this.setBackground(format.getBackground());
				 }
				 if ( format.getForeground() != null ) {
					 this.setForeground(format.getForeground());
				 }
			 }
		}
	}
	
}
