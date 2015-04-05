package br.ujr.scorecard.gui.view.screen.treecheck;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class NodeSelectionListener extends MouseAdapter {

	JTree tree;

	public NodeSelectionListener(JTree tree) {
		this.tree = tree;
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		//TreePath  path = tree.getSelectionPath();
		if (path != null) {
			CheckNode node = (CheckNode) path.getLastPathComponent();
			boolean isSelected = !(node.isSelected());
			node.setSelected(isSelected);
			if (node.getSelectionMode() == CheckNode.DIG_IN_SELECTION) {
				if (isSelected) {
					tree.expandPath(path);
				} else {
					if ( row > 0 ) {
						tree.collapsePath(path);
					}
				}
			}
			((DefaultTreeModel) tree.getModel()).nodeChanged(node);
			tree.revalidate();
			tree.repaint();
		}
	}
}