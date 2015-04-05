package br.ujr.scorecard.gui.view.screen;


import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
public class ContaFrame extends JDialog implements ActionListener, MouseListener {
    
	private static final long serialVersionUID = 1590966194914237864L;
	protected final int NULL_VALUE = 0; 
    protected HashMap points  = new HashMap();
    protected HashMap widths  = new HashMap(); 
    protected HashMap heights = new HashMap();
    
	private ScorecardManager manager = (ScorecardManager)Util.getBean("scorecardManager");
    private String          titulo  = "Contas Contábeis";
    private String          modoEdicao;
    
    private Conta selectedConta = null;
    
    /** Creates new form Conta */
    public ContaFrame() {
        initComponents();
    }
    public ContaFrame(JDialog dialog) {
    	super(dialog,true);
    	initComponents();
    }
    public ContaFrame(JFrame frame) {
    	super(frame,true);
    	initComponents();
    }
    
    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        tree         = new javax.swing.JTree();
        jPanel1      = new javax.swing.JPanel();
        txtNivel     = new javax.swing.JFormattedTextField();
        lblNivel     = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JFormattedTextField();
        lblDescricao = new javax.swing.JLabel();
        btnOk        = new javax.swing.JButton();
        btnCancelar  = new javax.swing.JButton();
        btnNovo      = new javax.swing.JButton();
        btnExcluir   = new javax.swing.JButton();
        jPanel2      = new javax.swing.JPanel();
        btnSair      = new javax.swing.JButton();

