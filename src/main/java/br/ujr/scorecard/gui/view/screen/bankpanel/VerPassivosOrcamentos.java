package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.components.gui.tabela.SortButtonRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.EfetivadoTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.MonetarioTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.ParcelaTableCellRenderer;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.PassivoOrdenador;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.Util;

public class VerPassivosOrcamentos extends JFrame implements KeyListener, MouseListener, WindowFocusListener {
	
	private static final long serialVersionUID = 7163781301991259862L;
	private int F_WIDTH = 800;
	private int F_HEIGHT = 350;
	
	private JLabel lblTotalOrcado;
	private JLabel lblTotalRealizado;
	private JLabel lblTotalRestante;
	
	private BigDecimal totalOrcado;
	private BigDecimal totalRealizado;
	
	@SuppressWarnings("serial")
	public VerPassivosOrcamentos(ScorecardManager scorecard, ContaCorrente contaCorrente, Orcamento orcamento, Date refIni, Date refFim, 
			BigDecimal totalOrcado) {
		
		this.totalOrcado    = totalOrcado;
		
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		int x = ((screenSize.width-F_WIDTH)/2)   + 110;
		int y = ((screenSize.height-F_HEIGHT)/2) + 80;
        this.setBounds(x, y, F_WIDTH, F_HEIGHT);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panMain = new JPanel();
		panMain.setLayout(null);
		panMain.setBackground(Color.GRAY);
		panMain.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		DefaultModelTabela tableModelPassivos = new DefaultModelTabela(null, new Object[]{"Vencimento","Valor","Historico","Parcela","Efetivado","Tipo"});
		JTable            tablePassivos      = new JTable(tableModelPassivos){
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		tablePassivos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tablePassivos.setPreferredScrollableViewportSize(new Dimension(500, 300));
		tablePassivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tablePassivos.getTableHeader().setReorderingAllowed(false);
		tablePassivos.getTableHeader().setResizingAllowed(false);
		JScrollPane panelPassivos = new JScrollPane(tablePassivos);
		panelPassivos.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		
		/**
		 * Table Layout
		 */
		TableColumn vencimentoColumn = tablePassivos.getColumnModel().getColumn(0);
		TableColumn valorColumn      = tablePassivos.getColumnModel().getColumn(1);
		TableColumn historicoColumn  = tablePassivos.getColumnModel().getColumn(2);
		TableColumn parcelaColumn    = tablePassivos.getColumnModel().getColumn(3);
		TableColumn efetivadoColumn  = tablePassivos.getColumnModel().getColumn(4);
		TableColumn tipoColumn       = tablePassivos.getColumnModel().getColumn(5);
		Color foregroundColor        = Color.DARK_GRAY;
		DefaultTableCellRenderer vencimentoRenderer = new ParcelaTableCellRenderer(SwingConstants.CENTER, foregroundColor);
		DefaultTableCellRenderer valorRenderer      = new MonetarioTableCellRenderer(foregroundColor);
		DefaultTableCellRenderer historicoRenderer  = new ParcelaTableCellRenderer(" ", foregroundColor);
		DefaultTableCellRenderer parcelaRenderer    = new ParcelaTableCellRenderer(SwingConstants.CENTER, foregroundColor);
		TableCellRenderer efetivadoRenderer         = new EfetivadoTableCellRenderer();
		DefaultTableCellRenderer tipoRenderer       = new ParcelaTableCellRenderer(" ", foregroundColor);
		
		SortButtonRenderer renderer = new SortButtonRenderer();
		for(int i = 0; i < tablePassivos.getColumnModel().getColumnCount(); i++) {
			tablePassivos.getColumnModel().getColumn(i).setHeaderRenderer(renderer);
		}
		JTableHeader header = tablePassivos.getTableHeader();
		header.addMouseListener(new HeaderListener(header, renderer, (DefaultModelTabela)tablePassivos.getModel()));
		
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
		
		/**
		 * Carga dos Dados
		 */
		Conta conta = orcamento.getContaAssociada();
		
		this.totalRealizado = new BigDecimal(0);
		
		Set<Passivo> passivos = scorecard.getPassivosAssociadosOrcamento(contaCorrente, Util.extrairReferencia(refIni), Util.extrairReferencia(refFim), conta.getNivel());
		List<Passivo> listPassivos = new ArrayList<Passivo>(passivos);
		PassivoOrdenador ordenador = PassivoOrdenador.PARCELA_DATA_VENCIMENTO;
		ordenador.setReferenciaInicial(200807);
		ordenador.setReferenciaFinal(200807);
		Collections.sort(listPassivos,ordenador);
		for (Passivo passivo : listPassivos) {
			
			String tipo = passivo.getClass().getName().substring(passivo.getClass().getName().lastIndexOf(".") + 1);
			if (passivo instanceof Cartao) {
				Cartao cartao = (Cartao) passivo;
				tipo = cartao.getNomeOperadora();
			}
			
			for (Parcela parcela : passivo.getParcelas()) {
				this.totalRealizado = this.totalRealizado.add(parcela.getValor());
				Object row[] = new Object[] {
					Util.formatDate(parcela.getDataVencimento()),
					Util.formatCurrency(parcela.getValor()),
					passivo.getHistorico(),
					parcela.getLabelNumeroTotalParcelas(),
					parcela.isEfetivado(),
					tipo.toUpperCase()
				};
				tableModelPassivos.addRow(row);
			}
		}
		
		
		this.addKeyListener(this);
		tablePassivos.addKeyListener(this);
		panelPassivos.addKeyListener(this);
		panMain.addKeyListener(this);
		panMain.addMouseListener(this);
		panelPassivos.addMouseListener(this);
		this.setResizable(false);
		this.setUndecorated(true);
		this.addWindowFocusListener(this);
		
		/**
		 * Show Screen
		 */
		panelPassivos.setBounds(10, 33, F_WIDTH - 20, 307);
		
		JPanel panTotais = buildLabelTotais(F_WIDTH, 24);
		panMain.add(panTotais);
		
		panMain.add(panelPassivos);
		this.getContentPane().add(panMain);
		
		tablePassivos.requestFocusInWindow();
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
		this.dispose();
	}
	public void windowGainedFocus(WindowEvent e) {
	}
	public void windowLostFocus(WindowEvent e) {
		this.dispose();
	}
	
	public JPanel buildLabelTotais(int width, int height) {
		int X_TOTAIS = 10;
		int Y_TOTAIS = 1;
		
		JPanel panelTotais = new JPanel();
		panelTotais.setBackground(Color.GRAY);
		panelTotais.setLayout(null);
		panelTotais.setBounds(0,0,width,height);
		panelTotais.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel lblLabelTotal = new JLabel("TOTAIS");
		lblLabelTotal.setForeground(Color.YELLOW);
		lblLabelTotal.setFont(new Font("Verdana",Font.BOLD,9));
		lblLabelTotal.setBounds(X_TOTAIS,Y_TOTAIS + 1,40,20);
		panelTotais.add(lblLabelTotal);
		
		X_TOTAIS += lblLabelTotal.getWidth() + 40;
		JLabel lblLabelOrcado = new JLabel("Orçado:");
		lblLabelOrcado.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelOrcado.setForeground(Color.WHITE);
		lblLabelOrcado.setBounds(X_TOTAIS,Y_TOTAIS,52,20);
		panelTotais.add(lblLabelOrcado);
		X_TOTAIS += lblLabelOrcado.getWidth();
		lblTotalOrcado = new JLabel(Util.formatCurrency(this.totalOrcado,false));
		lblTotalOrcado.setFont(new Font("Verdana",Font.BOLD,11));
		lblTotalOrcado.setBounds(X_TOTAIS,Y_TOTAIS,85,20);
		lblTotalOrcado.setForeground(Color.YELLOW);
		panelTotais.add(lblTotalOrcado);
		
		X_TOTAIS += lblTotalOrcado.getWidth() + 40;
		JLabel lblLabelRealizado = new JLabel("Realizado:");
		lblLabelRealizado.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelRealizado.setForeground(Color.WHITE);
		lblLabelRealizado.setBounds(X_TOTAIS,Y_TOTAIS,70,20);
		panelTotais.add(lblLabelRealizado);
		X_TOTAIS += lblLabelRealizado.getWidth();
		lblTotalRealizado = new JLabel(Util.formatCurrency(this.totalRealizado,false));
		lblTotalRealizado.setFont(new Font("Verdana",Font.BOLD,11));
		lblTotalRealizado.setBounds(X_TOTAIS,Y_TOTAIS,85,20);
		lblTotalRealizado.setForeground(Color.YELLOW);
		panelTotais.add(lblTotalRealizado);
		
		X_TOTAIS += lblTotalRealizado.getWidth() + 40;
		JLabel lblLabelRestante = new JLabel("Restante:");
		lblLabelRestante.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelRestante.setForeground(Color.WHITE);
		lblLabelRestante.setBounds(X_TOTAIS,Y_TOTAIS,62,20);
		panelTotais.add(lblLabelRestante);
		X_TOTAIS += lblLabelRestante.getWidth();
		BigDecimal restante = this.totalOrcado.subtract(totalRealizado);
		lblTotalRestante = new JLabel(Util.formatCurrency(restante,false));
		lblTotalRestante.setFont(new Font("Verdana",Font.BOLD,11));
		lblTotalRestante.setBounds(X_TOTAIS,Y_TOTAIS,85,20);
		lblTotalRestante.setForeground(Color.YELLOW);
		panelTotais.add(lblTotalRestante);
		
		return panelTotais;
	}

}
