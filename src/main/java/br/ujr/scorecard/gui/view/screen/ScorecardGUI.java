/*
 * ScoredcardGUI.java created on 07/11/2004, 11:02:11
 */

package br.ujr.scorecard.gui.view.screen;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import br.ujr.scorecard.analisador.extrato.contacorrente.bb.ProcessadorExtratoCCBancoBrasilGUI;
import br.ujr.scorecard.analisador.extrato.contacorrente.deutsche.AnalisadorExtratoCCDeutscheGUI;
import br.ujr.scorecard.analisador.extrato.contacorrente.santander.AnalisadorExtratoCCSantanderGUI;
import br.ujr.scorecard.analisador.fatura.cartao.AnalisadorLancamentosCartaoGUI;
import br.ujr.scorecard.gui.view.screen.bankpanel.BankPanel;
import br.ujr.scorecard.gui.view.screen.passivo.PassivoConstanteFrame;
import br.ujr.scorecard.gui.view.screen.reports.ResumoMensalFrame;
import br.ujr.scorecard.gui.view.screen.reports.TotalContaContabilFrame;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.cc.ContaCorrenteOrdenador;
import br.ujr.scorecard.util.ScorecardProperties;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;

/**
 * @author ualter.junior
 */
public class ScorecardGUI extends JFrame implements WindowFocusListener, WindowListener, ActionListener, MouseListener {
    
    private   int         largura           = 1074;
    private   int         altura            = 763;
    private   JTabbedPane tabBancos         = new JTabbedPane();
    private   JPanel      panMain           = null;
    private   boolean     updateResumoGeral = false;
    
    private static Logger logger = Logger.getLogger(ScorecardGUI.class);
	private ResumoPeriodoGeral resumoPeriodoGeral;
   
    public ScorecardGUI() {
        initComponents();
    }
    
