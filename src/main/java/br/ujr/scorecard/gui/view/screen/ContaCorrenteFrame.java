package br.ujr.scorecard.gui.view.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.UjrNumberField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.gui.view.screen.passivo.ChequeFrame;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.cc.ContaCorrenteOrdenador;
import br.ujr.scorecard.util.Util;

/**
 * @author ualter.junior
 */
public class ContaCorrenteFrame extends AbstractDialog implements FocusListener, MouseListener {
	
	protected DefaultModelTabela    modelList     = null;
	protected JTable         tableList     = null;
	protected int            HEIGHT_LIST   = 200;
	protected int            HEIGHT_MNT    = 220;
	protected UjrTextField   txtNome       = new UjrTextField();
	protected UjrTextField   txtCodigo     = new UjrTextField();
	protected UjrTextField   txtNumero     = new UjrTextField();
	protected UjrComboBox    txtBanco      = new UjrComboBox();
	protected UjrNumberField txtOrdem      = new UjrNumberField();
	protected ScorecardBusinessDelegate scoreBusinessDelegate = ScorecardBusinessDelegate.getInstance();
	private JButton btnExcluir;
	private JButton btnNovo;
	private JButton btnOk;
	private JButton btnSair;
	private JButton btnCancelar;
	private ContaCorrente   loadedContaCorrente;
	
