
package br.ujr.scorecard.analisador.extrato.contacorrente.deutsche;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.components.gui.tabela.DefaultOrdenadorTabela;
import br.ujr.components.gui.tabela.SortButtonRenderer;
import br.ujr.scorecard.analisador.extrato.contacorrente.deutsche.AnalisadorExtratoCCDeutsche.LinhaExtratoContaCorrenteDeutsche;
import br.ujr.scorecard.gui.view.screen.ContaFrame;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.bankpanel.HeaderListener;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

//public class AnalisadorExtratoCCDeutscheGUI extends JDialog implements MouseListener, ActionListener, FocusListener {
public class AnalisadorExtratoCCDeutscheGUI extends AbstractDialog implements MouseListener, ActionListener, FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1005761561571805210L;
	private int							width		= 1235;
	private int							height		= 602;
	private JPanel						panMain		= null;

	private JTable						tabNaoEncontrados;
	private DefaultTableModel			modelTabNaoEncontrados;

	private static Logger				logger		= Logger.getLogger(AnalisadorExtratoCCDeutscheGUI.class);

	private JDateChooser				txtDtRef	= new JDateChooser("MM/yyyy", "##/####", '_');
	private ScorecardManager	scorecardManager;

	public AnalisadorExtratoCCDeutscheGUI() {
		this(null);
	}

	public AnalisadorExtratoCCDeutscheGUI(JFrame owner) {
		super(owner);

		this.setModal(true);
		this.scorecardManager = (ScorecardManager)Util.getBean("scorecardManager");

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(((screenSize.width - width) / 2), ((screenSize.height - height) / 2) - 15, width, height);
		this.setPreferredSize(new Dimension(width, height));

		this.panMain = new JPanel();
		this.panMain.setLayout(null);

		this.panMain.setBounds(0, 0, width, 768);
		this.getContentPane().setLayout(null);
		this.getContentPane().add(this.panMain);

		this.setTitle("Scoredcard - Analisador Extrato Conta Corrente: D E U T S C H E");
		this.setName("AnalisadorExtratoContaCorrenteBancoDeutsche");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// Table Nao Encontrados na Base de Dados
		this.modelTabNaoEncontrados = new DefaultModelLinhaExtrato(null,
				new Object[] { "Data Operação", "Data Valor", "Histórico", "Valor", "Saldo", "Tipo", "C.Contabil", "Object" });

		tabNaoEncontrados = new JTable(this.modelTabNaoEncontrados) {

			private static final long serialVersionUID = 2055391675568864661L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return true;
			}
		};
		tabNaoEncontrados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabNaoEncontrados.getTableHeader().setReorderingAllowed(false);
		tabNaoEncontrados.getTableHeader().setResizingAllowed(false);
		tabNaoEncontrados.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabNaoEncontrados.setPreferredScrollableViewportSize(new Dimension(400, 80));
		tabNaoEncontrados.setFont(new Font("Courier New", Font.PLAIN, 12));
		tabNaoEncontrados.addMouseListener(this);
		this.layOutTableNaoEncontrados();
		JScrollPane jScrollPaneTabNaoEncontrados = new JScrollPane(tabNaoEncontrados);
		jScrollPaneTabNaoEncontrados.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPaneTabNaoEncontrados.setBounds(10, 50, 1209, 400);
		this.tabNaoEncontrados.setEnabled(false);

		JLabel lblCodigo = new JLabel("Mês/Ano:");
		lblCodigo.setFont(new Font("Verdana", Font.BOLD, 10));
		lblCodigo.setBounds(10, 18, 80, 20);
		this.txtDtRef.setBounds(70, 18, 75, 20);
		this.txtDtRef.setEnabled(true);
		this.txtDtRef.setFocusable(true);
		((JTextFieldDateEditor) this.txtDtRef.getDateEditor()).addFocusListener(this);
		((JTextFieldDateEditor) this.txtDtRef.getDateEditor()).setName("MES_ANO");

		JLabel lblLctoNaoEncontrados = new JLabel("Lançamentos  D E U T S C H E  não encontrados na Base de Dados", SwingConstants.LEFT);
		lblLctoNaoEncontrados.setFont(new Font("Arial", Font.BOLD, 13));
		lblLctoNaoEncontrados.setHorizontalAlignment(SwingConstants.CENTER);
		lblLctoNaoEncontrados.setOpaque(true);
		lblLctoNaoEncontrados.setBackground(Color.BLUE);
		lblLctoNaoEncontrados.setForeground(Color.WHITE);
		lblLctoNaoEncontrados.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblLctoNaoEncontrados.setBounds(150, 10, 1068, 35);

		btnTranferir = new JButton(" Transferir");
		btnTranferir.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		Util.setToolTip(this, btnTranferir, "Transfere o lançamento para a base de dados");
		btnTranferir.setBounds(230, 25, 170, 50);
		btnTranferir.setActionCommand("TRANSFERIR");
		btnTranferir.addActionListener(this);

		btnRemover = new JButton(" Remover");
		btnRemover.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		Util.setToolTip(this, btnRemover, "Ignora linha de lançamento do extrado");
		btnRemover.setBounds(btnTranferir.getX() + btnTranferir.getWidth() + 10, btnTranferir.getY(), btnTranferir.getWidth(), btnTranferir.getHeight());
		btnRemover.setActionCommand("REMOVER");
		btnRemover.addActionListener(this);

		btnContaContabil = new JButton(" Conta Contábil ");
		btnContaContabil.setIcon(new ImageIcon(Util.loadImage(this, "search2.png")));
		btnContaContabil.setBounds(btnRemover.getX() + btnRemover.getWidth() + 10, btnRemover.getY(), btnRemover.getWidth(), btnRemover.getHeight());
		btnContaContabil.setActionCommand("CONTA_CONTABIL");
		btnContaContabil.addActionListener(this);
		
		btnSair = new JButton(" Sair");
		btnSair.setIcon(new ImageIcon(Util.loadImage(this, "exit.png")));
		btnSair.setBounds(btnContaContabil.getX() + btnContaContabil.getWidth() + 10, btnContaContabil.getY(), btnContaContabil.getWidth(), btnContaContabil.getHeight());
		btnSair.setActionCommand("SAIR");
		btnSair.addActionListener(this);

		this.btnRemover.setEnabled(false);
		this.btnTranferir.setEnabled(false);
		this.btnContaContabil.setEnabled(false);
		JPanel panelBtn = new JPanel();
		panelBtn.setLayout(null);
		panelBtn.setBorder(BorderFactory.createEtchedBorder(1));
		panelBtn.add(btnTranferir);
		panelBtn.add(btnRemover);
		panelBtn.add(btnContaContabil);
		panelBtn.add(btnSair);
		panelBtn.setBounds(10, 460, 1209, 100);

		this.panMain.add(lblLctoNaoEncontrados);
		this.panMain.add(jScrollPaneTabNaoEncontrados);
		this.panMain.add(lblCodigo);
		this.panMain.add(txtDtRef);
		this.panMain.add(panelBtn);

		((JTextFieldDateEditor) this.txtDtRef.getDateEditor()).requestFocus();
		this.txtDtRef.requestFocus();
		this.txtDtRef.requestFocusInWindow();
		this.txtDtRef.setDate(AnalisadorExtratoCCDeutsche.foundInThePath());

		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		tabNaoEncontrados.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "SELECT_CONTA_CONTABIL_CELL");
		tabNaoEncontrados.getActionMap().put("SELECT_CONTA_CONTABIL_CELL", new EnterAction());
	}

	private class EnterAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			tabNaoEncontrados.changeSelection(tabNaoEncontrados.getSelectedRow(), COLUMN_CONTA_CONTABIL, false, false);
			/*
			 * Robot robot; try { robot = new Robot();
			 * robot.keyPress(KeyEvent.VK_F12); } catch (AWTException e1) {
			 * e1.printStackTrace(); }
			 */

		}
	}

	/**
	 * 
	 */
	protected void calculateSizePosition() {
		int deslocar = 10;
		int stWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int stHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		if (this.width > stWidth)
			this.width = stWidth;
		if (this.height > stHeight)
			this.height = stHeight;
		int x = ((((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()) - this.width) / 2) + deslocar;
		int y = ((((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()) - this.height) / 2) + deslocar;
		this.setResizable(false);
		this.setSize(width, height);
		this.setLocation(x, y);
	}

	private void sair() {
		this.dispose();
	}

	/**
	 * Conta Corrente do Deutsche
	 * 
	 * @return
	 */
	private ContaCorrente getContaCorrente() {
		ContaCorrente cc = this.scorecardManager.getContaCorrentePorId(ScorecardPropertyKeys.IdCCDeutsche);
		return cc;
	}

	/**
	 * CC Banco do Brasil Alvo transferência
	 * 
	 * @return
	 */
	private ContaCorrente getContaCorrenteAlvoTransferencia() {
		ContaCorrente cc = this.scorecardManager.getContaCorrentePorId(64);
		return cc;
	}

	private Conta getContaContabilDeposito() {
		return this.scorecardManager.getContaPorId(854);
	}

	private Conta getContaContabilSalario() {
		return this.scorecardManager.getContaPorId(853);
	}

	private void carregarTabNaoEncontrados(long ref) {
		AnalisadorExtratoCCDeutsche analisador = new AnalisadorExtratoCCDeutsche(ref);
		String status = analisador.analisarExtrato();
		if (status == null) {
			// this.carregarSaldos(analisador.getSaldo().getSaldoAsBigDecimal(),
			// analisador.getSaldoAnterior().getSaldoAsBigDecimal());
			List<AnalisadorExtratoCCDeutsche.LinhaExtratoContaCorrenteDeutsche> listaNaoEncontrados = analisador.getLancamentosNaoExistentesBaseDados();
			for (LinhaExtratoContaCorrenteDeutsche linha : listaNaoEncontrados) {
				Object row[] = new Object[] { linha.getDataOperacao(), linha.getDataValor(), linha.getHistorico(), linha.getValor(), linha.getSaldo(),
						(linha.getTipo() != null ? linha.getTipo() : ""), (linha.getContaContabil() != null ? linha.getContaContabil() : ""), linha };
				this.modelTabNaoEncontrados.addRow(row);
			}
			this.btnRemover.setEnabled(true);
			this.btnContaContabil.setEnabled(true);
		} else {
			JOptionPane.showMessageDialog(this, "Problemas na leitura do Clipboard:\n\n" + status, "Atenção", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static final int	COLUMN_DATA_OPERACAO	= 0;
	private static final int	COLUMN_DATA_VALOR		= 1;
	private static final int	COLUMN_HISTORICO		= 2;
	private static final int	COLUMN_VALOR			= 3;
	private static final int	COLUMN_SALDO			= 4;
	private static final int	COLUMN_COMBO_IDENT		= 5;
	private static final int	COLUMN_CONTA_CONTABIL	= 6;
	private static final int	COLUMN_OBJECT			= 7;
	private String[]			valoresCombos			= new String[] { "", "Visa Electron", "Saque", "Cheque", "Débito", "Depósito", "Transferência", "Investimento",
			"Estipêndios" };
	private JButton				btnTranferir;
	private JButton				btnRemover;
	private JButton				btnSair;
	private JButton				btnContaContabil;
	private JLabel				lblSaldoAnterior;
	private JLabel				lblSaldo;
	private BigDecimal			bdSaldoExtrato;
	private BigDecimal			bdSaldoAnteriorExtrato;

	private void layOutTableNaoEncontrados() {
		tabNaoEncontrados.removeColumn(tabNaoEncontrados.getColumnModel().getColumn(COLUMN_OBJECT));

		TableColumn dataOperacaoColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_DATA_OPERACAO);
		TableColumn dataValorColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_DATA_VALOR);
		TableColumn historicoColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_HISTORICO);
		TableColumn valorColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_VALOR);
		TableColumn saldoColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_SALDO);
		TableColumn contaColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_CONTA_CONTABIL);
		TableColumn identColumn = tabNaoEncontrados.getColumnModel().getColumn(COLUMN_COMBO_IDENT);

		DefaultTableCellRenderer dataOperacaoRenderer = new AnalisadorExtratoTableCellRenderer();
		DefaultTableCellRenderer dataValorRenderer = new AnalisadorExtratoTableCellRenderer();
		DefaultTableCellRenderer historicoRenderer = new AnalisadorExtratoTableCellRenderer();
		DefaultTableCellRenderer valorRenderer = new AnalisadorExtratoTableCellRenderer();
		DefaultTableCellRenderer saldoRenderer = new AnalisadorExtratoTableCellRenderer();
		DefaultTableCellRenderer contaRenderer = new AnalisadorExtratoTableCellRenderer();
		DefaultTableCellRenderer identificacaoRenderer = new AnalisadorExtratoTableCellRenderer();

		JComboBox j = new JComboBox(valoresCombos);
		j.setFont(new Font("Courier New", Font.PLAIN, 11));
		identColumn.setCellEditor(new IdentificacaoTableCellEditor(j));

		UjrComboBox jContas = new UjrComboBox();
		jContas.getEditor().getEditorComponent().addKeyListener(new KeyListenerComboContaContabil(this, jContas));
		jContas.setName("CONTAS");
		jContas.setFont(new Font("Courier New", Font.PLAIN, 11));
		for (Conta c : this.scorecardManager.listarContas(ContaOrdenador.Descricao)) {
			c.toStringMode = 1;
			jContas.addItem(c);
		}
		contaColumn.setCellEditor(new ContaTableCellEditor(jContas));

		SortButtonRenderer renderer = new SortButtonRenderer();
		for (int i = 0; i < tabNaoEncontrados.getColumnModel().getColumnCount(); i++) {
			tabNaoEncontrados.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tabNaoEncontrados.getTableHeader();
		HeaderListener headerListener = new HeaderListener(header, renderer, (DefaultModelTabela) tabNaoEncontrados.getModel());
		DefaultOrdenadorTabelaExtrato defaultOrdenadorTabelaExtrato = new DefaultOrdenadorTabelaExtrato((DefaultModelTabela) tabNaoEncontrados.getModel());
		headerListener.setOrdenador(defaultOrdenadorTabelaExtrato);
		header.addMouseListener(headerListener);

		dataOperacaoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		saldoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		dataValorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		historicoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

		dataOperacaoColumn.setCellRenderer(dataOperacaoRenderer);
		dataValorColumn.setCellRenderer(dataValorRenderer);
		historicoColumn.setCellRenderer(historicoRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		saldoColumn.setCellRenderer(saldoRenderer);
		identColumn.setCellRenderer(identificacaoRenderer);
		contaColumn.setCellRenderer(contaRenderer);

		dataOperacaoColumn.setPreferredWidth(85);
		dataValorColumn.setPreferredWidth(85);
		historicoColumn.setPreferredWidth(383);
		valorColumn.setPreferredWidth(120);
		saldoColumn.setPreferredWidth(120);
		identColumn.setPreferredWidth(120);
		contaColumn.setPreferredWidth(280);
	}

	private class DefaultModelLinhaExtrato extends DefaultModelTabela {

		private static final long serialVersionUID = 5949818842699067816L;

		public DefaultModelLinhaExtrato(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}

	}

	private class DefaultOrdenadorTabelaExtrato extends DefaultOrdenadorTabela {

		public DefaultOrdenadorTabelaExtrato(DefaultModelTabela model) {
			super(model);
		}

		@Override
		public int compare(int column, int row1, int row2) {
			Object o1 = this.model.getValueAt(row1, column);
			Object o2 = this.model.getValueAt(row2, column);
			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 == null) {
				return -1;
			} else if (o2 == null) {
				return 1;
			} else {
				if (column == COLUMN_VALOR) {
					BigDecimal bd1 = Util.parseCurrency(o1.toString());
					BigDecimal bd2 = Util.parseCurrency(o2.toString());
					return bd1.compareTo(bd2);
				} else if (column == COLUMN_HISTORICO) {
					try {
						double d1 = Double.parseDouble(o1.toString());
						double d2 = Double.parseDouble(o2.toString());
						if (d1 > d2)
							return 1;
						if (d1 < d2)
							return -1;
						return 0;
					} catch (NumberFormatException e) {
						return 0;
					}
				} else {
					Class type = this.model.getColumnClass(column);
					if (type == String.class) {
						return ((String) o1).compareTo((String) o2);
					} else if (type == Boolean.class) {
						return compare((Boolean) o1, (Boolean) o2);
					} else {
						return ((String) o1).compareTo((String) o2);
					}
				}
			}
		}

	}

	private class AnalisadorExtratoTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2418901921193844216L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			LinhaExtratoContaCorrenteDeutsche linha = (LinhaExtratoContaCorrenteDeutsche) table.getModel().getValueAt(row, COLUMN_OBJECT);

			if (isSelected) {
				this.setFont(new Font("Courier New", Font.BOLD, 11));
				this.setForeground(table.getSelectionForeground());
				this.setBackground(table.getSelectionBackground());
			} else {
				this.setFont(new Font("Courier New", Font.PLAIN, 11));
				if (linha.isPassivo()) {
					this.setForeground(Color.RED);
				} else {
					this.setForeground(Color.BLUE);
				}
				this.setBackground(table.getBackground());
			}

			this.setText(String.valueOf(value));
			this.setBorder(table.getBorder());

			boolean selectedTemp = false;
			if (table.getModel().getColumnCount() == 7 && table.getModel().getValueAt(row, 6) instanceof Boolean) {
				selectedTemp = ((Boolean) table.getModel().getValueAt(row, 6)).booleanValue();
			}

			if (column == COLUMN_DATA_VALOR) {
				this.setValue(" " + value);
			} else if (column == COLUMN_DATA_OPERACAO) {
				this.setValue(" " + value);
			} else if (column == COLUMN_HISTORICO) {
				this.setValue(" " + value);
			} else if (column == COLUMN_VALOR) {
				this.layOutCellMoney(value, linha);
			} else if (column == COLUMN_SALDO) {
				this.layOutCellMoney(value, linha);
			} else if (column == COLUMN_COMBO_IDENT) {
				this.setValue(" " + value);
				this.setFont(new Font("Courier New", Font.BOLD, 11));
			}

			if (selectedTemp) {
				if (isSelected) {
					this.setBackground(Color.GREEN);
				} else {
					this.setBackground(Color.YELLOW);
				}
			}

			return this;
		}

		private void layOutCellMoney(Object value, LinhaExtratoContaCorrenteDeutsche linha) {
			this.layOutCellMoney(value, null, linha);
		}

		private void layOutCellMoney(Object value, Color color, LinhaExtratoContaCorrenteDeutsche linha) {
			String valor = String.valueOf(value);
			valor = StringUtils.leftPad(valor, 9);
			valor = " R$ " + Util.formatCurrency(linha.getValorAsBigDecimal()) + " ";
			this.setValue(valor);
			if (color != null)
				this.setForeground(color);
		}

	}

	private class IdentificacaoTableCellEditor extends DefaultCellEditor implements ItemListener, FocusListener {

		private static final long serialVersionUID = -238271661268695313L;

		public IdentificacaoTableCellEditor(JComboBox j) {
			super(j);
			j.addItemListener(this);
			j.addFocusListener(this);
		}

		public void itemStateChanged(ItemEvent e) {
			int col = tabNaoEncontrados.getSelectedColumn();
			int row = tabNaoEncontrados.getSelectedRow();

			if (row > -1) {
				JComboBox j = (JComboBox) e.getSource();
				LinhaExtratoContaCorrenteDeutsche linha = (LinhaExtratoContaCorrenteDeutsche) modelTabNaoEncontrados.getValueAt(row, COLUMN_OBJECT);
				String vlr = j.getSelectedItem().toString();

				// "Visa Electron","Saque","Cheque","Débito","Depósito",
				if (StringUtils.isNotBlank(vlr)) {
					if (linha.isPassivo()) {
						if (StringUtils.equalsIgnoreCase("Depósito", vlr) || StringUtils.equalsIgnoreCase("Estipêndios", vlr)) {
							JOptionPane.showMessageDialog(null, "Este registro é um lançamento negativo, não pode ser ativo!", "Atenção", JOptionPane.ERROR_MESSAGE);
							modelTabNaoEncontrados.setValueAt("", row, COLUMN_COMBO_IDENT);
							j.setSelectedItem("");
						} else {
							checkBotoes(row);
						}
					} else {
						if (StringUtils.equalsIgnoreCase("Visa Electron", vlr) || StringUtils.equalsIgnoreCase("Saque", vlr) || StringUtils.equalsIgnoreCase("Cheque", vlr)
								|| StringUtils.equalsIgnoreCase("Débito", vlr)) {
							JOptionPane.showMessageDialog(null, "Este registro é um lançamento positivo, não pode ser passivo!", "Atenção", JOptionPane.ERROR_MESSAGE);
							modelTabNaoEncontrados.setValueAt("", row, COLUMN_COMBO_IDENT);
							j.setSelectedItem("");
						} else {
							checkBotoes(row);
						}
					}
				} else {
					checkBotoes(row);
				}
			}
		}

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
			int row = tabNaoEncontrados.getSelectedRow();
			checkBotoes(row);
		}
	}

	private void checkBotoes(int row) {
		if (row > -1) {
			String ident = (String) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_COMBO_IDENT);
			boolean state = false;
			if (StringUtils.isNotEmpty(ident)) {
				if (!(this.modelTabNaoEncontrados.getValueAt(row, COLUMN_CONTA_CONTABIL) instanceof String)) {
					Conta conta = (Conta) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_CONTA_CONTABIL);
					this.modelTabNaoEncontrados.setValueAt(conta, row, COLUMN_CONTA_CONTABIL);
					state = true;
				}
			}
			btnTranferir.setEnabled(state);
		}
	}

	private class ContaTableCellEditor extends DefaultCellEditor implements ItemListener, FocusListener {

		private static final long serialVersionUID = -238271661268695313L;

		public ContaTableCellEditor(JComboBox j) {
			super(j);
			j.addItemListener(this);
			j.getEditor().getEditorComponent().addFocusListener(this);
		}

		public void itemStateChanged(ItemEvent e) {
			int row = tabNaoEncontrados.getSelectedRow();
			if (row > -1)
				checkBotoes(row);
		}

		public void focusGained(FocusEvent arg0) {
		}

		public void focusLost(FocusEvent arg0) {
			int row = tabNaoEncontrados.getSelectedRow();
			if (row > -1)
				checkBotoes(row);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// For Mac OS
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Scorecard");

		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		AnalisadorExtratoCCDeutscheGUI gui = new AnalisadorExtratoCCDeutscheGUI();
		gui.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JTable) {
			JTable tab = (JTable) e.getSource();
			int row = tab.getSelectedRow();
			int col = tab.getSelectedColumn();
			if (row > -1 && col > -1) {
				LinhaExtratoContaCorrenteDeutsche linha = (LinhaExtratoContaCorrenteDeutsche) tab.getModel().getValueAt(row, COLUMN_OBJECT);
				this.checkBotoes(row);
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();
			if (StringUtils.equals("SAIR", btn.getActionCommand())) {
				this.sair();
			}

			if ( e.getActionCommand().indexOf("CONTA_CONTABIL") != -1 ){
				if ( this.modelTabNaoEncontrados != null && this.modelTabNaoEncontrados.getRowCount() > 0) {
					int rowSelected = this.tabNaoEncontrados.getSelectedRow();
					if ( rowSelected > -1 ) {
						ContaTableCellEditor contaEditor = (ContaTableCellEditor)this.tabNaoEncontrados.getCellEditor(rowSelected, 6);
						contaEditor.cancelCellEditing();
						
						ContaFrame contaFrame = new ContaFrame(this);
						contaFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						contaFrame.setVisible(true);
						
						this.tabNaoEncontrados.setValueAt(contaFrame.getConta(), rowSelected, 6);
						this.checkBotoes(rowSelected);
					}
				}
			}

			int rows[] = this.tabNaoEncontrados.getSelectedRows();

			if (rows != null && rows.length > 0) {
				if (StringUtils.equals("TRANSFERIR", btn.getActionCommand())) {
					this.transferirLancamentoBaseDados(rows[0]);
				} else if (StringUtils.equals("REMOVER", btn.getActionCommand())) {
					int resp = JOptionPane.showConfirmDialog(this, "     Confirma ?    ", "  Remover Lançamento", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (resp == 0) {
						for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
							if (this.tabNaoEncontrados.isRowSelected(rows[0])) {
								this.modelTabNaoEncontrados.removeRow(rows[0]);
							}
						}
					}
				}
			} else {
				if (StringUtils.equals("TRANSFERIR", btn.getActionCommand()) || StringUtils.equals("REMOVER", btn.getActionCommand()))
					JOptionPane.showMessageDialog(this, "Nenhum lançamento selecionado.", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private class Salvar extends SwingWorker<String, String> {

		private AnalisadorExtratoCCDeutscheGUI		frame;
		private LinhaExtratoContaCorrenteDeutsche	linha;
		private LoadingFrame						loadingFrame;
		private Conta								conta;
		private ContaCorrente						cc;
		private ScorecardManager			        scorecardManager;
		private String								tipo;

		public Salvar(AnalisadorExtratoCCDeutscheGUI frame, LinhaExtratoContaCorrenteDeutsche linha, Conta conta, ContaCorrente cc, ScorecardManager scorecardManager,
				String tipo) {
			this.frame = frame;
			this.linha = linha;
			this.conta = conta;
			this.cc = cc;
			this.tipo = tipo;
			this.scorecardManager = scorecardManager;
			UtilGUI.coverBlinder(frame);
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando lançamento do extrato...");
			this.loadingFrame.showLoadinFrame();
		}

		protected String doInBackground() throws Exception {
			if ("Transferência".equalsIgnoreCase(tipo)) {
				Transferencia transferencia = AnalisadorExtratoCCDeutsche.converterLinhaTransferencia(linha, conta, cc, tipo);
				transferencia.setAtivoTransferido(getContaCorrenteAlvoTransferencia(), getContaContabilDeposito(), Deposito.class, "Depósito Deutsche");
				scorecardManager.saveTransferencia(transferencia);
			} else if (linha.isPassivo()) {
				Passivo passivo = AnalisadorExtratoCCDeutsche.converterLinhaPassivo(linha, conta, cc, tipo);
				scorecardManager.savePassivo(passivo);
			} else if (linha.isAtivo()) {
				Ativo ativo = AnalisadorExtratoCCDeutsche.converterLinhaAtivo(linha, conta, cc, tipo);
				scorecardManager.saveAtivo(ativo);
			}
			return null;
		}

		protected void done() {
			UtilGUI.uncoverBlinder(frame);
			if (this.frame.tabNaoEncontrados.getRowCount() > 0) {
				this.frame.tabNaoEncontrados.setRowSelectionInterval(0, 0);
			}
			this.loadingFrame.dispose();
		}
	}

	private void transferirLancamentoBaseDados(int row) {
		if (!(this.modelTabNaoEncontrados.getValueAt(row, COLUMN_CONTA_CONTABIL) instanceof String)) {
			String ident = (String) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_COMBO_IDENT);
			if (StringUtils.isNotEmpty(ident)) {
				LinhaExtratoContaCorrenteDeutsche linha = (LinhaExtratoContaCorrenteDeutsche) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_OBJECT);
				Conta conta = (Conta) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_CONTA_CONTABIL);
				String tipo = (String) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_COMBO_IDENT);
				String hist = (String) this.modelTabNaoEncontrados.getValueAt(row, COLUMN_HISTORICO);
				linha.setHistorico(hist);
				try {
					new Salvar(this, linha, conta, getContaCorrente(), this.scorecardManager, tipo).execute();
					this.modelTabNaoEncontrados.removeRow(row);
				} catch (Throwable t) {
					logger.fatal(t);
					JOptionPane.showMessageDialog(this, t.getMessage(), "Atencao!!!", JOptionPane.ERROR_MESSAGE);
					throw new RuntimeException(t);
				} finally {
				}
			} else {
				JOptionPane.showMessageDialog(this, "Escolha a identificação do lançamento.", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Escolha a Conta Contábil.", "Atenção!!!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void focusGained(FocusEvent e) {
		if (StringUtils.equalsIgnoreCase("MES_ANO", e.getComponent().getName())) {
			this.btnRemover.setEnabled(false);
			this.btnContaContabil.setEnabled(false);
			while (this.modelTabNaoEncontrados.getRowCount() > 0) {
				this.modelTabNaoEncontrados.removeRow(this.modelTabNaoEncontrados.getRowCount() - 1);
			}
			this.tabNaoEncontrados.setEnabled(false);
			this.tabNaoEncontrados.updateUI();
		}
	}

	public void focusLost(FocusEvent e) {
		if (StringUtils.equalsIgnoreCase("MES_ANO", e.getComponent().getName())) {
			JTextFieldDateEditor dt = (JTextFieldDateEditor) e.getSource();
			if (dt.getDate() != null) {
				final long ref = Util.extrairReferencia(dt.getDate());
				this.carregarTabNaoEncontrados(ref);
				this.tabNaoEncontrados.setEnabled(true);
				if (this.tabNaoEncontrados.getRowCount() > 0) {
					this.tabNaoEncontrados.setRowSelectionInterval(0, 0);
				}
				this.tabNaoEncontrados.requestFocus();
			} else {
				while (this.modelTabNaoEncontrados.getRowCount() > 0) {
					this.modelTabNaoEncontrados.removeRow(this.modelTabNaoEncontrados.getRowCount() - 1);
				}
				this.tabNaoEncontrados.setEnabled(false);
				if (this.tabNaoEncontrados.getRowCount() > 0) {
					this.tabNaoEncontrados.setRowSelectionInterval(0, 0);
				}
				this.tabNaoEncontrados.updateUI();
				this.tabNaoEncontrados.requestFocus();
			}
		}

	}

	private final class KeyListenerComboContaContabil implements KeyListener {

		private JDialog				j;
		private final UjrComboBox	jContas;

		private KeyListenerComboContaContabil(JDialog j, UjrComboBox jContas) {
			this.jContas = jContas;
			this.j = j;
		}

		public void keyTyped(KeyEvent arg0) {
		}

		public void keyReleased(KeyEvent arg0) {
		}

		public void keyPressed(KeyEvent evt) {
			if (KeyEvent.VK_F1 == evt.getKeyCode()) {
				ContaFrame contaFrame = new ContaFrame(j);
				contaFrame.setVisible(true);
				if (contaFrame.getConta() != null) {
					jContas.getModel().setSelectedItem(contaFrame.getConta());
				} else {
					jContas.getModel().setSelectedItem(null);
				}
			}
		}
	}
}
