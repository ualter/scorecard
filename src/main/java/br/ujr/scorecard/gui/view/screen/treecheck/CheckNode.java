package br.ujr.scorecard.gui.view.screen.treecheck;

import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckNode extends DefaultMutableTreeNode {

  public final static int SINGLE_SELECTION = 0;
  public final static int DIG_IN_SELECTION = 4;
  protected int selectionMode;
  protected boolean isSelected;
  protected String iconName;


  public CheckNode() {
    this(null);
  }

  public CheckNode(Object userObject) {
    this(userObject, true, false);
  }

  public CheckNode(Object userObject, boolean allowsChildren
                                    , boolean isSelected) {
    super(userObject, allowsChildren);
    this.isSelected = isSelected;
    setSelectionMode(DIG_IN_SELECTION);
  }


  public void setSelectionMode(int mode) {
    selectionMode = mode;
  }

  public int getSelectionMode() {
    return selectionMode;
  }

  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
    
    if ((selectionMode == DIG_IN_SELECTION)
        && (children != null)) {
      Enumeration enumeration = children.elements();     
      while (enumeration.hasMoreElements()) {
        CheckNode node = (CheckNode)enumeration.nextElement();
        node.setSelected(isSelected);
      }
    }
  }
  
  public boolean isSelected() {
    return isSelected;
  }
  
  public String getIconName() {
	return iconName;
  }

  /*
   * public String getIconName() { return iconName; }
   */
	
  public void setIconName(String name) {
	iconName = name;
  }



  // If you want to change "isSelected" by CellEditor,
  /*
	 * public void setUserObject(Object obj) { if (obj instanceof Boolean) {
	 * setSelected(((Boolean)obj).booleanValue()); } else {
	 * super.setUserObject(obj); } }
	 */

}