	public ContaCorrenteFrame(JFrame owner) {
		super(owner);
		this.title = "Contas Correntes";
		this.createUI();
		this.componentsReady();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ContaCorrente cc = ScorecardBusinessDelegate.getInstance().getContaCorrentePorId(64);
				JFrame frame = new JFrame();
				frame.setLayout(null);
				frame.setBounds(10, 10, 200, 200);
				JPanel panel = new JPanel();
				panel.setLayout(null);
				frame.setContentPane(panel);
				frame.setVisible(true);
				try {
					ContaCorrenteFrame f = new ContaCorrenteFrame(frame);
					f.setVisible(true);
				} catch (Throwable e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
	}

	protected void createUI() {
		this.width = 640;
		this.height = 480;
		super.createUI();
		
		JPanel panList = new JPanel();
		JPanel panMnt  = new JPanel();
		panList.setLayout(null);
		panMnt.setLayout(null);
		panList.setBorder(BorderFactory.createEtchedBorder());
		panMnt.setBorder(BorderFactory.createEtchedBorder());

		buildPanelList(panList);
		buildPanelMnt(panMnt);

		panList.setBounds(10, 10, this.getWidth() - 23, HEIGHT_LIST);
		panMnt.setBounds(10, HEIGHT_LIST + 20, this.getWidth() - 23, HEIGHT_MNT);
		
		panMain.add(panList);
		panMain.add(panMnt);
	}

	/**
	 * @param panBtnAcoes
	 */
	private void buildPanelMnt(JPanel panel) {
		int LBL_WIDTH = 80;
		
		int btn_x = (this.getWidth() - 100) / 2;
		btnOk = new JButton();
		btnCancelar = new JButton();
		btnOk.setBounds(btn_x, 10, 50, 45);
		btnCancelar.setBounds(btn_x + 50, 10, 50, 45);
		btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		btnCancelar.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		btnOk.setActionCommand("OK");
		btnCancelar.setActionCommand("CANCELAR");
		btnOk.addActionListener(this);
		btnCancelar.addActionListener(this);
		btnCancelar.setEnabled(false);
		btnOk.setEnabled(false);
		Util.setToolTip(this, btnCancelar, "Cancelar edição");
		Util.setToolTip(this, btnOk, "Confirmar edição");
		
		int Y = 65;
		JLabel lblCodigo = new JLabel("Código:");
		lblCodigo.setBounds(10,	Y, LBL_WIDTH, 20);
		this.txtCodigo = new UjrTextField();
		this.txtCodigo.setBounds(LBL_WIDTH, Y, 30, 20);
		this.txtCodigo.setEnabled(false);
		this.txtCodigo.setFocusable(false);
		this.txtCodigo.setDisabledTextColor(Color.DARK_GRAY);
		
		Y += 30;
		JLabel lblNome = new JLabel("Descrição:");
		lblNome.setBounds(10,Y,LBL_WIDTH,20);
		txtNome.setBounds(LBL_WIDTH,Y,300,20);
		txtNome.setEnabled(false);
		
		Y += 30;
		JLabel lblNumero = new JLabel("Número:");
		lblNumero.setBounds(10,Y,LBL_WIDTH,20);
		txtNumero.setBounds(LBL_WIDTH,Y,100,20);
		txtNumero.setEnabled(false);
		
		Y += 30;
		JLabel lblBanco = new JLabel("Banco:");
		lblBanco.setBounds(10,Y,LBL_WIDTH,20);
		txtBanco.setBounds(LBL_WIDTH,Y,200,20);
		txtBanco.setEnabled(false);
		List<Banco> bancos = this.scoreBusinessDelegate.listarBanco();
		for (Banco banco : bancos) {
			txtBanco.addItem(banco);
		}
		
		Y += 30;
		JLabel lblOrdem = new JLabel("Ordem:");
		lblOrdem.setBounds(10,Y,LBL_WIDTH,20);
		txtOrdem.setBounds(LBL_WIDTH,Y,30,20);
		txtOrdem.setEnabled(false);
				
		panel.add(btnOk);
		panel.add(btnCancelar);
		panel.add(txtCodigo);
		panel.add(lblCodigo);
		panel.add(lblNome);
		panel.add(lblNumero);
		panel.add(lblBanco);
		panel.add(lblOrdem);
		panel.add(txtNome);
		panel.add(txtNumero);
		panel.add(txtBanco);
		panel.add(txtOrdem);
	}
	
	private void buildPanelList(JPanel panel) {
		this.loadList();
		this.tableList = new JTable(this.modelList){
			private static final long serialVersionUID = -8973196493153799384L;
			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		this.tableList.setName("LIST");
		this.tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableList.addMouseListener(this);
		this.tableList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.tableList.setPreferredScrollableViewportSize(new Dimension(350, 80));
		this.tableList.setFont(new Font("Courier New",Font.PLAIN,11));
		
		this.layOutTableList();
		
		JScrollPane jScrollPane = new JScrollPane(this.tableList);
		jScrollPane.setBounds(10, 64, this.getWidth() - 43, HEIGHT_LIST - 75);
		panel.add(jScrollPane);
		
		int btn_x = (this.getWidth() - 150) / 2;
		btnExcluir = new JButton();
		btnNovo = new JButton();
		btnSair = new JButton();
		btnExcluir.setBounds(btn_x, 10, 50, 45);
		btnNovo.setBounds(btn_x + 50, 10, 50, 45);
		btnSair.setBounds(btn_x + 100, 10, 50, 45);
		btnExcluir.setIcon(new ImageIcon(Util.loadImage(this, "trash.png")));
		btnNovo.setIcon(new ImageIcon(Util.loadImage(this, "edit_add.png")));
		btnSair.setIcon(new ImageIcon(Util.loadImage(this, "exit.png")));
		btnExcluir.setActionCommand("EXCLUIR");
		btnNovo.setActionCommand("NOVO");
		btnSair.setActionCommand("SAIR");
		btnExcluir.addActionListener(this);
		btnNovo.addActionListener(this);
		btnSair.addActionListener(this);
		Util.setToolTip(this, btnNovo, "Criar novo contaCorrente");
		Util.setToolTip(this, btnExcluir, "Excluir o contaCorrente selecionado");
		Util.setToolTip(this, btnSair, "Sair");
		btnExcluir.setEnabled(false);
		btnNovo.setEnabled(true);
		btnSair.setEnabled(true);
		
		panel.add(btnExcluir);
		panel.add(btnNovo);
		panel.add(btnSair);
	}

	private void layOutTableList() {
		TableColumn idColumn      = this.tableList.getColumnModel().getColumn(0);
		TableColumn nomeColumn    = this.tableList.getColumnModel().getColumn(1);
		TableColumn numeroColumn  = this.tableList.getColumnModel().getColumn(2);
		TableColumn bancoColumn   = this.tableList.getColumnModel().getColumn(3);
		TableColumn ordemColumn   = this.tableList.getColumnModel().getColumn(4);
		
		DefaultTableCellRenderer idRenderer     = new DefaultTableCellRenderer();
		DefaultTableCellRenderer nomeRenderer   = new DefaultTableCellRenderer();
		DefaultTableCellRenderer numeroRenderer = new DefaultTableCellRenderer();
		DefaultTableCellRenderer bancoRenderer  = new DefaultTableCellRenderer();
		DefaultTableCellRenderer ordemRenderer  = new DefaultTableCellRenderer();
		
		idRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		nomeRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		numeroRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		bancoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		ordemRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		
		idColumn.setCellRenderer(idRenderer);
		nomeColumn.setCellRenderer(nomeRenderer);
		numeroColumn.setCellRenderer(numeroRenderer);
		bancoColumn.setCellRenderer(bancoRenderer);
		ordemColumn.setCellRenderer(ordemRenderer);
		
		idColumn.setPreferredWidth(30);
		nomeColumn.setPreferredWidth(230);
		numeroColumn.setPreferredWidth(80);
		bancoColumn.setPreferredWidth(200);
		ordemColumn.setPreferredWidth(40);
	}
	
	private void loadList() {
		this.modelList = 
			new DefaultModelTabela(null,
				new Object[]{ "Cód.","Descrição","Número", "Banco","Ordem" });
		
		List<ContaCorrente> contaCorrentes = this.scoreBusinessDelegate.listarContaCorrente();
		Collections.sort(contaCorrentes,ContaCorrenteOrdenador.ORDEM);
		for (ContaCorrente contaCorrente : contaCorrentes) {
			Object row[] = new Object[]{
					contaCorrente.getId(), 
					contaCorrente.getDescricao(),
					contaCorrente.getNumero(),
					contaCorrente.getBanco().getNome(),
					contaCorrente.getOrdem()};
			this.modelList.addRow(row);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if ( "SAIR".equalsIgnoreCase(action)) {
			this.dispose();
		} else
		if ( "OK".equalsIgnoreCase(action) ) {
			if ( this.loadedContaCorrente != null ) {
				this.loadedContaCorrente.setDescricao(this.txtNome.getText());
				this.loadedContaCorrente.setNumero(this.txtNumero.getText());
				this.loadedContaCorrente.setBanco((Banco)this.txtBanco.getSelectedItem());
				this.loadedContaCorrente.setOrdem(new Integer(this.txtOrdem.getText()));
				this.scoreBusinessDelegate.saveContaCorrente(this.loadedContaCorrente);
				this.loadedContaCorrente = null;
				((ScorecardGUI)this.getParent()).refreshContas();
			} else {
				ContaCorrente contaCorrente = new ContaCorrente();
				contaCorrente.setDescricao(this.txtNome.getText());
				contaCorrente.setNumero(this.txtNumero.getText());
				contaCorrente.setBanco((Banco)this.txtBanco.getSelectedItem());
				contaCorrente.setOrdem(new Integer(this.txtOrdem.getText()));
				this.scoreBusinessDelegate.saveContaCorrente(contaCorrente);
				((ScorecardGUI)this.getParent()).refreshContas();
			}
			this.resetScreen();
		} else
		if ( "CANCELAR".equalsIgnoreCase(action) ) {
			this.resetScreen();
		} else
		if ( "NOVO".equalsIgnoreCase(action) ) {
			this.prepararInsercao();
		} else
		if ( "EXCLUIR".equalsIgnoreCase(action) ) {
			int resp = JOptionPane.showConfirmDialog(this, "Confirma a Exclusão ?","Exclusão",
					   JOptionPane.YES_NO_OPTION,
					   JOptionPane.QUESTION_MESSAGE);
			if ( resp == 0 ) {
				if ( !this.scoreBusinessDelegate.isContaCorrenteRemovable(loadedContaCorrente) ) {
					JOptionPane.showMessageDialog(this,"Este Conta Corrente possui Movimento Financeiro associado.\n Exclusão não permitida!","Atenção",JOptionPane.WARNING_MESSAGE);
				} else {
					this.scoreBusinessDelegate.deleteContaCorrente(this.loadedContaCorrente);
					this.loadedContaCorrente = null;
					((ScorecardGUI)this.getParent()).refreshContas();
					this.resetScreen();
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
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		String nameComponent = ((Component)e.getSource()).getName();
		if ( "LIST".equals(nameComponent)) {
			int id = ((Integer)this.tableList.getValueAt(this.tableList.getSelectedRow(), 0)).intValue();
			this.loadedContaCorrente = this.scoreBusinessDelegate.getContaCorrentePorId(id);
			this.prepararAlteracaoExclusao();
		}
	}
	
	private void prepararAlteracaoExclusao() {
		this.btnCancelar.setEnabled(true);
		this.btnOk.setEnabled(true);
		this.btnExcluir.setEnabled(true);
		this.txtNome.setEnabled(true);
		this.txtNumero.setEnabled(true);
		this.txtBanco.setEnabled(true);
		this.txtOrdem.setEnabled(true);
		this.btnNovo.setEnabled(false);
		this.txtCodigo.setText("" + this.loadedContaCorrente.getId());
		this.txtNome.setText(this.loadedContaCorrente.getDescricao());
		this.txtNumero.setText(this.loadedContaCorrente.getNumero());
		this.txtBanco.setSelectedItem(this.loadedContaCorrente.getBanco());
		this.txtOrdem.setText(this.loadedContaCorrente.getOrdem().toString());
		this.txtNome.selectAll();
		this.txtNome.requestFocus();
		this.btnSair.setEnabled(false);
		this.setTitle("ContaCorrentes - Alterando: " + this.loadedContaCorrente.getId() + "-" + this.loadedContaCorrente.getDescricao());
	}
	
	private void prepararInsercao() {
		this.btnCancelar.setEnabled(true);
		this.btnOk.setEnabled(true);
		this.btnExcluir.setEnabled(false);
		this.txtNome.setEnabled(true);
		this.txtNumero.setEnabled(true);
		this.txtBanco.setEnabled(true);
		this.txtOrdem.setEnabled(true);
		this.btnNovo.setEnabled(false);
		this.txtCodigo.setText("");
		this.txtNome.setText("");
		this.txtNumero.setText("");
		this.txtBanco.setSelectedIndex(0);
		this.txtNome.requestFocus();
		this.btnSair.setEnabled(false);
		this.setTitle("ContaCorrentes - Inserindo... ");
	}
	
	private void resetScreen() {
		this.btnCancelar.setEnabled(false);
		this.btnOk.setEnabled(false);
		this.btnExcluir.setEnabled(false);
		this.txtNome.setEnabled(false);
		this.txtNumero.setEnabled(false);
		this.txtBanco.setEnabled(false);
		this.txtOrdem.setEnabled(false);
		this.btnNovo.setEnabled(true);
		this.btnExcluir.setEnabled(false);
		this.txtCodigo.setText("");
		this.txtNome.setText("");
		this.txtNumero.setText("");
		this.txtBanco.setSelectedIndex(0);
		this.btnSair.setEnabled(true);
		this.setTitle("ContaCorrentes");
		
		this.loadList();
		this.tableList.setModel(this.modelList);
		this.layOutTableList();
		this.tableList.clearSelection();
		
		this.loadedContaCorrente = null;
	}
}
