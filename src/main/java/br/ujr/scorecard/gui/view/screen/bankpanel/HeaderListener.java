package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.components.gui.tabela.DefaultOrdenadorTabela;
import br.ujr.components.gui.tabela.SortButtonRenderer;

public class HeaderListener extends MouseAdapter {
	
	private JTableHeader header;
	private SortButtonRenderer renderer;
	private DefaultOrdenadorTabela ordenador;

	public DefaultOrdenadorTabela getOrdenador() {
		return ordenador;
	}

	public void setOrdenador(DefaultOrdenadorTabela ordenador) {
		this.ordenador = ordenador;
	}

	public HeaderListener(JTableHeader header, SortButtonRenderer renderer, DefaultModelTabela model) {
		this.header = header;
		this.renderer = renderer;
		this.ordenador = new DefaultOrdenadorTabela(model);
	}
	
	public HeaderListener(JTableHeader header, SortButtonRenderer renderer, DefaultOrdenadorTabela ordenador) {
		this.header = header;
		this.renderer = renderer;
		this.ordenador = ordenador;
	}

	public void mousePressed(MouseEvent e) {
		int col = header.columnAtPoint(e.getPoint());
		int sortCol = header.getTable().convertColumnIndexToModel(col);
		renderer.setPressedColumn(col);
		renderer.setSelectedColumn(col);
		header.repaint();

		if (header.getTable().isEditing()) {
			header.getTable().getCellEditor().stopCellEditing();
		}

		boolean isAscent;
		if (SortButtonRenderer.DOWN == renderer.getState(col)) {
			isAscent = true;
		} else {
			isAscent = false;
		}
		//DefaultModelTabela model = (DefaultModelTabela)header.getTable().getModel();
		ordenador.sortByColumn(sortCol,isAscent);
	}

	public void mouseReleased(MouseEvent e) {
		//int col = header.columnAtPoint(e.getPoint());
		renderer.setPressedColumn(-1);
		header.repaint();
	}
}