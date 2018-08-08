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
import java.util.Date;
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

import org.apache.commons.lang3.StringUtils;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
public abstract class AbstractAtivoFrame extends AbstractDialog implements FocusListener {
	
	protected JLabel                      lblReferencia     = new JLabel("Mês / Ano:");
	protected JDateChooser                txtReferencia     = new JDateChooser("MM/yyyy","##/####",'_');
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
	protected Ativo                       loadedAtivo       = null;
	protected Date                        periodoDataIni    = null;

	public AbstractAtivoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Ativo ativo) {
		super(owner);
		this.periodoDataIni = periodoDataIni;
		this.title = MessageManager.getMessage(MessagesEnum.TituloFrameOrcamento, 
				new String[]{getTitulo()}, 
				new Object[]{contaCorrente,contaCorrente.getBanco()});
		this.contaCorrente   = contaCorrente;
		this.loadedAtivo     = ativo;
		
		if ( this.loadedAtivo != null ) {
			this.txtReferencia.setDate(this.loadedAtivo.getReferenciaAsDate());
			this.txtConta.setSelectedItem(this.loadedAtivo.getConta());
			this.txtHistorico.setText(this.loadedAtivo.getHistorico());
			this.txtValor.setText(Util.formatCurrency(this.loadedAtivo.getValor(),true));
			
			if ( this.loadedAtivo.isOrigemTransferencia() ) {
				this.txtValor.setEnabled(false);
				this.txtValor.setDisabledTextColor(Color.DARK_GRAY);
			}
		}
		this.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
	}
	public abstract String getTitulo();
	
	public AbstractAtivoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		this(owner,contaCorrente,periodoDataIni,null);
	}
	
	protected void createUI() {
		this.width = 475;
		this.height = 263;
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

		buildPanelAtivo(panOrcamento);
		buildPanelBtnAcoes(panBtnAcoes);

		panOrcamento.setBounds(10, 82, 452, 140);
		panBtnAcoes.setBounds(10, 10, 452, 65);
		
		this.txtConta.addFocusListener(this);
		this.txtConta.getEditor().getEditorComponent().addFocusListener(this);
		this.txtConta.getEditor().getEditorComponent().setName("txtConta");
		this.txtConta.setName("txtConta");
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");
		
		Util.setToolTip(this,btnOk, "Salvar Investimento");
		Util.setToolTip(this,btnCancela, "Cancelar "  + (this.loadedAtivo != null ? "Alteração" : "Inclusão"));
		Util.setToolTip(this,btnContas, "Pesquisar Contas");

		panMain.add(panOrcamento);
		panMain.add(panBtnAcoes);

		this.txtReferencia.setDate(this.periodoDataIni);	
		Component firstFocus = this.loadedAtivo != null ? this.txtValor : this.txtReferencia;
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
	
	private void buildPanelAtivo(JPanel panOrcamento) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 105;
		int SPACE_VERT = 30;
		
		lblReferencia.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblReferencia.getWidth();
		txtReferencia.setBounds(X, Y, 86, 20);
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
		List<Conta> list = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : list) {
			conta.toStringMode = 1;
			txtConta.addItem(conta); 
		}
		if (this.loadedAtivo == null)
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
		
		panOrcamento.add(lblReferencia);
		panOrcamento.add(txtReferencia);
		panOrcamento.add(lblDescricao);
		panOrcamento.add(txtHistorico);
		panOrcamento.add(lblConta);
		panOrcamento.add(txtConta);
		panOrcamento.add(btnContas);
		panOrcamento.add(txtValor);
		panOrcamento.add(lblValor);
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
			this.saveOrcamento();
		} else
		if ( e.getActionCommand().indexOf("CONTA") != -1) {
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setVisible(true);
			if ( contaFrame.getConta() != null ) {
				this.txtConta.getModel().setSelectedItem(contaFrame.getConta());
			}
		}
	}
	
	public abstract Ativo newAtivo();
	
	protected void saveOrcamento() {
		if ( isValidValueOfFields() ) {
			Ativo ativo = newAtivo();
			if ( this.loadedAtivo != null ) {
				ativo = this.loadedAtivo;
			}
			
			ativo.setContaCorrente(this.getContaCorrente());
			ativo.setReferencia(this.txtReferencia.getDate());
			ativo.setDataMovimento(this.txtReferencia.getDate());
			ativo.setConta((Conta)this.txtConta.getSelectedItem());
			ativo.setValor(Util.parseCurrency(this.txtValor.getText()));
			ativo.setHistorico(this.txtHistorico.getText());
			
			new SalvarAtivo(this,ativo).execute();
		}
	}
	
	private class SalvarAtivo extends SwingWorker<String, String> {
		private AbstractAtivoFrame frame;
		private Ativo ativo;
		private LoadingFrame loadingFrame;
		public SalvarAtivo(AbstractAtivoFrame frame, Ativo ativo) {
			this.frame = frame;
			UtilGUI.coverBlinder(frame);
			this.ativo = ativo;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(ativo.getValor(), false) + " - " + ativo.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			frame.scorecardBusiness.saveAtivo(ativo);
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
			JOptionPane.showMessageDialog(this,"Escolha a Conta Contábil associada ao Ativo !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtConta.requestFocus();
			return false;
		}
		if ( this.txtHistorico.getText() == null ) {
			JOptionPane.showMessageDialog(this,"Digite a descrição !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtHistorico.requestFocus();
			return false;
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
			if ( this.txtConta.getEditor().getItem() != null ) {
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
}
