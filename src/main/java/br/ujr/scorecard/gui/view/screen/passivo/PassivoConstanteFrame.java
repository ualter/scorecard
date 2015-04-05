package br.ujr.scorecard.gui.view.screen.passivo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXPanel;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.gui.view.screen.ContaFrame;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.Util;

/**
 * Screen Utilitária para inserção de Passivos que se repetem todos os meses mas não estão interligados
 * como parcelas de um mesmo débito.
 * Exemplos: Provedor Internet, TV a Cabo.
 * 
 * @author ualter.junior
 */
public class PassivoConstanteFrame extends AbstractDialog implements FocusListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092445806277556773L;
	
	protected JLabel                      lblRefInicial     = new JLabel("Iniciar:");
	protected JDateChooser                txtRefInicial     = new JDateChooser("MM/yyyy","##/####",'_');
	protected JLabel                      lblRefFinal       = new JLabel("Terminar:");
	protected JDateChooser                txtRefFinal       = new JDateChooser("MM/yyyy","##/####",'_');
	protected JLabel                      lblConta          = new JLabel("Conta:");
	protected JLabel                      lblDescricao      = new JLabel("Histórico:");
	protected JLabel                      lblValor          = new JLabel("Valor:");
	protected UjrComboBox                 txtConta          = new UjrComboBox();
	protected UjrTextField                txtHistorico      = new UjrTextField();
	protected UjrCurrencyField            txtValor          = new UjrCurrencyField();
	protected JButton                     btnOk             = new JButton();
	protected JButton                     btnCancela        = new JButton();
	protected JButton                     btnContas         = new JButton();
	protected Date                        periodoDataIni    = null;
	protected JLabel                      lblPassivoTipo    = new JLabel("Passivo:");
	protected UjrComboBox                 txtPassivoTipo    = new UjrComboBox();
	protected JLabel                      lblDiaVencimento  = new JLabel("Dia Vencimento:");
	protected UjrTextField                txtDiaVencimento  = new UjrTextField();
	protected JLabel                      lblContaCorrente  = new JLabel("Conta Corrente:");
	protected UjrComboBox                 txtContaCorrente  = new UjrComboBox();
	private JXPanel blinder;

	public PassivoConstanteFrame(JFrame owner, Date periodoDataIni) {
		super(owner);
		this.periodoDataIni = periodoDataIni;
		this.title = this.getTitulo();
		
		this.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
	}
	
	public String getTitulo() {
		return "Inserção de Passivo Constante";
	}
	
	protected void createUI() {
		this.width = 475;
		this.height = 380;
		super.createUI();
		
		// Panels, painéis containers para os componentes
		JPanel panOrcamento = new JPanel();
		JPanel panBtnAcoes  = new JPanel();
		// Setting Layouts, configurando o Layout criado anteriormente para os
		// painéis
		panOrcamento.setLayout(null);
		panBtnAcoes.setLayout(null);
		// Borders
		panOrcamento.setBorder(BorderFactory.createEtchedBorder());
		panBtnAcoes.setBorder(BorderFactory.createEtchedBorder());

		buildPanelOrcamento(panOrcamento);
		buildPanelBtnAcoes(panBtnAcoes);

		panOrcamento.setBounds(10, 82, 452, 257);
		panBtnAcoes.setBounds(10, 10, 452, 65);
		
		this.txtConta.getEditor().getEditorComponent().addFocusListener(this);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");
		
		Util.setToolTip(this,btnOk, "Confirmar Inserção de Passivo Constante");
		Util.setToolTip(this,btnCancela, "Cancelar Inclusão");
		Util.setToolTip(this,btnContas, "Pesquisar Contas");

		blinder = new JXPanel();
		blinder.setBackground(Color.BLACK);
		blinder.setBounds(0, 0, width, height);
		blinder.setAlpha(0.5f);
		blinder.setOpaque(true);
		blinder.setVisible(false);
		panMain.add(blinder);
		
		panMain.add(panOrcamento);
		panMain.add(panBtnAcoes);

		this.txtRefInicial.setDate(this.periodoDataIni);	
		this.txtRefFinal.setDate(this.periodoDataIni);
		this.setFocusTraversalPolicy(new InternalFocusManager(txtRefInicial));
		
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
	
	private void buildPanelOrcamento(JPanel panOrcamento) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 105;
		int SPACE_VERT = 30;
		
		lblRefInicial.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblRefInicial.getWidth();
		txtRefInicial.setBounds(X, Y, 86, 20);
		txtRefInicial.setFont(new Font("Courier New",Font.PLAIN,13));
		txtRefInicial.setName("txtRefOrigem");
		((JTextFieldDateEditor)txtRefInicial.getDateEditor()).setName("txtRefOrigem");
		
		X  = 10;
		Y += SPACE_VERT;
		lblRefFinal.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblRefFinal.getWidth();
		txtRefFinal.setBounds(X, Y, 86, 20);
		txtRefFinal.setFont(new Font("Courier New",Font.PLAIN,13));
		txtRefFinal.setName("txtRefFinal");
		((JTextFieldDateEditor)txtRefFinal.getDateEditor()).setName("txtRefFinal");
		
		X  = 10;
		Y += SPACE_VERT;
		lblContaCorrente.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblContaCorrente.getWidth();
		txtContaCorrente.setBounds(X, Y, 300, 20);
		txtContaCorrente.getEditor().getEditorComponent().setName("txtContaCorrente");
		txtContaCorrente.setEditable(false);
		txtContaCorrente.setName("txtContaCorrente");
		List<ContaCorrente> ccs = this.scorecardBusiness.listarContaCorrente();
		for (ContaCorrente corrente : ccs) {
			txtContaCorrente.addItem(corrente);
		}
		X += txtContaCorrente.getWidth() + 2;
		
		X  = 10;
		Y += SPACE_VERT;
		this.txtConta.setEditable(false);
		this.txtConta.setMaximumRowCount(20);
		lblConta.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblConta.getWidth();
		txtConta.setBounds(X, Y, 300, 20);
		txtConta.getEditor().getEditorComponent().setName("txtConta");
		txtConta.setName("txtConta");
		List<Conta> list = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : list) {
			conta.toStringMode = 1;
			txtConta.addItem(conta); 
		}
		btnContas.setName("btnConta");
		X += txtConta.getWidth() + 2;
		btnContas.setBounds(X,Y,20,20);
		
		X  = 10;
		Y += SPACE_VERT;
		lblPassivoTipo.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblPassivoTipo.getWidth();
		txtPassivoTipo.setBounds(X, Y, 300, 20);
		txtPassivoTipo.getEditor().getEditorComponent().setName("txtPassivo");
		txtPassivoTipo.setEditable(false);
		txtPassivoTipo.setName("txtPassivo");
		txtPassivoTipo.addItem("Cheque");
		txtPassivoTipo.addItem("Visa");
		txtPassivoTipo.addItem("Visa Electron");
		txtPassivoTipo.addItem("MasterCard");
		txtPassivoTipo.addItem("Débito");
		X += txtPassivoTipo.getWidth() + 2;
		
		X  = 10;
		Y += SPACE_VERT;
		lblDiaVencimento.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblDiaVencimento.getWidth();
		txtDiaVencimento.setBounds(X, Y, 30, 20);
		txtDiaVencimento.setName("txtDiaVencimento");
		txtDiaVencimento.setFont(new Font("Courier New",Font.PLAIN,13));
		txtDiaVencimento.addFocusListener(this);
		
		X  = 10;
		Y += SPACE_VERT;
		lblDescricao.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblDescricao.getWidth();
		txtHistorico.setBounds(X, Y, 300, 20);
		txtHistorico.setName("txtHistorico");
		txtHistorico.setFont(new Font("Courier New",Font.PLAIN,13));
		txtHistorico.addFocusListener(this);
		
		X  = 10;
		Y += SPACE_VERT;
		lblValor.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblValor.getWidth();
		txtValor.setBounds(X, Y, 105, 20);
		txtValor.setName("txtValor");
		txtValor.setFont(new Font("Courier New",Font.PLAIN,13));
		txtValor.addFocusListener(this);
		
		panOrcamento.add(lblRefInicial);
		panOrcamento.add(txtRefInicial);
		panOrcamento.add(lblRefFinal);
		panOrcamento.add(txtRefFinal);
		panOrcamento.add(lblDescricao);
		panOrcamento.add(txtHistorico);
		panOrcamento.add(lblConta);
		panOrcamento.add(txtConta);
		panOrcamento.add(btnContas);
		panOrcamento.add(txtValor);
		panOrcamento.add(lblValor);
		panOrcamento.add(lblPassivoTipo);
		panOrcamento.add(txtPassivoTipo);
		panOrcamento.add(lblDiaVencimento);
		panOrcamento.add(txtDiaVencimento);
		panOrcamento.add(txtContaCorrente);
		panOrcamento.add(lblContaCorrente);
	}

	public class InternalFocusManager extends FocusTraversalPolicy {

		private Component firstFocus = null;
		
		public InternalFocusManager(Component firstFocus) {
			this.firstFocus = firstFocus;
		}
		
		public Component getComponentAfter(Container ctn, Component comp) {
			String componentName = comp.getName();
			if ( componentName == null ) {
				System.out.println("Ops... getComponentAfter retornou nulo Component " + comp);
			} else {
				if (componentName.equals("txtRefOrigem")) {
					return (JFormattedTextField)txtRefFinal.getDateEditor();
				} else
				if (componentName.equals("txtRefFinal")) {
					return txtContaCorrente;
				} else
				if (componentName.equals("txtContaCorrente")) {
					return txtConta;
				} else
				if (componentName.equals("txtConta")) {
					return txtPassivoTipo;
				} else
				if (componentName.equals("txtPassivo")) {
					return txtDiaVencimento;
				} else if (componentName.equals("txtDiaVencimento")) {
					return txtHistorico;	
				} else if (componentName.equals("txtHistorico")) {
					return txtValor;	
				} else if (componentName.equals("txtValor")) {
					return btnOk;	
				} else if (componentName.equals("btnOk")) {
					return btnCancela;
				} else if (componentName.equals("btnCancela")) {
					return (JFormattedTextField)txtRefInicial.getDateEditor();
				}
			}
			return null;
		}

		public Component getComponentBefore(Container ctn, Component comp) {
			String componentName = comp.getName();
			if (componentName == null) {
				System.out.println("Ops... getComponentAfter retornou nulo Component " + comp);
			} else {
				if (componentName.equals("btnOk")) {
					return btnCancela;
				} else if (componentName.equals("txtRefOrigem")) {
					return btnOk;	
				} else if (componentName.equals("txtRefFinal")) {
					return (JFormattedTextField)txtRefInicial.getDateEditor();
				} else if (componentName.equals("txtContaCorrente")) {
					return (JFormattedTextField)txtRefFinal.getDateEditor();	
				} else if (componentName.equals("txtConta")) {
					return txtContaCorrente;
				} else	if (componentName.equals("txtPassivo")) {
					return txtConta;
				} else if (componentName.equals("txtDiaVencimento")) {
					return txtPassivoTipo;	
				} else if (componentName.equals("txtHistorico")) {
					return txtDiaVencimento;
				} else if (componentName.equals("txtValor")) {
					return txtHistorico;
				} else if (componentName.equals("btnCancela")) {
					return txtValor;
				}
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
			this.savePassivoConstante();
		} else
		if ( e.getActionCommand().indexOf("CONTA") != -1) {
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setVisible(true);
			if ( contaFrame.getConta() != null ) {
				this.txtConta.getModel().setSelectedItem(contaFrame.getConta());
			}
		}
	}
	
	private class CriarPassivosConstantes extends SwingWorker<String, Passivo> {

		private PassivoConstanteFrame frame;
		private LoadingFrame loadingFrame;
		private long refIni;
		private long refFim;
		private ContaCorrente contaCorrente;
		private Conta conta;
		private String tipoPassivo;
		private String diaVencimento;
		private String historico;
		private String valor;
		private int totalMeses;
		
		public CriarPassivosConstantes(PassivoConstanteFrame frame, 
				long refIni, long refFim, ContaCorrente contaCorrente, Conta conta,
				String tipoPassivo, String diaVencimento, String historico, String valor) {
			this.frame         = frame;
			this.refIni        = refIni;
			this.refFim        = refFim;
			this.contaCorrente = contaCorrente;
			this.conta         = conta;
			this.tipoPassivo   = tipoPassivo;
			this.diaVencimento = diaVencimento;
			this.historico     = historico;
			this.valor         = valor;
			
			for(long i = refIni; i <= refFim; i = Util.computeReferencia(i, 1)) {
				this.totalMeses++;
			}
			
			frame.btnCancela.setEnabled(false);
			frame.btnOk.setEnabled(false);
			frame.blinder.setVisible(true);
		}
		
		@Override
		protected String doInBackground() throws Exception {
			loadingFrame = new LoadingFrame(this.totalMeses);
			loadingFrame.showLoadinFrame();
			
			for(long i = refIni; i <= refFim; i = Util.computeReferencia(i, 1)) {
			
				Passivo passivo     = null;
				boolean isCheque    = false;
				if ( "Cheque".equalsIgnoreCase(tipoPassivo) ) {
					passivo = new Cheque();
					isCheque = true;
				} else
				if ( "Visa".equalsIgnoreCase(tipoPassivo) ) {
					passivo = new Cartao();
					((Cartao)passivo).setOperadora(Operadora.VISA);
				} else
				if ( "Visa Electron".equalsIgnoreCase(tipoPassivo) ) {
					passivo = new Cartao();
					((Cartao)passivo).setOperadora(Operadora.VISA_ELECTRON);
				} else
				if ( "MasterCard".equalsIgnoreCase(tipoPassivo) ) {
					passivo = new Cartao();
					((Cartao)passivo).setOperadora(Operadora.MASTERCARD);
				} else
				if ( "Débito".equalsIgnoreCase(tipoPassivo) ) {
					passivo = new DebitoCC();
				} else {
					throw new RuntimeException("Verificar, problemas de Lógica, não deveria acontecer.");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				DecimalFormat df = new DecimalFormat("00");
				String dataVencimento = 
					df.format(Integer.parseInt(this.diaVencimento)) 
					+ "/" 
					+ (String.valueOf(i)).substring(4) 
					+ "/" + (String.valueOf(i)).substring(0, 4);
				
				passivo.setDataMovimento(sdf.parse(dataVencimento));
				passivo.setConta(this.conta);
				passivo.setContaCorrente(this.contaCorrente);
				passivo.setHistorico(this.historico);
				passivo.setTotalParcelas(1);
				
				HashSet<Parcela> parcelas = new HashSet<Parcela>();
				Parcela parcela = new Parcela();
				parcela.setCheque(isCheque);
				parcela.setNumeroCheque("000000");
				parcela.setDataVencimento(dataVencimento);
				parcela.setReferencia(i);
				parcela.setEfetivado(false);
				parcela.setNumero(1);
				parcela.setValor(Util.parseCurrency(this.valor));
				parcela.setPassivo(passivo);
				parcelas.add(parcela);
				
				passivo.setParcelas(parcelas);
				
				scorecardBusiness.savePassivo(passivo);
				
				publish(new Passivo[]{passivo});
				loadingFrame.incrementProgressValue();
			}
			
			loadingFrame.dispose();
			return null;
		}

		@Override
		protected void done() {
			frame.blinder.setVisible(false);
			frame.btnCancela.setEnabled(true);
			frame.btnOk.setEnabled(true);
		}

		@Override
		protected void process(List<Passivo> chunks) {
			for (Passivo passivo : chunks) {
				String msg = "Salvando " + 
					passivo.getClass().getName().substring(passivo.getClass().getName().lastIndexOf(".") + 1)
					+ " para " + 
					Util.formatReferencia(passivo.getParcela().getReferencia(),false,false);
				loadingFrame.setMessage(msg);
			}
		}
		
	}
	
	protected void savePassivoConstante() {
		if ( isValidValueOfFields() ) {
			
			long refIni = Util.extrairReferencia(this.txtRefInicial.getDate());
			long refFim = Util.extrairReferencia(this.txtRefFinal.getDate());
			
			CriarPassivosConstantes task = new CriarPassivosConstantes(this,refIni,refFim,
					(ContaCorrente)this.txtContaCorrente.getSelectedItem(),
					(Conta)this.txtConta.getSelectedItem(),
					(String)this.txtPassivoTipo.getSelectedItem(),
					this.txtDiaVencimento.getText(),this.txtHistorico.getText(),this.txtValor.getText());
			task.execute();
			
			this.txtDiaVencimento.setText("");
			this.txtHistorico.setText("");
			this.txtValor.setText("");
		}
	}
	
	public boolean isValidValueOfFields() {
		if ( this.txtRefInicial.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Referência Inicial Inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			((JFormattedTextField)this.txtRefInicial.getDateEditor()).requestFocus();
			return false;
		}
		if ( this.txtRefFinal.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Referência Final Inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			((JFormattedTextField)this.txtRefFinal.getDateEditor()).requestFocus();
			return false;
		}
		if ( this.txtConta.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha a conta contábil associada !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtConta.requestFocus();
			return false;
		}
		if ( StringUtils.isBlank(this.txtHistorico.getText()) ) {
			JOptionPane.showMessageDialog(this,"Digite o histórico !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtHistorico.requestFocus();
			return false;
		}
		if ( StringUtils.isBlank(this.txtDiaVencimento.getText()) ) {
			JOptionPane.showMessageDialog(this,"Digite o Dia de Vencimento !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDiaVencimento.requestFocus();
			return false;
		}
		if ( !StringUtils.isNumeric(this.txtDiaVencimento.getText()) ) {
			JOptionPane.showMessageDialog(this,"O Dia de Vencimento deve ser númerico !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDiaVencimento.requestFocus();
			return false;
		} else {
			int dia = Integer.parseInt(this.txtDiaVencimento.getText());
			if ( dia < 1 || dia > 30 ) {
				JOptionPane.showMessageDialog(this,"O Dia de Vencimento deve estar entre 1 a 30 !","Atenção",JOptionPane.ERROR_MESSAGE);
				this.txtDiaVencimento.requestFocus();
				return false;
			}
		}
		if ( StringUtils.isBlank(this.txtValor.getText()) ) {
			JOptionPane.showMessageDialog(this,"Digite o valor !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtValor.requestFocus();
			return false;
		} 
		return true;
	}

	public void focusGained(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtHistorico")) {
			txtHistorico.selectAll();
		}
	}

	public void focusLost(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtConta") ) {
			String desc = "";
			Conta conta = (Conta) this.txtConta.getEditor().getItem();
			if ( conta != null ) {
				desc = conta.getDescricao();
			}
			if ( StringUtils.isBlank(txtHistorico.getText()) || !txtHistorico.getText().equals(desc)) {
				txtHistorico.setText(desc);
			}
		}
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
		((JTextFieldDateEditor)this.txtRefInicial.getDateEditor()).requestFocus();
	}
	
	public static void main(String[] args) {
		PassivoConstanteFrame p = new PassivoConstanteFrame(null,Util.today());
		p.setVisible(true);
	}
}
