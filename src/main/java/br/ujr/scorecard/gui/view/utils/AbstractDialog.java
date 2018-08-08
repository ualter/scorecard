package br.ujr.scorecard.gui.view.utils;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import br.ujr.scorecard.gui.view.screen.FocusListenerHelper;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.util.Util;

public abstract class AbstractDialog extends JDialog implements ActionListener, WindowListener {
	
	protected int                       width             = 660; 
	protected int                       height            = 485;//350;
	protected int                       deslocar          = 0;
	protected String                    title             = "Title";
	protected JPanel                    panMain           = null;
	protected boolean                   transparent       = false;
	protected JFrame                    owner             = null;
	protected ScorecardManager          scorecardBusiness = null;
	
	public AbstractDialog(JFrame owner, boolean transparent) {
		super(owner, true);
		this.owner = owner;
		this.transparent = transparent;
		this.scorecardBusiness = (ScorecardManager)Util.getBean("scorecardManager");
		if ( this.isTransparent() ) {
			this.panMain = new TransparentBackground(this.owner);
		} else {
			this.panMain = new JPanel();
		}
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
	}
	public AbstractDialog(JFrame owner) {
		this(owner,false);
	}
	
	protected void createUI() {
		this.addWindowListener(this);
		this.setTitle(" " + title);
		
		this.panMain.setLayout(null);
		this.panMain.setBorder(BorderFactory.createBevelBorder(1));
		this.panMain.setVisible(true);
		
		this.getContentPane().add(panMain);
		this.calculateSizePosition();	
	}
	
	protected void calculateSizePosition() {
		int stWidth  = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int stHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		if ( this.width  > stWidth  ) this.width  = stWidth;
		if ( this.height > stHeight ) this.height = stHeight;
		int x = ((((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth() ) - this.width ) / 2) + deslocar; 
        int y = ((((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()) - this.height) / 2) + deslocar; 
        this.setResizable(false); 
        this.setSize(width, height); 
        this.setLocation(x, y);
	}
	
	public void windowClosing(WindowEvent evt) {
	}
	public void windowActivated(WindowEvent evt) {
	}
	public void windowClosed(WindowEvent evt) {
	}
	public void windowDeactivated(WindowEvent evt) {
	}
	public void windowDeiconified(WindowEvent evt) {
	}
	public void windowIconified(WindowEvent evt) {
	}
	public void windowOpened(WindowEvent evt) {
	}
	public void actionPerformed(ActionEvent e) {
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}
	
	public void componentsReady() {
		new FocusListenerHelper(this);
	}
}
