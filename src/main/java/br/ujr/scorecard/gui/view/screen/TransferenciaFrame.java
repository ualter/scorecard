package br.ujr.scorecard.gui.view.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
public class TransferenciaFrame extends AbstractDialog implements FocusListener {
	
	protected JLabel                      lblReferencia           = new JLabel("Mês / Ano:");
	protected JDateChooser                txtReferencia           = new JDateChooser("MM/yyyy","##/####",'_');
	protected JLabel                      lblConta                = new JLabel("Conta Contábil:");
	protected JLabel                      lblHistorico            = new JLabel("Histórico:");
	protected JLabel                      lblValor                = new JLabel("Valor:");
	protected UjrComboBox                 txtConta                = new UjrComboBox();
	protected UjrTextField                txtDescricao            = new UjrTextField();
	protected UjrCurrencyField            txtValor                = new UjrCurrencyField();
	protected JButton                     btnOk                   = new JButton();
	protected JButton                     btnCancela              = new JButton();
	protected JButton                     btnContasTransf         = new JButton();
	protected JLabel                      lblContaCorrenteDestino = new JLabel("Conta Corrente:");
	protected JLabel                      lblAtivoDestino         = new JLabel("Ativo:");
	protected JLabel                      lblContaDestino         = new JLabel("Conta Contábil:");
	protected JLabel                      lblHistoricoDestino     = new JLabel("Histórico:");
	protected UjrComboBox                 txtContaCorrenteDestino = new UjrComboBox();
	protected UjrComboBox                 txtContaContabilDestino = new UjrComboBox();
	protected UjrComboBox                 txtAtivoDestino         = new UjrComboBox();
	protected UjrTextField                txtHistoricoDestino     = new UjrTextField();
	protected JButton                     btnContasAtivo          = new JButton();
	protected boolean                     isUpdate                = false;
	protected ContaCorrente               contaCorrente           = null;
	protected Transferencia               loadedTransferencia     = null;
	protected String[]                    ativos                  = new String[]{"DEPÓSITO","ESTIPÊNDIO"};

	public TransferenciaFrame(JFrame owner, ContaCorrente contaCorrente, Transferencia orcamento) {
		super(owner);
		this.title = MessageManager.getMessage(MessagesEnum.TituloFrameTransferencia, 
				new String[]{"TRANSFERÊNCIA"}, 
				new Object[]{contaCorrente,contaCorrente.getBanco()});
		this.contaCorrente   = contaCorrente;
		this.loadedTransferencia = orcamento;
		
		if ( this.loadedTransferencia != null ) {
			this.txtReferencia.setDate(this.loadedTransferencia.getReferenciaAsDate());
			this.txtConta.setSelectedItem(this.loadedTransferencia.getConta());
			this.txtDescricao.setText(this.loadedTransferencia.getHistorico());
			this.txtValor.setText(Util.formatCurrency(this.loadedTransferencia.getValor(),true));
			if ( this.loadedTransferencia.getAtivoTransferido() instanceof Deposito ) {
				this.txtAtivoDestino.setSelectedItem(ativos[0]);
			} else 
			if ( this.loadedTransferencia.getAtivoTransferido() instanceof Salario ) {
				this.txtAtivoDestino.setSelectedItem(ativos[1]);
			}
			this.txtContaCorrenteDestino.setSelectedItem(this.loadedTransferencia.getAtivoTransferido().getContaCorrente());
			this.txtContaContabilDestino.setSelectedItem(this.loadedTransferencia.getAtivoTransferido().getConta());
			this.txtHistoricoDestino.setText(this.loadedTransferencia.getAtivoTransferido().getHistorico());
		}
		this.createUI();
	}
	public TransferenciaFrame(JFrame owner, ContaCorrente contaCorrente) {
		this(owner,contaCorrente,null);
	}
	
