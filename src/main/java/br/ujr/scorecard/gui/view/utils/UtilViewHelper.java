package br.ujr.scorecard.gui.view.utils;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author Ualter
 */
public class UtilViewHelper {
	
	public static GridBagConstraints makeConstraints(ConstraintsGrid constraints) {
		GridBagConstraints g = new GridBagConstraints();
		g.gridx      = constraints.x;
		g.gridy      = constraints.y;
		g.gridwidth  = constraints.gridwidth;
		g.gridheight = constraints.gridheight;
		g.weightx    = constraints.weightx;
		g.weighty    = constraints.weighty;
		g.anchor     = constraints.anchor;
		g.fill       = constraints.fill;
		g.insets     = constraints.insets;
		g.ipadx      = constraints.padx;
		g.ipady      = constraints.pady;
		return g;
	}

	public static class ConstraintsGrid {
		public int x          = 0;
		public int y          = 0;
		public int weightx    = 1;
		public int weighty    = 1;
		public int anchor     = GridBagConstraints.CENTER;
		public int fill       = GridBagConstraints.BOTH;
		public Insets insets  = new Insets(1, 1, 1, 1);
		public int padx       = 1;
		public int pady       = 1;
		public int gridwidth  = 1;
		public int gridheight = 1;
	}

}

