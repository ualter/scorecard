package br.ujr.scorecard.gui.view.screen.cellrenderer;

import javax.swing.table.DefaultTableModel;

@FunctionalInterface
public interface PredicateRenderer {
	
	public RendererRules.Format checkFormat(DefaultTableModel model, int row);

}
