package br.ujr.components.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class PopupListener extends MouseAdapter {
	
	protected JPopupMenu popup;
	protected int posX = -1;
	protected int posY = -1;
	
	public PopupListener(JPopupMenu popup){
		this.popup = popup;
	}
	
	public PopupListener(JPopupMenu popup, int posX, int posY){
		this.popup = popup;
		this.posX  = posX;
		this.posY  = posY;
	}
	
	public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        //if (e.isPopupTrigger()) {
        	int x = this.getPosX() == -1 ? e.getX() : this.getPosY();
        	int y = this.getPosY() == -1 ? e.getY() : this.getPosX();
            popup.show(e.getComponent(),x, y);
        //}
    }

	public int getPosX() {
		return posX;
	}

	public void setPosX(int x) {
		this.posX = x;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int y) {
		this.posY = y;
	}

}
