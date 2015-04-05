
package br.ujr.scorecard.analisador.fatura.cartao;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXPanel;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.gui.view.screen.ContaFrame;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.ScorecardGUI;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;

/**
 * Analise da Fatura do Cartao que esta no Clipboard (copia da pagina do IB), apresentacao na tela dos lancamentos
 * e oportunidade para lancar no programa o que estiver faltando
 * @author ualter.junior
 */
public class AnalisadorLancamentosCartaoGUI extends AbstractDialog implements FocusListener, MouseListener, ItemListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092445806277556773L;
	
	protected JLabel                      lblVecto = new JLabel("Vencimento:");
	protected JDateChooser                txtRefVecto = new JDateChooser("dd/MM/yyyy","##/####",'_');
	protected DefaultModelTabela                 tableModelFatura;
	protected JTable                      tableFatura;
	protected JButton                     btnOk             = new JButton();
	protected JButton                     btnCancela        = new JButton();
	protected JButton                     btnVerificar      = new JButton();
	protected JButton                     btnContas         = new JButton();
	protected JComboBox                   cmbBanco          = new JComboBox();
	protected JComboBox                   cmbCartao         = new JComboBox();
	protected JComboBox                   cmbOrigem         = new JComboBox();
	private JXPanel blinder;
	private ContaCorrente contaCorrente;
	

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public AnalisadorLancamentosCartaoGUI(JFrame owner) {
		super(owner);
		this.title = this.getTitulo();
		this.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnVerificar.setIcon(new ImageIcon(Util.loadImage(this, "button_ok.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "Search2.png")));
		
	}
	
	public String getTitulo() {
		return "Análise Lançamentos de Cartão Crédito (Em Memória - Clipboard) v3";
	}
	
	protected void createUI() {
		this.width = 900;
		this.height = 485;
		super.createUI();
		
		// Panels, painéis containers para os componentes
		//JPanel panContaCorrente = new JPanel();
		JPanel panOrigem        = new JPanel();
		JPanel panBtnAcoes      = new JPanel();
		// Setting Layouts, configurando o Layout criado anteriormente para os
		// painéis
		panOrigem.setLayout(null);
		panBtnAcoes.setLayout(null);
		//panContaCorrente.setLayout(null);
		// Borders
		panOrigem.setBorder(BorderFactory.createEtchedBorder());
		panBtnAcoes.setBorder(BorderFactory.createEtchedBorder());
		//panContaCorrente.setBorder(BorderFactory.createEtchedBorder());

		buildPanelCartaoFatura(panOrigem);
		buildPanelBtnAcoes(panBtnAcoes);
		//buildPanelContaCorrente(panContaCorrente);

		panBtnAcoes.setBounds(10, 10, 877, 65);
		//panContaCorrente.setBounds(10, 82, 657, 55);
		//panOrigem.setBounds(10, 144, 657, 236);
		panOrigem.setBounds(10, 82, 877, 360);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnVerificar.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnVerificar.setActionCommand("VERIFICAR");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");
		
		Util.setToolTip(this,btnOk, "Confirmar Lançamento dos Registros Selecionados");
		Util.setToolTip(this,btnCancela, "Fechar Programa");
		Util.setToolTip(this,btnVerificar, "Verificar lançamentos em memória");
		Util.setToolTip(this,btnContas, "Pesquisar Conta Contábil");

		blinder = new JXPanel();
		blinder.setBackground(Color.BLACK);
		blinder.setBounds(0, 0, width, height);
		blinder.setAlpha(0.5f);
		blinder.setOpaque(true);
		blinder.setVisible(false);
		panMain.add(blinder);
		
		panMain.add(panOrigem);
		panMain.add(panBtnAcoes);
		//panMain.add(panContaCorrente);

		this.setFocusTraversalPolicy(new InternalFocusManager(txtRefVecto));
	}

	/**
	 * @param panBtnAcoes
	 */
	private void buildPanelBtnAcoes(JPanel panBtnAcoes) {
		int x = (this.width - 230) / 2;
		
		btnOk.setBounds(x, 9, 50, 45);
		btnVerificar.setBounds(x+50, 9, 50, 45);
		btnContas.setBounds(x+100, 9, 50, 45);
		btnCancela.setBounds(x+150, 9, 50, 45);
		

		btnOk.setName("btnOk");
		btnCancela.setName("btnCancela");
		btnContas.setName("btnContas");
		btnVerificar.setName("btnVerificar");
		
		panBtnAcoes.add(btnOk);
		panBtnAcoes.add(btnCancela);
		panBtnAcoes.add(btnContas);
		panBtnAcoes.add(btnVerificar);
	}
	
	protected int getIndexColumnEfetivado() {
		return UtilTableCells.DEFAULT_COLUMN_EFETIVADO - 1;
	}
	
	
	private void buildPanelCartaoFatura(JPanel panOrcamento) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 80;
		int SPACE_VERT = 30;
		
		JLabel lblBanco = new JLabel("Banco:");
		lblBanco.setBounds(X, Y, 43, 20);
		X += lblBanco.getWidth();
		this.cmbBanco.setBounds(X,Y,150,20);
		this.cmbBanco.addItemListener(this);
		this.cmbBanco.setName("BANCO");
		List<Banco> bancos = this.scorecardBusiness.listarBanco();
		for (Banco banco : bancos) {
			if ( banco.isAtivo() )
				this.cmbBanco.addItem(banco);
		}
		
		JLabel lblCartao = new JLabel("Cartão:");
		lblCartao.setBounds(X + 180, Y, 100, 20);
		this.cmbCartao.setBounds(X + 225,Y,150,20);
		this.cmbCartao.addItem(Cartao.Operadora.MASTERCARD);
		this.cmbCartao.addItem(Cartao.Operadora.VISA);

		lblVecto.setBounds(X+410, Y, WIDTH_LBLS, 20);
		X += lblVecto.getWidth();
		txtRefVecto.setBounds(X+410, Y, 123, 20);
		txtRefVecto.setFont(new Font("Courier New",Font.PLAIN,13));
		((JTextFieldDateEditor)txtRefVecto.getDateEditor()).addFocusListener(this);
		((JTextFieldDateEditor)txtRefVecto.getDateEditor()).setName("txtRefOrigem");
		
		JLabel lblOrigem = new JLabel("Origem:");
		lblOrigem.setBounds(X+570, Y, 100, 20);
		this.cmbOrigem.setBounds(X+620,Y,115,20);
		this.cmbOrigem.addItem("FATURA");
		this.cmbOrigem.addItem("SMS");
		
		this.cmbCartao.getEditor().getEditorComponent().setName("txtConta");
		this.cmbCartao.setName("cmbCartao");
		this.cmbCartao.setActionCommand("CARTAO_ALTERADO");
		this.cmbCartao.addFocusListener(this);
		this.cmbCartao.addActionListener(this);
		this.cmbCartao.addItemListener(this);
		
		X  = 10;
		Y += SPACE_VERT;	
		this.tableModelFatura = new DefaultModelTabela(null,new Object[]{"X","Movimento","Descrição","Valor","Conta Contábil"});
		this.tableFatura = new JTable(this.tableModelFatura){
			private static final long serialVersionUID = 4634987309460177002L;
			@Override
			public boolean isCellEditable(int row, int col) {
				if ( col != 1 )
					return true;
				return false;
			}
		};
		InputMap tableInputMap = tableFatura.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		tableInputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "none");
		
		this.tableFatura.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableFatura.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.tableFatura.setPreferredScrollableViewportSize(new Dimension(400, 300));
		this.tableFatura.setFont(new Font("Courier New",Font.PLAIN,12));
		this.tableFatura.addMouseListener(this);
		this.layOutTableFaturaCartao();
		JScrollPane jScrollPane = new JScrollPane(tableFatura);
		JLabel lblOrcamentos = new JLabel("Orçamentos",SwingConstants.CENTER);
		lblOrcamentos.setFont(new Font("Verdana",Font.BOLD,10));
		lblOrcamentos.setOpaque(true);
		lblOrcamentos.setBackground(Color.GRAY);
		lblOrcamentos.setForeground(Color.WHITE);
		lblOrcamentos.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jScrollPane.setBounds(X, Y, 857, 305);
		
		this.alterarDiaVecto();

		panOrcamento.add(lblOrcamentos);
		panOrcamento.add(jScrollPane);
		
		panOrcamento.add(lblBanco);
		panOrcamento.add(lblCartao);
		panOrcamento.add(lblVecto);
		panOrcamento.add(lblOrigem);
		panOrcamento.add(txtRefVecto);
		panOrcamento.add(cmbBanco);
		panOrcamento.add(cmbCartao);
		panOrcamento.add(cmbOrigem);
	}
	
	private void layOutTableFaturaCartao() {
		
		tableFatura.setRowHeight(18);
		
		ScorecardBusinessDelegate bd = ScorecardBusinessDelegate.getInstance();
		UjrComboBox cmbCC = new UjrComboBox();
		List<Conta> list = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : list) {
			conta.toStringMode = 1;
			cmbCC.addItem(conta); 
		}
		
		tableFatura.setPreferredScrollableViewportSize(new Dimension(350, 80));
		TableColumn selecionarColumn                = tableFatura.getColumnModel().getColumn(0);
		TableColumn dataColumn                      = tableFatura.getColumnModel().getColumn(1);
		TableColumn descricaoColumn                 = tableFatura.getColumnModel().getColumn(2);
		TableColumn valorColumn                     = tableFatura.getColumnModel().getColumn(3);
		TableColumn contaContabilColumn             = tableFatura.getColumnModel().getColumn(4);
		
		descricaoColumn.setCellEditor(new FaturaCartaoTextFieldCellEditor());
		valorColumn.setCellEditor( new FaturaCartaoMoneyCellEditor() );
		contaContabilColumn.setCellEditor( new FaturaCartaoContaContabilCellEditor(cmbCC) );
		
		TableCellRenderer        referenciaRenderer    = new FaturaCartaoCheckBoxCellRenderer();
		DefaultTableCellRenderer dataRenderer          = new FaturaCartaoTextFieldCellRenderer();
		DefaultTableCellRenderer descricaoRenderer     = new FaturaCartaoTextFieldCellRenderer();
		DefaultTableCellRenderer valorRenderer         = new FaturaCartaoTextFieldCellRenderer();
		DefaultTableCellRenderer contaContabilRenderer = new FaturaCartaoContaContabilCellRenderer();
		
		dataRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		descricaoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		contaContabilRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		
		selecionarColumn.setCellRenderer(referenciaRenderer);
		dataColumn.setCellRenderer(dataRenderer);
		descricaoColumn.setCellRenderer(descricaoRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		contaContabilColumn.setCellRenderer(contaContabilRenderer);
		
		selecionarColumn.setPreferredWidth(20);
		dataColumn.setPreferredWidth(90);
		descricaoColumn.setPreferredWidth(315);
		valorColumn.setPreferredWidth(100);
		contaContabilColumn.setPreferredWidth(300);
	}

	public class InternalFocusManager extends FocusTraversalPolicy {

		private Component firstFocus = null;
		
		public InternalFocusManager(Component firstFocus) {
			this.firstFocus = firstFocus;
		}
		
		public Component getComponentAfter(Container ctn, Component comp) {
			String componentName = comp.getName();
			if ( componentName == null ) {
				//System.out.println("Ops... getComponentAfter retornou nulo Component " + comp);
			} else {
				if (componentName.equals("txtContaCorrente")) {
					return (JFormattedTextField)txtRefVecto.getDateEditor();
				} else
				if (componentName.equals("txtRefDestino")) {
					return btnOk;
				} else	
				if (componentName.equals("btnOk")) {
					return btnCancela;
				}
			}
			return null;
		}

		public Component getComponentBefore(Container ctn, Component comp) {
			String componentName = comp.getName();
			if (componentName == null) {
				System.out.println("Ops... getComponentAfter retornou nulo Component " + comp);
			} else {
				if (componentName.equals("txtContaCorrente")) {
					return btnCancela;
				} else
				if (componentName.equals("txtRefDestino")) {
					return (JFormattedTextField)txtRefVecto.getDateEditor();
				} else	
				if (componentName.equals("btnOk")) {
					return (JFormattedTextField)txtRefVecto.getDateEditor();
				} else
				if (componentName.equals("btnCancela")) {
					return btnOk;
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
			Object[] options  = {"Gravar","Não"};
			int n = JOptionPane.showOptionDialog(this,
					"Confirma a gravação dos lançamentos selecionados?",
				    "Atenção!",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[1]);
			
			if ( n == 0 ) {
				this.salvarRegistroSelecionadosFatura();
			}
		} else
		if ( e.getActionCommand().indexOf("VERIFICAR") != -1 ){
			int banco = getSelectedBanco();
			if (banco == ScorecardPropertyKeys.IdSANTANDER) {
				this.setContaCorrente(this.scorecardBusiness.getInstance().getContaCorrentePorId(ScorecardPropertyKeys.IdCCSantander));
			} else
			if (banco == ScorecardPropertyKeys.IdITAU) {
				this.setContaCorrente(this.scorecardBusiness.getInstance().getContaCorrentePorId(ScorecardPropertyKeys.IdCCItau));
			}
			this.dispararCargaFatura();
		} else
		if ( e.getActionCommand().indexOf("CONTAS") != -1 ){
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			contaFrame.setVisible(true);
			if ( this.tableModelFatura != null && this.tableModelFatura.getRowCount() > 0) {
				int rowSelected = this.tableFatura.getSelectedRow();
				if ( rowSelected > -1 ) {
					this.tableModelFatura.setValueAt(contaFrame.getConta(), rowSelected, 4);
				}
			}
		}
	}

	private int getSelectedBanco() {
		return ((Banco)this.cmbBanco.getSelectedItem()).getId();
	}
	
	private class CarregarLancamentosFromClipboard extends SwingWorker<String, Object[]> {
		private LoadingFrame loadingFrame;
		private AnalisadorLancamentosCartaoGUI frame;
		private Cartao.Operadora operadora;
		private String origem;
		private int banco;
		private Date ref;
		
		public CarregarLancamentosFromClipboard(AnalisadorLancamentosCartaoGUI frame) {
			this.frame         = frame;
			this.operadora     = (Cartao.Operadora)frame.cmbCartao.getSelectedItem();
			this.origem	       = frame.cmbOrigem.getSelectedItem().toString();
			this.ref           = frame.txtRefVecto.getDate();
			this.banco         = frame.getSelectedBanco();
		}
		
		@Override
		protected String doInBackground() throws Exception {
			loadingFrame = new LoadingFrame();
			loadingFrame.showLoadinFrame();
			List<LinhaLancamento> linhasLancamentos = new ArrayList<LinhaLancamento>();
			try {
				if (this.banco == ScorecardPropertyKeys.IdSANTANDER) {
					if (this.origem.equalsIgnoreCase("SMS")) {
						AnalisadorSMSCartaoTransacaoSantander analisadorSMSCartaoSantander = new AnalisadorSMSCartaoTransacaoSantander(this.ref, this.operadora);
						linhasLancamentos = analisadorSMSCartaoSantander.getLista();
					} else if (this.origem.equalsIgnoreCase("Fatura")) {
						AnalisadorFaturaCartaoSantander analisadorFaturaCartaoSantander = new AnalisadorFaturaCartaoSantander(this.ref, this.operadora);
						linhasLancamentos = analisadorFaturaCartaoSantander.getLista();
					}
				} else
				if (this.banco == ScorecardPropertyKeys.IdITAU) {
					/**
					 * No caso do ITAU o Cartão VISA o Emissor é a empresa Porto Seguro
					 */
					if (this.origem.equalsIgnoreCase("Fatura")) {
						AnalisadorFaturaCartaoItau analisadorFaturaCartaoItau = new AnalisadorFaturaCartaoItau(this.ref, this.operadora);
						linhasLancamentos = analisadorFaturaCartaoItau.getLista();
					}
				}
			} catch (Throwable e) {
				loadingFrame.dispose();
				frame.btnCancela.requestFocus();
				JOptionPane.showMessageDialog(null, e.getMessage());
				throw new RuntimeException(e);
			}
			loadingFrame.setMaxProgress(linhasLancamentos.size());
			
			ScorecardBusinessDelegate bd = ScorecardBusinessDelegate.getInstance();
			Conta conta = bd.getContaPorId(756);
			
			Object row[];
			for(LinhaLancamento linhaLcto : linhasLancamentos) {
				row = new Object[]{
						!linhaLcto.isConfere(),
						linhaLcto.getData(),
						linhaLcto.getDescricao(),
						linhaLcto.getValor(),
						linhaLcto.getContaContabil() == null ? "" : linhaLcto.getContaContabil()
				};
				publish(new Object[]{row});
				Thread.sleep(1);
			}
			loadingFrame.dispose();
			return null;
		}

		@Override
		protected void done() {
		}

		@Override
		protected void process(List<Object[]> chunks) {
			for (Object[] linhasFaturas : chunks) {
				Object[] linhaFatura = (Object[])linhasFaturas[0];
				frame.tableModelFatura.addRow(linhaFatura);
				loadingFrame.incrementProgressValue();
				loadingFrame.setMessage("Carregando Orçamento " + linhaFatura[2]);
			}
		}
		
	}
	
	private class GravarRegistrosFaturaCartao extends SwingWorker<String, Cartao> {
		private LoadingFrame loadingFrame;
		private AnalisadorLancamentosCartaoGUI frame;
		private Date refVecto;
		private Cartao.Operadora operadora;
		private List<Cartao> listaRegistrosGravar = new ArrayList<Cartao>();
		
		public GravarRegistrosFaturaCartao(AnalisadorLancamentosCartaoGUI frame) {
			this.frame      = frame;
			this.operadora  = (Cartao.Operadora)frame.cmbCartao.getSelectedItem();
			this.refVecto   = frame.txtRefVecto.getDate();
			loadingFrame = new LoadingFrame();
			int total = 0;
			
			/*
			 * Buscando os lancamentos selecionados para serem gravados como Cartoes na Base de Dados
			 */
			for(int i = 0; i < frame.tableModelFatura.getRowCount(); i++ ) {
				if ( ((Boolean)frame.tableModelFatura.getValueAt(i, 0)).booleanValue() ) {
					
					Date       movimento     = Util.parseDate(frame.tableModelFatura.getValueAt(i, 1).toString());
					String     historico     = frame.tableModelFatura.getValueAt(i, 2).toString();
					BigDecimal valor         = Util.parseCurrency(frame.tableModelFatura.getValueAt(i, 3).toString());
					Conta      contaContabil = (Conta) frame.tableModelFatura.getValueAt(i, 4);
					
					Cartao cartao = new Cartao();
					cartao.setOperadora(this.operadora);
					cartao.setContaCorrente(frame.contaCorrente);
					cartao.setConta(contaContabil);
					cartao.setDataMovimento(movimento);
					cartao.setHistorico(historico);
					Parcela parcela = new Parcela();
					parcela.setDataVencimento(new java.sql.Date(this.refVecto.getTime()));
					parcela.setReferencia(this.refVecto);
					parcela.setValor(valor);
					cartao.addParcela(parcela);
					
					listaRegistrosGravar.add(cartao);
					
					total++;
				}
				
			}
			loadingFrame.setMaxProgress(total);
			
			this.frame.btnCancela.setEnabled(false);
			this.frame.btnOk.setEnabled(false);
			this.frame.txtRefVecto.setEnabled(false);
		}
		
		@Override
		protected String doInBackground() throws Exception {
			loadingFrame.showLoadinFrame();
			
			for (Cartao cartao : this.listaRegistrosGravar) {
				frame.scorecardBusiness.savePassivo(cartao);
				publish(cartao);
				Thread.sleep(100);
			}
			
			return null;
		}

		@Override
		protected void done() {
			this.loadingFrame.dispose();
			this.frame.btnCancela.setEnabled(true);
			this.frame.btnOk.setEnabled(true);
			this.frame.txtRefVecto.setEnabled(true);
			this.frame.resetTableLctosFatura();
			this.frame.btnCancela.requestFocus();
		}

		@Override
		protected void process(List<Cartao> chunks) {
			for (Cartao cartao : chunks) {
				loadingFrame.setMessage("Gravando Linha Fatura: " + cartao.getDataMovimento() + " - " + cartao.getHistorico() + " - " + Util.formatCurrency(cartao.getParcela().getValor()));
				loadingFrame.incrementProgressValue();
			}
		}
	}
	
	protected void salvarRegistroSelecionadosFatura() {
		if ( isValidValueOfFields() ) {
			GravarRegistrosFaturaCartao task = new GravarRegistrosFaturaCartao(this);
			task.execute();
			this.resetTableLctosFatura();
			this.loadFaturaFromClipboard();
		}
		
	}
	
	public boolean isValidValueOfFields() {
		
		for(int i = 0; i < this.tableModelFatura.getRowCount(); i++ ) {
			if ( ((Boolean)this.tableModelFatura.getValueAt(i, 0)).booleanValue() ) {
				boolean isContaSelected = this.tableModelFatura.getValueAt(i, 4) instanceof Conta;
				if ( !isContaSelected ) {
					JOptionPane.showMessageDialog(this,"Verifique linha " + (i+1) + ", deve ser selecionada a Conta Contábil\npara gravação da linha da fatura na Base de Dados!","Atenção",JOptionPane.ERROR_MESSAGE);
					this.btnCancela.requestFocus();
					return false;
				}
			}
		}
		
		if ( this.txtRefVecto.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Data de Vencimento Inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			((JFormattedTextField)this.txtRefVecto.getDateEditor()).requestFocus();
			return false;
		}
		
		return true;
	}

	public void focusGained(FocusEvent evt) {
	}
	public void focusLost(FocusEvent evt) {
	}

	private void dispararCargaFatura() {
		this.resetTableLctosFatura();
		this.loadFaturaFromClipboard();
	}

	private void resetTableLctosFatura() {
		if ( this.tableModelFatura.getRowCount() > 0 ) {
			while (this.tableModelFatura.getRowCount() > 0) {
				this.tableModelFatura.removeRow(0);
			}
		}
	}
	
	private void loadFaturaFromClipboard() {
		try {
			CarregarLancamentosFromClipboard task = new CarregarLancamentosFromClipboard(this);
			task.execute();
		} catch (Throwable e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
	}

	@Override
	public void windowOpened(WindowEvent evt) {
		//txtRefVecto.requestFocusInWindow();
	}
	
	public static void main(String[] args) {
		AnalisadorLancamentosCartaoGUI p = new AnalisadorLancamentosCartaoGUI(null);
		p.setVisible(true);
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
		if ( e.getSource() instanceof JTable) {
			this.tableFatura.repaint();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		if ( evt.getSource() instanceof JComboBox ) {
			alterarDiaVecto();
		}
	}

	private void alterarDiaVecto() {
		JComboBox cmb = this.cmbBanco;
		if ("BANCO".equalsIgnoreCase(cmb.getName())) {
			int diaVcto = 0;
			Banco banco = (Banco)cmb.getSelectedItem();
			if ( cmbCartao.getSelectedItem() != null && cmbCartao.getSelectedItem().toString().equals(Cartao.Operadora.MASTERCARD.name()) ) {
				diaVcto = banco.getDiaVencimentoMastercard();
			} else
			if ( cmbCartao.getSelectedItem() != null && cmbCartao.getSelectedItem().toString().equals(Cartao.Operadora.VISA.name()) ) {
				diaVcto = banco.getDiaVencimentoVisa();
			}
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH,diaVcto);
			this.txtRefVecto.setDate(cal.getTime());
		}
	}

}
