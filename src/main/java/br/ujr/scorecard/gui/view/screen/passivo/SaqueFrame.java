package br.ujr.scorecard.gui.view.screen.passivo;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.util.Calendar;
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

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.scorecard.gui.view.screen.ContaFrame;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * TODO: Alterar de Mês/Ano para Data Movimento no formato completo dd/MM/yyyy 
 * e gravar a data de vencimento igual a data de movimento informada  
 * @author Ualter
 *
 */
public class SaqueFrame extends AbstractDialog implements FocusListener {

	protected JLabel                      lblReferencia     = new JLabel("Movimento:");
	protected JDateChooser                txtReferencia     = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
	protected JLabel                      lblConta          = new JLabel("Conta:");
	protected JLabel                      lblDescricao      = new JLabel("Histórico:");
	protected JLabel                      lblValor          = new JLabel("Valor:");
	protected UjrComboBox                 txtConta          = new UjrComboBox();
	protected UjrTextField                txtHistorico      = new UjrTextField();
	protected UjrCurrencyField            txtValor          = new UjrCurrencyField();
	protected JButton                     btnOk             = new JButton();
	protected JButton                     btnCancela        = new JButton();
	protected JButton                     btnContas         = new JButton();
	protected boolean                     isUpdate          = false;
	protected ContaCorrente               contaCorrente     = null;
	protected Passivo                     loadedPassivo     = null;

	public SaqueFrame(JFrame owner, ContaCorrente contaCorrente, Passivo passivo) {
		super(owner);
		this.title = MessageManager.getMessage(MessagesEnum.TituloFrameSaque, 
				new String[]{getTitulo()}, 
				new Object[]{contaCorrente,contaCorrente.getBanco()});
		this.contaCorrente   = contaCorrente;
		this.loadedPassivo   = passivo;
		
		if ( this.loadedPassivo != null ) {
			this.txtReferencia.setDate(this.loadedPassivo.getParcela().getReferenciaAsDate());
			this.txtConta.setSelectedItem(this.loadedPassivo.getConta());
			this.txtHistorico.setText(this.loadedPassivo.getHistorico());
			this.txtValor.setText(Util.formatCurrency(this.loadedPassivo.getParcela().getValor(),true));
		}
		this.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
	}
	public String getTitulo() {
		return "SAQUES";
	}
	
	public SaqueFrame(JFrame owner, ContaCorrente contaCorrente) {
		this(owner,contaCorrente,null);
	}
	
