package br.ujr.scorecard.gui.view.screen.reports;

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultFocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.treecheck.CheckNode;
import br.ujr.scorecard.gui.view.screen.treecheck.CheckRenderer;
import br.ujr.scorecard.gui.view.screen.treecheck.NodeSelectionListener;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.reports.ReportManager;
import br.ujr.scorecard.model.reports.ReportManagerListener;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
@SuppressWarnings("serial")
public class TotalContaContabilFrame extends AbstractDialog  {
	
	protected JDateChooser     txtDtIni         = new JDateChooser("MM/yyyy","##/####",'_');
	protected JDateChooser     txtDtFim         = new JDateChooser("MM/yyyy","##/####",'_');
	protected UjrComboBox      txtContaCorrente = new UjrComboBox(); 
	protected ScorecardBusinessDelegate scoreBusinessDelegate = ScorecardBusinessDelegate.getInstance();
	private JButton btnProcessarSaldo;
	private JButton btnSair;
	private JTree tree;
	private JScrollPane scrollPane;
	private JRadioButton radSintetico = new JRadioButton("Sintético");
	private JRadioButton radAnalitico = new JRadioButton("Analítico");
	private JRadioButton radGrafico   = new JRadioButton("Gráfico");
	private ButtonGroup  tipoRadio = new ButtonGroup();
	
	private JRadioButton radLinhas = new JRadioButton("Linhas");
	private JRadioButton radBarras = new JRadioButton("Barras");
	private ButtonGroup  tipoGrafico = new ButtonGroup();
	private JPanel panelTipoGrafico;
	
	private JCheckBox chkEfetivado    = new JCheckBox("Efetivadas");
	private JCheckBox chkNaoEfetivado = new JCheckBox("Não efetivadas");
	
	private static Logger logger = Logger.getLogger(TotalContaContabilFrame.class);
	
	public TotalContaContabilFrame(JFrame owner) {
		super(owner);
		this.title = "Relatórios - Total por Conta Contábil";
		this.createUI();
		this.componentsReady();
	}

	protected void createUI() {
		this.width  = 800;
		this.height = 650;
		super.createUI();
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createEtchedBorder());

		buildPanel(panel);
		panel.setBounds(10, 10, this.getWidth() - 23, this.getHeight() - 50);
		
		JLabel lblContas = new JLabel("Selecionar Contas Contábeis:");
		lblContas.setBounds(13,190,180,20);
		panel.add(lblContas);
		tree = new JTree();
		scrollPane = new JScrollPane();
		this.loadTree();
		scrollPane.setViewportView(tree);
		panel.add(scrollPane);
		scrollPane.setBounds(13, 212, 750, 375);
		