    private void initComponents() {
    	java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(((screenSize.width-largura)/2), ((screenSize.height-737)/2) - 15, largura, 737);
        this.setPreferredSize(new Dimension(largura, 737));
        
    	//this.panMain = new TransparentBackground(this);
        this.panMain = new JPanel();
    	this.panMain.setLayout(null);
    		
    	this.loadContasCorrentes();
    	
    	tabBancos.setBounds(1, 63, 1066, 618);
    	this.panMain.add(tabBancos);
    	
    	this.panMain.setBounds(0,0,largura,768);
		this.getContentPane().setLayout(null);
		this.getContentPane().add(this.panMain);
    	
        this.setTitle("Scoredcard by Ualter Jr. - Release " + ScorecardProperties.getProperty(ScorecardPropertyKeys.ScorecardVersion));
        this.setName("ScorecardGUI");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        this.addWindowFocusListener(this);
        
        /**
         * Menus
         */
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuMain = new JMenu("Manutenção");
        menuMain.setMnemonic(KeyEvent.VK_M);
        
        JMenuItem itemBanco = new JMenuItem("Banco");
        itemBanco.addActionListener(this);
        itemBanco.setActionCommand("BANCO");
        itemBanco.setMnemonic(KeyEvent.VK_B);
        menuMain.add(itemBanco);
        
        JMenuItem itemContaCorrente = new JMenuItem("Conta Corrente");
        itemContaCorrente.addActionListener(this);
        itemContaCorrente.setActionCommand("CONTA_CORRENTE");
        itemContaCorrente.setMnemonic(KeyEvent.VK_C);
        menuMain.add(itemContaCorrente);
        
        JMenuItem itemContaContabil = new JMenuItem("Conta Contábil");
        itemContaContabil.addActionListener(this);
        itemContaContabil.setActionCommand("CONTA_CONTABIL");
        itemContaContabil.setMnemonic(KeyEvent.VK_O);
        // Apple
        itemContaContabil.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        menuMain.add(itemContaContabil);
        
        JMenu menuProcessamentos = new JMenu("Processamentos");
        menuProcessamentos.setMnemonic(KeyEvent.VK_S);
        
        JMenuItem itemProcessarSaldo = new JMenuItem("Processar Saldos");
        itemProcessarSaldo.addActionListener(this);
        itemProcessarSaldo.setActionCommand("PROCESSAR_SALDO");
        itemProcessarSaldo.setMnemonic(KeyEvent.VK_P);
        menuProcessamentos.add(itemProcessarSaldo);
        
        JMenuItem itemPassivoConstante = new JMenuItem("Inserir Passivo Constantes");
        itemPassivoConstante.addActionListener(this);
        itemPassivoConstante.setActionCommand("PASSIVO_CONSTANTE");
        itemPassivoConstante.setMnemonic(KeyEvent.VK_S);
        menuProcessamentos.add(itemPassivoConstante);
        
        JMenuItem itemReplicarOrcamentos = new JMenuItem("Replicar Orçamento");
        itemReplicarOrcamentos.addActionListener(this);
        itemReplicarOrcamentos.setActionCommand("REPLICAR_ORCAMENTO");
        itemReplicarOrcamentos.setMnemonic(KeyEvent.VK_M);
        menuProcessamentos.add(itemReplicarOrcamentos);
        
        JMenuItem itemAnalisadorExtratoCCSantander = new JMenuItem("Processar extrato C/C SANTANDER");
        itemAnalisadorExtratoCCSantander.addActionListener(this);
        itemAnalisadorExtratoCCSantander.setActionCommand("ANALISAR_EXTRATO_CC_SANTANDER");
        itemAnalisadorExtratoCCSantander.setMnemonic(KeyEvent.VK_R);
        menuProcessamentos.add(itemAnalisadorExtratoCCSantander);
        
        JMenuItem itemAnalisadorFaturaCartaoSantander = new JMenuItem("Analisar Lançamentos Cartão de Crédito (ClipBoard)");
        itemAnalisadorFaturaCartaoSantander.addActionListener(this);
        itemAnalisadorFaturaCartaoSantander.setActionCommand("ANALISAR_FATURA_CARTAO_LCTO");
        itemAnalisadorFaturaCartaoSantander.setMnemonic(KeyEvent.VK_N);
        menuProcessamentos.add(itemAnalisadorFaturaCartaoSantander);
        
        JMenu menuRelatorios = new JMenu("Relatórios");
        menuRelatorios.setMnemonic(KeyEvent.VK_R);
        JMenuItem itemResumoMensal = new JMenuItem("1. Resumo Mensal");
        itemResumoMensal.addActionListener(this);
        itemResumoMensal.setActionCommand("RELATORIO_RESUMO_MENSAL");
        itemResumoMensal.setMnemonic(KeyEvent.VK_1);
        menuRelatorios.add(itemResumoMensal);
        JMenuItem itemTotalContabil = new JMenuItem("2. Total Conta Contábil");
        itemTotalContabil.addActionListener(this);
        itemTotalContabil.setActionCommand("RELATORIO_TOTAL_CONTABIL");
        itemTotalContabil.setMnemonic(KeyEvent.VK_2);
        menuRelatorios.add(itemTotalContabil);
        
        menuBar.add(menuMain);
        menuBar.add(menuRelatorios);
        menuBar.add(menuProcessamentos);
        this.setJMenuBar(menuBar);
        
        /**
         * ToolBar
         */
        JToolBar toolBar = new JToolBar("Still draggable");
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        
        JButton btnBanco = new JButton(new ImageIcon(Util.loadImage(this, "banco.png")));
        btnBanco.setActionCommand("BANCO");
        btnBanco.addActionListener(this);
        Util.setToolTip(this, btnBanco, "Bancos");
        toolBar.add(btnBanco);
        
        JButton btnContaCorrente = new JButton(new ImageIcon(Util.loadImage(this, "conta_corrente.png")));
        btnContaCorrente.setActionCommand("CONTA_CORRENTE");
        btnContaCorrente.addActionListener(this);
        Util.setToolTip(this, btnContaCorrente, "Contas Correntes");
        toolBar.add(btnContaCorrente);
        
        JButton btnContaContabil = new JButton(new ImageIcon(Util.loadImage(this, "conta_contabil.png")));
        btnContaContabil.setActionCommand("CONTA_CONTABIL");
        btnContaContabil.addActionListener(this);
        Util.setToolTip(this, btnContaContabil, "Contas Contábeis");
        toolBar.add(btnContaContabil);
        
        JButton btnContaLctoCartaoCredito = new JButton(new ImageIcon(Util.loadImage(this, "credit-cards-icon.png")));
        btnContaLctoCartaoCredito.setActionCommand("ANALISAR_FATURA_CARTAO_LCTO");
        btnContaLctoCartaoCredito.addActionListener(this);
        Util.setToolTip(this, btnContaLctoCartaoCredito, "Analisar Lançamentos Cartão de Crédito");
        toolBar.add(btnContaLctoCartaoCredito);
        
        JButton btnAnalisarExtratoDeutsche = new JButton(new ImageIcon(Util.loadImage(this, "deutsche_bank_logo.png")));
        btnAnalisarExtratoDeutsche.setActionCommand("ANALISAR_EXTRATO_DEUTSCHE");
        btnAnalisarExtratoDeutsche.addActionListener(this);
        Util.setToolTip(this, btnAnalisarExtratoDeutsche, "Analisar Extrato Deutsche Bank em memória");
        toolBar.add(btnAnalisarExtratoDeutsche);
        
        toolBar.addSeparator();
        
        JButton btnSair = new JButton(new ImageIcon(Util.loadImage(this, "desligar.png")));
        btnSair.setActionCommand("SAIR");
        btnSair.addActionListener(this);
        Util.setToolTip(this, btnSair, "Sair do Sistema");
        toolBar.add(btnSair);
        
        toolBar.setBounds(0, 0, this.panMain.getWidth(), 60);
        this.panMain.add(toolBar);
        
        this.setResizable(false);
        this.setVisible(true);
    }

