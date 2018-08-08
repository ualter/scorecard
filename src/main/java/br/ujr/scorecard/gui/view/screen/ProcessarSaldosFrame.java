package br.ujr.scorecard.gui.view.screen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.SaldoProcessadoEvent;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ScorecardManagerListener;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.Util;

/**
 * @author ualter.junior
 */
public class ProcessarSaldosFrame extends AbstractDialog implements FocusListener, MouseListener, ScorecardManagerListener {
	
	protected DefaultListModel modelList        = null;
	protected JList            listMessages     = null;
	protected int              HEIGHT_LIST      = 460;
	protected int              HEIGHT_MNT       = 70;
	protected JDateChooser     txtDtIni         = new JDateChooser("MM/yyyy","##/####",'_');
	protected JDateChooser     txtDtFim         = new JDateChooser("MM/yyyy","##/####",'_');
	protected UjrComboBox      txtContaCorrente = new UjrComboBox(); 
	protected ScorecardManager scorecardManager = (ScorecardManager)Util.getBean("scorecardManager");
	private JButton btnProcessarSaldo;
	private JButton btnSair;
	private JScrollPane scrollPane;
	private static Logger logger = Logger.getLogger(ProcessarSaldosFrame.class);
	
	public ProcessarSaldosFrame(JFrame owner) {
		super(owner);
		this.title = "Processar Saldos";
		this.createUI();
		scorecardManager.addScorecardManagerListener(this);
		this.componentsReady();
	}

	protected void createUI() {
		this.width = 800;
		this.height = 600;
		super.createUI();
		
		JPanel panList = new JPanel();
		JPanel panMnt  = new JPanel();
		panList.setLayout(null);
		panMnt.setLayout(null);
		panList.setBorder(BorderFactory.createEtchedBorder());
		panMnt.setBorder(BorderFactory.createEtchedBorder());

		buildPanelList(panList);
		buildPanelMnt(panMnt);

		panList.setBounds(10, 10, this.getWidth() - 23, HEIGHT_LIST);
		panMnt.setBounds(10, HEIGHT_LIST + 20, this.getWidth() - 23, HEIGHT_MNT);
		
		panMain.add(panList);
		panMain.add(panMnt);
	}

	/**
	 * @param panBtnAcoes
	 */
	private void buildPanelMnt(JPanel panel) {
		int LBL_WIDTH = 80;
		
		int Y = 25;
		int X = 10;
		JLabel lblCodigo = new JLabel("Período de:");
		lblCodigo.setBounds(X,Y,LBL_WIDTH, 20);
		this.txtDtIni.setBounds(LBL_WIDTH, Y, 75, 20);
		this.txtDtIni.setEnabled(true);
		this.txtDtIni.setFocusable(true);
		
		X += 155;
		JLabel lblNome = new JLabel("até:");
		lblNome.setBounds(X,Y,30,20);
		txtDtFim.setBounds(X + lblNome.getWidth(),Y,75,20);
		txtDtFim.setEnabled(true);
		
		X += 200;
		JLabel lblConta = new JLabel("Conta Corrente:");
		lblConta.setBounds(X,Y,100,20);
		this.txtContaCorrente.setBounds(X + 100, Y, 280, 20);
		this.txtContaCorrente.setEditable(false);
		List<ContaCorrente> ccs = scorecardManager.listarContaCorrente();
		for(ContaCorrente cc : ccs) {
			this.txtContaCorrente.addItem(cc);
		}
		
		panel.add(lblCodigo);
		panel.add(lblNome);
		panel.add(lblConta);
		panel.add(txtDtIni);
		panel.add(txtDtFim);
		panel.add(txtContaCorrente);
	}
	
	
	private void buildPanelList(JPanel panel) {
		this.loadList();
		this.listMessages = new JList(this.modelList);
		this.listMessages.setName("LIST");
		this.listMessages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.listMessages.addMouseListener(this);
		this.listMessages.setFont(new Font("Courier New",Font.PLAIN,12));
		
		scrollPane = new JScrollPane(this.listMessages);
		scrollPane.setBounds(10, 64, this.getWidth() - 43, HEIGHT_LIST - 75);
		panel.add(scrollPane);
		
		int btn_x = (this.getWidth() - 100) / 2;
		btnProcessarSaldo = new JButton();
		btnSair = new JButton();
		btnProcessarSaldo.setBounds(btn_x, 10, 50, 45);
		btnSair.setBounds(btn_x + 50, 10, 50, 45);
		btnProcessarSaldo.setIcon(new ImageIcon(Util.loadImage(this, "button_ok.png")));
		btnSair.setIcon(new ImageIcon(Util.loadImage(this, "exit.png")));
		btnProcessarSaldo.setActionCommand("PROCESSAR");
		btnSair.setActionCommand("SAIR");
		btnProcessarSaldo.addActionListener(this);
		btnSair.addActionListener(this);
		Util.setToolTip(this, btnProcessarSaldo, "Criar novo banco");
		Util.setToolTip(this, btnSair, "Sair");
		btnProcessarSaldo.setEnabled(true);
		btnSair.setEnabled(true);
		
		panel.add(btnProcessarSaldo);
		panel.add(btnSair);
	}

