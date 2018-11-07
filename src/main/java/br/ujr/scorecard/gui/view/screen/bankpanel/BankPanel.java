package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.components.gui.tabela.OrcamentoOrdenadorTabela;
import br.ujr.components.gui.tabela.SortButtonRenderer;
import br.ujr.scorecard.analisador.fatura.cartao.AnalisadorSMSCartaoSaqueSantander;
import br.ujr.scorecard.analisador.fatura.cartao.LinhaLancamento;
import br.ujr.scorecard.gui.view.screen.DepositoFrame;
import br.ujr.scorecard.gui.view.screen.InvestimentoFrame;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.OrcamentoFrame;
import br.ujr.scorecard.gui.view.screen.PeriodoFrame;
import br.ujr.scorecard.gui.view.screen.SalarioFrame;
import br.ujr.scorecard.gui.view.screen.ScorecardGUI;
import br.ujr.scorecard.gui.view.screen.TransferenciaFrame;
import br.ujr.scorecard.gui.view.screen.cellrenderer.EfetivadoTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.MonetarioTableAtivoCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.MonetarioTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.OrcamentoTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.ParcelaTableAtivoCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.ParcelaTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.PredicateRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.RendererRules;
import br.ujr.scorecard.gui.view.screen.cellrenderer.ResumoTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.passivo.CartaoFrame;
import br.ujr.scorecard.gui.view.screen.passivo.ChequeFrame;
import br.ujr.scorecard.gui.view.screen.passivo.DebitoFrame;
import br.ujr.scorecard.gui.view.screen.passivo.SaqueFrame;
import br.ujr.scorecard.model.ResumoPeriodo;
import br.ujr.scorecard.model.ResumoPeriodoTotalCartao;
import br.ujr.scorecard.model.SaldoProcessadoEvent;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ScorecardManagerListener;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratadoSorter;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.extrato.LinhaExtratoCartao;
import br.ujr.scorecard.model.extrato.VerificarExtratoCartao;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.orcamento.PassivosForaOrcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.CartaoCatalogo;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author Ualter
 */