		tree.setCellRenderer(new CheckRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.putClientProperty("JTree.lineStyle", "Horizontal");
	    tree.addMouseListener(new NodeSelectionListener(tree));
		
	    Calendar calIni = Calendar.getInstance();
	    calIni.add(Calendar.MONTH, -1);
	    
	    Calendar calFim = Calendar.getInstance();
	    
	    this.txtDtIni.setDate(calIni.getTime());
	    this.txtDtFim.setDate(calFim.getTime());
	    
		panMain.add(panel);
	}

	
	/**
	 * @param panBtnAcoes
	 */
	private void buildPanel(JPanel panel) {
		int LBL_WIDTH = 100;
		int Y = 10;
		
		int btn_x = (this.getWidth() - 100) / 2;
		btnProcessarSaldo = new JButton();
		btnSair = new JButton();
		btnProcessarSaldo.setBounds(btn_x, Y, 50, 45);
		btnSair.setBounds(btn_x + 50, Y, 50, 45);
		btnProcessarSaldo.setIcon(new ImageIcon(Util.loadImage(this, "button_ok.png")));
		btnSair.setIcon(new ImageIcon(Util.loadImage(this, "exit.png")));
		btnProcessarSaldo.setActionCommand("IMPRIMIR");
		btnSair.setActionCommand("SAIR");
		btnProcessarSaldo.addActionListener(this);
		btnSair.addActionListener(this);
		Util.setToolTip(this, btnProcessarSaldo, "Imprimir Relatório");
		Util.setToolTip(this, btnSair, "Sair");
		btnProcessarSaldo.setEnabled(true);
		btnSair.setEnabled(true);
		
		Y += 65;
		int X = 14;
		JLabel lblCodigo = new JLabel("Período de:");
		lblCodigo.setBounds(X,Y,LBL_WIDTH, 20);
		this.txtDtIni.setBounds(LBL_WIDTH + X, Y, 82, 20);
		this.txtDtIni.setEnabled(true);
		this.txtDtIni.setFocusable(true);
		
		X += LBL_WIDTH + txtDtIni.getWidth() + 10;
		JLabel lblNome = new JLabel("até:");
		lblNome.setBounds(X,Y,30,20);
		txtDtFim.setBounds(X + lblNome.getWidth(),Y,82,20);
		txtDtFim.setEnabled(true);
		
		X  = 14;
		Y += 30;
		JLabel lblConta = new JLabel("Conta Corrente:");
		lblConta.setBounds(X,Y,LBL_WIDTH,20);
		this.txtContaCorrente.setBounds(X + LBL_WIDTH, Y, 280, 20);
		this.txtContaCorrente.setEditable(false);
		List<ContaCorrente> ccs = scoreBusinessDelegate.listarContaCorrente();
		ContaCorrente ct = new ContaCorrente();
		ct.setNumero("");
		ct.setDescricao("Geral");
		this.txtContaCorrente.addItem(ct);
		for(ContaCorrente cc : ccs) {
			this.txtContaCorrente.addItem(cc);
		}
		
		X  = 14;
		Y += 30;
		JLabel lblParcelas = new JLabel("Parcelas:");
		lblParcelas.setBounds(X,Y,LBL_WIDTH, 20);
		this.chkEfetivado.setBounds(LBL_WIDTH + X, Y, 100, 20);
		this.chkNaoEfetivado.setBounds(LBL_WIDTH + X + this.chkEfetivado.getWidth() + 10, Y, 110, 20);
		this.chkEfetivado.setSelected(true);
		this.chkNaoEfetivado.setSelected(true);
		
		X  = 14;
		Y += 30;
		JLabel lblTipo = new JLabel("Tipo:");
		lblTipo.setBounds(X,Y,180,20);
		panel.add(lblTipo);
		
		radSintetico.getModel().setActionCommand("SINTETICO");
		radAnalitico.getModel().setActionCommand("ANALITICO");
		radGrafico.getModel().setActionCommand("GRAFICO");
		
		tipoRadio.add(radAnalitico);
		tipoRadio.add(radSintetico);
		tipoRadio.add(radGrafico);
		tipoRadio.setSelected(radSintetico.getModel(), true);
		
		radSintetico.setBounds(LBL_WIDTH + X, Y, 100, 20);
		panel.add(radSintetico);
		radAnalitico.setBounds(LBL_WIDTH + X + radSintetico.getWidth(), Y, 100, 20);
		panel.add(radAnalitico);
		radGrafico.setBounds(LBL_WIDTH + X + radSintetico.getWidth() + radAnalitico.getWidth(), Y, 70, 20);
		radGrafico.addActionListener(this);
		radSintetico.addActionListener(this);
		radAnalitico.addActionListener(this);
		panel.add(radGrafico);
		
		tipoGrafico.add(radBarras);
		tipoGrafico.add(radLinhas);
		tipoGrafico.setSelected(radBarras.getModel(), true);
		panelTipoGrafico = new JPanel();
		panelTipoGrafico.setVisible(false);
		panelTipoGrafico.setBorder(BorderFactory.createEtchedBorder());
		panelTipoGrafico.setLayout(null);
		radLinhas.setBounds(5, 3, 65, 32);
		radBarras.setBounds(74, 3, 65, 32);
		radLinhas.getModel().setActionCommand("LINHAS");
		radBarras.getModel().setActionCommand("BARRAS");
		panelTipoGrafico.add(radLinhas);
		panelTipoGrafico.add(radBarras);
		panelTipoGrafico.setBounds(LBL_WIDTH + X + radSintetico.getWidth() + radAnalitico.getWidth() + 70, Y - 10, 148, 40);
		panel.add(panelTipoGrafico);
		
		panel.add(lblCodigo);
		panel.add(lblNome);
		panel.add(lblConta);
		panel.add(lblParcelas);
		panel.add(chkEfetivado);
		panel.add(chkNaoEfetivado);
		panel.add(txtDtIni);
		panel.add(txtDtFim);
		panel.add(txtContaCorrente);
		panel.add(btnProcessarSaldo);
		panel.add(btnSair);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ( "SAIR".equalsIgnoreCase(action)) {
			this.dispose();
		} else
		if ( "IMPRIMIR".equalsIgnoreCase(action) ) {
			if ( this.txtDtFim.getDate() == null  || this.txtDtIni.getDate() == null ) {
				JOptionPane.showMessageDialog(this, "Período informado inválido!","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				txtDtIni.requestFocus();
			} else
			if ( !this.chkEfetivado.isSelected() && !this.chkNaoEfetivado.isSelected() ) {
				JOptionPane.showMessageDialog(this, "Informe a condição para as parcelas: Efetivado, Não Efetivado ou ambos!","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				chkEfetivado.requestFocus();
			} else
			if ( !this.isContasSelected((CheckNode)tree.getModel().getRoot(), new ArrayList<String>()) ) {
				JOptionPane.showMessageDialog(this, "Selecione as Contas Contábeis!","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				this.tree.requestFocus();
			} else {
				//ContaCorrente cc = (ContaCorrente)this.txtContaCorrente.getSelectedItem();
				long refIni = Util.extrairReferencia(this.txtDtIni.getDate());
				long refFim = Util.extrairReferencia(this.txtDtFim.getDate());
				
				if ( refFim < refIni ) {
					JOptionPane.showMessageDialog(this, "Período inválido!\nReferência Final < Referência Inicial","Atenção!!!",JOptionPane.WARNING_MESSAGE);
				} else {
					
					int tipoRelatorio = 1; //SINTETICO
					if ( "ANALITICO".equals(this.tipoRadio.getSelection().getActionCommand()) ) {
						tipoRelatorio = 2;
					} else 
					if ( "GRAFICO".equals(this.tipoRadio.getSelection().getActionCommand()) ) {
						tipoRelatorio = 3;
					}
					int tipoGrafico   = 0; //BARRAS
					if ( "LINHAS".equals(this.tipoGrafico.getSelection().getActionCommand()) ) {
					     tipoGrafico = 1;
				    }
					PrintReport task = new PrintReport(this,tipoRelatorio,tipoGrafico);
					task.execute();
				}
			}
		} else
		if ( "GRAFICO".equalsIgnoreCase(action) ) {
			this.panelTipoGrafico.setVisible(true);
		} else
		if ( "SINTETICO".equalsIgnoreCase(action) || "ANALITICO".equalsIgnoreCase(action) ) {
			this.panelTipoGrafico.setVisible(false);
		}
	}
	
	private boolean isContasSelected(CheckNode nodeParent, List<String> contasSelected) {
		for(int i = 0; i < nodeParent.getChildCount(); i++) {
			CheckNode chkNode = (CheckNode)nodeParent.getChildAt(i);
			if ( chkNode.isSelected() ) {
				Conta conta = (Conta)chkNode.getUserObject();
				contasSelected.add(conta.getNivel());
			} else {
				if ( chkNode.getChildCount() > 0 ) {
					this.isContasSelected(chkNode, contasSelected);
				}
			}
		}
		return contasSelected.size() > 0 ? true : false;
	}
	
	private class PrintReport extends SwingWorker<String, String> implements ReportManagerListener {
		private LoadingFrame loadingFrame;
		private TotalContaContabilFrame frame;
		private int tipoRelatorio = 0;
		private int tipoGrafico = 0; //BARRAS (default)
		
		public PrintReport(TotalContaContabilFrame frame, int tipoRelatorio, int tipoGrafico) {
			this.frame = frame;
			this.loadingFrame = new LoadingFrame(100);
			this.tipoRelatorio = tipoRelatorio;
			this.tipoGrafico = tipoGrafico;
		}
		
		@Override
		protected String doInBackground() throws Exception {
			try {
				UtilGUI.coverBlinder(frame);
				loadingFrame.showLoadinFrame();
				loadingFrame.setMessage("Inicializando Relatório");
				ReportManager reportManager = new ReportManager();
				reportManager.addListener(this);
				ContaCorrente contaCorrente = (ContaCorrente)txtContaCorrente.getSelectedItem();
				if ( contaCorrente.getNumero().equals("") ) {
					contaCorrente = null;
				}

				List<String> contasSelected = new ArrayList<String>();
				CheckNode checkNodeRoot = (CheckNode)tree.getModel().getRoot();
				this.getSelectedContas(checkNodeRoot, contasSelected);

				String[] niveis = new String[contasSelected.size()];
				contasSelected.toArray(niveis);
				
				Boolean efetivados = null;
				if ( chkEfetivado.isSelected() && !chkNaoEfetivado.isSelected() ) {
					efetivados = new Boolean(true);
				} else
				if ( !chkEfetivado.isSelected() && chkNaoEfetivado.isSelected() ) {
					efetivados = new Boolean(false);
				}
				
				reportManager.printTotalContaContabil(contaCorrente, txtDtIni.getDate(), txtDtFim.getDate(), niveis, tipoRelatorio, tipoGrafico, efetivados);
			} catch (Throwable e) {
				e.printStackTrace();
				logger.error(e);
				throw new Exception(e);
			}
			return null;
		}
		@Override
		protected void done() {
			UtilGUI.uncoverBlinder(frame);
			txtDtIni.requestFocus();
		}
		public void reportStarted() {
		}
		public void reportFinished() {
			loadingFrame.setMessage("Finalizado");
			loadingFrame.dispose();
		}
		public void reportMaxProgress(int value) {
			loadingFrame.setMaxProgress(value);
			loadingFrame.setMessage("Imprimindo Relatório");
		}
		public void reportProgress(String msg, int value) {
			loadingFrame.setProgressValue(value);
		}
		
		private void getSelectedContas(CheckNode nodeParent, List<String> contasSelected) {
			for(int i = 0; i < nodeParent.getChildCount(); i++) {
				CheckNode chkNode = (CheckNode)nodeParent.getChildAt(i);
				if ( chkNode.isSelected() ) {
					Conta conta = (Conta)chkNode.getUserObject();
					contasSelected.add(conta.getNivel());
				} else {
					if ( chkNode.getChildCount() > 0 ) {
						this.getSelectedContas(chkNode, contasSelected);
					}
				}
			}
		}
	}
	
	public void focusGained(FocusEvent evt) {
		
	}

	public void focusLost(FocusEvent evt) {
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
	}
	
	public static void main(String[] args) {
		//SwingUtilities.invokeLater(new Runnable(){
			//public void run() {
			TotalContaContabilFrame totalContaContabil = new TotalContaContabilFrame(null);
			totalContaContabil.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			totalContaContabil.setVisible(true);
			//}
    	//});
	}

	public class InternalFocusManager extends DefaultFocusTraversalPolicy {
		
		@Override
		protected boolean accept(Component aComponent) {
			return super.accept(aComponent);
		}

		@Override
		public Component getComponentAfter(Container aContainer, Component aComponent) {
			return super.getComponentAfter(aContainer, aComponent);
		}

		@Override
		public Component getComponentBefore(Container aContainer, Component aComponent) {
			return super.getComponentBefore(aContainer, aComponent);
		}
	}
	
	public void windowActivated(WindowEvent evt) {
		((JTextFieldDateEditor)this.txtDtIni.getDateEditor()).requestFocus();
	}
	
	private void loadTree()
    {
        DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
        CheckNode root = new CheckNode("Contas");
        root.setIconName("root");
        model.setRoot(root);
        
        List<Conta> contas = this.scoreBusinessDelegate.getContasPorNivel("%.0");
        Collections.sort(contas,ContaOrdenador.Nivel);
        
        for (Conta conta : contas) {
        	CheckNode parent = new CheckNode(conta);
        	parent.setIconName("node");
            List<Conta> filhas = new ArrayList<Conta>(conta.getContasFilhos());
            Collections.sort(filhas,ContaOrdenador.Nivel);
            this.addNodeTree(parent,filhas.iterator());
            root.add(parent);
		}
        
        this.tree.setModel(model);
        this.tree.updateUI();
    }
	
    private void addNodeTree(DefaultMutableTreeNode parent, Iterator children)
    {
        while(children.hasNext())
        {
            Conta conta = (Conta)children.next();
            CheckNode node = new CheckNode(conta);
            node.setIconName("leaf");
            if ( conta.getContasFilhos().size() > 0 )
            {
            	List<Conta> filhas = new ArrayList<Conta>(conta.getContasFilhos());
                Collections.sort(filhas,ContaOrdenador.Nivel);
                node.setIconName("node");
                this.addNodeTree(node,filhas.iterator());
            }
            parent.add(node);
        }
    }

}
