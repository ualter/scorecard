package br.ujr.scorecard.gui.view.screen;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrCurrencyField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.scorecard.config.ScorecardConfigBootStrap;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
public class OrcamentoFrame extends AbstractDialog implements FocusListener {
	
	protected JLabel                      lblReferencia     = new JLabel("Mês / Ano:");
	protected JDateChooser                txtReferencia     = new JDateChooser("MM/yyyy","##/####",'_');
	protected JLabel                      lblConta          = new JLabel("Conta Associada:");
	protected JLabel                      lblDescricao      = new JLabel("Descrição:");
	protected JLabel                      lblValor          = new JLabel("Valor Previsto:");
	protected UjrComboBox                 txtConta          = new UjrComboBox();
	protected UjrTextField                txtDescricao      = new UjrTextField();
	protected UjrCurrencyField            txtValor          = new UjrCurrencyField();
	protected JButton                     btnOk             = new JButton();
	protected JButton                     btnCancela        = new JButton();
	protected JButton                     btnContas         = new JButton();
	protected boolean                     isUpdate          = false;
	protected ContaCorrente               contaCorrente     = null;
	protected Orcamento                   loadedOrcamento   = null;
	
	private static Logger logger = Logger.getLogger(OrcamentoFrame.class);

	public OrcamentoFrame(JFrame owner, ContaCorrente contaCorrente, Orcamento orcamento) {
		super(owner);
		this.title = MessageManager.getMessage(MessagesEnum.TituloFrameOrcamento, 
				new String[]{"ORÇAMENTO"}, 
				new Object[]{contaCorrente,contaCorrente.getBanco()});
		this.contaCorrente   = contaCorrente;
		this.loadedOrcamento = orcamento;
		
		if ( this.loadedOrcamento != null ) {
			this.txtReferencia.setDate(this.loadedOrcamento.getReferenciaAsDate());
			this.txtConta.setSelectedItem(this.loadedOrcamento.getContaAssociada());
			this.txtDescricao.setText(this.loadedOrcamento.getDescricao());
			this.txtValor.setText(Util.formatCurrency(this.loadedOrcamento.getOrcado(),true));
			
			/*this.txtReferencia.setEnabled(false);
			this.txtConta.setEnabled(false);
			this.btnContas.setEnabled(false);*/
		}
		this.createUI();
	}
	public OrcamentoFrame(JFrame owner, ContaCorrente contaCorrente) {
		this(owner,contaCorrente,null);
	}
	
