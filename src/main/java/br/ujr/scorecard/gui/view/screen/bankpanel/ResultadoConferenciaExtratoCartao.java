package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.components.gui.tabela.SortButtonRenderer;
import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.cellrenderer.EfetivadoTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.MonetarioTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.ParcelaTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.passivo.MastercardFrame;
import br.ujr.scorecard.gui.view.screen.passivo.VisaCreditoFrame;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.extrato.LinhaExtratoCartao;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.Util;

public class ResultadoConferenciaExtratoCartao extends JDialog implements KeyListener, MouseListener, WindowFocusListener, ActionListener {
	
	private static final long serialVersionUID = 7163781301991259862L;
	private int F_WIDTH = 800;
	private int F_HEIGHT = 355;
	
	private DefaultModelTabela tableModelLancamentosFaltando;
	private JTable tableLancamentosFaltando;
	
	private DefaultModelTabela tableModelLancamentosRestando;
	private JTable tableLancamentosRestando;
	
	private JPopupMenu popupMenuFaltantes;
	private JPopupMenu popupMenuRestantes;
	private BankPanel bankPanel;
	
	private int selectedRowtableLancamentosFaltando = -1;
	private int selectedRowtableLancamentosRestando = -1;
	private Cartao.Operadora operadora;
	
	private static Logger logger = Logger.getLogger("br.ujr.scorecard");
	
	@SuppressWarnings("serial")
	public ResultadoConferenciaExtratoCartao(Cartao.Operadora operadora, BankPanel bankPanel, ScorecardBusinessDelegate scorecard, ContaCorrente contaCorrente,
			String valorTotal, List<LinhaExtratoCartao> linhasFaltando, List<Passivo> passivosRestando) {
		
		super(bankPanel.getOwner(),true);
		
		this.bankPanel = bankPanel;
		this.operadora = operadora;
		
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = ((screenSize.width-F_WIDTH)/2)   + 130;
		int y = ((screenSize.height-F_HEIGHT)/2) + 30;
        this.setBounds(x, y, F_WIDTH, F_HEIGHT);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panMain = new JPanel();
		panMain.setLayout(null);
		panMain.setBackground(Color.GRAY);
		panMain.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		JScrollPane panelFaltantes = setupTableFaltando(panMain, linhasFaltando);
		JScrollPane panelRestantes = setupTableRestando(panMain, passivosRestando);

		JLabel lblLabelFaltante = new JLabel("Não Encontrados:");
		Util.setToolTip(this, lblLabelFaltante, "Lançamentos na Fatura do Cartão que não estão presentes nos registros");
		lblLabelFaltante.setForeground(Color.WHITE);
		lblLabelFaltante.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelFaltante.setBounds(10,25, 400,20);
		
		JLabel lblLabelRestante = new JLabel("Excedentes:");
		Util.setToolTip(this, lblLabelRestante, "Registros que não estão presentes na Fatura do Cartão");
		lblLabelRestante.setForeground(Color.WHITE);
		lblLabelRestante.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelRestante.setBounds(10,185, 400,20);
		
		panelFaltantes.setBounds(10, 45, F_WIDTH - 20, 140);
		panelRestantes.setBounds(10, 205, F_WIDTH - 20, 140);
		
		this.popupMenuFaltantes = new JPopupMenu();
		JLabel lblPopupFaltantes = new JLabel("  OPÇÕES:                        ");
		lblPopupFaltantes.setFont(new Font("Verdana",Font.BOLD,10));
		lblPopupFaltantes.setHorizontalAlignment(SwingConstants.CENTER);
		JMenuItem itemPopupFaltantes = new JMenuItem("Criar Passivo...");
		itemPopupFaltantes.setFont(new Font("Verdana",Font.BOLD,10));
		itemPopupFaltantes.setActionCommand("POPUP_CRIAR_PASSIVO");
		itemPopupFaltantes.addActionListener(this);
		this.popupMenuFaltantes.add(lblPopupFaltantes);
		this.popupMenuFaltantes.addSeparator();
		this.popupMenuFaltantes.add(itemPopupFaltantes);
		this.tableLancamentosFaltando.addMouseListener(new PopupMouseListenerFaltantes(this));
		
		this.popupMenuRestantes = new JPopupMenu();
		JLabel lblPopupRestantes = new JLabel("  OPÇÕES:                        ");
		lblPopupRestantes.setFont(new Font("Verdana",Font.BOLD,10));
		lblPopupRestantes.setHorizontalAlignment(SwingConstants.CENTER);
		JMenuItem itemPopupRestantesAbrir = new JMenuItem("Editar passivo...");
		itemPopupRestantesAbrir.setFont(new Font("Verdana",Font.BOLD,10));
		itemPopupRestantesAbrir.setActionCommand("POPUP_ABRIR_PASSIVO");
		itemPopupRestantesAbrir.addActionListener(this);
		JMenuItem itemPopupRestantes = new JMenuItem("Adiar Passivo para próximo mês...");
		itemPopupRestantes.setFont(new Font("Verdana",Font.BOLD,10));
		itemPopupRestantes.setActionCommand("POPUP_ADIAR_PASSIVO");
		itemPopupRestantes.addActionListener(this);
		this.popupMenuRestantes.add(lblPopupRestantes);
		this.popupMenuRestantes.addSeparator();
		this.popupMenuRestantes.add(itemPopupRestantesAbrir);
		this.popupMenuRestantes.add(itemPopupRestantes);
		this.tableLancamentosRestando.addMouseListener(new PopupMouseListenerRestantes(this));
		
		this.setResizable(false);
		this.setUndecorated(true);
		this.addWindowFocusListener(this);
		this.addKeyListener(this);
		panMain.addKeyListener(this);
		panMain.addMouseListener(this);
		
		JPanel panTotais = buildLabelTitulo(valorTotal, F_WIDTH, 24);
		panMain.add(panTotais);
		panMain.add(lblLabelFaltante);
		panMain.add(lblLabelRestante);
		panMain.add(panelFaltantes);
		panMain.add(panelRestantes);
		this.getContentPane().add(panMain);
		
		tableLancamentosFaltando.requestFocusInWindow();
	}
	