	protected void createUI() {
		this.width = 475;
		this.height = 440;
		super.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnContasTransf.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
		this.btnContasAtivo.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
		
		// Panels, painéis containers para os componentes
		JPanel panTransferencia = new JPanel();
		JPanel panAtivoDestino  = new JPanel();
		JPanel panBtnAcoes      = new JPanel();
		// Setting Layouts, configurando o Layout criado anteriormente para os
		// painéis
		panTransferencia.setLayout(null);
		panBtnAcoes.setLayout(null);
		panAtivoDestino.setLayout(null);
		// Borders
		panTransferencia.setBorder(BorderFactory.createEtchedBorder());
		panBtnAcoes.setBorder(BorderFactory.createEtchedBorder());
		panAtivoDestino.setBorder(BorderFactory.createEtchedBorder());

		buildPanelTransferencia(panTransferencia);
		buildPanelBtnAcoes(panBtnAcoes);
		buildPanelAtivoDestino(panAtivoDestino);

		panTransferencia.setBounds(10, 82, 452, 155);
		panAtivoDestino.setBounds(10, 245, 452, 155);
		panBtnAcoes.setBounds(10, 10, 452, 65);
		
		this.txtConta.getEditor().getEditorComponent().addFocusListener(this);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnContasTransf.addActionListener(this);
		this.btnContasAtivo.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnContasTransf.setName("Conta");
		this.btnContasTransf.setActionCommand("CONTAS_TRANSF");
		this.btnContasAtivo.setActionCommand("CONTAS_ATIVO");
		
		Util.setToolTip(this,btnOk, "Salvar Orçamento");
		Util.setToolTip(this,btnCancela, "Cancelar "  + (this.loadedTransferencia != null ? "Alteração" : "Inclusão"));
		Util.setToolTip(this,btnContasTransf, "Pesquisar Contas Contábeis");
		Util.setToolTip(this,btnContasAtivo, "Pesquisar Contas Contábeis");

		panMain.add(panTransferencia);
		panMain.add(panBtnAcoes);
		panMain.add(panAtivoDestino);

		if ( this.txtReferencia.getDate() == null )
			this.txtReferencia.setDate(Util.today());	
		Component firstFocus = this.loadedTransferencia != null ? this.txtValor : this.txtReferencia;
		
		if ( this.loadedTransferencia == null ) {
			Conta transf = this.scorecardBusiness.getContaPorDescricao("Transferencia").get(0);
			transf.toStringMode = 1;
			this.txtConta.setSelectedItem(transf);
			this.txtDescricao.setText("Repasse Santander");
			this.txtContaCorrenteDestino.setSelectedItem(this.scorecardBusiness.getContaCorrentePorId( Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCSantander) ));
			this.txtHistoricoDestino.setText("Deposito Itaú");
			Conta depos = this.scorecardBusiness.getContaPorDescricao("Depósito").get(0);
			depos.toStringMode = 1;
			this.txtContaContabilDestino.setSelectedItem(depos);
		}
		this.setFocusTraversalPolicy(new InternalFocusManager(firstFocus));
	}

	/**
	 * @param panBtnAcoes
	 */
	private void buildPanelBtnAcoes(JPanel panBtnAcoes) {
		
		int x = (this.width - 100) / 2;
		
		btnOk.setBounds(x, 9, 50, 45);
		btnCancela.setBounds(x+50, 9, 50, 45);

		btnOk.setName("btnOk");
		btnCancela.setName("btnCancela");
		
		panBtnAcoes.add(btnOk);
		panBtnAcoes.add(btnCancela);
	}
	
	protected int getIndexColumnEfetivado() {
		return UtilTableCells.DEFAULT_COLUMN_EFETIVADO - 1;
	}
	
	private void buildPanelTransferencia(JPanel panTransferencia) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 100;
		int SPACE_VERT = 30;
		
		JLabel lblTransferencia = new JLabel("T R A N S F E R Ê N C I A",SwingConstants.CENTER);
		lblTransferencia.setFont(new Font("Verdana",Font.BOLD,10));
		lblTransferencia.setOpaque(true);
		lblTransferencia.setBackground(Color.GRAY);
		lblTransferencia.setForeground(Color.WHITE);
		lblTransferencia.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblTransferencia.setBounds(X - 6,Y - 10, 443,20);
		
		X  = 10;
		Y += (SPACE_VERT - 10);
		
		lblReferencia.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblReferencia.getWidth();
		txtReferencia.setBounds(X, Y, 86, 20);
		txtReferencia.setFont(new Font("Courier New",Font.PLAIN,13));
		txtReferencia.setName("txtRefOrigem");
		
		X  = 10;
		Y += SPACE_VERT;
		this.txtConta.setEditable(true);
		this.txtConta.setMaximumRowCount(20);
		lblConta.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblConta.getWidth();
		txtConta.setBounds(X, Y, 300, 20);
		txtConta.getEditor().getEditorComponent().setName("txtConta");
		List<Conta> list = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : list) {
			conta.toStringMode = 1;
			txtConta.addItem(conta); 
		}
		if (this.loadedTransferencia == null)
			txtConta.setSelectedItem(null);
		btnContasTransf.setName("btnConta");
		X += txtConta.getWidth() + 2;
		btnContasTransf.setBounds(X,Y,20,20);
		
		X  = 10;
		Y += SPACE_VERT;
		lblHistorico.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblHistorico.getWidth();
		txtDescricao.setBounds(X, Y, 300, 20);
		txtDescricao.setName("txtHistorico");
		txtDescricao.setFont(new Font("Courier New",Font.PLAIN,13));
		txtDescricao.addFocusListener(this);
		
		X  = 10;
		Y += SPACE_VERT;
		lblValor.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblValor.getWidth();
		txtValor.setBounds(X, Y, 105, 20);
		txtValor.setName("txtValor");
		txtValor.setFont(new Font("Courier New",Font.PLAIN,13));
		txtValor.addFocusListener(this);
		
		panTransferencia.add(lblTransferencia);
		panTransferencia.add(lblReferencia);
		panTransferencia.add(txtReferencia);
		panTransferencia.add(lblHistorico);
		panTransferencia.add(txtDescricao);
		panTransferencia.add(lblConta);
		panTransferencia.add(txtConta);
		panTransferencia.add(btnContasTransf);
		panTransferencia.add(txtValor);
		panTransferencia.add(lblValor);
	}
	
	private void buildPanelAtivoDestino(JPanel panAtivoDestino) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 100;
		int SPACE_VERT = 30;
		
		JLabel lblDestino = new JLabel("D E S T I N O",SwingConstants.CENTER);
		lblDestino.setFont(new Font("Verdana",Font.BOLD,10));
		lblDestino.setOpaque(true);
		lblDestino.setBackground(Color.GRAY);
		lblDestino.setForeground(Color.WHITE);
		lblDestino.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblDestino.setBounds(X - 6,Y - 10, 443,20);
		
		X  = 10;
		Y += (SPACE_VERT - 10);
		
		lblContaCorrenteDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		lblContaCorrenteDestino.setName("lblContaCorrenteDestino");
		
		this.txtContaCorrenteDestino.setEditable(true);
		this.txtContaCorrenteDestino.setMaximumRowCount(20);
		txtContaCorrenteDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		X += txtContaCorrenteDestino.getWidth();
		txtContaCorrenteDestino.setBounds(X, Y, 300, 20);
		txtContaCorrenteDestino.getEditor().getEditorComponent().setName("txtContaCorrenteDestino");
		List<ContaCorrente> list = this.scorecardBusiness.listarContaCorrente();
		for (ContaCorrente cc : list) {
			if ( !cc.equals(this.getContaCorrente()) && cc.getBanco().isAtivo() ) {
				txtContaCorrenteDestino.addItem(cc); 
			}
		}
		if (this.loadedTransferencia == null)
			txtContaCorrenteDestino.setSelectedItem(null);
		
		X  = 10;
		Y += SPACE_VERT;
		lblContaDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		lblContaDestino.setName("lblContaDestino");
		
		this.txtContaContabilDestino.setEditable(true);
		this.txtContaContabilDestino.setMaximumRowCount(20);
		txtContaContabilDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		X += txtContaContabilDestino.getWidth();
		txtContaContabilDestino.setBounds(X, Y, 300, 20);
		txtContaContabilDestino.getEditor().getEditorComponent().setName("txtContaContabilDestino");
		List<Conta> listC = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : listC) {
			conta.toStringMode = 1;
			txtContaContabilDestino.addItem(conta); 
		}
		if (this.loadedTransferencia == null)
			txtContaContabilDestino.setSelectedItem(null);	
		btnContasAtivo.setName("btnConta");
		X += txtContaContabilDestino.getWidth() + 2;
		btnContasAtivo.setBounds(X,Y,20,20);
		
		X  = 10;
		Y += SPACE_VERT;
		lblAtivoDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		lblAtivoDestino.setName("lblAtivoDestino");
		
		this.txtAtivoDestino.setEditable(false);
		this.txtAtivoDestino.setMaximumRowCount(20);
		txtAtivoDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		X += txtAtivoDestino.getWidth();
		txtAtivoDestino.setBounds(X, Y, 300, 20);
		txtAtivoDestino.setName("txtAtivoDestino");
		txtAtivoDestino.getEditor().getEditorComponent().setName("txtAtivoDestino");
		for (int i = 0; i < ativos.length; i++) {
			txtAtivoDestino.addItem(ativos[i]);
		}
		
		X  = 10;
		Y += SPACE_VERT;
		lblHistoricoDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblHistoricoDestino.getWidth();
		txtHistoricoDestino.setBounds(X, Y, 300, 20);
		txtHistoricoDestino.setName("txtHistoricoDestino");
		txtHistoricoDestino.setFont(new Font("Courier New",Font.PLAIN,13));
		txtHistoricoDestino.addFocusListener(this);
		
		panAtivoDestino.add(lblDestino);
		panAtivoDestino.add(lblContaCorrenteDestino);
		panAtivoDestino.add(txtContaCorrenteDestino);
		panAtivoDestino.add(lblContaDestino);
		panAtivoDestino.add(txtContaContabilDestino);
		panAtivoDestino.add(btnContasAtivo);
		panAtivoDestino.add(lblAtivoDestino);
		panAtivoDestino.add(txtAtivoDestino);
		panAtivoDestino.add(lblHistoricoDestino);
		panAtivoDestino.add(txtHistoricoDestino);
	}

	public class InternalFocusManager extends FocusTraversalPolicy {

		private Component firstFocus = null;
		
		public InternalFocusManager(Component firstFocus) {
			this.firstFocus = firstFocus;
		}
		
		public Component getComponentAfter(Container ctn, Component comp) {
			String componentName = comp.getName();
			if ( componentName == null ) {
				return txtConta;
			}
			if (componentName.equals("txtConta")) {
				return txtDescricao;
			} else if (componentName.equals("txtHistorico")) {
				return txtValor;	
			} else if (componentName.equals("txtValor")) {
				return txtContaCorrenteDestino;	
			} else if (componentName.equals("txtContaCorrenteDestino")) {
				return txtContaContabilDestino;	
			} else if (componentName.equals("txtContaContabilDestino")) {
				return txtAtivoDestino;	
			} else if (componentName.equals("txtAtivoDestino")) {
				return txtHistoricoDestino;	
			} else if (componentName.equals("txtHistoricoDestino")) {
				return btnOk;	
			} else if (componentName.equals("btnOk")) {
				return btnCancela;
			} else if (componentName.equals("btnCancela")) {
				return (JFormattedTextField)txtReferencia.getDateEditor();
			}
			return null;
		}

		public Component getComponentBefore(Container ctn, Component comp) {
			String componentName = comp.getName();
			if ( componentName == null ) {
				return btnOk;
			}
			if (componentName.equals("btnOk")) {
				return btnCancela;
			} else
			if (componentName.equals("btnCancela")) {
				return txtHistoricoDestino;
			} else
			if (componentName.equals("txtHistoricoDestino")) {
				return txtAtivoDestino;
			} else
			if (componentName.equals("txtAtivoDestino")) {
				return txtContaContabilDestino;
			} else
			if (componentName.equals("txtContaContabilDestino")) {
				return txtContaCorrenteDestino;
			} else
			if (componentName.equals("txtContaCorrenteDestino")) {
				return txtValor;
			} else
			if (componentName.equals("txtValor")) {
				return txtDescricao;
			} else
			if (componentName.equals("txtHistorico")) {
				return txtConta;
			} else
			if (componentName.equals("txtConta")) {
				return (JFormattedTextField)txtReferencia.getDateEditor();
			}	
			return null;
		}

		public Component getDefaultComponent(Container ctn) {
			return this.firstFocus;
		}

		public Component getFirstComponent(Container ctn) {
			return this.firstFocus;
		}

		public Component getLastComponent(Container ctn) {
			return btnCancela;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().indexOf("CANCELAR") != -1) {
			this.dispose();
		} else
		if ( e.getActionCommand().indexOf("OK") != -1) {
			this.saveTransferencia();
		} else
		if ( e.getActionCommand().indexOf("CONTAS_TRANSF") != -1) {
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setVisible(true);
			if ( contaFrame.getConta() != null ) {
				this.txtConta.getModel().setSelectedItem(contaFrame.getConta());
			}
		} else
		if ( e.getActionCommand().indexOf("CONTAS_ATIVO") != -1) {
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setVisible(true);
			if ( contaFrame.getConta() != null ) {
				this.txtContaContabilDestino.getModel().setSelectedItem(contaFrame.getConta());
			}
		}
	}
	
	protected void saveTransferencia() {
		if ( isValidValueOfFields() ) {
			Transferencia transferencia = new Transferencia();
			String        strAtivo      = (String)txtAtivoDestino.getSelectedItem();
			if ( this.loadedTransferencia != null ) {
				transferencia = this.loadedTransferencia;
			}
			
			transferencia.setContaCorrente(this.getContaCorrente());
			transferencia.setReferencia(this.txtReferencia.getDate());
			transferencia.setDataMovimento(this.txtReferencia.getDate());
			transferencia.setValor(Util.parseCurrency(this.txtValor.getText()));
			transferencia.setHistorico(this.txtDescricao.getText());
			transferencia.setConta((Conta)this.txtConta.getSelectedItem());
			
			Class classAtivo = null;
			if ( strAtivo.equals("DEPÓSITO") ) {
				classAtivo = Deposito.class;
			} else
			if ( strAtivo.equals("ESTIPÊNDIO") ) {
				classAtivo = Salario.class;
			}
			transferencia.setAtivoTransferido((ContaCorrente)this.txtContaCorrenteDestino.getSelectedItem(), 
											  (Conta)this.txtContaContabilDestino.getSelectedItem(),classAtivo,
											  this.txtHistoricoDestino.getText());
			UtilGUI.coverBlinder(this);
			new SalvarTransferencia(this,transferencia).execute();
		}
	}
	
	private class SalvarTransferencia extends SwingWorker<String, String> {
		private TransferenciaFrame frame;
		private Transferencia transferencia;
		private LoadingFrame loadingFrame;
		public SalvarTransferencia(TransferenciaFrame frame, Transferencia transferencia) {
			this.frame = frame;
			this.transferencia = transferencia;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(transferencia.getValor(), false) + " - " + transferencia.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			frame.scorecardBusiness.saveTransferencia(transferencia);
			return null;
		}
		protected void done() {
			this.loadingFrame.dispose();
			frame.dispose();
		}
	}
	
	public boolean isValidValueOfFields() {
		if ( this.txtReferencia.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Referência inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtReferencia.requestFocus();
			return false;
		}
		if ( this.txtConta.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha a Conta Contábil associada a esta Transferência !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtConta.requestFocus();
			return false;
		}
		if ( this.txtDescricao.getText() == null ) {
			JOptionPane.showMessageDialog(this,"Digite um Histórico !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDescricao.requestFocus();
			return false;
		}
		if ( this.txtContaCorrenteDestino.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha a Conta Corrente destino do Ativo !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtContaCorrenteDestino.requestFocus();
			return false;
		}
		if ( this.txtContaContabilDestino.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha a Conta Contábil destino do Ativo criado por esta Transferência !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtContaContabilDestino.requestFocus();
			return false;
		}
		if ( this.txtAtivoDestino.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha o Ativo a ser criado por essa Transferência !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtAtivoDestino.requestFocus();
			return false;
		}
		if ( this.txtHistoricoDestino.getText() == null ) {
			JOptionPane.showMessageDialog(this,"Digite um histórico para o Ativo a ser criado !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtHistoricoDestino.requestFocus();
			return false;
		}
		return true;
	}

	public void focusGained(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtHistorico")) {
			txtDescricao.selectAll();
		}
	}

	public void focusLost(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		/*if ( component.getName().equals("txtConta") ) {
			String desc = "";
			if ( this.txtConta.getEditor().getItem() instanceof Conta ) {
				Conta conta = (Conta) this.txtConta.getEditor().getItem();
				if ( conta != null ) {
					desc = conta.getDescricao();
				}
				if ( StringUtils.isBlank(txtDescricao.getText()) || !txtDescricao.getText().equals(desc)) {
					txtDescricao.setText(desc);
				}
			}
		}*/
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
		((JTextFieldDateEditor)this.txtReferencia.getDateEditor()).requestFocus();
	}
	
	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}
	
	public static void main(String[] args) {
		TransferenciaFrame frame = new TransferenciaFrame(null,((ScorecardManager)Util.getBean("scorecardManager")).getContaCorrentePorId(41));
		frame.setVisible(true);
	}

}