	private void loadList() {
		this.modelList = new DefaultListModel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ( "SAIR".equalsIgnoreCase(action)) {
			this.dispose();
		} else
		if ( "PROCESSAR".equalsIgnoreCase(action) ) {
			if ( this.txtDtFim.getDate() == null  || this.txtDtIni.getDate() == null ) {
				JOptionPane.showMessageDialog(this, "Parâmetros incorretos!","Atenção!!!",JOptionPane.WARNING_MESSAGE);
			} else {
				this.modelList.clear();
				final ContaCorrente cc = (ContaCorrente)this.txtContaCorrente.getSelectedItem();
				final long refIni = Util.extrairReferencia(this.txtDtIni.getDate());
				final long refFim = Util.extrairReferencia(this.txtDtFim.getDate());
				
				/**
				 * Segurança (Apenas para o usuário UALTER JR.)
				 */
				if ( refIni <= 200505 ) {
					JOptionPane.showMessageDialog(this, "Não existe movimento anterior a 05/2005, não havia dados de planilhas para importação.\n Este saldo anterior foi arbitrariamente gravado, não deve ser alterado.","Atenção!!!",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				if ( refFim < refIni ) {
					JOptionPane.showMessageDialog(this, "Período inválido!\nReferência Final < Referência Inicial","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				} else {
					ConsistirSaldoTask task = new ConsistirSaldoTask(this,cc,refIni,refFim);
					task.execute();
				}
			}
		}
	}
	
	private class ConsistirSaldoTask extends SwingWorker<String, String> implements  ScorecardManagerListener {
		private ProcessarSaldosFrame frame;
		private ContaCorrente cc;
		private long refIni;
		private long refFim;
		private long refProcessado;
		private LoadingFrame loadingFrame;
		private int totalMeses;
		
		public ConsistirSaldoTask(ProcessarSaldosFrame frame, ContaCorrente cc, long refIni, long refFim) {
			this.frame = frame;
			this.cc = cc;
			this.refIni = refIni;
			this.refFim = refFim;
			scorecardManager.addScorecardManagerListener(this);
			/**
			 * Calculando total de meses a serem processados
			 */
			for(long i = refIni; i <= refFim; i = Util.computeReferencia(i, 1)) {
				this.totalMeses++;
			}
		}
		@Override
		protected String doInBackground() throws Exception {
			loadingFrame = new LoadingFrame(this.totalMeses);
			loadingFrame.showLoadinFrame();
			
			frame.btnProcessarSaldo.setEnabled(false);
			frame.btnSair.setEnabled(false);
			scorecardManager.consistirSaldosAnteriores(this.cc,this.refIni,this.refFim,true);
			
			loadingFrame.dispose();
			return null;
		}
		@Override
		protected void done() {
			frame.btnProcessarSaldo.setEnabled(true);
			frame.btnSair.setEnabled(true);
		}
		public void actionConsistirSaldosAnteriores(SaldoProcessadoEvent event) {
			switch (event.getEvent()) {
				case MENSAGEM: {
					loadingFrame.setMessage(event.getMessage());
					if ( event.getPayLoad() != null ) {
						long ref = ((Long)event.getPayLoad()).longValue();
						if ( ref != refProcessado ) {
							refProcessado = ref;
							loadingFrame.incrementProgressValue();
						}
					}
					break;
				}
			}
		}
		public void actionAtivo(Ativo ativo) {}
		public void actionOrcamento(Orcamento orcamento) {}
		public void actionPassivo(Passivo passivo) {}
		public void actionTransferencia(Transferencia transferencia) {}
	}
	
	public void focusGained(FocusEvent evt) {
	}

	public void focusLost(FocusEvent evt) {
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
	}
	
	public static void main(String[] args) {
		ProcessarSaldosFrame bancoFrame = new ProcessarSaldosFrame(null);
		bancoFrame.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void actionAtivo(Ativo ativo) {
	}
	public void actionConsistirSaldosAnteriores(SaldoProcessadoEvent event) {
		switch (event.getEvent()) {
			case MENSAGEM: {
				this.modelList.addElement(event.getMessage());
				break;
			}
		}
	}
	public void actionOrcamento(Orcamento orcamento) {
	}
	public void actionPassivo(Passivo passivo) {
	}
	public void actionTransferencia(Transferencia transferencia) {
	}
	
	public void windowActivated(WindowEvent evt) {
		((JTextFieldDateEditor)this.txtDtIni.getDateEditor()).requestFocus();
	}
}