public class BankPanel extends JPanel implements ActionListener, MouseListener, KeyListener, ScorecardManagerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8777546439175016138L;

	protected ContaCorrente contaCorrente;
	protected List<CartaoContratado> cartoesContratados;
	protected JFrame owner;

	protected JButton btnInserirCheque = new JButton();
	protected JButton btnExcluirCheque = new JButton();
	protected JButton btnConfirmarCheque = new JButton();

	protected Map<CartaoContratado,JButton> btnInserirCartao          = new HashMap<CartaoContratado,JButton>();
	protected Map<CartaoContratado,JButton> btnExcluirCartao          = new HashMap<CartaoContratado,JButton>();
	protected Map<CartaoContratado,JButton> btnConfirmarCartao        = new HashMap<CartaoContratado,JButton>();
	protected Map<CartaoContratado,JButton> btnVerificarExtratoCartao = new HashMap<CartaoContratado,JButton>();

	protected JButton btnInserirDebito = new JButton();
	protected JButton btnExcluirDebito = new JButton();
	protected JButton btnConfirmarDebito = new JButton();

	protected JButton btnInserirOrcamento = new JButton();
	protected JButton btnExcluirOrcamento = new JButton();
	protected JToggleButton btnDesconsiderarOrcamento = new JToggleButton();

	protected JButton btnInserirDepositos = new JButton();
	protected JButton btnExcluirDepositos = new JButton();

	protected JButton btnInserirInvestimentos = new JButton();
	protected JButton btnExcluirInvestimentos = new JButton();

	protected JButton btnInserirSalario = new JButton();
	protected JButton btnExcluirSalario = new JButton();

	protected JButton btnInserirTransferencia = new JButton();
	protected JButton btnExcluirTransferencia = new JButton();

	protected JButton btnInserirSaque = new JButton();
	protected JButton btnExcluirSaque = new JButton();
	protected JButton btnLerSaqueMemoria = new JButton();
	protected JButton btnConfirmarSaque = new JButton();

	protected Date periodoDataInicial;
	protected Date periodoDataFinal;

	protected DefaultModelTabela tableModelCheque;
	protected JTable tableCheque;

	protected Map<CartaoContratado, DefaultModelTabela> tableModelCartoes;
	protected Map<CartaoContratado, JTable> tableCartoes = new HashMap<CartaoContratado,JTable>();

	protected DefaultModelTabela tableModelDebito;
	protected JTable tableDebito;

	protected DefaultModelTabela tableModelOrcamento;
	protected JTable tableOrcamento;
	
	protected DefaultModelTabela tableModelNaoOrcado;
	protected JTable tableNaoOrcado;

	protected DefaultModelTabela tableModelInvestimento;
	protected JTable tableInvestimento;

	protected DefaultModelTabela tableModelDeposito;
	protected JTable tableDeposito;

	protected DefaultModelTabela tableModelSalario;
	protected JTable tableSalario;

	protected DefaultModelTabela tableModelTransferencia;
	protected JTable tableTransferencia;

	protected DefaultModelTabela tableModelSaque;
	protected JTable tableSaque;

	protected JTabbedPane tabs = new JTabbedPane();

	protected JPanel panCheque;

	protected Map<CartaoContratado,JPanel> panCartoes;

	protected JPanel panDebito;
	protected JPanel panOrcamento;
	protected JPanel panAtivos;
	protected JPanel panTransferencia;
	protected JPanel panSaque;

	protected ScorecardManager scorecardManager = null;
	private static Logger logger = Logger.getLogger("br.ujr.scorecard");
	protected DefaultModelTabela tableModelResumo;
	private JLabel lblPeriodoResumo;
	private JPopupMenu popupMenuOrcamento;
	private JLabel lblPopupOrcamento;
	private int popUpOrcamentoX;
	private int popUpOrcamentoY;
	
	private Map<Integer,ImageIcon> tabIconsUnselected = new HashMap<Integer,ImageIcon>();
	private Map<Integer,ImageIcon> tabIconsSelected   = new HashMap<Integer,ImageIcon>();

	private float valorTotalOrcamento;
	private float valorTotalRealizado;
	private float valorTotalNaoOrcado;

	private JLabel lblOrcamentoTotal;
	private JLabel lblNaoOrcadoTotal;

	public BankPanel(JFrame owner, ContaCorrente contaCorrente, ScorecardManager scorecardManager) {
		this.owner             = owner;
		this.contaCorrente     = contaCorrente;
		this.scorecardManager  = scorecardManager;
		this.scorecardManager.addScorecardManagerListener(this);
		
		this.setUpDataInicialFinal();
		if (this.getContaCorrente().isCheque()) {
			this.loadCheques();
		}

		this.loadCartoes();
		this.loadSaque();
		this.loadDebito();
		this.loadOrcamento();
		this.loadInvestimento();
		this.loadDeposito();
		this.loadSalario();
		this.loadTransferencia();
		this.init();
	}

	private void init() {
		this.setLayout(null);

		List<CartaoContratado> cartoesContratados = getCartoesContratados();
		for (CartaoContratado cartaoContratado : cartoesContratados) {
			JButton btnInserir          = new JButton();
			JButton btnExcluir          = new JButton();
			JButton btnConfirmar        = new JButton();
			JButton btnVerificarExtrato = new JButton();
			
			this.btnInserirCartao.put(cartaoContratado, btnInserir);
			this.btnExcluirCartao.put(cartaoContratado, btnExcluir);
			this.btnConfirmarCartao.put(cartaoContratado, btnConfirmar);
			this.btnVerificarExtratoCartao.put(cartaoContratado, btnVerificarExtrato);
		}
		

		this.initBtnNames();
		
		cartoesContratados = getCartoesContratados();
		for (CartaoContratado cartaoContratado : cartoesContratados) {
			JButton btnInserir          = this.btnInserirCartao.get(cartaoContratado);
			JButton btnExcluir          = this.btnExcluirCartao.get(cartaoContratado);
			JButton btnConfirmar        = this.btnConfirmarCartao.get(cartaoContratado);
			JButton btnVerificarExtrato = this.btnVerificarExtratoCartao.get(cartaoContratado);
			
			Util.setToolTip(this, btnInserir, "Inserir lançamento");
			Util.setToolTip(this, btnExcluir, "Excluir lançamento<br/> Shortcut --> DEL");
			Util.setToolTip(this, btnConfirmar, "Confirmar/Cancelar");
			Util.setToolTip(this, btnVerificarExtrato, "Conferir com Extrato da Fatura do Cartão");
			
			this.setPanelButtonBounds(btnInserir);
			this.setPanelButtonBounds(btnExcluir);
			this.setPanelButtonBounds(btnConfirmar);
			
			btnInserir.setActionCommand("CARTAO_INSERIR_" + cartaoContratado.getId());
			btnExcluir.setActionCommand("CARTAO_EXCLUIR_" + cartaoContratado.getId());
			btnConfirmar.setActionCommand("CARTAO_CONFIRMAR_" + cartaoContratado.getId());
			btnVerificarExtrato.setActionCommand("CARTAO_EXTRATO_" + cartaoContratado.getId());
			btnInserir.addActionListener(this);
			btnExcluir.addActionListener(this);
			btnConfirmar.addActionListener(this);
			btnVerificarExtrato.addActionListener(this);
		}
		
		Util.setToolTip(this, btnInserirCheque, "Inserir lançamento");
		Util.setToolTip(this, btnExcluirCheque, "Excluir lançamento");
		Util.setToolTip(this, btnConfirmarCheque, "Confirmar/Cancelar");
		Util.setToolTip(this, btnInserirSaque, "Inserir lançamento");
		Util.setToolTip(this, btnExcluirSaque, "Excluir lançamento");
		Util.setToolTip(this, btnConfirmarSaque, "Confirmar/Cancelar");
		Util.setToolTip(this, btnLerSaqueMemoria, "Verificar lançamentos de Saques em memória");
		Util.setToolTip(this, btnInserirDebito, "Inserir lançamento");
		Util.setToolTip(this, btnExcluirDebito, "Excluir lançamento");
		Util.setToolTip(this, btnConfirmarDebito, "Confirmar/Cancelar");
		Util.setToolTip(this, btnDesconsiderarOrcamento,
				"Desconsiderar/Considerar do Saldo Previsto o valor restante, <br>o valor ainda não gasto no planejado em orçamento.");

		this.setPanelButtonBounds(btnInserirDebito);
		this.setPanelButtonBounds(btnExcluirDebito);
		this.setPanelButtonBounds(btnConfirmarDebito);
		this.setPanelButtonBounds(btnInserirCheque);
		this.setPanelButtonBounds(btnExcluirCheque);
		this.setPanelButtonBounds(btnInserirOrcamento);
		this.setPanelButtonBounds(btnExcluirOrcamento);
		this.setPanelButtonBounds(btnExcluirTransferencia);
		this.setPanelButtonBounds(btnInserirTransferencia);
		this.setPanelButtonBounds(btnInserirSaque);
		this.setPanelButtonBounds(btnExcluirSaque);
		this.setPanelButtonBounds(btnConfirmarSaque);
		this.setPanelButtonBounds(btnLerSaqueMemoria);

		this.btnInserirCheque.setActionCommand("INSERIR_CHEQUE");
		this.btnExcluirCheque.setActionCommand("EXCLUIR_CHEQUE");
		this.btnConfirmarCheque.setActionCommand("CONFIRMAR_CHEQUE");
		this.btnInserirCheque.addActionListener(this);
		this.btnExcluirCheque.addActionListener(this);
		this.btnConfirmarCheque.addActionListener(this);

		this.btnInserirDebito.setActionCommand("INSERIR_DEBITO");
		this.btnExcluirDebito.setActionCommand("EXCLUIR_DEBITO");
		this.btnConfirmarDebito.setActionCommand("CONFIRMAR_DEBITO");
		this.btnInserirDebito.addActionListener(this);
		this.btnExcluirDebito.addActionListener(this);
		this.btnConfirmarDebito.addActionListener(this);

		this.btnInserirOrcamento.setActionCommand("INSERIR_ORCAMENTO");
		this.btnExcluirOrcamento.setActionCommand("EXCLUIR_ORCAMENTO");
		this.btnDesconsiderarOrcamento.setActionCommand("DESCONSIDERAR_ORCAMENTO");
		this.btnInserirOrcamento.addActionListener(this);
		this.btnExcluirOrcamento.addActionListener(this);
		this.btnDesconsiderarOrcamento.addActionListener(this);

		this.btnInserirInvestimentos.setActionCommand("INSERIR_INVESTIMENTO");
		this.btnExcluirInvestimentos.setActionCommand("EXCLUIR_INVESTIMENTO");
		this.btnInserirInvestimentos.addActionListener(this);
		this.btnExcluirInvestimentos.addActionListener(this);

		this.btnInserirDepositos.setActionCommand("INSERIR_DEPOSITO");
		this.btnExcluirDepositos.setActionCommand("EXCLUIR_DEPOSITO");
		this.btnInserirDepositos.addActionListener(this);
		this.btnExcluirDepositos.addActionListener(this);

		this.btnInserirSalario.setActionCommand("INSERIR_SALARIO");
		this.btnExcluirSalario.setActionCommand("EXCLUIR_SALARIO");
		this.btnInserirSalario.addActionListener(this);
		this.btnExcluirSalario.addActionListener(this);

		this.btnInserirTransferencia.setActionCommand("INSERIR_TRANSFERENCIA");
		this.btnExcluirTransferencia.setActionCommand("EXCLUIR_TRANSFERENCIA");
		this.btnInserirTransferencia.addActionListener(this);
		this.btnExcluirTransferencia.addActionListener(this);

		this.btnInserirSaque.setActionCommand("INSERIR_SAQUE");
		this.btnExcluirSaque.setActionCommand("EXCLUIR_SAQUE");
		this.btnConfirmarSaque.setActionCommand("CONFIRMAR_SAQUE");
		this.btnLerSaqueMemoria.setActionCommand("LER_SAQUE");
		this.btnInserirSaque.addActionListener(this);
		this.btnExcluirSaque.addActionListener(this);
		this.btnConfirmarSaque.addActionListener(this);
		this.btnLerSaqueMemoria.addActionListener(this);

		this.btnInserirCheque.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirCheque.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));
		this.btnConfirmarCheque.setIcon(new ImageIcon(Util.loadImage(this, "confirm.png")));

		cartoesContratados = getCartoesContratados();
		for (CartaoContratado cartaoContratado : cartoesContratados) {
			this.btnInserirCartao.get(cartaoContratado).setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
			this.btnExcluirCartao.get(cartaoContratado).setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));
			this.btnConfirmarCartao.get(cartaoContratado).setIcon(new ImageIcon(Util.loadImage(this, "confirm.png")));
			this.btnVerificarExtratoCartao.get(cartaoContratado).setIcon(new ImageIcon(Util.loadImage(this, "conferir_extrato.png")));
		}
		
		this.btnInserirDebito.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirDebito.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));
		this.btnConfirmarDebito.setIcon(new ImageIcon(Util.loadImage(this, "confirm.png")));

		this.btnInserirOrcamento.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirOrcamento.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));
		this.btnDesconsiderarOrcamento.setIcon(new ImageIcon(Util.loadImage(this, "desconsiderarOrcamentoOff.png")));
		this.btnDesconsiderarOrcamento
				.setSelectedIcon(new ImageIcon(Util.loadImage(this, "desconsiderarOrcamentoOn.png")));

		this.btnInserirDepositos.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirDepositos.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));

		this.btnInserirInvestimentos.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirInvestimentos.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));

		this.btnInserirSalario.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirSalario.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));

		this.btnInserirTransferencia.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirTransferencia.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));

		this.btnInserirSaque.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnExcluirSaque.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));
		this.btnConfirmarSaque.setIcon(new ImageIcon(Util.loadImage(this, "confirm.png")));
		this.btnLerSaqueMemoria.setIcon(new ImageIcon(Util.loadImage(this, "clipboard-paste.png")));

		this.setBorder(BorderFactory.createEmptyBorder());

		panCheque = new JPanel(null, true);
		
		this.panCartoes = new HashMap<CartaoContratado, JPanel>();
		cartoesContratados = getCartoesContratados();
		for (CartaoContratado cartaoContratado : cartoesContratados) {
			JPanel panCartao = new JPanel(null, true);
			this.panCartoes.put(cartaoContratado, panCartao);
		}
		panDebito = new JPanel(null, true);
		panOrcamento = new JPanel(null, true);
		panAtivos = new JPanel(null, true);
		panTransferencia = new JPanel(null, true);
		panSaque = new JPanel(null, true);

		AtomicInteger indexTab = new AtomicInteger(0);
		if (this.getContaCorrente().isCheque()) {
			buildPanelCheque();
			tabs.addTab("Cheques", panCheque);
			indexTab.getAndIncrement();
		}

		buildPanelCartoes();
		buildPanelDebito();
		buildPanelOrcamento();
		buildPanelAtivos(panAtivos);
		buildPanelResumo(this);
		buildPanelTransferencia();
		buildPanelSaque();

		cartoesContratados.sort(CartaoContratadoSorter.ORDEM);
		cartoesContratados.forEach(cartaoContratado -> {
			indexTab.getAndIncrement();
			
			String imageCartao         = cartaoContratado.getLogo();
			String imageCartaoSelected = StringUtils.replaceAll(imageCartao, "\\.", "_selected.");
			Image  imgSelected         = Util.loadImageIfExists(this, imageCartaoSelected);
			
			ImageIcon imgIconUnSelected = new ImageIcon(Util.loadImage(this, imageCartao));
			ImageIcon imgIconSelected   = imgSelected != null ? new ImageIcon(imgSelected) : null;
			
			this.tabIconsUnselected.put(indexTab.get(),imgIconUnSelected);
			this.tabIconsSelected.put(indexTab.get(), imgIconSelected);
			
			tabs.addTab("", imgIconUnSelected, this.panCartoes.get(cartaoContratado));
		});

		tabs.addTab("Saques", panSaque);
		tabs.addTab("Débitos C/C", panDebito);
		tabs.addTab("Transferências", panTransferencia);
		tabs.addTab("Orçamentos", panOrcamento);
		tabs.addTab("Ativos", panAtivos);
		tabs.setBounds(271, 15, ((ScorecardGUI)this.owner).largura - 292, ((ScorecardGUI)this.owner).altura - 163);
		tabs.setVisible(true);
		tabs.setName("Tabs");
		tabs.addMouseListener(this);

		this.add(tabs);
		tabs.setIconAt(0, this.tabIconsSelected.get(1));
		this.setVisible(true);
	}

	private void initBtnNames() {
		this.btnInserirCheque.setName("ADD");
		this.btnExcluirCheque.setName("REMOVE");
		this.btnConfirmarCheque.setName("CONFIRM");
		
		List<CartaoContratado> cartoesContratados = this.getCartoesContratados();
		for (CartaoContratado cartaoContratado : cartoesContratados) {
			this.btnInserirCartao.get(cartaoContratado).setName("ADD");
			this.btnExcluirCartao.get(cartaoContratado).setName("REMOVE");
			this.btnConfirmarCartao.get(cartaoContratado).setName("CONFIRM");
		}

		this.btnInserirDebito.setName("ADD");
		this.btnExcluirDebito.setName("REMOVE");
		this.btnConfirmarDebito.setName("CONFIRM");

		this.btnInserirOrcamento.setName("ADD");
		this.btnExcluirOrcamento.setName("REMOVE");
		this.btnDesconsiderarOrcamento.setName("CONSIDERAR");

		this.btnInserirDepositos.setName("ADD");
		this.btnExcluirDepositos.setName("REMOVE");

		this.btnInserirInvestimentos.setName("ADD");
		this.btnExcluirInvestimentos.setName("REMOVE");

		this.btnInserirSalario.setName("ADD");
		this.btnExcluirSalario.setName("REMOVE");

		this.btnInserirTransferencia.setName("ADD");
		this.btnExcluirTransferencia.setName("REMOVE");

		this.btnInserirSaque.setName("ADD");
		this.btnExcluirSaque.setName("REMOVE");
		this.btnConfirmarSaque.setName("CONFIRM");
		this.btnLerSaqueMemoria.setName("READ");
	}

	private void buildPanelCartoes() {
		
		for (CartaoContratado cartaoContratado : this.getCartoesContratados()) {
			
			JTable tableCartao = new JTable(this.tableModelCartoes.get(cartaoContratado)){
				private static final long serialVersionUID = 4634987309460177002L;
	
				@Override
				public boolean isCellEditable(int arg0, int arg1) {
					return false;
				}
			};
			this.setUpTable(tableCartao,"TABLE_CARTAO_" + cartaoContratado.getId());
			this.layOutTableCartaoCredito(tableCartao);
			tableCartao.addKeyListener(this);
			tableCartao.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			this.tableCartoes.put(cartaoContratado, tableCartao);
			
			JScrollPane jScrollPane = new JScrollPane(tableCartao);
			JLabel lblCartao = new JLabel(cartaoContratado.getNome(),SwingConstants.CENTER);
			lblCartao.setFont(new Font("Verdana",Font.BOLD,10));
			lblCartao.setOpaque(true);
			lblCartao.setBackground(Color.GRAY);
			lblCartao.setForeground(Color.WHITE);
			lblCartao.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			JLabel logo = new JLabel(new ImageIcon(Util.loadImage(this, cartaoContratado.getLogo())));
			logo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			setGenericBounds(jScrollPane, lblCartao, logo);
			
			lblCartao.setBounds(100, 10, ((ScorecardGUI)this.owner).largura - 406, 26);
			btnInserirCartao.get(cartaoContratado).setBounds(10, 10, 27, 26);
			btnExcluirCartao.get(cartaoContratado).setBounds(40, 10, 27, 26);
			btnConfirmarCartao.get(cartaoContratado).setBounds(70, 10, 27, 26);
			
			JPanel panelCartao = this.panCartoes.get(cartaoContratado);
			
			panelCartao.add(this.btnConfirmarCartao.get(cartaoContratado));
			panelCartao.add(lblCartao);
			panelCartao.add(jScrollPane);
			panelCartao.add(this.btnInserirCartao.get(cartaoContratado));
			panelCartao.add(this.btnExcluirCartao.get(cartaoContratado));
			
		}
	}

	private void setGenericBounds(JScrollPane scrollPane, JLabel lbl, JLabel logo) {
		logo.setBounds(10, 10, 40, 26);
		lbl.setBounds(143, 10, 624, 26);
		scrollPane.setBounds(10, 41, ((ScorecardGUI)this.owner).largura - 315, ((ScorecardGUI)this.owner).altura - 276);
	}

	private int[][] getGenericBounds() {
		int[][] result = { { 143, 10, ((ScorecardGUI)this.owner).largura - 449, 26 }, { 10, 41, ((ScorecardGUI)this.owner).largura - 315, 380 } };
		return result;
	}

	private void setPanelButtonBounds(JButton btn) {
		String buttonType = btn.getName().toString();
		if (buttonType.indexOf("ADD") != -1) {
			btn.setBounds(53, 10, 27, 26);
		} else if (buttonType.indexOf("REMOVE") != -1) {
			btn.setBounds(83, 10, 27, 26);
		} else if (buttonType.indexOf("CONFIRM") != -1) {
			btn.setBounds(113, 10, 27, 26);
		}
	}

	private void buildPanelResumo(JPanel panelParent) {
		ResumoPeriodo resumoPeriodo = getResumoPeriodo();
		String margin = "\u0020\u0020\u0020";
		
		int qtdeCartoes = 0;
		
		tableModelResumo = new DefaultModelTabela(
				new Object[][] {
						{ margin + "Saldo Anterior", Util.formatCurrency(resumoPeriodo.getSaldoAnterior()) + margin }},
				new Object[] { "Descrição", "Valor" });
		
		if ( this.getContaCorrente().isCheque() ) {
			tableModelResumo.addRow(new String[]{ margin + "Cheques", Util.formatCurrency(resumoPeriodo.getCheques()) + margin });
		}
		
		List<ResumoPeriodoTotalCartao> listTotalCartoes = resumoPeriodo.getCartoes();
		for (ResumoPeriodoTotalCartao resumoPeriodoTotalCartao : listTotalCartoes) {
			tableModelResumo.addRow(new String[]{ margin + resumoPeriodoTotalCartao.getCartaoContratado().getNome(), 
														   Util.formatCurrency(resumoPeriodoTotalCartao.getTotal()) + margin });
			qtdeCartoes++;
		}
		
		tableModelResumo.addRow(new String[]{ margin + "Saques", Util.formatCurrency(resumoPeriodo.getSaques()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Débitos", Util.formatCurrency(resumoPeriodo.getDebitosCC()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Despesas", Util.formatCurrency(resumoPeriodo.getDespesas()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Depósitos", Util.formatCurrency(resumoPeriodo.getDepositos()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Ivestimentos", Util.formatCurrency(resumoPeriodo.getInvestimentos()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Transferências", Util.formatCurrency(resumoPeriodo.getTransferencias()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Estipêndios", Util.formatCurrency(resumoPeriodo.getSalario()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Saldo Previsto", Util.formatCurrency(resumoPeriodo.getSaldoPrevisto()) + margin });
		tableModelResumo.addRow(new String[]{ margin + "Saldo Real", Util.formatCurrency(resumoPeriodo.getSaldoReal()) + margin });

		JTable tableResumo = new JTable(tableModelResumo) {
			private static final long serialVersionUID = -7192115991350831533L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		tableResumo.setFont(new Font("Verdana", Font.PLAIN, 11));
		tableResumo.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
			}
		});

		tableResumo.setPreferredScrollableViewportSize(new Dimension(380, 180));
		TableColumn descricaoColumn = tableResumo.getColumnModel().getColumn(0);
		TableColumn valorColumn = tableResumo.getColumnModel().getColumn(1);
		DefaultTableCellRenderer descricaoRenderer = new ResumoTableCellRenderer(qtdeCartoes, this.getContaCorrente().isCheque());
		DefaultTableCellRenderer valorRenderer = new ResumoTableCellRenderer(qtdeCartoes, this.getContaCorrente().isCheque());

		descricaoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);

		valorColumn.setCellRenderer(valorRenderer);
		descricaoColumn.setCellRenderer(descricaoRenderer);

		valorColumn.setPreferredWidth(35);
		descricaoColumn.setPreferredWidth(75);

		JScrollPane jScrollPane = new JScrollPane(tableResumo);

		String lblPeriodoResumoText = this.updateLabelResumo();
		lblPeriodoResumo = new JLabel(lblPeriodoResumoText, SwingConstants.CENTER);
		lblPeriodoResumo.setFont(new Font("Verdana", Font.BOLD, 10));
		lblPeriodoResumo.setOpaque(true);
		lblPeriodoResumo.setBackground(Color.GRAY);
		lblPeriodoResumo.setForeground(Color.WHITE);
		lblPeriodoResumo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblPeriodoResumo.setName("PERIODO");
		lblPeriodoResumo.setBounds(30, 123, 209, 26);
		lblPeriodoResumo.addMouseListener(this);

		jScrollPane.setBounds(10, 153, 250, 279);
		panelParent.add(lblPeriodoResumo);
		panelParent.add(jScrollPane);

		ImageIcon imgBtnMesAnterior = new ImageIcon(Util.loadImage(this, "rewind.png"));
		JButton btnMesAnterior = new JButton(imgBtnMesAnterior);
		btnMesAnterior.setBounds(10, 123, 20, 26);
		btnMesAnterior.setActionCommand("BACK_MONTH");
		btnMesAnterior.addActionListener(this);
		panelParent.add(btnMesAnterior);

		ImageIcon imgBtnMesPosterior = new ImageIcon(Util.loadImage(this, "forwarding.png"));
		JButton btnMesPosterior = new JButton(imgBtnMesPosterior);
		btnMesPosterior.setBounds(239, 123, 20, 26);
		btnMesPosterior.setActionCommand("NEXT_MONTH");
		btnMesPosterior.addActionListener(this);
		panelParent.add(btnMesPosterior);

		/**
		 * Logo Conta Corrente x Scorecard
		 */
		JPanel panelImg = new JPanel();
		panelImg.setLayout(null);
		panelImg.setBounds(0, 20, 267, 80);
		ImageIcon img = new ImageIcon(Util.loadImage(this, "Logo-Scorecard.png"));
		JLabel lblImage = new JLabel(img);
		lblImage.setBounds(0, 0, panelImg.getWidth(), panelImg.getHeight());
		panelImg.add(lblImage);
		panelParent.add(panelImg);
	}

	private ResumoPeriodo getResumoPeriodo() {
		/**
		 * Considerar sempre o orçamento para possibilitar o controle individual de cada
		 * conta (valor) do orçamento desconsiderando cada um por vez ou todos
		 * selecionando(amarelo) ou não.
		 */
		boolean considerarOrcamento = true;
		ResumoPeriodo resumoPeriodo = this.scorecardManager.getResumoPeriodo(this.getContaCorrente(),
				this.periodoDataInicial, this.periodoDataFinal, considerarOrcamento);
		return resumoPeriodo;
	}

	private void setUpDataInicialFinal() {
		// Get movimentos for testing
		// TODO: Apagar estas linha depois dos testes
//		int mesAtual = 10;
//		int anoAtual = 2018;
//		int diaFinal = 31;
//		this.periodoDataInicial = Util.parseDate(1, mesAtual, anoAtual);
//		this.periodoDataFinal = Util.parseDate(diaFinal, mesAtual, anoAtual);

		// ORIGINAL
		int mesAtual = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
		int diaFinal = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
		this.periodoDataInicial = Util.parseDate(1, mesAtual, anoAtual);
		this.periodoDataFinal = Util.parseDate(diaFinal, mesAtual, anoAtual);
	}

	private void setUpTable(JTable table, String name) {
		table.setName(name);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(this);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setPreferredScrollableViewportSize(new Dimension(400, 80));
		table.setFont(new Font("Courier New", Font.PLAIN, 12));
	}

	private void buildPanelCheque() {
		this.tableCheque = new JTable(tableModelCheque) {
			private static final long serialVersionUID = 2055391675568864661L;

			@Override
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		this.setUpTable(tableCheque, "TABLE_CHEQUES");
		this.layOutTableCheque();
		JScrollPane jScrollPane = new JScrollPane(tableCheque);

		JLabel lblCheques = new JLabel("Cheques", SwingConstants.CENTER);
		lblCheques.setFont(new Font("Verdana", Font.BOLD, 10));
		lblCheques.setOpaque(true);
		lblCheques.setBackground(Color.GRAY);
		lblCheques.setForeground(Color.WHITE);
		lblCheques.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		setGenericBounds(jScrollPane, lblCheques, lblCheques);

		lblCheques.setBounds(100, 10, ((ScorecardGUI)this.owner).largura - 406, 26);
		btnInserirCheque.setBounds(10, 10, 27, 26);
		btnExcluirCheque.setBounds(40, 10, 27, 26);
		btnConfirmarCheque.setBounds(70, 10, 27, 26);

		panCheque.add(lblCheques);
		panCheque.add(jScrollPane);
		panCheque.add(btnInserirCheque);
		panCheque.add(btnExcluirCheque);
		panCheque.add(btnConfirmarCheque);
		panCheque.setVisible(true);
	}

	private void layOutTableCheque() {
		tableCheque.removeColumn(tableCheque.getColumnModel().getColumn(6));
		tableCheque.removeColumn(tableCheque.getColumnModel().getColumn(6));

		TableColumn vencimentoColumn = tableCheque.getColumnModel().getColumn(0);
		TableColumn valorColumn = tableCheque.getColumnModel().getColumn(1);
		TableColumn descricaoColumn = tableCheque.getColumnModel().getColumn(2);
		TableColumn parcelaColumn = tableCheque.getColumnModel().getColumn(3);
		TableColumn efetivadoColumn = tableCheque.getColumnModel().getColumn(4);
		TableColumn numeroColumn = tableCheque.getColumnModel().getColumn(5);
		DefaultTableCellRenderer vencimentoRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER);
		DefaultTableCellRenderer valorRenderer = new MonetarioTableCellRenderer();
		DefaultTableCellRenderer descricaoRenderer = new ParcelaTableCellRenderer(" ");
		DefaultTableCellRenderer parcelaRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER);
		DefaultTableCellRenderer numeroRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER);
		TableCellRenderer efetivadoRenderer = new EfetivadoTableCellRenderer();

		SortButtonRenderer renderer = new SortButtonRenderer();
		for (int i = 0; i < tableCheque.getColumnModel().getColumnCount(); i++) {
			tableCheque.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableCheque.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela) tableCheque.getModel()));

		vencimentoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		descricaoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		parcelaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		numeroRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

		vencimentoColumn.setCellRenderer(vencimentoRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		descricaoColumn.setCellRenderer(descricaoRenderer);
		parcelaColumn.setCellRenderer(parcelaRenderer);
		numeroColumn.setCellRenderer(numeroRenderer);
		efetivadoColumn.setCellRenderer(efetivadoRenderer);

		vencimentoColumn.setPreferredWidth(95);
		valorColumn.setPreferredWidth(109);
		descricaoColumn.setPreferredWidth(345);
		parcelaColumn.setPreferredWidth(60);
		efetivadoColumn.setPreferredWidth(42);
		numeroColumn.setPreferredWidth(73);
	}

	/**
	 * Remove mais uma coluna para fazê-la invisível, existir somente no Modelo da
	 * JTable, O campo utilizada par Seleção temporária na conferência da Fatura do
	 * Cartão de Crédito
	 * 
	 * @param table
	 */
	private void layOutTableCartaoCredito(JTable table) {
		table.removeColumn(table.getColumnModel().getColumn(5));
		layOutTable(table);
	}

	private void layOutTable(JTable table) {
		table.removeColumn(table.getColumnModel().getColumn(5));
		table.removeColumn(table.getColumnModel().getColumn(5));

		TableColumn vencimentoColumn = table.getColumnModel().getColumn(0);
		TableColumn valorColumn = table.getColumnModel().getColumn(1);
		TableColumn descricaoColumn = table.getColumnModel().getColumn(2);
		TableColumn parcelaColumn = table.getColumnModel().getColumn(3);
		TableColumn efetivadoColumn = table.getColumnModel().getColumn(4);
		DefaultTableCellRenderer vencimentoRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER);
		DefaultTableCellRenderer valorRenderer = new MonetarioTableCellRenderer();
		DefaultTableCellRenderer descricaoRenderer = new ParcelaTableCellRenderer(" ");
		DefaultTableCellRenderer parcelaRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER);
		TableCellRenderer efetivadoRenderer = new EfetivadoTableCellRenderer();

		SortButtonRenderer renderer = new SortButtonRenderer();
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = table.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela) table.getModel()));

		vencimentoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		descricaoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		parcelaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);

		vencimentoColumn.setCellRenderer(vencimentoRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		descricaoColumn.setCellRenderer(descricaoRenderer);
		parcelaColumn.setCellRenderer(parcelaRenderer);
		efetivadoColumn.setCellRenderer(efetivadoRenderer);

		vencimentoColumn.setPreferredWidth(95);
		valorColumn.setPreferredWidth(109);
		descricaoColumn.setPreferredWidth(410);
		parcelaColumn.setPreferredWidth(65);
		efetivadoColumn.setPreferredWidth(45);
	}

	private void buildPanelAtivos(JPanel panAtivos) {
		buildAtivosDepositos(panAtivos);
		buildAtivosInvestimentos(panAtivos);
		buildAtivosSalario(panAtivos);
	}

	private void buildAtivosDepositos(JPanel panAtivos) {
		this.tableDeposito = new JTable(this.tableModelDeposito) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableDeposito, "TABLE_DEPOSITO");
		this.layOutTableAtivo(tableDeposito);

		int[][] genericBounds = this.getGenericBounds();
		JScrollPane jScrollPane = new JScrollPane(tableDeposito);
		jScrollPane.setBounds(genericBounds[1][0], genericBounds[1][1], genericBounds[1][2], 120);

		JLabel lblDepositos = new JLabel("Depósitos", SwingConstants.CENTER);
		lblDepositos.setFont(new Font("Verdana", Font.BOLD, 10));
		lblDepositos.setOpaque(true);
		lblDepositos.setBackground(Color.GRAY);
		lblDepositos.setForeground(Color.WHITE);
		lblDepositos.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblDepositos.setBounds(70, 10, ((ScorecardGUI)this.owner).largura - 376, 26);

		btnInserirDepositos.setBounds(10, 10, 27, 26);
		btnExcluirDepositos.setBounds(40, 10, 27, 26);

		panAtivos.add(lblDepositos);
		panAtivos.add(jScrollPane);
		panAtivos.add(btnInserirDepositos);
		panAtivos.add(btnExcluirDepositos);
	}

	private void buildAtivosSalario(JPanel panAtivos) {
		this.tableSalario = new JTable(this.tableModelSalario) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableSalario, "TABLE_SALARIO");
		this.layOutTableAtivo(tableSalario);

		int yDiff = 435;

		int[][] genericBounds = this.getGenericBounds();
		JScrollPane jScrollPane = new JScrollPane(tableSalario);
		jScrollPane.setBounds(genericBounds[1][0], genericBounds[1][1] + yDiff, genericBounds[1][2], 120);

		JLabel lblSalario = new JLabel("Estipêndios", SwingConstants.CENTER);
		lblSalario.setFont(new Font("Verdana", Font.BOLD, 10));
		lblSalario.setOpaque(true);
		lblSalario.setBackground(Color.GRAY);
		lblSalario.setForeground(Color.WHITE);
		lblSalario.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblSalario.setBounds(70, 10 + yDiff, genericBounds[0][2] + (genericBounds[0][0] - 70), 26);

		btnInserirSalario.setBounds(10, 10 + yDiff, 27, 26);
		btnExcluirSalario.setBounds(40, 10 + yDiff, 27, 26);

		panAtivos.add(lblSalario);
		panAtivos.add(jScrollPane);
		panAtivos.add(btnInserirSalario);
		panAtivos.add(btnExcluirSalario);
	}

	private void buildAtivosInvestimentos(JPanel panAtivos) {
		this.tableInvestimento = new JTable(this.tableModelInvestimento) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableInvestimento, "TABLE_INVESTIMENTO");
		this.layOutTableAtivo(tableInvestimento);

		int yDiff = 218;

		int[][] genericBounds = this.getGenericBounds();
		JScrollPane jScrollPane = new JScrollPane(tableInvestimento);
		jScrollPane.setBounds(genericBounds[1][0], genericBounds[1][1] + yDiff, genericBounds[1][2], 120);

		JLabel lblInvestimentos = new JLabel("Investimentos", SwingConstants.CENTER);
		lblInvestimentos.setFont(new Font("Verdana", Font.BOLD, 10));
		lblInvestimentos.setOpaque(true);
		lblInvestimentos.setBackground(Color.GRAY);
		lblInvestimentos.setForeground(Color.WHITE);
		lblInvestimentos.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblInvestimentos.setBounds(71, 10 + yDiff,  ((ScorecardGUI)this.owner).largura - 376, 26);

		btnInserirInvestimentos.setBounds(10, 10 + yDiff, 27, 26);
		btnExcluirInvestimentos.setBounds(40, 10 + yDiff, 27, 26);

		panAtivos.add(lblInvestimentos);
		panAtivos.add(jScrollPane);
		panAtivos.add(btnInserirInvestimentos);
		panAtivos.add(btnExcluirInvestimentos);
	}

	private void layOutTableOrcamento() {
		tableOrcamento.removeColumn(tableOrcamento.getColumnModel().getColumn(5));

		tableOrcamento.setPreferredScrollableViewportSize(new Dimension(350, 80));
		TableColumn referenciaColumn = tableOrcamento.getColumnModel().getColumn(0);
		TableColumn orcadoColumn = tableOrcamento.getColumnModel().getColumn(1);
		TableColumn realizadoColumn = tableOrcamento.getColumnModel().getColumn(2);
		TableColumn restanteColumn = tableOrcamento.getColumnModel().getColumn(3);
		TableColumn contaColumn = tableOrcamento.getColumnModel().getColumn(4);
		DefaultTableCellRenderer referenciaRenderer = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer orcadoRenderer = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer realizadoRenderer = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer restanteRenderer = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer contaRenderer = new OrcamentoTableCellRenderer();

		SortButtonRenderer renderer = new SortButtonRenderer();
		for (int i = 0; i < tableOrcamento.getColumnModel().getColumnCount(); i++) {
			tableOrcamento.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableOrcamento.getTableHeader();
		OrcamentoOrdenadorTabela orcamOrdenadorTabela = new OrcamentoOrdenadorTabela(
				(DefaultModelTabela) tableOrcamento.getModel());
		header.addMouseListener(new HeaderListener(header, renderer, orcamOrdenadorTabela));

		referenciaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		orcadoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		realizadoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		restanteRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		contaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

		referenciaColumn.setCellRenderer(referenciaRenderer);
		orcadoColumn.setCellRenderer(orcadoRenderer);
		realizadoColumn.setCellRenderer(realizadoRenderer);
		restanteColumn.setCellRenderer(restanteRenderer);
		contaColumn.setCellRenderer(contaRenderer);

		referenciaColumn.setPreferredWidth(55);
		orcadoColumn.setPreferredWidth(100);
		realizadoColumn.setPreferredWidth(110);
		restanteColumn.setPreferredWidth(110);
		contaColumn.setPreferredWidth(349);
	}
	
	private void layOutTableNaoOrcado() {
		TableColumn vencimentoColumn = tableNaoOrcado.getColumnModel().getColumn(0);
		TableColumn valorColumn      = tableNaoOrcado.getColumnModel().getColumn(1);
		TableColumn historicoColumn  = tableNaoOrcado.getColumnModel().getColumn(2);
		TableColumn parcelaColumn    = tableNaoOrcado.getColumnModel().getColumn(3);
		TableColumn efetivadoColumn  = tableNaoOrcado.getColumnModel().getColumn(4);
		TableColumn contaColumn      = tableNaoOrcado.getColumnModel().getColumn(5);
		
		Color foregroundColor        = Color.DARK_GRAY;
		
		RendererRules rendererRules  = new RendererRules((tab,row) -> {
			
			RendererRules.Format header = new RendererRules.Format();
			header.setFont(new Font("Arial",Font.BOLD,12));
			header.setBackground(new Color(185, 244, 168));
			
			RendererRules.Format lines = new RendererRules.Format();
			lines.setFont(new Font("Courier New",Font.PLAIN,11));
			lines.setForeground(new Color(43, 69, 70));
			
			String value = (String)tab.getValueAt(row, 0);
			if ( StringUtils.isBlank(value) ) {
				return header;
			}
			return lines;
		});
		
		
		DefaultTableCellRenderer vencimentoRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER, foregroundColor, rendererRules);
		DefaultTableCellRenderer valorRenderer      = new MonetarioTableCellRenderer(foregroundColor, rendererRules);
		DefaultTableCellRenderer historicoRenderer  = new ParcelaTableCellRenderer(" ", foregroundColor, rendererRules);
		DefaultTableCellRenderer parcelaRenderer    = new ParcelaTableCellRenderer(SwingConstants.CENTER, foregroundColor, rendererRules);
		TableCellRenderer efetivadoRenderer         = new EfetivadoTableCellRenderer(rendererRules);
		DefaultTableCellRenderer contaRenderer      = new ParcelaTableCellRenderer(SwingConstants.LEADING, foregroundColor, rendererRules);
		
		SortButtonRenderer renderer = new SortButtonRenderer();
		for(int i = 0; i < tableNaoOrcado.getColumnModel().getColumnCount(); i++) {
			tableNaoOrcado.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableNaoOrcado.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela)tableNaoOrcado.getModel()));
		
		vencimentoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		historicoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		parcelaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		contaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		
		vencimentoColumn.setCellRenderer(vencimentoRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		historicoColumn.setCellRenderer(historicoRenderer);
		parcelaColumn.setCellRenderer(parcelaRenderer);
		efetivadoColumn.setCellRenderer(efetivadoRenderer);
		contaColumn.setCellRenderer(contaRenderer);
		
		vencimentoColumn.setPreferredWidth(84);
		valorColumn.setPreferredWidth(109);
		historicoColumn.setPreferredWidth(407);
		parcelaColumn.setPreferredWidth(50);
		efetivadoColumn.setPreferredWidth(55);
		contaColumn.setPreferredWidth(70);
	}

	private void layOutTableTransferencia() {
		this.tableTransferencia.removeColumn(this.tableTransferencia.getColumnModel().getColumn(4));

		this.tableTransferencia.setPreferredScrollableViewportSize(new Dimension(350, 80));
		TableColumn referenciaColumn = this.tableTransferencia.getColumnModel().getColumn(0);
		TableColumn valorColumn = this.tableTransferencia.getColumnModel().getColumn(1);
		TableColumn destinoColumn = this.tableTransferencia.getColumnModel().getColumn(2);
		TableColumn historicoColumn = this.tableTransferencia.getColumnModel().getColumn(3);
		DefaultTableCellRenderer referenciaRenderer = new ParcelaTableAtivoCellRenderer(SwingConstants.CENTER);
		DefaultTableCellRenderer valorRenderer = new MonetarioTableAtivoCellRenderer();
		DefaultTableCellRenderer destinoRenderer = new ParcelaTableAtivoCellRenderer();
		DefaultTableCellRenderer historicoRenderer = new ParcelaTableAtivoCellRenderer();

		SortButtonRenderer renderer = new SortButtonRenderer();
		for (int i = 0; i < tableTransferencia.getColumnModel().getColumnCount(); i++) {
			tableTransferencia.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableTransferencia.getTableHeader();
		header.addMouseListener(
				new HeaderListener(header, renderer, (DefaultModelTabela) tableTransferencia.getModel()));

		referenciaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		destinoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		historicoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

		referenciaColumn.setCellRenderer(referenciaRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		destinoColumn.setCellRenderer(destinoRenderer);
		historicoColumn.setCellRenderer(historicoRenderer);

		referenciaColumn.setPreferredWidth(55);
		valorColumn.setPreferredWidth(109);
		destinoColumn.setPreferredWidth(280);
		historicoColumn.setPreferredWidth(280);
	}

	private void layOutTableAtivo(JTable tableAtivo) {
		tableAtivo.removeColumn(tableAtivo.getColumnModel().getColumn(3));

		tableAtivo.setPreferredScrollableViewportSize(new Dimension(350, 80));
		TableColumn referenciaColumn = tableAtivo.getColumnModel().getColumn(0);
		TableColumn valorColumn = tableAtivo.getColumnModel().getColumn(1);
		TableColumn historicoColumn = tableAtivo.getColumnModel().getColumn(2);
		DefaultTableCellRenderer referenciaRenderer = new ParcelaTableAtivoCellRenderer(SwingConstants.CENTER);
		DefaultTableCellRenderer valorRenderer = new MonetarioTableAtivoCellRenderer();
		DefaultTableCellRenderer historicoRenderer = new ParcelaTableAtivoCellRenderer();

		SortButtonRenderer renderer = new SortButtonRenderer();
		for (int i = 0; i < tableAtivo.getColumnModel().getColumnCount(); i++) {
			tableAtivo.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableAtivo.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela) tableAtivo.getModel()));

		referenciaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		historicoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);

		referenciaColumn.setCellRenderer(referenciaRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		historicoColumn.setCellRenderer(historicoRenderer);

		referenciaColumn.setPreferredWidth(65);
		valorColumn.setPreferredWidth(170);
		historicoColumn.setPreferredWidth(489);
	}

	private void retirarSaldoPrevistoValorRestanteOrcamento(BigDecimal valorRestante) {
		String margin = "\u0020\u0020\u0020";
		BigDecimal saldoPrevisto = Util.parseCurrency(this.tableModelResumo.getValueAt(12, 1).toString());
		saldoPrevisto = saldoPrevisto.subtract(valorRestante);
		this.tableModelResumo.setValueAt(Util.formatCurrency(saldoPrevisto) + margin, 12, 1);
	}

	private void acrescentarSaldoPrevistoValorRestanteOrcamento(BigDecimal valorRestante) {
		String margin = "\u0020\u0020\u0020";
		BigDecimal saldoPrevisto = Util.parseCurrency(this.tableModelResumo.getValueAt(12, 1).toString());
		saldoPrevisto = saldoPrevisto.add(valorRestante);
		this.tableModelResumo.setValueAt(Util.formatCurrency(saldoPrevisto) + margin, 12, 1);
	}

	private void buildPanelOrcamento() {
		this.tableOrcamento = new JTable(this.tableModelOrcamento) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableOrcamento, "TABLE_ORCAMENTO");
		this.layOutTableOrcamento();
		tableOrcamento.removeColumn(tableOrcamento.getColumnModel().getColumn(5));

		tableOrcamento.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 32) {
					JTable t = (JTable) e.getSource();
					int rows[] = t.getSelectedRows();
					for (int i : rows) {
						Boolean selectedTemp = (Boolean) t.getModel().getValueAt(i, 6);
						t.getModel().setValueAt(new Boolean(!selectedTemp), i, 6);

						BigDecimal valorRestante = Util.parseCurrency(t.getModel().getValueAt(i, 3).toString());
						if (selectedTemp && valorRestante.floatValue() > 0) {
							retirarSaldoPrevistoValorRestanteOrcamento(valorRestante);
						} else if (!selectedTemp && valorRestante.floatValue() > 0) {
							acrescentarSaldoPrevistoValorRestanteOrcamento(valorRestante);
						}
					}
					t.updateUI();
				}
			}

			public void keyTyped(KeyEvent e) {
			}
		});

		JScrollPane jScrollPane = new JScrollPane(tableOrcamento);
		JLabel lblOrcamentos = new JLabel("Orçamentos", SwingConstants.CENTER);
		lblOrcamentos.setFont(new Font("Verdana", Font.BOLD, 10));
		lblOrcamentos.setOpaque(true);
		lblOrcamentos.setBackground(Color.GRAY);
		lblOrcamentos.setForeground(Color.WHITE);
		lblOrcamentos.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		setGenericBounds(jScrollPane, lblOrcamentos, lblOrcamentos);
		jScrollPane.setBounds(10, 41, ((ScorecardGUI)this.owner).largura - 315, ((ScorecardGUI)this.owner).altura - 575);
		
		String vlrLabelTotal = calculateOrcamentoLabelResumo();
		
		lblOrcamentoTotal = new JLabel(vlrLabelTotal, SwingConstants.CENTER);
		lblOrcamentoTotal.setFont(new Font("Verdana", Font.BOLD, 10));
		lblOrcamentoTotal.setOpaque(true);
		lblOrcamentoTotal.setBackground(Color.GRAY);
		lblOrcamentoTotal.setForeground(Color.GREEN);
		lblOrcamentoTotal.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		

		lblOrcamentos.setBounds(100, 10, ((ScorecardGUI)this.owner).largura - 920, 26);
		lblOrcamentoTotal.setBounds(lblOrcamentos.getWidth() + 103, 10, 511, 26);
		btnInserirOrcamento.setBounds(10, 10, 27, 26);
		btnExcluirOrcamento.setBounds(40, 10, 27, 26);
		btnDesconsiderarOrcamento.setBounds(70, 10, 27, 26);

		this.popupMenuOrcamento = new JPopupMenu();
		this.lblPopupOrcamento = new JLabel("Orçamento");
		this.lblPopupOrcamento.setFont(new Font("Verdana", Font.BOLD, 10));
		this.lblPopupOrcamento.setHorizontalAlignment(SwingConstants.CENTER);
		JMenuItem itemPopupOrcamento01 = new JMenuItem("Abrir...");
		itemPopupOrcamento01.setFont(new Font("Verdana", Font.BOLD, 10));
		itemPopupOrcamento01.setActionCommand("POPUP_ORCAMENTO_ABRIR");
		itemPopupOrcamento01.addActionListener(this);
		JMenuItem itemPopupOrcamento02 = new JMenuItem("Ver Passivos...");
		itemPopupOrcamento02.setFont(new Font("Verdana", Font.BOLD, 10));
		itemPopupOrcamento02.setActionCommand("POPUP_ORCAMENTO_VER_PASSIVOS");
		itemPopupOrcamento02.addActionListener(this);
		this.popupMenuOrcamento.add(lblPopupOrcamento);
		this.popupMenuOrcamento.addSeparator();
		this.popupMenuOrcamento.add(itemPopupOrcamento01);
		this.popupMenuOrcamento.add(itemPopupOrcamento02);
		this.tableOrcamento.addMouseListener(new PopupOrcamentoMouseListener(this));
		
		
		JLabel lblNaoOrcado = new JLabel("Não Orçado", SwingConstants.CENTER);
		lblNaoOrcado.setFont(new Font("Verdana", Font.BOLD, 10));
		lblNaoOrcado.setOpaque(true);
		lblNaoOrcado.setBackground(Color.GRAY);
		lblNaoOrcado.setForeground(Color.WHITE);
		lblNaoOrcado.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblNaoOrcado.setBounds(10, 310, 200, 26);
		
		
		String vlrLabelTotalNaoOrcado = calculateLabelNaoOrcado();
		
		lblNaoOrcadoTotal = new JLabel(vlrLabelTotalNaoOrcado, SwingConstants.CENTER);
		lblNaoOrcadoTotal.setFont(new Font("Verdana", Font.BOLD, 10));
		lblNaoOrcadoTotal.setOpaque(true);
		lblNaoOrcadoTotal.setBackground(Color.GRAY);
		lblNaoOrcadoTotal.setForeground(Color.GREEN);
		lblNaoOrcadoTotal.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblNaoOrcadoTotal.setBounds(213,310,605,26);
		
		// Nao Orcado Area GUI Components
		this.tableNaoOrcado = new JTable(this.tableModelNaoOrcado) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		this.setUpTable(tableNaoOrcado, "TABLE_NAO_ORCADO");
		this.layOutTableNaoOrcado();
		JScrollPane jScrollPaneNaoOrcado = new JScrollPane(tableNaoOrcado);
		jScrollPaneNaoOrcado.setBounds(10, 342, ((ScorecardGUI)this.owner).largura - 315, ((ScorecardGUI)this.owner).altura - 580);

		panOrcamento.add(lblNaoOrcado);
		panOrcamento.add(lblNaoOrcadoTotal);
		panOrcamento.add(lblOrcamentos);
		panOrcamento.add(lblOrcamentoTotal);
		panOrcamento.add(jScrollPane);
		panOrcamento.add(jScrollPaneNaoOrcado);
		panOrcamento.add(btnInserirOrcamento);
		panOrcamento.add(btnExcluirOrcamento);
		panOrcamento.add(btnDesconsiderarOrcamento);
	}

	private String calculateOrcamentoLabelResumo() {
		BigDecimal resultadoOrcamento = new BigDecimal(this.valorTotalOrcamento - this.valorTotalRealizado);
		
		StringBuffer sb = new StringBuffer();
		sb
		  .append("<html><font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("Orçado.....: ")
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:10px;color:yellow'>")
		  .append("$ ")
		  .append(Util.formatCurrency(new BigDecimal(this.valorTotalOrcamento)))
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("  -  ")
		  .append("</font>")
		  .append("<html><font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("Realizado..: ")
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:10px;color:yellow'>")
		  .append("$ ")
		  .append(Util.formatCurrency(new BigDecimal(this.valorTotalRealizado)))
		  .append("</font>")
		  .append("<html><font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("  =  ")
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:10px;color:#FFFFCC;font-weight:bold'>")
		  .append("$ ")
		  .append(Util.formatCurrency(resultadoOrcamento))
		  .append("</font></html>");
		
		return sb.toString();
	}
	
	private String calculateLabelNaoOrcado() {
		BigDecimal resultadoTotalOrcamento = new BigDecimal(this.valorTotalOrcamento - this.valorTotalRealizado);
		BigDecimal resultadoSaldoFinal     = new BigDecimal(resultadoTotalOrcamento.floatValue() - this.valorTotalNaoOrcado);
		
		StringBuffer sb = new StringBuffer();
		sb
		  .append("<html><font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("(Orçado - Realizado)..: ")
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:10px;color:yellow'>")
		  .append("$ ")
		  .append(Util.formatCurrency(resultadoTotalOrcamento))
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("  -  ")
		  .append("</font>")
		  .append("<html><font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("Não Orçado..: ")
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:10px;color:yellow'>")
		  .append("$ ")
		  .append(Util.formatCurrency(new BigDecimal(this.valorTotalNaoOrcado)))
		  .append("</font>")
		  .append("<html><font style='font-family:Consolas;font-size:9px;color:white'>")
		  .append("  =  ")
		  .append("</font>")
		  .append("<font style='font-family:Consolas;font-size:10px;color:#FFFFCC;font-weight:bold'>")
		  .append("$ ")
		  .append(Util.formatCurrency(resultadoSaldoFinal))
		  .append("</font></html>");
		
		return sb.toString();
	}

	private class PopupOrcamentoMouseListener extends MouseAdapter {
		private BankPanel parent;

		public PopupOrcamentoMouseListener(BankPanel parent) {
			this.parent = parent;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {

			if (e.isPopupTrigger()) {
				int row = this.parent.tableOrcamento.rowAtPoint(e.getPoint());
				this.parent.tableOrcamento.setRowSelectionInterval(row, row);
				Orcamento orcamento = parent.getSelectedOrcamento(row);
				if (orcamento != null) {
					String lbl = "   " + orcamento.getDescricao() + "    [" + orcamento.getContaAssociada().getNivel()
							+ " - " + orcamento.getContaAssociada().getDescricao() + "]";
					parent.lblPopupOrcamento.setText(lbl);
					popupMenuOrcamento.show(tableOrcamento, e.getX(), e.getY());
					parent.popUpOrcamentoX = e.getX();
					parent.popUpOrcamentoY = e.getY();
				}
			}
		}
	}

	private void buildPanelDebito() {
		this.tableDebito = new JTable(this.tableModelDebito) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableDebito, "TABLE_DEBITO");
		this.layOutTable(tableDebito);

		JScrollPane jScrollPane = new JScrollPane(tableDebito);
		JLabel lblDebito = new JLabel("Débitos C/C", SwingConstants.CENTER);
		lblDebito.setFont(new Font("Verdana", Font.BOLD, 10));
		lblDebito.setOpaque(true);
		lblDebito.setBackground(Color.GRAY);
		lblDebito.setForeground(Color.WHITE);
		lblDebito.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		setGenericBounds(jScrollPane, lblDebito, lblDebito);

		lblDebito.setBounds(100, 10, ((ScorecardGUI)this.owner).largura - 406, 26);
		btnInserirDebito.setBounds(10, 10, 27, 26);
		btnExcluirDebito.setBounds(40, 10, 27, 26);
		btnConfirmarDebito.setBounds(70, 10, 27, 26);

		panDebito.add(lblDebito);
		panDebito.add(jScrollPane);
		panDebito.add(btnInserirDebito);
		panDebito.add(btnExcluirDebito);
		panDebito.add(btnConfirmarDebito);

	}

	private void buildPanelSaque() {
		this.tableSaque = new JTable(this.tableModelSaque) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableSaque, "TABLE_SAQUE");
		this.layOutTable(tableSaque);

		JScrollPane jScrollPane = new JScrollPane(tableSaque);
		JLabel lblSaque = new JLabel("Saques", SwingConstants.CENTER);
		lblSaque.setFont(new Font("Verdana", Font.BOLD, 10));
		lblSaque.setOpaque(true);
		lblSaque.setBackground(Color.GRAY);
		lblSaque.setForeground(Color.WHITE);
		lblSaque.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		setGenericBounds(jScrollPane, lblSaque, lblSaque);

		lblSaque.setBounds(130, 10, ((ScorecardGUI)this.owner).largura - 436, 26);
		btnInserirSaque.setBounds(10, 10, 27, 26);
		btnExcluirSaque.setBounds(40, 10, 27, 26);
		btnConfirmarSaque.setBounds(70, 10, 27, 26);
		btnLerSaqueMemoria.setBounds(100, 10, 27, 26);

		panSaque.add(lblSaque);
		panSaque.add(jScrollPane);
		panSaque.add(btnInserirSaque);
		panSaque.add(btnExcluirSaque);
		panSaque.add(btnConfirmarSaque);
		panSaque.add(btnLerSaqueMemoria);

	}

	private void buildPanelTransferencia() {
		this.tableTransferencia = new JTable(this.tableModelTransferencia) {
			private static final long serialVersionUID = 4634987309460177002L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};

		this.setUpTable(tableTransferencia, "TABLE_TRANSFERENCIA");
		this.layOutTableTransferencia();

		JScrollPane jScrollPane = new JScrollPane(tableTransferencia);
		JLabel lblTransferencia = new JLabel("Transferências", SwingConstants.CENTER);
		lblTransferencia.setFont(new Font("Verdana", Font.BOLD, 10));
		lblTransferencia.setOpaque(true);
		lblTransferencia.setBackground(Color.GRAY);
		lblTransferencia.setForeground(Color.WHITE);
		lblTransferencia.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		setGenericBounds(jScrollPane, lblTransferencia, lblTransferencia);

		lblTransferencia.setBounds(70, 10, ((ScorecardGUI)this.owner).largura - 376, 26);
		btnInserirTransferencia.setBounds(10, 10, 27, 26);
		btnExcluirTransferencia.setBounds(40, 10, 27, 26);

		panTransferencia.add(lblTransferencia);
		panTransferencia.add(jScrollPane);
		panTransferencia.add(btnInserirTransferencia);
		panTransferencia.add(btnExcluirTransferencia);

	}

	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.indexOf("INSERIR") != -1) {
			this.doInserir(actionCommand);
		} else if (actionCommand.indexOf("EXCLUIR") != -1) {
			this.doRemover(actionCommand);
		} else if (actionCommand.indexOf("CONFIRMAR") != -1) {
			this.doConfirmar(actionCommand);
		} else if (actionCommand.indexOf("EXTRATO") != -1) {
			this.doVerificarExtrato(actionCommand);
		} else if (actionCommand.equals("POPUP_ORCAMENTO_ABRIR")) {
			this.openOrcamento();
		} else if (actionCommand.equals("POPUP_ORCAMENTO_VER_PASSIVOS")) {
			Orcamento orcamento = this.getSelectedOrcamento();
			BigDecimal totalOrcado = orcamento.getOrcado();
			VerPassivosOrcamentos verPassivosOrcamentos = new VerPassivosOrcamentos(scorecardManager,
					this.contaCorrente, orcamento, this.periodoDataInicial, this.periodoDataFinal, totalOrcado);
			verPassivosOrcamentos.setVisible(true);
		} else if (actionCommand.equals("DESCONSIDERAR_ORCAMENTO")) {
			boolean desconsiderarOrcamento = this.isDesconsiderarOrcamento();
			for (int i = 0; i < this.tableOrcamento.getRowCount(); i++) {
				boolean selected = ((Boolean) this.tableOrcamento.getModel().getValueAt(i, 6)).booleanValue();
				if (selected != desconsiderarOrcamento) {
					this.tableOrcamento.getModel().setValueAt(new Boolean(desconsiderarOrcamento), i, 6);
					BigDecimal valorRestante = Util
							.parseCurrency(this.tableOrcamento.getModel().getValueAt(i, 3).toString());
					if (!desconsiderarOrcamento && valorRestante.floatValue() > 0) {
						retirarSaldoPrevistoValorRestanteOrcamento(valorRestante);
					} else if (desconsiderarOrcamento && valorRestante.floatValue() > 0) {
						acrescentarSaldoPrevistoValorRestanteOrcamento(valorRestante);
					}
				}
			}
			this.tableOrcamento.updateUI();
		} else if (actionCommand.equals("BACK_MONTH")) {
			this.previousMonth();
		} else if (actionCommand.equals("NEXT_MONTH")) {
			this.nextMonth();
		} else if (actionCommand.equals("LER_SAQUE")) {
			this.lerSaqueMemoriaGravar();
		} else {
			System.out.println("ActionCommand: " + actionCommand + ", with not action!");
		}
	}

	private void lerSaqueMemoriaGravar() {
		AnalisadorSMSCartaoSaqueSantander analisadorMemoriaSAque = new AnalisadorSMSCartaoSaqueSantander(
				this.periodoDataInicial, null);
		Passivo saque;
		for (LinhaLancamento linha : analisadorMemoriaSAque.getLista()) {
			saque = new Saque();
			saque.setContaCorrente(this.getContaCorrente());
			saque.setDataMovimento(linha.getData());
			saque.setConta(linha.getContaContabil());
			saque.setHistorico(linha.getDescricao());
			saque.getParcelas().removeAll(saque.getParcelas());

			Parcela parcela = new Parcela();
			parcela.setDataVencimento(linha.getData());
			parcela.setValor(Util.parseCurrency(linha.getValor()));
			parcela.setEfetivado(false);
			saque.addParcela(parcela);
			this.scorecardManager.savePassivo(saque);
			saque = this.scorecardManager.getPassivoPorId(saque.getId());
		}
	}

	private void previousMonth() {
		this.moveMonth(-1);
	}

	private void nextMonth() {
		this.moveMonth(1);
	}

	/**
	 * Move forward or backward the current Month in focus
	 * 
	 * @param quantity The amount of month to move to backward or forward
	 */
	private void moveMonth(int quantity) {
		/**
		 * Look for the current month and year reference throughout the current Date And
		 * then, calculate according the quantity parameter the movement of the month
		 * and year (backward or forward)
		 */
		long anoMesReferencia = Util.extrairReferencia(this.periodoDataInicial);
		long anoMesReferenciaAnterior = Util.computeReferencia(anoMesReferencia, quantity);

		/**
		 * Compose the initial date of the target Month, Day 01
		 */
		Date dataInicioMes = Util.parseDate("01/" + Util.formatReferencia(anoMesReferenciaAnterior));
		/**
		 * Compose the final date of the target Month, Day 31, 30, 29 or 28
		 */
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInicioMes);
		Date dataFimMes = Util.parseDate(new DecimalFormat("00").format(cal.getActualMaximum(Calendar.DAY_OF_MONTH))
				+ "/" + Util.formatReferencia(anoMesReferenciaAnterior));
		/**
		 * Set up the initial and final date on the corresponding fields, and then Use
		 * the UpdateViewTask to change the GUI for the new state
		 */
		this.periodoDataInicial = dataInicioMes;
		this.periodoDataFinal = dataFimMes;
		UpdateViewTask task = new UpdateViewTask(this);
		task.execute();
	}

	private boolean isDesconsiderarOrcamento() {
		return this.btnDesconsiderarOrcamento.isSelected();
	}

	protected void doRemover(String actionCommand) {
		if (actionCommand.indexOf("CHEQUE") != -1) {
			Cheque cheque = getSelectedCheque();
			if (cheque != null) {
				String msg = "Excluir Cheque:\n" + cheque.getHistorico() + " - R$ "
						+ Util.formatCurrency(cheque.getValorTotal()) + " ?";
				int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp == 0) {
					new RemoverObjeto(this, actionCommand, cheque).execute();
				}
			}
		} else if (actionCommand.indexOf("CARTAO") != -1) {
			//AQUI
			String[] infos              = actionCommand.split("_");
			//String   action             = infos[1];
			String   idCartaoContratado = infos[2];
			CartaoContratado cartaoContratado = this.scorecardManager.getCartaoContratado(Integer.parseInt(idCartaoContratado));
			if (cartaoContratado == null) {
				throw new RuntimeException("Nao foi encontrado o objeto Cartao Contratado com o ID:" + idCartaoContratado);
			}
			Cartao cartao = this.getSelectedCartao(cartaoContratado);
			if (cartao != null) {
				String msg = "Excluir Cartão:\n" + cartao.getHistorico() + " - R$ "
						+ Util.formatCurrency(cartao.getValorTotal()) + " ?";
				int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp == 0) {
					new RemoverObjeto(this, actionCommand, cartao, cartaoContratado).execute();
				}
			}
		} else if (actionCommand.indexOf("DEBITO") != -1) {
			DebitoCC debitos = getSelectedDebito();
			if (debitos != null) {
				String msg = "Excluir Débito:\n" + debitos.getHistorico() + " - R$ "
						+ Util.formatCurrency(debitos.getValorTotal()) + " ?";
				int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp == 0) {
					new RemoverObjeto(this, actionCommand, debitos).execute();
				}
			}
		} else if (actionCommand.indexOf("ORCAMENTO") != -1) {
			Orcamento orcamento = getSelectedOrcamento();
			if (orcamento != null) {
				String msg = "Excluir Orçamento:\n" + orcamento.getContaAssociada().getNivel() + "-"
						+ orcamento.getContaAssociada().getDescricao() + ", " + orcamento.getDescricao() + " - R$ "
						+ Util.formatCurrency(orcamento.getOrcado()) + " ?";
				int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp == 0) {
					new RemoverObjeto(this, actionCommand, orcamento).execute();
				}
			}
		} else if (actionCommand.indexOf("INVESTIMENTO") != -1) {
			Investimento investimento = getSelectedInvestimento();
			if (investimento != null) {
				if (investimento.isOrigemTransferencia()) {
					JOptionPane.showMessageDialog(this,
							"Este Investimento pertence a uma Transferência.\n Para eliminá-lo somente através da exclusão da transferência !  ",
							"Atenção", JOptionPane.ERROR_MESSAGE);
				} else {
					String msg = "Excluir Investimento:\n" + investimento.getHistorico() + " - R$ "
							+ Util.formatCurrency(investimento.getValor()) + " ?";
					int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (resp == 0) {
						new RemoverObjeto(this, actionCommand, investimento).execute();
					}
				}
			}
		} else if (actionCommand.indexOf("DEPOSITO") != -1) {
			Deposito deposito = getSelectedDeposito();
			if (deposito != null) {
				if (deposito.isOrigemTransferencia()) {
					JOptionPane.showMessageDialog(this,
							"Este Depósito pertence a uma Transferência.\n Para eliminá-lo somente através da exclusão da transferência !  ",
							"Atenção", JOptionPane.ERROR_MESSAGE);
				} else {
					String msg = "Excluir Depósito:\n" + deposito.getHistorico() + " - R$ "
							+ Util.formatCurrency(deposito.getValor()) + " ?";
					int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (resp == 0) {
						new RemoverObjeto(this, actionCommand, deposito).execute();
					}
				}
			}
		} else if (actionCommand.indexOf("SALARIO") != -1) {
			Salario salario = getSelectedSalario();
			if (salario != null) {
				if (salario.isOrigemTransferencia()) {
					JOptionPane.showMessageDialog(this,
							"Este Salário pertence a uma Transferência.\n Para eliminá-lo somente através da exclusão da transferência !  ",
							"Atenção", JOptionPane.ERROR_MESSAGE);
				} else {
					String msg = "Excluir Estipêndio:\n" + salario.getHistorico() + " - R$ "
							+ Util.formatCurrency(salario.getValor()) + " ?";
					int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (resp == 0) {
						new RemoverObjeto(this, actionCommand, salario).execute();
					}
				}
			}
		} else if (actionCommand.indexOf("TRANSFERENCIA") != -1) {
			Transferencia transferencia = getSelectedTransferencia();
			if (transferencia != null) {
				String msg = "Excluir Transferência:\n" + transferencia.getHistorico() + " - R$ "
						+ Util.formatCurrency(transferencia.getValor()) + " ?";
				int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp == 0) {
					new RemoverObjeto(this, actionCommand, transferencia).execute();
				}
				/*
				 * this.updateViewTransferencia(); this.updateViewSalario();
				 * this.updateViewDeposito(); this.updateViewInvestimento();
				 * this.updateViewPeriodo();
				 */
			}
		} else if (actionCommand.indexOf("SAQUE") != -1) {
			Saque saque = getSelectedSaque();
			if (saque != null) {
				String msg = "Excluir Saque:\n" + saque.getHistorico() + " - R$ "
						+ Util.formatCurrency(saque.getValorTotal()) + " ?";
				int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (resp == 0) {
					new RemoverObjeto(this, actionCommand, saque).execute();
				}
			}
		}
	}

	protected void doVerificarExtrato(String actionCommand) {
		String file = "";
		DefaultModelTabela model = new DefaultModelTabela(null, null);
		JTable table = new JTable();
		CartaoCatalogo operadora = null;

//		if (actionCommand.indexOf("MASTERCARD") != -1) {
//			file  = ScorecardProperties.getProperty(ScorecardPropertyKeys.ArquivoFaturaMastercard);
//			model = this.tableModelMastercard;
//			table = this.tableMastercard;
//			operadora = Operadora.MASTERCARD;
//		} else
//		if (actionCommand.indexOf("VISA") != -1) { 
//			file  = ScorecardProperties.getProperty(ScorecardPropertyKeys.ArquivoFaturaVisa);
//			model = this.tableModelVisaCredito;
//			table = this.tableVisaCredito;
//			operadora = Operadora.VISA;
//		}

		VerificarExtratoCartao verificarExtratoCartao = new VerificarExtratoCartao(file);
		if (!verificarExtratoCartao.isExtratoValido()) {
			JOptionPane.showMessageDialog(this, "Extrato da Fatura do Cartão não foi encontrado \n\n" + file + "\n",
					"Atenção!", JOptionPane.WARNING_MESSAGE);
		} else {
			List<Passivo> registrosEmExcesso = new ArrayList<Passivo>();
			/**
			 * Verifica os registros que não estão presentes nos lançamentos da Fatura
			 */
			verificarExtratoCartao.resetVerificacaoValores();
			for (int i = 0; i < model.getRowCount(); i++) {
				String valor = (String) model.getValueAt(i, 1);
				if (verificarExtratoCartao.exist(valor)) {
					model.setValueAt(new Boolean(true), i, 7);
				} else {
					String dataVencimento = (String) model.getValueAt(i, 0);
					String historico = (String) model.getValueAt(i, 2);
					String parc = (String) model.getValueAt(i, 3);
					Boolean efetivado = (Boolean) model.getValueAt(i, 4);
					Integer id = (Integer) model.getValueAt(i, 5);

					Cartao cartao = new Cartao();
					cartao.setHistorico(historico);
					cartao.setOperadora(operadora);
					cartao.setId(id);

					Parcela parcela = new Parcela();
					parcela.setDataVencimento(dataVencimento);
					parcela.setCheque(false);
					parcela.setValor(Util.parseCurrency(valor));
					parcela.setNumero(Integer.parseInt(parc.substring(parc.indexOf("/") + 1)));
					parcela.setEfetivado(efetivado);
					cartao.addParcela(parcela);

					registrosEmExcesso.add(cartao);
				}
			}
			List<LinhaExtratoCartao> linhasNaoEncontradas = verificarExtratoCartao.getLinhasNaoConferidas();
			table.updateUI();

			UtilGUI.coverBlinder(this.getOwner());
			ResultadoConferenciaExtratoCartao resultadoConferenciaExtratoCartao = new ResultadoConferenciaExtratoCartao(
					operadora, this, scorecardManager, this.contaCorrente, verificarExtratoCartao.getValorTotal(),
					linhasNaoEncontradas, registrosEmExcesso);
			resultadoConferenciaExtratoCartao.setVisible(true);
			UtilGUI.uncoverBlinder(this.getOwner());
		}
	}

	protected void doConfirmar(String actionCommand) {
		new ConfirmarPassivo(this, actionCommand).execute();
	}

	public Cheque getSelectedCheque() {
		if (this.tableCheque.getSelectedRow() > -1) {
			int chequeId = ((Integer) this.tableModelCheque.getValueAt(this.tableCheque.getSelectedRow(), 6))
					.intValue();
			Cheque cheque = (Cheque) this.scorecardManager.getPassivoPorId(chequeId);
			return cheque;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro do Cheque", "Cheques",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	public Cartao getSelectedCartao(CartaoContratado cartaoContratado) {
		if ( this.tableCartoes.get(cartaoContratado).getSelectedRow() > -1 ) {
			JTable table = this.tableCartoes.get(cartaoContratado);
			int cartaoId = ((Integer)tableModelCartoes.get(cartaoContratado).getValueAt(table.getSelectedRow(), 5)).intValue();
			Cartao cartao = (Cartao)this.scorecardManager.getPassivoPorId(cartaoId);
			return cartao;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro do " + cartaoContratado.getCartao(),"Cartões",JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	public DebitoCC getSelectedDebito() {
		if (this.tableDebito.getSelectedRow() > -1) {
			int debitoId = ((Integer) this.tableModelDebito.getValueAt(this.tableDebito.getSelectedRow(), 5))
					.intValue();
			DebitoCC debitoCC = (DebitoCC) this.scorecardManager.getPassivoPorId(debitoId);
			return debitoCC;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Débito C/C", "Débito",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	private Orcamento getSelectedOrcamento() {
		if (this.tableOrcamento.getSelectedRow() > -1) {
			return getSelectedOrcamento(this.tableOrcamento.getSelectedRow());
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Orçamento", "Orçamento",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	private Orcamento getSelectedOrcamento(int row) {
		int orcamentoId = ((Integer) this.tableModelOrcamento.getValueAt(row, 5)).intValue();
		Orcamento orcamento = (Orcamento) this.scorecardManager.getOrcamentoPorId(orcamentoId);
		return orcamento;
	}

	private Investimento getSelectedInvestimento() {
		if (this.tableInvestimento.getSelectedRow() > -1) {
			int id = ((Integer) this.tableModelInvestimento.getValueAt(this.tableInvestimento.getSelectedRow(), 3))
					.intValue();
			Investimento investimento = (Investimento) this.scorecardManager.getAtivoPorId(id);
			return investimento;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Investimento", "Investimento",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	private Transferencia getSelectedTransferencia() {
		if (this.tableTransferencia.getSelectedRow() > -1) {
			int id = ((Integer) this.tableModelTransferencia.getValueAt(this.tableTransferencia.getSelectedRow(), 4))
					.intValue();
			Transferencia investimento = (Transferencia) this.scorecardManager.getTransferenciaPorId(id);
			return investimento;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Transferência", "Transferência",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	public Saque getSelectedSaque() {
		if (this.tableSaque.getSelectedRow() > -1) {
			int id = ((Integer) this.tableModelSaque.getValueAt(this.tableSaque.getSelectedRow(), 5)).intValue();
			Saque saque = (Saque) this.scorecardManager.getPassivoPorId(id);
			return saque;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Saque", "Saque", JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	private Deposito getSelectedDeposito() {
		if (this.tableDeposito.getSelectedRow() > -1) {
			int id = ((Integer) this.tableModelDeposito.getValueAt(this.tableDeposito.getSelectedRow(), 3)).intValue();
			Deposito investimento = (Deposito) this.scorecardManager.getAtivoPorId(id);
			return investimento;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Depósito", "Depósito",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	private Salario getSelectedSalario() {
		if (this.tableSalario.getSelectedRow() > -1) {
			int id = ((Integer) this.tableModelSalario.getValueAt(this.tableSalario.getSelectedRow(), 3)).intValue();
			Salario investimento = (Salario) this.scorecardManager.getAtivoPorId(id);
			return investimento;
		} else {
			JOptionPane.showMessageDialog(this, "Selecione o registro de Salário", "Salário",
					JOptionPane.WARNING_MESSAGE);
		}
		return null;
	}

	public int getChequeSelectedParcelaId() {
		int parcelaId = ((Integer) this.tableModelCheque.getValueAt(this.tableCheque.getSelectedRow(), 7)).intValue();
		return parcelaId;
	}

	public int getDebitoSelectedParcelaId() {
		int parcelaId = ((Integer) this.tableModelDebito.getValueAt(this.tableDebito.getSelectedRow(), 6)).intValue();
		return parcelaId;
	}

	public int getCartaoSelectedParcelaId(CartaoContratado cartaoContratado) {
		int selectedRow = this.tableCartoes.get(cartaoContratado).getSelectedRow();
		int parcelaId   = ((Integer)this.tableModelCartoes.get(cartaoContratado).getValueAt(selectedRow, 6)).intValue();
		return parcelaId;
	}
	
	public int getSaqueSelectedParcelaId() {
		int parcelaId = ((Integer) this.tableModelSaque.getValueAt(this.tableSaque.getSelectedRow(), 6)).intValue();
		return parcelaId;
	}

	protected void doInserir(String actionCommand) {
		if (actionCommand.indexOf("CHEQUE") != -1) {
			ChequeFrame chequeFrame = new ChequeFrame(this.owner, this.getContaCorrente(), this.periodoDataInicial);
			chequeFrame.setVisible(true);
		} else
		if (actionCommand.indexOf("DEBITO") != -1) {
			DebitoFrame visaElectronFrame = new DebitoFrame(this.owner, this.getContaCorrente(),
					this.periodoDataInicial);
			visaElectronFrame.setVisible(true);
		} else if (actionCommand.indexOf("ORCAMENTO") != -1) {
			OrcamentoFrame visaElectronFrame = new OrcamentoFrame(this.owner, this.getContaCorrente());
			visaElectronFrame.setVisible(true);
		} else if (actionCommand.indexOf("INVESTIMENTO") != -1) {
			InvestimentoFrame investimentoFrame = new InvestimentoFrame(this.owner, this.getContaCorrente(),
					this.periodoDataInicial);
			investimentoFrame.setVisible(true);
		} else if (actionCommand.indexOf("DEPOSITO") != -1) {
			DepositoFrame depositoFrame = new DepositoFrame(this.owner, this.getContaCorrente(),
					this.periodoDataInicial);
			depositoFrame.setVisible(true);
		} else if (actionCommand.indexOf("SALARIO") != -1) {
			SalarioFrame salarioFrame = new SalarioFrame(this.owner, this.getContaCorrente(), this.periodoDataInicial);
			salarioFrame.setVisible(true);
		} else if (actionCommand.indexOf("TRANSFERENCIA") != -1) {
			TransferenciaFrame investimentoFrame = new TransferenciaFrame(this.owner, this.getContaCorrente());
			investimentoFrame.setVisible(true);
		} else if (actionCommand.indexOf("SAQUE") != -1) {
			SaqueFrame saqueFrame = new SaqueFrame(this.owner, this.getContaCorrente());
			saqueFrame.setVisible(true);
		} else if (actionCommand.indexOf("CARTAO") != -1 ) {
			String[] infos              = actionCommand.split("_");
			String   action             = infos[1];
			String   idCartaoContratado = infos[2];
			CartaoContratado cartaoContratado = this.scorecardManager.getCartaoContratado(Integer.parseInt(idCartaoContratado));
			if (cartaoContratado == null) {
				throw new RuntimeException("Nao foi encontrado o Cartao Contratado com o ID:" + idCartaoContratado);
			}
			CartaoFrame cartaoFrame = new CartaoFrame(this.owner, this.getContaCorrente(), cartaoContratado, this.periodoDataInicial);
			cartaoFrame.setVisible(true);
		} else {
			System.out.println("NOTHING found in the options of doInserir Method for:\"" + actionCommand + "\"");
		}
	}

	public void mouseClicked(MouseEvent evt) {
	}

	public void mouseEntered(MouseEvent evt) {
	}

	public void mouseExited(MouseEvent evt) {
	}

	public void mousePressed(MouseEvent evt) {
	}

	private class UpdateViewTask extends SwingWorker<String, String> {

		private BankPanel bankPanel = null;
		private LoadingFrame loadingFrame = new LoadingFrame(12);

		public UpdateViewTask(BankPanel bankPanel) {
			this.bankPanel = bankPanel;
			UtilGUI.coverBlinder(this.bankPanel.getOwner());
		}

		@Override
		protected String doInBackground() throws Exception {
			try {
				loadingFrame.showLoadinFrame();
				bankPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				loadingFrame.setStatus("Carregando Cheques", 1);
				bankPanel.updateViewCheque();
				loadingFrame.setStatus("Carregando Débitos", 2);
				bankPanel.updateViewDebito();
				loadingFrame.setStatus("Carregando Depósitos", 3);
				bankPanel.updateViewDeposito();
				loadingFrame.setStatus("Carregando Investimentos", 4);
				bankPanel.updateViewInvestimento();
				loadingFrame.setStatus("Carregando Orçamento", 5);
				bankPanel.updateViewOrcamento();
				bankPanel.updateLabelOrcamento();
				bankPanel.updateLabelNaoOrcado();
				loadingFrame.setStatus("Carregando Salário", 6);
				bankPanel.updateViewSalario();
				loadingFrame.setStatus("Carregando Transferências", 7);
				bankPanel.updateViewTransferencia();
				loadingFrame.setStatus("Carregando Cartoes", 8);
				bankPanel.updateViewCartoes();
				loadingFrame.setStatus("Carregando Saques", 9);
				bankPanel.updateViewSaque();
				loadingFrame.setStatus("Carregando Resumo do Período", 10);
				bankPanel.updateViewPeriodo();
				loadingFrame.setMessage("Finalizado");
				return null;
			} catch (Throwable e) {
				logger.error(ExceptionUtils.getStackTrace(e));
				System.out.println(ExceptionUtils.getStackTrace(e));
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		@Override
		protected void done() {
			loadingFrame.dispose();
			bankPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			UtilGUI.uncoverBlinder(bankPanel.getOwner());
		}
	}

	public void mouseReleased(MouseEvent evt) {
		String nameComponent = ((Component) evt.getSource()).getName();
		if ("PERIODO".equalsIgnoreCase(nameComponent)) {
			PeriodoFrame periodoResumoFrame = new PeriodoFrame(this.owner, this.periodoDataInicial,
					this.periodoDataFinal);
			periodoResumoFrame.setVisible(true);
			if (!periodoResumoFrame.isCanceled()) {
				this.periodoDataInicial = periodoResumoFrame.getPeriodoDataInicial();
				this.periodoDataFinal = periodoResumoFrame.getPeriodoDataFinal();
				UpdateViewTask task = new UpdateViewTask(this);
				task.execute();
			}
		} else if ("TABLE_CHEQUES".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				Cheque cheque = this.getSelectedCheque();
				if (cheque != null) {
					ChequeFrame chequeFrame = new ChequeFrame(this.owner, this.getContaCorrente(),
							this.periodoDataInicial, cheque);
					chequeFrame.setVisible(true);
				}
			}
		} else
		if ( nameComponent.indexOf("TABLE_CARTAO") > -1 ) {
			if ( evt.getClickCount() == 2 ) {
				String           idCartaoContratado = nameComponent.split("_")[2];
				CartaoContratado cartaoContratado   = this.scorecardManager.getCartaoContratado(Integer.parseInt(idCartaoContratado));
				Cartao           cartao             = this.getSelectedCartao(cartaoContratado);
				if ( cartao != null ) {
					CartaoFrame cartaoFrame = new CartaoFrame(this.owner, this.getContaCorrente(), cartaoContratado, this.periodoDataInicial, cartao);
					cartaoFrame.setVisible(true);
				}
			}
		} else
		if ("TABLE_DEBITO".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				DebitoCC debito = this.getSelectedDebito();
				if (debito != null) {
					DebitoFrame cartaoFrame = new DebitoFrame(this.owner, this.getContaCorrente(),
							this.periodoDataInicial, debito);
					cartaoFrame.setVisible(true);
				}
			}
		} else if ("TABLE_ORCAMENTO".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				this.openOrcamento();
			}
		} else if ("TABLE_SALARIO".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				Salario salario = this.getSelectedSalario();
				if (salario != null) {
					SalarioFrame salarioFrame = new SalarioFrame(this.owner, this.getContaCorrente(),
							this.periodoDataInicial, salario);
					salarioFrame.setVisible(true);
				}
			}
		} else if ("TABLE_DEPOSITO".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				Deposito deposito = this.getSelectedDeposito();
				if (deposito != null) {
					DepositoFrame depositoFrame = new DepositoFrame(this.owner, this.getContaCorrente(),
							this.periodoDataInicial, deposito);
					depositoFrame.setVisible(true);
				}
			}
		} else if ("TABLE_INVESTIMENTO".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				Investimento investimento = this.getSelectedInvestimento();
				if (investimento != null) {
					InvestimentoFrame investimentoFrame = new InvestimentoFrame(this.owner, this.getContaCorrente(),
							this.periodoDataInicial, investimento);
					investimentoFrame.setVisible(true);
				}
			}
		} else if ("TABLE_TRANSFERENCIA".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				Transferencia transferencia = this.getSelectedTransferencia();
				if (transferencia != null) {
					TransferenciaFrame investimentoFrame = new TransferenciaFrame(this.owner, this.getContaCorrente(),
							transferencia);
					investimentoFrame.setVisible(true);
				}
			}
		} else if ("TABLE_SAQUE".equalsIgnoreCase(nameComponent)) {
			if (evt.getClickCount() == 2) {
				Saque saque = this.getSelectedSaque();
				if (saque != null) {
					SaqueFrame saqueFrame = new SaqueFrame(this.owner, this.getContaCorrente(), saque);
					saqueFrame.setVisible(true);
				}
			}
		}
		
		if ( evt.getComponent() instanceof JTabbedPane ) {
			JTabbedPane tabbed = (JTabbedPane)evt.getComponent();
			
			
			if ( "Orçamentos".equalsIgnoreCase(tabbed.getTitleAt(tabbed.getSelectedIndex()))) {
				updateLabelOrcamento();
				updateLabelNaoOrcado();
			}
			
			Integer currentIndexTab  = tabbed.getSelectedIndex();
			Integer previousIndexTab = 0;
			
			Object previousTab = tabbed.getClientProperty("PREVIOUS_TAB");
			if ( previousTab instanceof Integer ) {
				previousIndexTab = (Integer)previousTab;
			}
			
			tabbed.putClientProperty("PREVIOUS_TAB", currentIndexTab);
			
			if ( currentIndexTab != previousIndexTab ) {
				if (this.tabIconsSelected.get(currentIndexTab + 1) != null) {
					tabbed.setIconAt(currentIndexTab, this.tabIconsSelected.get(currentIndexTab + 1));
				}
				if (this.tabIconsUnselected.get(previousIndexTab + 1) != null) {
					tabbed.setIconAt(previousIndexTab, this.tabIconsUnselected.get(previousIndexTab + 1));
				}
			}
		}
	}

	private void updateLabelOrcamento() {
		String vlrLabelTotal = calculateOrcamentoLabelResumo();
		this.lblOrcamentoTotal.setText(vlrLabelTotal);
	}
	
	private void updateLabelNaoOrcado() {
		String vlrLabelTotal = calculateLabelNaoOrcado();
		this.lblNaoOrcadoTotal.setText(vlrLabelTotal);
	}
	
	private void openOrcamento() {
		Orcamento orcamento = this.getSelectedOrcamento();
		if (orcamento != null) {
			OrcamentoFrame orcamentoFrame = new OrcamentoFrame(this.owner, this.getContaCorrente(), orcamento);
			orcamentoFrame.setVisible(true);
		}
	}

	public void actionOrcamento(Orcamento orcamento) {
		this.updateViewOrcamento();
		String vlrLabelTotal = calculateOrcamentoLabelResumo();
		lblOrcamentoTotal.setText(vlrLabelTotal);
		this.updateResumoPeriodo();
	}

	public void actionPassivo(Passivo passivo) {
		if (passivo instanceof Cheque) {
			updateViewCheque();
		} else if (passivo instanceof Cartao) {
			Cartao cartao = (Cartao) passivo;
			if (cartao.getContaCorrente().equals(this.getContaCorrente())) { 
				this.updateViewCartao(cartao.getCartaoContratado());
			}
		} else if (passivo instanceof DebitoCC) {
			this.updateViewDebito();
		} else if (passivo instanceof Saque) {
			this.updateViewSaque();
		}
		this.updateViewOrcamento();
		this.updateResumoPeriodo();
	}

	public void updateViewOrcamento() {
		this.loadOrcamento();
		this.tableOrcamento.setModel(this.tableModelOrcamento);
		this.tableNaoOrcado.setModel(this.tableModelNaoOrcado);
		this.layOutTableOrcamento();
		this.layOutTableNaoOrcado();
		tableOrcamento.removeColumn(tableOrcamento.getColumnModel().getColumn(5));
	}

	public void updateViewCheque() {
		if ( this.getContaCorrente().isCheque() ) {
			this.loadCheques();
			this.tableCheque.setModel(this.tableModelCheque);
			this.layOutTableCheque();
		}
	}

	public void  updateViewCartoes() {
		this.loadCartoes();
		List<CartaoContratado> cartoesContratados = getCartoesContratados();
		for (CartaoContratado cartaoContratado : cartoesContratados) {
			this.tableCartoes.get(cartaoContratado).setModel(this.tableModelCartoes.get(cartaoContratado));
			this.layOutTableCartaoCredito(this.tableCartoes.get(cartaoContratado));
		}
	}
	
	public void updateViewCartao(CartaoContratado cartaoContratado) {
		if ( this.getContaCorrente().equals(cartaoContratado.getContaCorrente()) ) {
			this.loadCartoes(cartaoContratado.getContaCorrente(), cartaoContratado);
			this.tableCartoes.get(cartaoContratado).setModel(this.tableModelCartoes.get(cartaoContratado));
			this.layOutTableCartaoCredito(this.tableCartoes.get(cartaoContratado));
		}
	}
	
	public void updateViewDebito() {
		this.loadDebito();
		this.tableDebito.setModel(this.tableModelDebito);
		this.layOutTable(this.tableDebito);
	}

	public void updateViewDeposito() {
		this.loadDeposito();
		this.tableDeposito.setModel(this.tableModelDeposito);
		this.layOutTableAtivo(this.tableDeposito);
	}

	public void updateViewInvestimento() {
		this.loadInvestimento();
		this.tableInvestimento.setModel(this.tableModelInvestimento);
		this.layOutTableAtivo(this.tableInvestimento);
	}

	public void updateViewSalario() {
		this.loadSalario();
		this.tableSalario.setModel(this.tableModelSalario);
		this.layOutTableAtivo(this.tableSalario);
	}

	public void updateViewSaque() {
		this.loadSaque();
		this.tableSaque.setModel(this.tableModelSaque);
		this.layOutTable(this.tableSaque);
	}

	public void updateViewTransferencia() {
		this.loadTransferencia();
		this.tableTransferencia.setModel(this.tableModelTransferencia);
		this.layOutTableTransferencia();
	}

	public void updateViewPeriodo() {
		this.updateViewOrcamento();
		this.updateResumoPeriodo();

		String lblPeriodoResumoText = this.updateLabelResumo();
		this.lblPeriodoResumo.setText(lblPeriodoResumoText);
	}

	public String updateLabelResumo() {
		String lblPeriodoResumoText = null;
		Calendar today = Calendar.getInstance();
		Calendar inicio = Calendar.getInstance();
		Calendar fim = Calendar.getInstance();
		inicio.setTime(periodoDataInicial);
		fim.setTime(periodoDataFinal);

		if ((inicio.get(Calendar.MONTH) == fim.get(Calendar.MONTH)) && (inicio.get(Calendar.DAY_OF_MONTH) == 1
				&& (fim.get(Calendar.DAY_OF_MONTH) == 30 || fim.get(Calendar.DAY_OF_MONTH) == 31
						|| ((fim.get(Calendar.DAY_OF_MONTH) == 29 || fim.get(Calendar.DAY_OF_MONTH) == 28)
								&& inicio.get(Calendar.MONTH) == 1)))) {
			String meses[] = new String[] { "J A N E I R O", "F E V E R E I R O", "M A R Ç O", "A B R I L", "M A I O",
					"J U N H O", "J U L H O", "A G O S T O", "S E T E M B R O", "O U T U B R O", "N O V E M B R O",
					"D E Z E M B R O" };

			if (inicio.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
				lblPeriodoResumoText = meses[inicio.get(Calendar.MONTH)] + "    -    " + inicio.get(Calendar.YEAR);
			} else {
				lblPeriodoResumoText = meses[inicio.get(Calendar.MONTH)];
			}
		} else {
			lblPeriodoResumoText = new StringBuffer(Util.formatDate(this.periodoDataInicial))
					.append(" \u0020\u0020 à \u0020\u0020 ").append(Util.formatDate(this.periodoDataFinal)).toString();
		}
		return lblPeriodoResumoText;
	}

	private void loadCheques() {
		if (this.getContaCorrente().isCheque()) {
			this.tableModelCheque = new DefaultModelTabela(null,
					new Object[] { "Data", "Valor", "Descrição", "Parc.", "C/C", "Cheque", "ID", "ID_PARCELA" });
			Set<Passivo> setCheques = this.scorecardManager.getEspecificoPassivoPorReferencia(this.getContaCorrente(),
					Cheque.class, this.periodoDataInicial, this.periodoDataFinal);
			List<Passivo> cheques = new ArrayList<Passivo>(setCheques);
			List<Parcela> parcelasCheques = new ArrayList<Parcela>();
			Collections.sort(parcelasCheques, ParcelaOrdenador.DATA_LANCAMENTO);
			for (Passivo passivo : cheques) {
				Cheque cheque = (Cheque) passivo;
				parcelasCheques.addAll(cheque.getParcelas());
			}
			this.scorecardManager.ordenarParcelas(parcelasCheques, ParcelaOrdenador.DATA_LANCAMENTO);
			Object row[];
			for (Parcela parcela : parcelasCheques) {
				row = new Object[] { Util.formatDate(parcela.getPassivo().getDataMovimento()),
						Util.formatCurrency(parcela.getValor()), parcela.getPassivo().getHistorico(),
						parcela.getLabelNumeroTotalParcelas(), parcela.isEfetivado(), parcela.getNumeroCheque(),
						parcela.getPassivo().getId(), parcela.getId() };
				this.tableModelCheque.addRow(row);
			}
		}
	}

	/**
	 * Dados dos cartoes que a conta corrente do Banco possui associado
	 */
	private void loadCartoes() {
		this.loadCartoes(this.getContaCorrente(), null);
	}
	private void loadCartoes(ContaCorrente contaCorrente, CartaoContratado cartaoContratadoCarregar) {
		if ( cartaoContratadoCarregar == null ) {
			// Carregar Todos
			this.tableModelCartoes = new HashMap<CartaoContratado, DefaultModelTabela>();
			List<CartaoContratado> cartoesContratados = getCartoesContratados();
			for (CartaoContratado cartaoContratado : cartoesContratados) {
				loadCartaoTableModel(contaCorrente, cartaoContratado);
			}
		} else {
			this.tableModelCartoes.remove(cartaoContratadoCarregar);
			loadCartaoTableModel(contaCorrente, cartaoContratadoCarregar);
		}
	}

	private void loadCartaoTableModel(ContaCorrente contaCorrente, CartaoContratado cartaoContratado) {
		DefaultModelTabela defaultModelTabela = new DefaultModelTabela(null,
				new Object[] { "Data", "Valor", "Descrição", "Parc.", "C/C", "ID", "ID_PARCELA", "SELECTED" });
		Set<Cartao> setCartoes = this.scorecardManager.getCartaoPorOperadora(contaCorrente,
				cartaoContratado.getCartaoOperadora(), this.periodoDataInicial, this.periodoDataFinal);
		List<Cartao> cartoes = new ArrayList<Cartao>(setCartoes);
		List<Parcela> parcelasCartao = new ArrayList<Parcela>();
		Collections.sort(parcelasCartao, ParcelaOrdenador.DATA_LANCAMENTO);
		for (Passivo passivo : cartoes) {
			Cartao cartao = (Cartao) passivo;
			parcelasCartao.addAll(cartao.getParcelas());
		}
		this.scorecardManager.ordenarParcelas(parcelasCartao, ParcelaOrdenador.DATA_LANCAMENTO);
		Object row[];
		for (Parcela parcela : parcelasCartao) {
			row = new Object[] { 
					Util.formatDate(parcela.getPassivo().getDataMovimento()),
					Util.formatCurrency(parcela.getValor()), 
					parcela.getPassivo().getHistorico(),
					parcela.getLabelNumeroTotalParcelas(),
					parcela.isEfetivado(),
					parcela.getPassivo().getId(),
					parcela.getId(),
					false 
			};
			defaultModelTabela.addRow(row);
		}
		this.tableModelCartoes.put(cartaoContratado, defaultModelTabela);
	}

	private List<CartaoContratado> getCartoesContratados() {
		if ( this.cartoesContratados == null ) {
			this.cartoesContratados = this.scorecardManager.getCartoesContaCorrente(this.contaCorrente);
		}
		return this.cartoesContratados;
	}

	private void loadDebito() {
		this.tableModelDebito = new DefaultModelTabela(null,
				new Object[] { "Data", "Valor", "Descrição", "Parc.", "C/C", "ID", "ID_PARCELA" });
		Set<Passivo> setDebitos = this.scorecardManager.getEspecificoPassivoPorReferencia(this.getContaCorrente(),
				DebitoCC.class, this.periodoDataInicial, this.periodoDataFinal);
		List<Passivo> debitos = new ArrayList<Passivo>(setDebitos);
		List<Parcela> parcelasDebito = new ArrayList<Parcela>();
		Collections.sort(parcelasDebito, ParcelaOrdenador.DATA_LANCAMENTO);
		for (Passivo passivo : debitos) {
			DebitoCC debito = (DebitoCC) passivo;
			parcelasDebito.addAll(debito.getParcelas());
		}
		this.scorecardManager.ordenarParcelas(parcelasDebito, ParcelaOrdenador.DATA_LANCAMENTO);
		Object row[];
		for (Parcela parcela : parcelasDebito) {
			row = new Object[] { Util.formatDate(parcela.getPassivo().getDataMovimento()),
					Util.formatCurrency(parcela.getValor()), parcela.getPassivo().getHistorico(),
					parcela.getLabelNumeroTotalParcelas(), parcela.isEfetivado(), parcela.getPassivo().getId(),
					parcela.getId() };
			this.tableModelDebito.addRow(row);
		}
	}

	private void loadSaque() {
		this.tableModelSaque = new DefaultModelTabela(null,
				new Object[] { "Data", "Valor", "Descrição", "Parc.", "C/C", "ID", "ID_PARCELA" });
		Set<Passivo> setSaques = this.scorecardManager.getEspecificoPassivoPorReferencia(this.getContaCorrente(),
				Saque.class, this.periodoDataInicial, this.periodoDataFinal);
		List<Passivo> debitos = new ArrayList<Passivo>(setSaques);
		List<Parcela> parcelasSaque = new ArrayList<Parcela>();
		Collections.sort(parcelasSaque, ParcelaOrdenador.DATA_LANCAMENTO);
		for (Passivo passivo : debitos) {
			Saque saque = (Saque) passivo;
			parcelasSaque.addAll(saque.getParcelas());
		}
		this.scorecardManager.ordenarParcelas(parcelasSaque, ParcelaOrdenador.DATA_LANCAMENTO);
		Object row[];
		for (Parcela parcela : parcelasSaque) {
			row = new Object[] { Util.formatDate(parcela.getPassivo().getDataMovimento()),
					Util.formatCurrency(parcela.getValor()), parcela.getPassivo().getHistorico(),
					parcela.getLabelNumeroTotalParcelas(), parcela.isEfetivado(), parcela.getPassivo().getId(),
					parcela.getId() };
			this.tableModelSaque.addRow(row);
		}
	}

	private void loadOrcamento() {
		this.tableModelOrcamento = new DefaultModelTabela(null,
				new Object[] { "Ref.", "Orçado", "Realizado", "Restante", "Conta", "ID", "SELECTED" });
		
		List<PassivosForaOrcamento> listPassivosForaOrcamento = new ArrayList<PassivosForaOrcamento>();
		Set<Orcamento>              setOrcamentos             = this.scorecardManager.getOrcamentosPorReferencia(this.getContaCorrente(), this.periodoDataInicial, this.periodoDataFinal, listPassivosForaOrcamento);
		List<Orcamento>             orcamentos                = new ArrayList<Orcamento>(setOrcamentos);
		
		Object row[];
		valorTotalOrcamento = 0;
		valorTotalRealizado = 0;
		for (Orcamento orcamento : orcamentos) {
			row = new Object[] { orcamento.getMesAno(), Util.formatCurrency(orcamento.getOrcado()),
					Util.formatCurrency(orcamento.getRealizado()),
					Util.formatCurrency(orcamento.getOrcado().subtract(orcamento.getRealizado())),
					" " + orcamento.getContaAssociada().getNivel() + " " + orcamento.getContaAssociada().getDescricao(),
					orcamento.getId(), false };
			this.tableModelOrcamento.addRow(row);
			valorTotalOrcamento += orcamento.getOrcado().floatValue();
			valorTotalRealizado += orcamento.getRealizado().floatValue();
		}
		
		this.tableModelNaoOrcado = new DefaultModelTabela(null,
				new Object[]{"Vencimento","Valor","Historico","Parcela","Efetivado","C.Contábil"});
		
		LocalDate localDateInicial = this.periodoDataInicial.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDateFinal   = this.periodoDataFinal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		this.valorTotalNaoOrcado = 0;
		
		for(PassivosForaOrcamento p : listPassivosForaOrcamento) {
			
			Conta conta = this.scorecardManager.getContasPorNivel(p.getContaContabil()).get(0);
			String rowGroupLabel = p.getContaContabil() + " - " +  conta.getDescricao();
			row = new Object[] {
				"",Util.formatCurrency(p.getTotal()),rowGroupLabel,"",false,""	
			};
			this.tableModelNaoOrcado.addRow(row);
			
			for(Passivo pass : p.getPassivos()) {
				for(Parcela parcela : pass.getParcelas()) {
					
					LocalDate localDateParcela = new java.util.Date(parcela.getDataVencimento().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					
					if ( (localDateParcela.isEqual(localDateInicial) || localDateParcela.isAfter(localDateInicial) )
						&& 
						 (localDateParcela.isEqual(localDateFinal) || localDateParcela.isBefore(localDateFinal) )
					   ) {
						
						row = new Object[] {
							Util.formatDate(parcela.getDataVencimento()),
							Util.formatCurrency(parcela.getValor()),
							pass.getHistorico(),
							parcela.getNumero() + "/" + parcela.getNumeroParcelas(),
							parcela.isEfetivado(),
							pass.getConta().getNivel()	
						};
						this.tableModelNaoOrcado.addRow(row);
						
						this.valorTotalNaoOrcado += parcela.getValor().floatValue();
					}
				}
			}
			
		}
	}

	private void loadInvestimento() {
		this.tableModelInvestimento = new DefaultModelTabela(null, new Object[] { "Ref.", "Valor", "Histórico", "ID" });
		List<Ativo> ativos = this.scorecardManager.getAtivosPorReferencia(this.getContaCorrente(), Investimento.class,
				this.periodoDataInicial, this.periodoDataFinal);
		Object row[];
		for (Ativo ativo : ativos) {
			row = new Object[] { ativo.getMesAno(), Util.formatCurrency(ativo.getValor()), ativo.getHistorico(),
					ativo.getId() };
			this.tableModelInvestimento.addRow(row);
		}
	}

	private void loadDeposito() {
		this.tableModelDeposito = new DefaultModelTabela(null, new Object[] { "Ref.", "Valor", "Histórico", "ID" });
		List<Ativo> ativos = this.scorecardManager.getAtivosPorReferencia(this.getContaCorrente(), Deposito.class,
				this.periodoDataInicial, this.periodoDataFinal);
		Object row[];
		for (Ativo ativo : ativos) {
			row = new Object[] { ativo.getMesAno(), Util.formatCurrency(ativo.getValor()), ativo.getHistorico(),
					ativo.getId() };
			this.tableModelDeposito.addRow(row);
		}
	}

	private void loadSalario() {
		this.tableModelSalario = new DefaultModelTabela(null, new Object[] { "Ref.", "Valor", "Histórico", "ID" });
		List<Ativo> ativos = this.scorecardManager.getAtivosPorReferencia(this.getContaCorrente(), Salario.class,
				this.periodoDataInicial, this.periodoDataFinal);
		Object row[];
		for (Ativo ativo : ativos) {
			row = new Object[] { ativo.getMesAno(), Util.formatCurrency(ativo.getValor()), ativo.getHistorico(),
					ativo.getId() };
			this.tableModelSalario.addRow(row);
		}
	}

	private void loadTransferencia() {
		this.tableModelTransferencia = new DefaultModelTabela(null,
				new Object[] { "Ref.", "Valor", "Destino", "Histórico", "ID" });
		List<Transferencia> transfs = this.scorecardManager.getTransferenciasPorReferencia(this.getContaCorrente(),
				this.periodoDataInicial, this.periodoDataFinal);
		Object row[];
		for (Transferencia transferencia : transfs) {
			row = new Object[] { transferencia.getMesAno(), Util.formatCurrency(transferencia.getValor()),
					transferencia.getDestino(), transferencia.getHistorico(), transferencia.getId() };
			this.tableModelTransferencia.addRow(row);
		}
	}

	private void updateResumoPeriodo() {
		ResumoPeriodo resumoPeriodo = getResumoPeriodo();

		int row = 0;
		String margin = "\u0020\u0020\u0020";

		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaldoAnterior()) + margin, row, 1);
		
		if ( this.getContaCorrente().isCheque() ) {
			row++;
			this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getCheques())   + margin, row, 1);
		}
		
		List<ResumoPeriodoTotalCartao> listTotalCartoes = resumoPeriodo.getCartoes();
		for (ResumoPeriodoTotalCartao resumoPeriodoTotalCartao : listTotalCartoes) {
			row++;
			this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodoTotalCartao.getTotal()) + margin, row, 1);
		}
		
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaques())         + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getDebitosCC())      + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getDespesas())       + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getDepositos())      + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getInvestimentos())  + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getTransferencias()) + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSalario())        + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaldoPrevisto())  + margin, row, 1);
		row++;
		this.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaldoReal())      + margin, row, 1);
	}

	protected ContaCorrente getContaCorrente() {
		return this.contaCorrente;
	}

	public void actionAtivo(Ativo ativo) {
		if (ativo instanceof Deposito) {
			this.updateViewDeposito();
		} else if (ativo instanceof Salario) {
			this.updateViewSalario();
		} else if (ativo instanceof Investimento) {
			this.updateViewInvestimento();
		}
		this.updateResumoPeriodo();
	}

	public void actionTransferencia(Transferencia transferencia) {
		this.updateViewTransferencia();
		this.actionAtivo(transferencia.getAtivoTransferido());
	}

	public void actionConsistirSaldosAnteriores(SaldoProcessadoEvent event) {
	}

	public JFrame getOwner() {
		return owner;
	}

	public void setOwner(JFrame owner) {
		this.owner = owner;
	}

	public ScorecardGUI getScorecardGUI() {
		return (ScorecardGUI) this.owner;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if ( e.getSource() instanceof JTable ) {
			JTable t             = (JTable)e.getSource();
			String nameComponent = t.getName();
			if ( e.getKeyCode() == 32 ) {
				int rows[] = t.getSelectedRows();
				for (int i : rows) {
					Boolean selectedTemp = (Boolean)t.getModel().getValueAt(i,7);
					t.getModel().setValueAt(new Boolean(!selectedTemp),i,7);
				}
				t.updateUI();
			} else
			if ( e.getKeyCode() == 127 ) {
				String           idCartaoContratado = nameComponent.split("_")[2];
				CartaoContratado cartaoContratado   = this.scorecardManager.getCartaoContratado(Integer.parseInt(idCartaoContratado));
				Cartao           cartao             = this.getSelectedCartao(cartaoContratado);
				if (cartao != null) {
					String msg = "Excluir Cartão:\n" + cartao.getHistorico() + " - R$ "
							+ Util.formatCurrency(cartao.getValorTotal()) + " ?";
					int resp = JOptionPane.showConfirmDialog(this, msg, "Exclusão", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (resp == 0) {
						String actionCommand = "CARTAO_EXCLUIR_" + cartaoContratado.getId();
						new RemoverObjeto(this, actionCommand, cartao, cartaoContratado).execute();
					}
				}
				t.updateUI();
			}
		}
		
	}
	
}

