package br.ujr.scorecard.gui.view.screen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class LoadingFrame extends JDialog {
	
	private JLabel message;
	private JProgressBar progressBar;
	private boolean withoutProgressBar;

	public LoadingFrame() {
		this.createUI(1);
	}
	
	public LoadingFrame(boolean withoutProgressBar) {
		this.withoutProgressBar = withoutProgressBar;
		this.createUI(0);
	}
	
	public LoadingFrame(int maxPrograssBar) {
		this.createUI(maxPrograssBar);
	}

	private void createUI(int maxPrograssBar) {
		this.setAlwaysOnTop(true);
		this.setLayout(null);
		this.setUndecorated(true);
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int frameWidth = 350;
		int frameHeight = !this.withoutProgressBar ? 150 : 120;
		this.setBounds(((screenSize.width-frameWidth)/2), ((screenSize.height-frameHeight)/2) - 15, frameWidth, frameHeight);
		
		JPanel panMain = new JPanel();
		panMain.setBorder(BorderFactory.createEtchedBorder(1));
		panMain.setLayout(null);
		panMain.setBounds(0,0,this.getWidth(),this.getHeight());
		
		JPanel panSec = new JPanel();
		panSec.setOpaque(true);
		panSec.setBorder(BorderFactory.createEtchedBorder(1));
		panSec.setBounds(10,10,this.getWidth() - 20, this.getHeight() - 20);
		panSec.setLayout(null);
		panMain.add(panSec);
		
		Random random = new Random();
		int imgLoading = random.nextInt(2);
		JLabel icon = new JLabel(
				"Aguarde...",
				new ImageIcon("c:/eclipse-workspace/Scorecard/graficoss/loading" + imgLoading + ".gif"),
				SwingConstants.CENTER);
		icon.setOpaque(true);
		icon.setHorizontalAlignment(SwingConstants.CENTER);
		icon.setBounds(10,10,panSec.getWidth() - 20, 50);
		panSec.add(icon);
		
		message = new JLabel("");
		message.setOpaque(true);
		message.setHorizontalAlignment(SwingConstants.CENTER);
		message.setFont(new Font("Verdana",Font.BOLD,10));
		message.setForeground(Color.BLUE);
		message.setBounds(10,62,panSec.getWidth() - 20, 30);
		panSec.add(message);
		
		if ( !this.withoutProgressBar ) {
			progressBar = new JProgressBar();
			progressBar.setBounds(10,95,panSec.getWidth() - 20, 23);
			progressBar.setMaximum(maxPrograssBar);
			progressBar.setStringPainted(true);
			panSec.add(progressBar);
		}
		
		this.add(panMain);
	}
	
	public static void main(String[] args) {
		LoadingFrame f = new LoadingFrame(true);
		f.setMessage("Salvando");
		f.showLoadinFrame();
	}
	
	public void setProgressValue(int value) {
		this.progressBar.setValue(value);
	}
	
	public void showLoadinFrame() {
		this.setVisible(true);
	}
	
	public void setMessage(String msg) {
		this.message.setText(msg);
	}
	
	public void setStatus(String msg, int progressValue) {
		this.setMessage(msg);
		this.setProgressValue(progressValue);
	}
	
	public void setMaxProgress(int value) {
		this.progressBar.setMaximum(value);
	}
	
	public void incrementProgressValue() {
		this.setProgressValue(this.progressBar.getValue() + 1);
	}

}
