package br.ujr.scorecard.gui.view.screen.cellrenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class ResumoGeralTableCellRenderer extends DefaultTableCellRenderer {
	
	
	private int qtdeCartoes;
	private boolean hasCheques;
	
	
	public ResumoGeralTableCellRenderer(int qtdeCartoes, boolean hasCheques) {
		super();
		this.qtdeCartoes = qtdeCartoes;
		this.hasCheques  = hasCheques;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2418901921193844216L;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		this.setForeground(table.getForeground());
		this.setBackground(table.getBackground());
		this.setText(String.valueOf(value));
		this.setFont(new Font("Verdana",Font.PLAIN,11));
		this.setBorder(table.getBorder());
		
		int linhaHeader         = 0;
		int linhaCheque         = 1;
		int linhasCartoes       = qtdeCartoes;
		int linhaSaques         = hasCheques ? qtdeCartoes + 2 : qtdeCartoes + 1;
		int linhaDebito         = linhaSaques + 1;
		int linhaDespesas       = linhaDebito + 1;
		int linhaDepositos      = linhaDespesas + 1;
		int linhaInvestimentos  = linhaDepositos + 1;
		int linhaTransferencias = linhaInvestimentos + 1;
		int linhaEstipendios    = linhaTransferencias + 1;
		int linhaSaldoPrevisto  = linhaEstipendios + 1;
		int linhaSaldoReal      = linhaSaldoPrevisto + 1;
		
		if ( row == linhaHeader ) {
			layoutPositive();
			this.setForeground(Color.BLUE);
			table.setRowHeight(linhaHeader, 25);
		} else
		if ( hasCheques && row == linhaCheque ) {
			this.setForeground(Color.RED);
		} else
		// Cartoes
		if ( hasCheques && (row > linhaCheque && row <= (linhaCheque + linhasCartoes)) ) {
			this.setBackground(new Color(240,240,240));
			this.setForeground(Color.RED);
		} else
		if ( !hasCheques && ( row > 0  && row <= linhasCartoes ) ) {
			this.setBackground(new Color(248,248,248));
			this.setForeground(Color.RED);
		} else
		if ( row == linhaSaques || row == linhaDebito ) {
			this.setForeground(Color.RED);
		} else
		if ( row == linhaDespesas ) {
			layoutNegativeSumUp();
			table.setRowHeight(linhaDespesas, 25);
		} else
		if ( row >= linhaDepositos && row <= linhaEstipendios ) {
			this.setForeground(Color.BLUE);
		} else
		if ( row >= linhaSaldoPrevisto && row <= linhaSaldoReal ) {
			table.setRowHeight(row, 25);
			layoutNeutral();
		}
			
		return this;
	}

	private void layoutNegativeSumUp() {
		this.setFont(new Font("Verdana",Font.BOLD,11));
		this.setBackground(new Color(255,255,153));
		this.setForeground(Color.RED);
	}

	private void layoutPositive() {
		this.setFont(new Font("Verdana",Font.BOLD,11));
		this.setBackground(new Color(255,255,153));
		this.setForeground(Color.BLUE);
	}
	
	private void layoutNeutral() {
		this.setFont(new Font("Verdana",Font.BOLD,11));
		this.setBackground(new Color(255,255,153));
		this.setForeground(Color.BLACK);
	}

}
