package br.ujr.scorecard.gui.view.screen.reports;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.reports.ReportManager;
import br.ujr.scorecard.model.reports.ReportManagerListener;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
public class ResumoMensalFrame extends AbstractDialog  {
	
	protected JDateChooser     txtDtIni         = new JDateChooser("MM/yyyy","##/####",'_');
	protected JDateChooser     txtDtFim         = new JDateChooser("MM/yyyy","##/####",'_');
	protected UjrComboBox      txtContaCorrente = new UjrComboBox(); 
	protected ScorecardManager scoreBusinessDelegate = (ScorecardManager)Util.getBean("scorecardManager");
	private JButton btnProcessarSaldo;
	private JButton btnSair;
	
	public ResumoMensalFrame(JFrame owner) {
		super(owner);
		this.title = "Relatórios - Resumo Mensal";
		this.createUI();
		this.componentsReady();
	}

	protected void createUI() {
		this.width  = 450;
		this.height = 250;
		super.createUI();
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createEtchedBorder());

		buildPanel(panel);
		panel.setBounds(10, 10, this.getWidth() - 23, this.getHeight() - 50);
		
		panMain.add(panel);
		
		//this.setFocusTraversalPolicy(new InternalFocusManager());
	}

	
	/**
	 * @param panBtnAcoes
	 */
	private void buildPanel(JPanel panel) {
		int LBL_WIDTH = 100;
		int Y = 10;
		
		int btn_x = (this.getWidth() - 100) / 2;
		btnProcessarSaldo = new JButton();
		btnSair = new JButton();
		btnProcessarSaldo.setBounds(btn_x, Y, 50, 45);
		btnSair.setBounds(btn_x + 50, Y, 50, 45);
		btnProcessarSaldo.setIcon(new ImageIcon(Util.loadImage(this, "button_ok.png")));
		btnSair.setIcon(new ImageIcon(Util.loadImage(this, "exit.png")));
		btnProcessarSaldo.setActionCommand("IMPRIMIR");
		btnSair.setActionCommand("SAIR");
		btnProcessarSaldo.addActionListener(this);
		btnSair.addActionListener(this);
		Util.setToolTip(this, btnProcessarSaldo, "Imprimir Relatório");
		Util.setToolTip(this, btnSair, "Sair");
		btnProcessarSaldo.setEnabled(true);
		btnSair.setEnabled(true);
		
		Y += 85;
		int X = 15;
		JLabel lblCodigo = new JLabel("Período de:");
		lblCodigo.setBounds(X,Y,LBL_WIDTH, 20);
		this.txtDtIni.setBounds(LBL_WIDTH + X, Y, 75, 20);
		this.txtDtIni.setEnabled(true);
		this.txtDtIni.setFocusable(true);
		
		X += LBL_WIDTH + txtDtIni.getWidth() + 10;
		JLabel lblNome = new JLabel("até:");
		lblNome.setBounds(X,Y,30,20);
		txtDtFim.setBounds(X + lblNome.getWidth(),Y,75,20);
		txtDtFim.setEnabled(true);
		
		X  = 15;
		Y += 40;
		JLabel lblConta = new JLabel("Conta Corrente:");
		lblConta.setBounds(X,Y,LBL_WIDTH,20);
		this.txtContaCorrente.setBounds(X + LBL_WIDTH, Y, 280, 20);
		this.txtContaCorrente.setEditable(false);
		List<ContaCorrente> ccs = scoreBusinessDelegate.listarContaCorrente();
		for(ContaCorrente cc : ccs) {
			this.txtContaCorrente.addItem(cc);
		}
		
		panel.add(lblCodigo);
		panel.add(lblNome);
		panel.add(lblConta);
		panel.add(txtDtIni);
		panel.add(txtDtFim);
		panel.add(txtContaCorrente);
		panel.add(btnProcessarSaldo);
		panel.add(btnSair);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ( "SAIR".equalsIgnoreCase(action)) {
			this.dispose();
		} else
		if ( "IMPRIMIR".equalsIgnoreCase(action) ) {
			if ( this.txtDtFim.getDate() == null  || this.txtDtIni.getDate() == null ) {
				JOptionPane.showMessageDialog(this, "Parâmetros incorretos!","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				txtDtIni.requestFocus();
			} else {
				ContaCorrente cc = (ContaCorrente)this.txtContaCorrente.getSelectedItem();
				long refIni = Util.extrairReferencia(this.txtDtIni.getDate());
				long refFim = Util.extrairReferencia(this.txtDtFim.getDate());
				
				if ( refFim < refIni ) {
					JOptionPane.showMessageDialog(this, "Período inválido!\nReferência Final < Referência Inicial","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				} else {
					PrintReport task = new PrintReport(this);
					task.execute();
				}
			}
		}
	}
	
	private class PrintReport extends SwingWorker<String, String> implements ReportManagerListener {
		
		private LoadingFrame loadingFrame = new LoadingFrame(100);
		ResumoMensalFrame frame = null;
		
		public PrintReport(ResumoMensalFrame frame) {
			this.frame = frame;
		}
		
		@Override
		protected String doInBackground() throws Exception {
			UtilGUI.coverBlinder(frame);
			loadingFrame.showLoadinFrame();
			loadingFrame.setMessage("Processando Relatório");
			ReportManager reportManager = new ReportManager();
			reportManager.addListener(this);
			ContaCorrente contaCorrente = (ContaCorrente)txtContaCorrente.getSelectedItem();
			reportManager.printResumoMensal(contaCorrente, txtDtIni.getDate(), txtDtFim.getDate());
			return null;
		}
		@Override
		protected void done() {
			UtilGUI.uncoverBlinder(frame);
			txtDtIni.requestFocus();
		}
		public void reportStarted() {
		}
		public void reportFinished() {
			loadingFrame.setMessage("Imprimindo Relatório");
			loadingFrame.dispose();
		}
		public void reportMaxProgress(int value) {
			loadingFrame.setMaxProgress(value);
		}
		public void reportProgress(String msg,int value) {
			loadingFrame.setProgressValue(value);
		}
	}
	
	public void focusGained(FocusEvent evt) {
		
	}

	public void focusLost(FocusEvent evt) {
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				ResumoMensalFrame bancoFrame = new ResumoMensalFrame(null);
				bancoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				bancoFrame.setVisible(true);
			}
    	});
	}

	public class InternalFocusManager extends DefaultFocusTraversalPolicy {
		
		@Override
		protected boolean accept(Component aComponent) {
			return super.accept(aComponent);
		}

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent) {
			return super.getComponentAfter(aContainer, aComponent);
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent) {
			return super.getComponentBefore(aContainer, aComponent);
		}
	}
	
	public void windowActivated(WindowEvent evt) {
		((JTextFieldDateEditor)this.txtDtIni.getDateEditor()).requestFocus();
	}
	

}