        tree.setFont(new Font("Courier New",Font.PLAIN,12));
        getContentPane().setLayout(null);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        setTitle(this.titulo);
        this.loadTree();
        this.tree.addMouseListener(this);
        this.tree.putClientProperty("JTree.lineStyle", "Horizontal");
        jScrollPane1.setViewportView(tree);
        
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 20, 750, 425);

        jPanel1.setLayout(null);

        this.savePoint("P01", 90, 74, NULL_VALUE,19);
        
        jPanel1.setBorder(new javax.swing.border.EtchedBorder());
        txtNivel.setEnabled(false);
        jPanel1.add(txtNivel);
        txtNivel.setBounds(this.getRectangle("P01", 119, NULL_VALUE));
        
        lblNivel.setLabelFor(txtNivel);
        lblNivel.setText("N\u00edvel:");
        lblNivel.setEnabled(false);
        jPanel1.add(lblNivel);
        lblNivel.setBounds(this.getRectangle("P01",30,15,-70,4));
        
        txtDescricao.setEnabled(false);
        jPanel1.add(txtDescricao);
        txtDescricao.setBounds(this.getRectangle("P01", 410, NULL_VALUE, NULL_VALUE, 30, true));

        lblDescricao.setLabelFor(txtDescricao);
        lblDescricao.setText("Descri\u00e7\u00e3o:");
        lblDescricao.setEnabled(false);
        jPanel1.add(lblDescricao);
        lblDescricao.setBounds(this.getRectangle("P01", 70,15,-70,4,true));

        this.savePoint("BTN", 160, 15, 50, 45);
        int btnSpace  = 0;
        
        btnCancelar.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(this);
        btnCancelar.setMnemonic('C');
        btnCancelar.setActionCommand("CANCELAR");
        jPanel1.add(btnCancelar);
        btnCancelar.setBounds(this.getRectangle("BTN"));
        Util.setToolTip(this,btnCancelar,"Cancelar a Operação");
        
        jPanel1.add(btnOk);
        btnOk.setMnemonic('O');
        btnOk.setActionCommand("OK");
        btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
        btnOk.setEnabled(false);
        btnOk.addActionListener(this);
        btnOk.setBounds(this.incrementX("BTN", this.getPointWidth("BTN") + btnSpace));
        Util.setToolTip(this,btnOk, "Salvar a Operação");
        
        btnExcluir.setIcon(new ImageIcon(Util.loadImage(this, "trash.png")));
        btnExcluir.setMnemonic('E');
        btnExcluir.setActionCommand("EXCLUIR");
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(this);
        jPanel1.add(btnExcluir);
        btnExcluir.setBounds(this.incrementX("BTN", this.getPointWidth("BTN") + btnSpace));
        Util.setToolTip(this,btnExcluir, "Excluir esta Conta Contábil");
        
        btnNovo.setMnemonic('N');
        btnNovo.setActionCommand("NOVO");
        btnNovo.setIcon(new ImageIcon(Util.loadImage(this, "edit_add.png")));
        btnNovo.addActionListener(this);
        jPanel1.add(btnNovo);
        btnNovo.setBounds(this.incrementX("BTN", this.getPointWidth("BTN") + btnSpace));
        Util.setToolTip(this,btnNovo, "Criar uma nova Conta Contábil");

        getContentPane().add(jPanel1);
        jPanel1.setBounds(20, 455, 525, 140);

        jPanel2.setLayout(null);

        jPanel2.setBorder(new javax.swing.border.EtchedBorder());
        btnSair.setMnemonic('S');
        btnSair.setIcon(new ImageIcon(Util.loadImage(this, "exit.png")));
        btnSair.setActionCommand("SAIR");
        btnSair.addActionListener(this);
        jPanel2.add(btnSair);
        btnSair.setBounds(79, 47, 50, 45);
        Util.setToolTip(btnSair,btnSair,"Fechar");

        getContentPane().add(jPanel2);
        jPanel2.setBounds(555, 455, 215, 140);

        setBounds(UtilGUI.getRectangle(800,650));
    }
    
    private void loadTree()
    {
        DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Contas");
        model.setRoot(root);
        
        List<Conta> contas = this.manager.getContasPorNivel("%.0");
        Collections.sort(contas,ContaOrdenador.Nivel);
        
        for (Conta conta : contas) {
        	DefaultMutableTreeNode parent = new DefaultMutableTreeNode(conta);
            List<Conta> filhas = new ArrayList<Conta>(conta.getContasFilhos());
            Collections.sort(filhas,ContaOrdenador.Nivel);
            this.addNodeTree(parent,filhas.iterator());
            root.add(parent);
		}
        
        this.tree.setModel(model);
        this.expandAll();
        this.collapseRoots();
        this.tree.updateUI();
    }
    private void collapseRoots()
    {
    	TreeNode root   = (TreeNode)this.tree.getModel().getRoot();
    	TreePath parent = new TreePath(root);
        TreeNode node   = (TreeNode)parent.getLastPathComponent();
        if ( node.getChildCount() > 0 )
        {
	        for(Enumeration e = node.children();e.hasMoreElements();)
	        {
	            TreeNode n    = (TreeNode)e.nextElement();
	            TreePath path = parent.pathByAddingChild(n);
	            this.tree.collapsePath(path);
	        }
        }
        
    }
    private void expandAll()
    {
        TreeNode root = (TreeNode)this.tree.getModel().getRoot();
        TreePath path = new TreePath(root);
        this.expandAll(path);
    }
    private void expandAll(TreePath parent)
    {
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if ( node.getChildCount() > 0 )
        {
	        for(Enumeration e = node.children();e.hasMoreElements();)
	        {
	            TreeNode n    = (TreeNode)e.nextElement();
	            TreePath path = parent.pathByAddingChild(n);
	            expandAll(path);
	        }
        }
        this.tree.expandPath(parent);
    }
    
    private void addNodeTree(DefaultMutableTreeNode parent, Iterator children)
    {
        while(children.hasNext())
        {
            Conta conta = (Conta)children.next();
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(conta);
            if ( conta.getContasFilhos().size() > 0 )
            {
            	List<Conta> filhas = new ArrayList<Conta>(conta.getContasFilhos());
                Collections.sort(filhas,ContaOrdenador.Nivel);
                
                this.addNodeTree(node,filhas.iterator());
            }
            parent.add(node);
        }
    }
    
    
    // Variables declaration - do not modify
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree tree;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblNivel;
    private javax.swing.JFormattedTextField txtDescricao;
    private javax.swing.JFormattedTextField txtNivel;
    // End of variables declaration
    
    public static void main(String[] args)
    {
    	SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				ContaFrame contaFrame = new ContaFrame();
		    	contaFrame.setVisible(true);
			}
    	});
    }

    /**
     * Ações
     */
    public void actionPerformed(ActionEvent evt)
    {
        String act = evt.getActionCommand();
        if ("SAIR".equals(act))
        {
            this.dispose();
        }
        else
        if ("NOVO".equals(act))
        {
            this.prepararNovo();   
        }
        else
        if ("OK".equals(act))
        {
            if ( "I".equals(this.modoEdicao) )
            {
                 this.salvar();
            }
            else
            if ( "A".equals(this.modoEdicao) )
            {
                 this.salvar();
            }
        }
        else
        if ("CANCELAR".equals(act))
        {
            this.limpar();
        }
        else
        if ("EXCLUIR".equals(act))
        {
            this.excluir();
        }
    }
    
    private void prepararNovo()
    {
        Conta conta = this.getSelectedConta();
        String nivel = conta != null ? conta.getNivel() : null;
        nivel = this.manager.getContaProximoNivel(nivel);
        
        this.lblNivel.setEnabled(true);
        this.lblDescricao.setEnabled(true);
        this.txtNivel.setText("");
        this.txtNivel.setEnabled(true);
        this.txtDescricao.setEnabled(true);
        this.txtDescricao.setText("");
        this.btnCancelar.setEnabled(true);
        this.btnOk.setEnabled(true);
        this.btnExcluir.setEnabled(false);
        this.btnNovo.setEnabled(false);
        this.modoEdicao = "I";
        this.txtNivel.setText(nivel);
        
        String contaDescr = conta != null ? conta.getDescricao() : "Contas";
        String contaNivel = conta != null ? conta.getNivel() : "0.0(Raiz)";
        this.setEditionInformation("[Inserção] Criando conta em " + contaNivel + " - " + contaDescr);
    }
    private void limpar()
    {
        this.lblNivel.setEnabled(false);
        this.lblDescricao.setEnabled(false);
        this.txtNivel.setEnabled(false);
        this.txtDescricao.setEnabled(false);
        this.txtDescricao.setText("");
        this.btnCancelar.setEnabled(false);
        this.btnOk.setEnabled(false);
        this.btnExcluir.setEnabled(false);
        this.btnNovo.setEnabled(true);
        this.modoEdicao = "";
        this.setEditionInformation(null);
    }
    private void prepararEdicao(Conta conta)
    {
        this.lblNivel.setEnabled(true);
        this.lblDescricao.setEnabled(true);
        this.txtDescricao.setEnabled(true);
        this.txtNivel.setText(conta.getNivel());
        this.txtDescricao.setText(conta.getDescricao());
        this.btnExcluir.setEnabled(true);
        this.btnOk.setEnabled(true);
        this.modoEdicao = "A";
        this.setEditionInformation("[Alteração] Editando conta " + conta.getNivel() + " - " + conta.getDescricao());
    }
    private void setEditionInformation(String state)
    {
        String msg = state != null ? this.titulo + " - " + state : this.titulo;
        this.setTitle(msg);
    }
    private Conta getSelectedConta()
    {
        Conta conta = null;
        TreePath path = this.tree.getSelectionPath();
        if ( path != null )
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
            if ( node.getUserObject() instanceof Conta )
            {
                 conta = (Conta)node.getUserObject();
            }
        }
        return conta;
    }
    
    private void salvar()
    {
        if (this.consistir())
        {
           TreePath               path = this.tree.getSelectionPath();
           DefaultMutableTreeNode node = null;
           if ( path != null )
           {
               node = (DefaultMutableTreeNode)path.getLastPathComponent();
           }
           
           if ( "I".equals(this.modoEdicao) )
           {
               Conta    parent = this.getSelectedConta();
               Conta    conta  = this.buildObject();
               if ( parent != null )
               {
                    conta.setContaPai(parent);
               }
               conta = this.manager.saveConta(conta);
               if ( node == null )
               {
                    node = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
               }
               DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(conta);
               node.add(newNode);
               
               this.tree.updateUI();
               this.prepararNovo();
           }
           else
           if ( "A".equals(this.modoEdicao) )
           {
               Conta conta       = this.getSelectedConta();
               Conta updateConta = this.buildObject();
               conta.setDescricao(updateConta.getDescricao());
               conta             = this.manager.saveConta(conta);
               if ( node != null )
               {
                   node.setUserObject(conta);
                   this.tree.updateUI();
               }
               this.limpar();
           }
        }
    }
    
    private void excluir()
    {
        Conta conta = this.getSelectedConta();
        this.manager.deleteConta(conta);
        TreePath path = this.tree.getSelectionPath();
        this.tree.removeSelectionPath(path);
        this.tree.updateUI();
    }
    
    private boolean consistir()
    {
        if (this.txtNivel.getText().trim().equals(""))
        {
            UtilGUI.showErrorMessage(this, "Nível inválido!");
            return false;
        }
        else
        if (this.txtDescricao.getText().trim().equals(""))
        {
            UtilGUI.showErrorMessage(this, "Descrição inválida!");
            return false;
        }
        return true;
    }
    private Conta buildObject()
    {
        String nivel      = this.txtNivel.getText().trim();
        String descricao  = this.txtDescricao.getText().trim();
        Conta conta = new Conta(nivel,descricao);
        return conta;
    }

    public void mouseClicked(MouseEvent evt)
    {
        if ( evt.getClickCount() == 1 )
        {
             if ( evt.getSource() instanceof JTree )
             {
	             JTree                  tree = (JTree)evt.getSource();
	             TreePath               path = tree.getSelectionPath();
	             if ( tree.getSelectionPath() != null )
	             {
		             DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		             if ( node.getUserObject() instanceof Conta )
		             {
		                 Conta conta = (Conta)node.getUserObject();
		                 this.prepararEdicao(conta);
		             }
	             }
             }
        } else
    	if ( evt.getClickCount() == 2 )
        {
    		if ( evt.getSource() instanceof JTree )
            {
	             JTree                  tree = (JTree)evt.getSource();
	             TreePath               path = tree.getSelectionPath();
	             if ( tree.getSelectionPath() != null )
	             {
		             DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		             if ( node.getUserObject() instanceof Conta )
		             {
		                 Conta conta = (Conta)node.getUserObject();
		                 this.selectedConta = conta;
		                 this.selectedConta.toStringMode = 1;
		                 this.dispose();
		             }
	             }
            }
        }
    }
    public void mouseEntered(MouseEvent evt)
    {
    }
    public void mouseExited(MouseEvent evt)
    {
    }
    public void mousePressed(MouseEvent evt)
    {
    }
    public void mouseReleased(MouseEvent evt)
    {
    }
    
    /**
     * Screen GUI Utilities
     */
    private Point getPoint(String point)
    {
        if ( this.points.containsKey(point) )
        {
            return (Point)this.points.get(point);
        }
        else
        {
            throw new RuntimeException("[Programmer] Não existe nenhum objeto \"Point\" configurado para o point:" + point + ".");
        }
    }
    protected void savePoint(String point, int x, int y)
    {
        this.savePoint(point, x, y, NULL_VALUE,NULL_VALUE);
    }
    protected void savePoint(String point, int x, int y, int widht, int height)
    {
        this.points.put(point, new Point(x,y));
        if ( widht != NULL_VALUE )
        {
            this.saveWidth(point, widht);
        }
        if ( height != NULL_VALUE )
        {
            this.saveHeight(point, height);
        }
    }
    private void saveHeight(String point, int height)
    {
        this.heights.put(point, new Integer(height));
    }
    private void saveWidth(String point, int widht)
    {
        this.widths.put(point, new Integer(widht));
    }
    
    protected Rectangle getRectangle(String point)
    {
        return this.getRectangle(point, NULL_VALUE, NULL_VALUE);
    }
    
    protected Rectangle getRectangle(String point, int width, int height)
    {
        return this.getRectangle(point, width, height, NULL_VALUE, NULL_VALUE);
    }
    
    protected Rectangle getRectangle(String point, int width, int height, int extraX, int extraY)
    {
        return this.getRectangle(point, width, height, extraX, extraY,false);
    }
    
    protected Rectangle getRectangle(String point, int width, int height, int extraX, int extraY, boolean save)
    {
        if ( width == NULL_VALUE )
        {
            width = ((Integer)this.widths.get(point)).intValue();
        }
        if ( height == NULL_VALUE )
        {
            height = ((Integer)this.heights.get(point)).intValue();
        }
        
        Point     p = this.getPoint(point);
        //Dimension d = new Dimension(width,height);
        Rectangle r = new Rectangle(p.x, p.y, width, height);
        if ( extraX != NULL_VALUE )
        {
            r.x += extraX;
        }
        if ( extraY != NULL_VALUE )
        {
            r.y += extraY;
        }
        if ( save )
        {
            p.x = r.x;
            p.y = r.y;
        }
        return r;
    }
    
    protected Rectangle increment(String point, int x, int y, int width, int height)
    {
        width  += this.getPointWidth(point);
        height += this.getPointHeight(point);
        return this.getRectangle(point, width, height, x, y, true);
    }
    
    protected Rectangle incrementX(String point, int x)
    {
        return this.getRectangle(point, NULL_VALUE, NULL_VALUE, x, NULL_VALUE, true);
    }
    protected Rectangle incrementY(String point, int y)
    {
        return this.getRectangle(point, NULL_VALUE, NULL_VALUE, NULL_VALUE, y, true);
    }
    protected Rectangle incrementPoint(String point, int x, int y)
    {
        return this.getRectangle(point, NULL_VALUE, NULL_VALUE, x, y, true);
    }
    
    protected Rectangle incrementWidth(String point, int width)
    {
        width += ((Integer)this.widths.get(point)).intValue();
        return this.getRectangle(point, width, NULL_VALUE, NULL_VALUE, NULL_VALUE, true);
    }
    protected Rectangle incrementHeight(String point, int height)
    {
        height += ((Integer)this.heights.get(point)).intValue();
        return this.getRectangle(point, NULL_VALUE, height, NULL_VALUE, NULL_VALUE, true);
    }
    protected Rectangle incrementDimension(String point, int width, int height)
    {
        return this.getRectangle(point, width, height, NULL_VALUE, NULL_VALUE, true);
    }
    
    protected int getPointWidth(String point)
    {
        if ( this.widths.containsKey(point) )
        {
            return ((Integer)this.widths.get(point)).intValue();
        }
        else
        {
            throw new RuntimeException("[Programmer] Não existe nenhum \"Width\" configurado para o point:" + point + ".");
        }
        
    }
    protected int getPointHeight(String point)
    {
        if ( this.heights.containsKey(point) )
        {
            return ((Integer)this.heights.get(point)).intValue();
        }
        else
        {
            throw new RuntimeException("[Programmer] Não existe nenhum \"Height\" configurado para o point:" + point + ".");
        }
        
    }
    
    public Conta getConta() {
    	return this.selectedConta;
    }
    
}