	private class PopupMouseListenerFaltantes extends MouseAdapter {
		private ResultadoConferenciaExtratoCartao parent;
		public PopupMouseListenerFaltantes(ResultadoConferenciaExtratoCartao parent){
			this.parent = parent;
		}
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
		private void maybeShowPopup(MouseEvent e) {
			if ( e.isPopupTrigger() ) {
				int row = this.parent.tableLancamentosFaltando.rowAtPoint(e.getPoint());
				this.parent.tableLancamentosFaltando.setRowSelectionInterval(row, row);
				this.parent.selectedRowtableLancamentosFaltando = row;
				popupMenuFaltantes.show(this.parent.tableLancamentosFaltando,e.getX(),e.getY());
			}
		}
	}
	
	private class PopupMouseListenerRestantes extends MouseAdapter {
		private ResultadoConferenciaExtratoCartao parent;
		public PopupMouseListenerRestantes(ResultadoConferenciaExtratoCartao parent){
			this.parent = parent;
		}
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}
		private void maybeShowPopup(MouseEvent e) {
			if ( e.isPopupTrigger() ) {
				int row = this.parent.tableLancamentosRestando.rowAtPoint(e.getPoint());
				this.parent.tableLancamentosRestando.setRowSelectionInterval(row, row);
				this.parent.selectedRowtableLancamentosRestando = row;
				popupMenuRestantes.show(this.parent.tableLancamentosRestando,e.getX(),e.getY());
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class LocalCellRenderer extends DefaultTableCellRenderer {
		private int align = SwingConstants.LEFT;
		
		public LocalCellRenderer() {
		}
		
		public LocalCellRenderer(int align) {
			this.align = align;
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			this.setHorizontalAlignment(this.align);
			this.setValue(value);
			if ( isSelected ) {
				Font font  = new Font("Courier New",Font.BOLD,11);
				this.setFont(font);
				this.setForeground(table.getSelectionForeground());
				this.setBackground(table.getSelectionBackground());
			} else {
				Font font  = new Font("Courier New",Font.PLAIN,11);
				this.setFont(font);
				this.setForeground(table.getForeground());
				this.setBackground(table.getBackground());
			}
			return this;
		}
	}

	@SuppressWarnings("serial")
	private JScrollPane setupTableFaltando(JPanel panMain, List<LinhaExtratoCartao> linhasFaltando) {
		tableModelLancamentosFaltando = new DefaultModelTabela(null, new Object[]{"Data","Historico","País","Valor"});
		tableLancamentosFaltando = new JTable(tableModelLancamentosFaltando){
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		tableLancamentosFaltando.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableLancamentosFaltando.setPreferredScrollableViewportSize(new Dimension(500, 300));
		tableLancamentosFaltando.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableLancamentosFaltando.getTableHeader().setReorderingAllowed(false);
		tableLancamentosFaltando.getTableHeader().setResizingAllowed(false);
		JScrollPane panelLancamentosFaltando = new JScrollPane(tableLancamentosFaltando);
		panelLancamentosFaltando.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		/**
		 * Table Layout
		 */
		TableColumn dataColumn       = tableLancamentosFaltando.getColumnModel().getColumn(0);
		TableColumn historicoColumn  = tableLancamentosFaltando.getColumnModel().getColumn(1);
		TableColumn paisColumn       = tableLancamentosFaltando.getColumnModel().getColumn(2);
		TableColumn valorColumn      = tableLancamentosFaltando.getColumnModel().getColumn(3);
		DefaultTableCellRenderer dataRenderer       = new LocalCellRenderer(SwingConstants.CENTER);
		DefaultTableCellRenderer valorRenderer      = new LocalCellRenderer(SwingConstants.RIGHT);
		DefaultTableCellRenderer historicoRenderer  = new LocalCellRenderer();
		DefaultTableCellRenderer paisRenderer       = new LocalCellRenderer(SwingConstants.CENTER);
		
		SortButtonRenderer renderer = new SortButtonRenderer();
		for(int i = 0; i < tableLancamentosFaltando.getColumnModel().getColumnCount(); i++) {
			tableLancamentosFaltando.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableLancamentosFaltando.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela)tableLancamentosFaltando.getModel()));
		
		dataRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		historicoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		paisRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		
		dataColumn.setCellRenderer(dataRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		historicoColumn.setCellRenderer(historicoRenderer);
		paisColumn.setCellRenderer(paisRenderer);
		
		dataColumn.setPreferredWidth(85);
		valorColumn.setPreferredWidth(109);
		historicoColumn.setPreferredWidth(350);
		paisColumn.setPreferredWidth(65);
		
		for (LinhaExtratoCartao linha : linhasFaltando) {
			Object[] row = new Object[]{
				linha.getData(),linha.getHistorico(),linha.getPais(),linha.getValor()	
			};
			tableModelLancamentosFaltando.addRow(row);
		}
		
		tableLancamentosFaltando.addKeyListener(this);
		panelLancamentosFaltando.addKeyListener(this);
		panelLancamentosFaltando.addMouseListener(this);
		
		return panelLancamentosFaltando;
	}
	
	@SuppressWarnings("serial")
	private JScrollPane setupTableRestando(JPanel panMain, List<Passivo> passivosRestando) {
		tableModelLancamentosRestando = new DefaultModelTabela(null, new Object[]{"Vencimento","Valor","Historico","Parcela","Efetivado","Tipo","ID"});
		tableLancamentosRestando      = new JTable(tableModelLancamentosRestando){
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		tableLancamentosRestando.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableLancamentosRestando.setPreferredScrollableViewportSize(new Dimension(500, 300));
		tableLancamentosRestando.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableLancamentosRestando.getTableHeader().setReorderingAllowed(false);
		tableLancamentosRestando.getTableHeader().setResizingAllowed(false);
		JScrollPane panelPassivos = new JScrollPane(tableLancamentosRestando);
		panelPassivos.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		/**
		 * Table Layout
		 */
		TableColumn vencimentoColumn = tableLancamentosRestando.getColumnModel().getColumn(0);
		TableColumn valorColumn      = tableLancamentosRestando.getColumnModel().getColumn(1);
		TableColumn historicoColumn  = tableLancamentosRestando.getColumnModel().getColumn(2);
		TableColumn parcelaColumn    = tableLancamentosRestando.getColumnModel().getColumn(3);
		TableColumn efetivadoColumn  = tableLancamentosRestando.getColumnModel().getColumn(4);
		TableColumn tipoColumn       = tableLancamentosRestando.getColumnModel().getColumn(5);
		Color foregroundColor        = Color.DARK_GRAY;
		DefaultTableCellRenderer vencimentoRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER, foregroundColor);
		DefaultTableCellRenderer valorRenderer      = new MonetarioTableCellRenderer(foregroundColor);
		DefaultTableCellRenderer historicoRenderer  = new ParcelaTableCellRenderer(" ", foregroundColor);
		DefaultTableCellRenderer parcelaRenderer    = new ParcelaTableCellRenderer(SwingConstants.CENTER, foregroundColor);
		TableCellRenderer efetivadoRenderer         = new EfetivadoTableCellRenderer();
		DefaultTableCellRenderer tipoRenderer       = new ParcelaTableCellRenderer(" ", foregroundColor);
		
		SortButtonRenderer renderer = new SortButtonRenderer();
		for(int i = 0; i < tableLancamentosRestando.getColumnModel().getColumnCount(); i++) {
			tableLancamentosRestando.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tableLancamentosRestando.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela)tableLancamentosRestando.getModel()));
		
		vencimentoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		historicoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		parcelaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		tipoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		
		vencimentoColumn.setCellRenderer(vencimentoRenderer);
		valorColumn.setCellRenderer(valorRenderer);
		historicoColumn.setCellRenderer(historicoRenderer);
		parcelaColumn.setCellRenderer(parcelaRenderer);
		efetivadoColumn.setCellRenderer(efetivadoRenderer);
		tipoColumn.setCellRenderer(tipoRenderer);
		
		vencimentoColumn.setPreferredWidth(85);
		valorColumn.setPreferredWidth(109);
		historicoColumn.setPreferredWidth(350);
		parcelaColumn.setPreferredWidth(65);
		efetivadoColumn.setPreferredWidth(55);
		tipoColumn.setPreferredWidth(85);
		
		for (Passivo passivo : passivosRestando) {
			
			String tipo = passivo.getClass().getName().substring(passivo.getClass().getName().lastIndexOf(".") + 1);
			if (passivo instanceof Cartao) {
				Cartao cartao = (Cartao) passivo;
				tipo = cartao.getNomeOperadora();
			}
			
			for (Parcela parcela : passivo.getParcelas()) {
				Object row[] = new Object[] {
					Util.formatDate(parcela.getDataVencimento()),
					Util.formatCurrency(parcela.getValor()),
					passivo.getHistorico(),
					parcela.getLabelNumeroTotalParcelas(),
					parcela.isEfetivado(),
					tipo.toUpperCase(),
					parcela.getPassivo().getId()
				};
				tableModelLancamentosRestando.addRow(row);
			}
		}
		tableLancamentosRestando.removeColumn(tableLancamentosRestando.getColumnModel().getColumn(6));
		
		tableLancamentosRestando.addKeyListener(this);
		panelPassivos.addKeyListener(this);
		panelPassivos.addMouseListener(this);
		
		return panelPassivos;
	}
	
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
		if ( e.getKeyCode() == 27 ) {
			this.dispose();
		}
	}
	public void keyTyped(KeyEvent e) {
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
	public void windowGainedFocus(WindowEvent e) {
	}
	public void windowLostFocus(WindowEvent e) {
		this.dispose();
	}
	
	public JPanel buildLabelTitulo(String valorTotal, int width, int height) {
		int X_TOTAIS = 10;
		int Y_TOTAIS = 1;
		
		JPanel panelTotais = new JPanel();
		panelTotais.setBackground(Color.GRAY);
		panelTotais.setLayout(null);
		panelTotais.setBounds(0,0,width,height);
		panelTotais.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel lblLabelTotal = new JLabel("Conferência Fatura Cartão - Total Fatura...: ");
		lblLabelTotal.setForeground(Color.WHITE);
		lblLabelTotal.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelTotal.setBounds(X_TOTAIS,Y_TOTAIS,288,20);
		
		JLabel lblLabelValor = new JLabel("R$ " + valorTotal);
		lblLabelValor.setForeground(Color.YELLOW);
		lblLabelValor.setFont(new Font("Courier New",Font.BOLD,12));
		lblLabelValor.setBounds(X_TOTAIS + lblLabelTotal.getWidth(),Y_TOTAIS + 2,100,20);
		
		panelTotais.add(lblLabelTotal);
		panelTotais.add(lblLabelValor);
		
		return panelTotais;
	}

	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals("POPUP_CRIAR_PASSIVO") ) {
			String data      = this.tableLancamentosFaltando.getValueAt(selectedRowtableLancamentosFaltando, 0).toString();
			String historico = this.tableLancamentosFaltando.getValueAt(selectedRowtableLancamentosFaltando, 1).toString();
			String pais      = this.tableLancamentosFaltando.getValueAt(selectedRowtableLancamentosFaltando, 2).toString();
			String valor     = this.tableLancamentosFaltando.getValueAt(selectedRowtableLancamentosFaltando, 3).toString();
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
			Date dataMovimento;
			try {
				dataMovimento = sdf.parse(data);
			} catch (ParseException e1) {
				logger.error(e1);
				throw new RuntimeException(e1);
			}
			
			switch(this.operadora) {
				case VISA: {
					VisaCreditoFrame visaCreditoFrame = new VisaCreditoFrame(this.bankPanel.getOwner(), this.bankPanel.getContaCorrente(), dataMovimento);
					visaCreditoFrame.getTxtDataMovimento().setDate(dataMovimento);
					visaCreditoFrame.getTxtHistorico().setText(historico);
					Object[] rowParcela = new Object[]{visaCreditoFrame.getFirstDateVencimento(this.bankPanel.getContaCorrente().getBanco(), Cartao.Operadora.VISA),valor,new Boolean(false),0};
					visaCreditoFrame.getModelParcelas().addRow(rowParcela);
					visaCreditoFrame.setVisible(true);
					break;
				}
				case MASTERCARD: {
					MastercardFrame masterCardCreditoFrame = new MastercardFrame(this.bankPanel.getOwner(), this.bankPanel.getContaCorrente(), dataMovimento);
					masterCardCreditoFrame.getTxtDataMovimento().setDate(dataMovimento);
					masterCardCreditoFrame.getTxtHistorico().setText(historico);
					Object[] rowParcela = new Object[]{masterCardCreditoFrame.getFirstDateVencimento(this.bankPanel.getContaCorrente().getBanco(), Cartao.Operadora.MASTERCARD),valor,new Boolean(false),0};
					masterCardCreditoFrame.getModelParcelas().addRow(rowParcela);
					masterCardCreditoFrame.setVisible(true);
					break;
				}
			}
		} else
		if ( e.getActionCommand().equals("POPUP_ADIAR_PASSIVO") ) {
			String id = this.tableLancamentosRestando.getModel().getValueAt(selectedRowtableLancamentosRestando, 6).toString();
			
			Cartao cartao = (Cartao) this.bankPanel.scorecardBusinessDelegate.getPassivoPorId(Integer.parseInt(id));
			for (Parcela parcela : cartao.getParcelas()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(parcela.getDataVencimento());
				cal.add(Calendar.MONTH, 1);
				parcela.setDataVencimento(new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()));
			}
			new AdiarDataVencimentoCartao(this.bankPanel,cartao).execute();
			this.tableModelLancamentosRestando.removeRow(selectedRowtableLancamentosRestando);
		} else
		if ( e.getActionCommand().equals("POPUP_ABRIR_PASSIVO") ) {
			
			String id = this.tableLancamentosRestando.getModel().getValueAt(selectedRowtableLancamentosRestando, 6).toString();
			Cartao cartao = (Cartao) this.bankPanel.scorecardBusinessDelegate.getPassivoPorId(Integer.parseInt(id));
			
			switch(this.operadora) {
			case VISA: {
				VisaCreditoFrame visaCreditoFrame = new VisaCreditoFrame(this.bankPanel.getOwner(), this.bankPanel.getContaCorrente(), null, cartao);
				visaCreditoFrame.setVisible(true);
				break;
			}
			case MASTERCARD: {
				MastercardFrame masterCardCreditoFrame = new MastercardFrame(this.bankPanel.getOwner(), this.bankPanel.getContaCorrente(), null, cartao);
				masterCardCreditoFrame.setVisible(true);
				break;
			}
		}
			
		}
	}
	
	private class AdiarDataVencimentoCartao extends SwingWorker<String, String> {
		private BankPanel frame;
		private Cartao cartao;
		private LoadingFrame loadingFrame;
		public AdiarDataVencimentoCartao(BankPanel frame, Cartao cartao) {
			this.frame = frame;
			this.cartao = cartao;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Adiando vencimento " + Util.formatCurrency(cartao.getValorTotal(), false) + " - " + cartao.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			this.frame.scorecardBusinessDelegate.savePassivo(cartao);	
			return null;
		}
		protected void done() {
			this.loadingFrame.dispose();
			this.frame.updateViewVisaCredito();
		}
	}

}
