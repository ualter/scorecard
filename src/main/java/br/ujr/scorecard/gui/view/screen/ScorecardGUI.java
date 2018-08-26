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

import br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell.AnalisadorExtratoCCBanSabadellGUI;
import br.ujr.scorecard.analisador.extrato.contacorrente.bb.ProcessadorExtratoCCBancoBrasilGUI;
import br.ujr.scorecard.analisador.extrato.contacorrente.deutsche.AnalisadorExtratoCCDeutscheGUI;
import br.ujr.scorecard.analisador.extrato.contacorrente.santander.AnalisadorExtratoCCSantanderGUI;
import br.ujr.scorecard.analisador.fatura.cartao.AnalisadorFaturaCartaoCreditoGUI;
import br.ujr.scorecard.config.ScorecardConfigBootStrap;
import br.ujr.scorecard.gui.view.screen.bankpanel.BankPanel;
import br.ujr.scorecard.gui.view.screen.passivo.PassivoConstanteFrame;
import br.ujr.scorecard.gui.view.screen.reports.ResumoMensalFrame;
import br.ujr.scorecard.gui.view.screen.reports.TotalContaContabilFrame;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.cc.ContaCorrenteOrdenador;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertiesUtil;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;

/**
 * @author ualter.junior
 */
public class ScorecardGUI extends JFrame implements WindowFocusListener, WindowListener, ActionListener, MouseListener {
    
    public    int         largura           = 1124;
    public    int         altura            = 834;
    private   JTabbedPane tabBancos         = new JTabbedPane();
    private   JPanel      panMain           = null;
    private   Integer     tabResumoNumber   = null;
    
    private static Logger logger = Logger.getLogger(ScorecardGUI.class);
	private ResumoPeriodoGeral resumoPeriodoGeral;
	
	//private final MetricRegistry metricsLevel_1 = new MetricRegistry();
	//private final MetricRegistry metricsLevel_2 = new MetricRegistry();
	
	//private final ConsoleReporter reporterLevel_1 = ConsoleReporter.forRegistry(metricsLevel_1).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
	//private final ConsoleReporter reporterLevel_2 = ConsoleReporter.forRegistry(metricsLevel_2).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
   
    public ScorecardGUI() {
    	//Timer.Context timerInitComponents = metricsLevel_1.timer("ScorecardGUI.initComponents").time();
    	
    	initComponents();
    	
    	//timerInitComponents.stop();
    	//reporterLevel_1.start(12, TimeUnit.SECONDS);
    	//reporterLevel_2.start(10, TimeUnit.SECONDS);
    }
    