	protected void createUI() {
		this.width = 475;
		this.height = 263;
		super.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
		
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

		panOrcamento.setBounds(10, 82, 452, 140);
		panBtnAcoes.setBounds(10, 10, 452, 65);
		
		this.txtConta.getEditor().getEditorComponent().addFocusListener(this);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");
		
		
		Util.setToolTip(this,btnOk, "Salvar Orçamento");
		Util.setToolTip(this,btnCancela, "Cancelar "  + (this.loadedOrcamento != null ? "Alteração" : "Inclusão"));
		Util.setToolTip(this,btnContas, "Pesquisar Contas Contábeis");

		panMain.add(panOrcamento);
		panMain.add(panBtnAcoes);

		this.txtReferencia.setDate(Util.today());	
		Component firstFocus = this.loadedOrcamento != null ? this.txtValor : this.txtReferencia;
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
	
	private void buildPanelOrcamento(JPanel panOrcamento) {
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
		txtConta.setName("txtConta");
		List<Conta> list = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : list) {
			conta.toStringMode = 1;
			txtConta.addItem(conta); 
		}
		if (this.loadedOrcamento == null)
			txtConta.setSelectedItem(null);
		btnContas.setName("btnConta");
		X += txtConta.getWidth() + 2;
		btnContas.setBounds(X,Y,20,20);
		
		X  = 10;
		Y += SPACE_VERT;
		lblDescricao.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblDescricao.getWidth();
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
		
		panOrcamento.add(lblReferencia);
		panOrcamento.add(txtReferencia);
		panOrcamento.add(lblDescricao);
		panOrcamento.add(txtDescricao);
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
					return txtDescricao;
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
					return txtDescricao;
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
	
	protected void saveOrcamento() {
		if ( isValidValueOfFields() ) {
			Orcamento orcamento = new Orcamento();
			if ( this.loadedOrcamento != null ) {
				orcamento = this.loadedOrcamento;
			}
			
			Calendar cal = Calendar.getInstance();
			int today = cal.get(Calendar.DAY_OF_MONTH);
			cal.setTime(this.txtReferencia.getDate());
			cal.set(Calendar.DAY_OF_MONTH,today);
			
			orcamento.setContaCorrente(this.getContaCorrente());
			orcamento.setReferencia(cal.getTime());
			orcamento.setContaAssociada((Conta)this.txtConta.getSelectedItem());
			orcamento.setOrcado(Util.parseCurrency(this.txtValor.getText()));
			orcamento.setDescricao(this.txtDescricao.getText());
			
			UtilGUI.coverBlinder(this);
			SalvarOrcamento salvarOrcamento = new SalvarOrcamento(this,orcamento);
			UtilGUI.makeSwingWorkerWait(this, salvarOrcamento);
			
			try {
				if ( !resultSalvarOrcamento.booleanValue()  ) {
					this.getGlassPane().setVisible(false);
					JOptionPane.showMessageDialog(this,"Já existe Orçamento associado a \nConta: " + 
							orcamento.getContaAssociada().getNivel() + "-" + orcamento.getContaAssociada().getDescricao() + 
							"  no  Mês/Ano: " + orcamento.getMesAno() 
							,"Atenção",JOptionPane.ERROR_MESSAGE);
				} else {
					this.dispose();
				}
			} catch (Exception e) {
				logger.error(this.getClass(), e);
				throw new RuntimeException(e);
			}
		}
	}
	
	private Boolean resultSalvarOrcamento = new Boolean(false);
	private class SalvarOrcamento extends SwingWorker<Boolean, String> {
		private OrcamentoFrame frame;
		private Orcamento orcamento;
		private LoadingFrame loadingFrame;
		public SalvarOrcamento(OrcamentoFrame frame, Orcamento orcamento) {
			this.frame = frame;
			this.orcamento = orcamento;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(orcamento.getOrcado(), false) + " - " + orcamento.getDescricao());
			this.loadingFrame.showLoadinFrame();
		}
		protected Boolean doInBackground() throws Exception {
			return new Boolean(frame.scorecardBusiness.saveOrcamento(orcamento));
		}
		protected void done() {
			this.loadingFrame.dispose();
			try {
				resultSalvarOrcamento = get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean isValidValueOfFields() {
		if ( this.txtReferencia.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Referência inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtReferencia.requestFocus();
			return false;
		}
		if ( this.txtConta.getSelectedItem() == null ) {
			JOptionPane.showMessageDialog(this,"Escolha a Conta Contábil associada ao Orçamento !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtConta.requestFocus();
			return false;
		}
		if ( this.txtDescricao.getText() == null ) {
			JOptionPane.showMessageDialog(this,"Digite a descrição !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDescricao.requestFocus();
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
		if ( component.getName().equals("txtConta") ) {
			String desc = "";
			Conta conta = (Conta) this.txtConta.getEditor().getItem();
			if ( conta != null ) {
				desc = conta.getDescricao();
			}
			if ( StringUtils.isBlank(txtDescricao.getText()) || !txtDescricao.getText().equals(desc)) {
				txtDescricao.setText(desc);
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
	
	public static void main(String[] args) {
		OrcamentoFrame o = new OrcamentoFrame(null,((ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager")).getContaCorrentePorId(41));
		o.setVisible(true);
	}

}