	private void loadContasCorrentes() {
		//this.manager = (ScorecardManager)Util.getBean("scorecardManager");
		ScorecardManager manager = (ScorecardManager)Util.getBean("scorecardManager");
		List<ContaCorrente> contasCorrentes = manager.listarContaCorrente();
    	Collections.sort(contasCorrentes,ContaCorrenteOrdenador.ORDEM);
    	for(ContaCorrente cc : contasCorrentes) {
    	
    		/**
    		 * Filtrar bancos/contas desativadas
    		 */
    		if ( cc.getBanco().isAtivo() ) {
    			/**
    			 * Verifica se os saldos anteriores dos 5 meses passados já foram processados(gerados)
    			 * Caso não: serão gerados!
    			 */
    			manager.consistirSaldosAnteriores(cc);
    			BankPanel bankPanel = new BankPanel(this,cc);
    			tabBancos.addTab(cc.getDescricao(), bankPanel); 
    		}
    	}
    	
    	/**
    	 * Resumo Geral
    	 */
    	JPanel panResumoGeral = new JPanel();
    	panResumoGeral.setLayout(null);
    	resumoPeriodoGeral = new ResumoPeriodoGeral(this,panResumoGeral,((ScorecardManager)Util.getBean("scorecardManager")));
		tabBancos.addTab("Resumo Geral",panResumoGeral);
		tabBancos.addMouseListener(this);
    	
	}
	