    private void initComponents() {
    	java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(((screenSize.width-largura)/2), ((screenSize.height-altura)/2) - 15, largura, altura);
        this.setPreferredSize(new Dimension(largura, altura));
        
    	//this.panMain = new TransparentBackground(this);
        this.panMain = new JPanel();
    	this.panMain.setLayout(null);
    		
    	this.loadContasCorrentes();
    	
    	tabBancos.setBounds(1, 63, 1116, altura - 116);
    	this.panMain.add(tabBancos);
    	
    	this.panMain.setBounds(0,0,largura,altura);
		this.getContentPane().setLayout(null);
		this.getContentPane().add(this.panMain);
    	
        this.setTitle("Scoredcard by Ualter Jr. - Release " + ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.ScorecardVersion));
        this.setName("ScorecardGUI");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        try {
        	this.tabResumoNumber = Integer.parseInt(ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.TabResumoGeral));
        } catch (Exception e) {
        	this.tabResumoNumber = null;
        }
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
        
        JMenuItem itemAnalisadorExtratoCCSantander = new JMenuItem("Processar extrato SANTANDER");
        itemAnalisadorExtratoCCSantander.addActionListener(this);
        itemAnalisadorExtratoCCSantander.setActionCommand("ANALISAR_EXTRATO_CC_SANTANDER");
        itemAnalisadorExtratoCCSantander.setMnemonic(KeyEvent.VK_R);
        menuProcessamentos.add(itemAnalisadorExtratoCCSantander);
        
        JMenuItem itemAnalisadorExtratoDeutsche = new JMenuItem("Processar extrato DEUTSCHE");
        itemAnalisadorExtratoDeutsche.addActionListener(this);
        itemAnalisadorExtratoDeutsche.setActionCommand("ANALISAR_EXTRATO_CC_DEUTSCHE");
        itemAnalisadorExtratoDeutsche.setMnemonic(KeyEvent.VK_D);
        menuProcessamentos.add(itemAnalisadorExtratoDeutsche);
        
        JMenuItem itemAnalisadorExtratoBanSabadell = new JMenuItem("Processar extrato BANC SABADELL");
        itemAnalisadorExtratoBanSabadell.addActionListener(this);
        itemAnalisadorExtratoBanSabadell.setActionCommand("ANALISAR_EXTRATO_CC_BANSABADELL");
        itemAnalisadorExtratoBanSabadell.setMnemonic(KeyEvent.VK_L);
        menuProcessamentos.add(itemAnalisadorExtratoBanSabadell);
        
        JMenuItem itemAnalisadorFaturaCartaoSantander = new JMenuItem("Analisar Fatura de Cartão de Crédito");
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
        Util.setToolTip(this, btnContaLctoCartaoCredito, "Analisar Fatura de Cartão de Crédito");
        toolBar.add(btnContaLctoCartaoCredito);
        
        JButton btnAnalisarExtratoDeutsche = new JButton(new ImageIcon(Util.loadImage(this, "deutsche_bank_logo.png")));
        btnAnalisarExtratoDeutsche.setActionCommand("ANALISAR_EXTRATO_DEUTSCHE");
        btnAnalisarExtratoDeutsche.addActionListener(this);
        Util.setToolTip(this, btnAnalisarExtratoDeutsche, "Analisar Extrato Deutsche Bank em memória");
        toolBar.add(btnAnalisarExtratoDeutsche);
        
        JButton btnAnalisarExtratoBanSabadell = new JButton(new ImageIcon(Util.loadImage(this, "banc_sabadell_logo.png")));
        btnAnalisarExtratoBanSabadell.setActionCommand("ANALISAR_EXTRATO_BANSABADELL");
        btnAnalisarExtratoBanSabadell.addActionListener(this);
        Util.setToolTip(this, btnAnalisarExtratoBanSabadell, "Analisar Extrato Banc Sabadell em arquivo N43 baixado");
        toolBar.add(btnAnalisarExtratoBanSabadell);
        
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
		//Timer.Context timerScorecardConfigUtilGetBean = metricsLevel_2.timer("loadContasCorrentes.ScorecardConfigUtil.getBean").time();
		ScorecardManager manager = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
		//timerScorecardConfigUtilGetBean.stop();
		
		//Timer.Context timerListarContaCorrente = metricsLevel_2.timer("loadContasCorrentes.listarContaCorrente").time();
		List<ContaCorrente> contasCorrentes = manager.listarContaCorrente();
		//timerListarContaCorrente.stop();
		
		
		//Timer.Context timerLoadAllBankPanels = metricsLevel_2.timer("loadContasCorrentes.loadAllBankPanels").time();
    	Collections.sort(contasCorrentes,ContaCorrenteOrdenador.ORDEM);
    	for(ContaCorrente cc : contasCorrentes) {
    		if ( cc.getBanco().isAtivo() ) {
    			/**
    			 * Verifica se os saldos anteriores dos 5 meses passados já foram processados(gerados)
    			 * Caso não: serão gerados!
    			 */
    			manager.consistirSaldosAnteriores(cc);
    			//Timer.Context timerBankPanel = metricsLevel_2.timer("loadContasCorrentes.new BankPanel(...)").time();
    			BankPanel bankPanel = new BankPanel(this,cc,manager);
    			//timerBankPanel.stop();
    			bankPanel.setName(cc.getBanco().getNome());
    			tabBancos.addTab(cc.getDescricao(), bankPanel); 
    		}
    	}
    	//timerLoadAllBankPanels.stop();
    	
    	/**
    	 * Resumo Geral
    	 */
    	JPanel panResumoGeral = new JPanel();
    	panResumoGeral.setLayout(null);
    	//Timer.Context timerResumoPeriodoGeral = metricsLevel_2.timer("resumoPeriodoGeral").time();
    	resumoPeriodoGeral = new ResumoPeriodoGeral(this,panResumoGeral,manager);
    	//timerResumoPeriodoGeral.stop();
    	panResumoGeral.setName("RESUMO_GERAL");
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
		if ("ANALISAR_EXTRATO_CC_DEUTSCHE".equals(a)) {
			AnalisadorExtratoCCDeutscheGUI deutscheGUI = new AnalisadorExtratoCCDeutscheGUI();
			deutscheGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			deutscheGUI.setVisible(true);
		} else
		if ("ANALISAR_EXTRATO_CC_BANSABADELL".equals(a)) {
			AnalisadorExtratoCCBanSabadellGUI banSabadellGUI = new AnalisadorExtratoCCBanSabadellGUI();
			banSabadellGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			banSabadellGUI.setVisible(true);
		} else	
		if ("ANALISAR_EXTRATO_CC_BB".equals(a)) {
			ProcessadorExtratoCCBancoBrasilGUI proSantanderGUI = new ProcessadorExtratoCCBancoBrasilGUI();
			proSantanderGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			proSantanderGUI.setVisible(true);
		} else
		if ("ANALISAR_FATURA_CARTAO_LCTO".equals(a)) {
			AnalisadorFaturaCartaoCreditoGUI anaFaturaCartaoSantanderGUI = new AnalisadorFaturaCartaoCreditoGUI(this);
			anaFaturaCartaoSantanderGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			anaFaturaCartaoSantanderGUI.setVisible(true);
		} else
		if ("ANALISAR_EXTRATO_DEUTSCHE".equals(a)) {
			AnalisadorExtratoCCDeutscheGUI analisadorExtratoCCDeutscheGUI = new AnalisadorExtratoCCDeutscheGUI(this);
			analisadorExtratoCCDeutscheGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			analisadorExtratoCCDeutscheGUI.setVisible(true);
		} else
		if ("ANALISAR_EXTRATO_BANSABADELL".equals(a)) {
			AnalisadorExtratoCCBanSabadellGUI analisadorExtratoCCBanSabadellGUI = new AnalisadorExtratoCCBanSabadellGUI(this);
			analisadorExtratoCCBanSabadellGUI.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			analisadorExtratoCCBanSabadellGUI.setVisible(true);
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
			if ( this.tabResumoNumber != null && (this.tabResumoNumber-1) == tabBancos.getSelectedIndex() ) {
				this.resumoPeriodoGeral.update(false);
			}
		}
	}

}
