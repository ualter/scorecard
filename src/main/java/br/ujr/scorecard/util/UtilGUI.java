/*
 * UtilGUI.java created on 07/11/2004, 18:32:23
 */

package br.ujr.scorecard.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * @author ualter.junior
 */
public class UtilGUI 
{
    
    public static Rectangle getRectangle(int largura, int altura)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width-largura)/2;
        int y = (screenSize.height-altura)/2;
        Rectangle rect = new Rectangle();
        rect.setBounds(x, y,largura,altura);
        return rect;
    }
    public static void showErrorMessage(Component parent,String message)
    {
        JOptionPane.showMessageDialog(parent, message,"Atenção",JOptionPane.ERROR_MESSAGE);
        
    }
    
    /**
     * "Cortina" utilizada para desabilitar toda a tela quando esta está em processamento 
     * e não pode ter interação com usuário, este deve somente esperar, as informações relevantes
     * estarão em outra janela superior, a LoadingFrame.
     * Estratégia adotada:
     * Utilizar o GlassPane da janela colocando sobre um JTextField que terá uma figura sendo renderizada
     * como background. Um screenshot será utilizado como figura. 
     * @param window
     * @author Ualter
     */
    public static void coverBlinder(JDialog dialog) {
    	Rectangle rectangle = getBlinderDimensions(dialog);
    	JTextField blinder = getBlinder(getBlinderBackground(rectangle), dialog.getWidth(), dialog.getHeight());
    	dialog.setGlassPane(blinder);
		dialog.getGlassPane().setVisible(true);
    }
    public static void coverBlinder(JFrame frame) {
    	Rectangle rectangle = getBlinderDimensions(frame);
    	JTextField blinder = getBlinder(getBlinderBackground(rectangle), frame.getWidth(), frame.getHeight());
    	frame.setGlassPane(blinder);
		frame.getGlassPane().setVisible(true);
    }
    @SuppressWarnings("serial")
	private static Rectangle getBlinderDimensions(Window window) {
    	int X = window.getX();
    	int Y = window.getY() + 29;
    	int W = window.getWidth();
    	int H = window.getHeight();
    	return new Rectangle(X,Y,W,H);
    }
    private static BufferedImage getBlinderBackground(Rectangle rectangle) {
    	return Util.captureScreenShot((int)rectangle.getX(),(int)rectangle.getY(),(int)rectangle.getWidth(),(int)rectangle.getHeight());
    }
	@SuppressWarnings("serial")
	private static JTextField getBlinder(final BufferedImage screenShot, int width, int height) {
		JTextField blinder = new JTextField(){
    	    @Override
    	    protected void paintComponent(Graphics g) {
    	        super.paintComponent(g);
    	        try {
    	            Border border = UIManager.getBorder("TextField.border");
    	            JTextField defaultField = new JTextField();
    	            final int x = getWidth() - border.getBorderInsets(defaultField).right - screenShot.getWidth();
    	            //setMargin(new Insets(2, 2, 2, getWidth() - x));
    	            setMargin(new Insets(0, 0, 0, getWidth() - x));
    	            int y = (getHeight() - screenShot.getHeight())/2;
    	            g.drawImage(screenShot, x, y, this);
    	            Graphics2D g2 = (Graphics2D)g;
    	            
    	            Rectangle2D rectangle = new Rectangle2D.Float(0,0,this.getWidth(),this.getHeight());
    	            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
   	                g2.setPaint(Color.BLACK);
    	            g2.setBackground(Color.BLACK);
    	            g2.fill(rectangle);
    	            
    	        } catch(Exception ignore) {ignore.printStackTrace();}
    	    }
    	};
    	blinder.setEnabled(false);
    	blinder.setLayout(null);
		blinder.setBackground(Color.GRAY);
		blinder.setBounds(0, 0, width, height);
		blinder.setOpaque(true);
		blinder.setVisible(false);
		return blinder;
	}
	public static void uncoverBlinder(JFrame frame) {
		frame.getGlassPane().setVisible(false);
	}
	public static void uncoverBlinder(JDialog dialog) {
		dialog.getGlassPane().setVisible(false);
	}
    /**
     * Faz com o que a thread de trabalho SwingWorker possa bloquear a thread do Event Dispatcher da GUI
     * para que a mesma recebe o resultado da execução do SwingWorker e não continue enquanto isso não ocorrer
     * @param owner
     * @param sw
     */
    public static void makeSwingWorkerWait(JDialog owner, SwingWorker sw) {
    	JDialog dialog = new JDialog(owner, true);
		dialog.setUndecorated(true);
		sw.addPropertyChangeListener(new SwingWorkerCompletionWaiter(dialog));
		sw.execute();
		dialog.setVisible(true);
    }
    /**
     * Class utilizada para "ouvir" a alteração de status do SwingWorker para DONE e assim
     * liberar a trava feita através de um JDialog Modal (mais info acima)
     * @author Ualter
     *
     */
    static class SwingWorkerCompletionWaiter implements PropertyChangeListener {
	     private JDialog dialog;
	     public SwingWorkerCompletionWaiter(JDialog dialog) {
	         this.dialog = dialog;
	     }
	     public void propertyChange(PropertyChangeEvent event) {
	         if ("state".equals(event.getPropertyName()) && SwingWorker.StateValue.DONE == event.getNewValue()) {
	             dialog.setVisible(false);
	             dialog.dispose();
	         }
	     }
	}
    
    

}