    public void refreshContas() {
    	this.tabBancos.removeAll();
    	this.loadContasCorrentes();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])  {
    	try {
			// For Mac OS
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Scorecard");
			
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				new ScorecardGUI();
			}
    	});
    }
    
    public Rectangle getSizeScreen()
    {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width-this.largura)/2;
        int y = (screenSize.height-this.altura)/2;
        Rectangle rect = new Rectangle();
        rect.setBounds(x, y, this.largura, this.altura);
        return rect;
    }

	private void renderChildren() {
		this.tabBancos.repaint();
	}

	public void windowGainedFocus(WindowEvent e) {
		this.renderChildren();
	}
	public void windowActivated(WindowEvent e) {
		this.renderChildren();
	}
	public void windowLostFocus(WindowEvent e) {
	}
	public void windowClosed(WindowEvent e) {
	}
	public void windowClosing(WindowEvent e) {
	}
	public void windowDeactivated(WindowEvent e) {
	}
	public void windowDeiconified(WindowEvent e) {
	}
	public void windowIconified(WindowEvent e) {
	}
	public void windowOpened(WindowEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		String a = e.getActionCommand();
		if ("SAIR".equals(a)) {
			System.exit(0);
		} else
		if ("BANCO".equals(a)) {
			BancoFrame bancoFrame = new BancoFrame(this);
			bancoFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			bancoFrame.setVisible(true);
		} else
		if ("CONTA_CORRENTE".equals(a)) {
			ContaCorrenteFrame bancoFrame = new ContaCorrenteFrame(this);
			bancoFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			bancoFrame.setVisible(true);
		} else 
		if ("CONTA_CONTABIL".equals(a)) {
			ContaFrame contaFrame = new ContaFrame();
			contaFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    	contaFrame.setVisible(true);
		} else 
		if ("PROCESSAR_SALDO".equals(a)) {
			ProcessarSaldosFrame processarFrame = new ProcessarSaldosFrame(this);
			processarFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			processarFrame.setVisible(true);
		} else 
		if ("RELATORIO_RESUMO_MENSAL".equals(a)) {
			ResumoMensalFrame processarFrame = new ResumoMensalFrame(this);
			processarFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			processarFrame.setVisible(true);
		} else
		if ("PASSIVO_CONSTANTE".equals(a)) {
			PassivoConstanteFrame passiConstanteFrame = new PassivoConstanteFrame(this,Util.today());
			passiConstanteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			passiConstanteFrame.setVisible(true);
		} else
		if ("REPLICAR_ORCAMENTO".equals(a)) {
			ReplicarOrcamentoFrame passiConstanteFrame = new ReplicarOrcamentoFrame(this,Util.today());
			passiConstanteFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			passiConstanteFrame.setVisible(true);
		} else
		if ("RELATORIO_TOTAL_CONTABIL".equals(a)) {
			TotalContaContabilFrame totalContabilFrame = new TotalContaContabilFrame(this);
			totalContabilFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			totalContabilFrame.setVisible(true);
		} else
		if ("ANALISAR_EXTRATO_CC_SANTANDER".equals(a)) {
			AnalisadorExtratoCCSantanderGUI proSantanderGUI = new AnalisadorExtratoCCSantanderGUI();
			proSantanderGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			proSantanderGUI.setVisible(true);
		} else
		if ("ANALISAR_EXTRATO_CC_BB".equals(a)) {
			ProcessadorExtratoCCBancoBrasilGUI proSantanderGUI = new ProcessadorExtratoCCBancoBrasilGUI();
			proSantanderGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			proSantanderGUI.setVisible(true);
		} else
		if ("ANALISAR_FATURA_CARTAO_LCTO".equals(a)) {
			AnalisadorLancamentosCartaoGUI anaFaturaCartaoSantanderGUI = new AnalisadorLancamentosCartaoGUI(this);
			anaFaturaCartaoSantanderGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			anaFaturaCartaoSantanderGUI.setVisible(true);
		} else
		if ("ANALISAR_EXTRATO_DEUTSCHE".equals(a)) {
			AnalisadorExtratoCCDeutscheGUI analisadorExtratoCCDeutscheGUI = new AnalisadorExtratoCCDeutscheGUI(this);
			analisadorExtratoCCDeutscheGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			analisadorExtratoCCDeutscheGUI.setVisible(true);
		}
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
		if ( e.getComponent() instanceof JTabbedPane ) {
			JTabbedPane tabBancos = (JTabbedPane)e.getComponent();
			// Resumo Geral
			if ( tabBancos.getSelectedIndex() == 2 && this.updateResumoGeral ) {
				this.resumoPeriodoGeral.update(false);
				this.updateResumoGeral = false;
			}
		}
	}

	public boolean isUpdateResumoGeral() {
		return updateResumoGeral;
	}

	public void setUpdateResumoGeral(boolean updateResumoGeral) {
		this.updateResumoGeral = updateResumoGeral;
	}
}