	protected void createUI() {
		this.width = 475;
		this.height = 263;
		super.createUI();
		
		// Panels, painéis containers para os componentes
		JPanel panSaque = new JPanel();
		JPanel panBtnAcoes  = new JPanel();
		// Setting Layouts, configurando o Layout criado anteriormente para os
		// painéis
		panSaque.setLayout(null);
		panBtnAcoes.setLayout(null);
		// Borders
		panSaque.setBorder(BorderFactory.createEtchedBorder());
		panBtnAcoes.setBorder(BorderFactory.createEtchedBorder());

		buildPanelSaque(panSaque);
		buildPanelBtnAcoes(panBtnAcoes);

		panSaque.setBounds(10, 82, 452, 140);
		panBtnAcoes.setBounds(10, 10, 452, 65);
		
		this.txtConta.addFocusListener(this);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");
		
		Util.setToolTip(this,btnOk, "Salvar Saque");
		Util.setToolTip(this,btnCancela, "Cancelar "  + (this.loadedPassivo != null ? "Alteração" : "Inclusão"));
		Util.setToolTip(this,btnContas, "Pesquisar Contas");

		panMain.add(panSaque);
		panMain.add(panBtnAcoes);

		if ( this.txtReferencia.getDate() == null ) {
			this.txtReferencia.setDate(Util.today());	
		}
		Component firstFocus = this.loadedPassivo != null ? this.txtValor : this.txtReferencia;
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
	
	private void buildPanelSaque(JPanel panSaque) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 105;
		int SPACE_VERT = 30;
		
		lblReferencia.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblReferencia.getWidth();
		txtReferencia.setBounds(X, Y, 110, 20);
		txtReferencia.setFont(new Font("Courier New",Font.PLAIN,13));
		txtReferencia.setName("txtRefOrigem");
		
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
		if (this.loadedPassivo == null)
			txtConta.setSelectedItem(null);
		btnContas.setName("btnConta");
		X += txtConta.getWidth() + 2;
		btnContas.setBounds(X,Y,20,20);
		
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
		
		panSaque.add(lblReferencia);
		panSaque.add(txtReferencia);
		panSaque.add(lblDescricao);
		panSaque.add(txtHistorico);
		panSaque.add(lblConta);
		panSaque.add(txtConta);
		panSaque.add(btnContas);
		panSaque.add(txtValor);
		panSaque.add(lblValor);
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
			} else {
				if (componentName.equals("txtConta")) {
					return txtHistorico;
				} else if (componentName.equals("txtHistorico")) {
					return txtValor;	
				} else if (componentName.equals("txtValor")) {
					return btnOk;	
				} else if (componentName.equals("btnOk")) {
					return btnCancela;
				} else if (componentName.equals("btnCancela")) {
					return (JFormattedTextField)txtReferencia.getDateEditor();
				}
			}
			return null;
		}

		public Component getComponentBefore(Container ctn, Component comp) {
			String componentName = comp.getName();
			if (componentName == null) {
				return btnOk;
			} else {
				if (componentName.equals("btnOk")) {
					return btnCancela;
				} else if (componentName.equals("txtConta")) {
					return (JFormattedTextField)txtReferencia.getDateEditor();	
				} else if (componentName.equals("txtHistorico")) {
					return txtConta;
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
			this.saveSaque();
		} else
		if ( e.getActionCommand().indexOf("CONTA") != -1) {
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setVisible(true);
			if ( contaFrame.getConta() != null ) {
				this.txtConta.getModel().setSelectedItem(contaFrame.getConta());
			}
		}
	}
	
	private class SalvarSaque extends SwingWorker<String, String> {
		private SaqueFrame frame;
		private Passivo saque;
		private LoadingFrame loadingFrame;
		public SalvarSaque(SaqueFrame frame, Passivo saque) {
			this.frame = frame;
			this.saque = saque;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(saque.getValorTotal(), false) + " - " + saque.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			frame.scorecardBusiness.savePassivo(saque);
			return null;
		}
		protected void done() {
			this.loadingFrame.dispose();
			frame.dispose();
		}
	}
	
	protected void saveSaque() {
		if ( isValidValueOfFields() ) {
			Passivo saque = new Saque();
			if ( this.loadedPassivo != null ) {
				saque = this.loadedPassivo;
			}
			
			saque.setContaCorrente(this.getContaCorrente());
			saque.setDataMovimento(this.txtReferencia.getDate());
			saque.setConta((Conta)this.txtConta.getSelectedItem());
			saque.setHistorico(this.txtHistorico.getText());
			saque.getParcelas().removeAll(saque.getParcelas());
			
			Parcela parcela = new Parcela();
			parcela.setDataVencimento(new java.sql.Date(this.txtReferencia.getDate().getTime()));
			parcela.setValor(Util.parseCurrency(this.txtValor.getText()));
			parcela.setEfetivado(true);
			saque.addParcela(parcela);
			
			UtilGUI.coverBlinder(this);
			new SalvarSaque(this,saque).execute();
		}
	}
	
	public boolean isValidValueOfFields() {
		if ( this.txtReferencia.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Referência inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtReferencia.requestFocus();
			return false;
		}
		if ( this.txtConta.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha a Conta Contábil associada ao Saque !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtConta.requestFocus();
			return false;
		}
		if ( this.txtHistorico.getText() == null ) {
			JOptionPane.showMessageDialog(this,"Digite a descrição !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtHistorico.requestFocus();
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
			Conta conta = (Conta) this.txtConta.getSelectedItem();
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
		((JTextFieldDateEditor)this.txtReferencia.getDateEditor()).requestFocus();
	}
	
	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ContaCorrente corrente = ((ScorecardManager)Util.getBean("scorecardManager")).getContaCorrentePorId(64);
		SaqueFrame frame = new SaqueFrame(null,corrente);
		frame.setVisible(true);

	}

}
