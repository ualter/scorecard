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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import br.ujr.components.gui.field.UjrNumberField;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.banco.BancoOrdenador;
import br.ujr.scorecard.util.Util;

/**
 * @author ualter.junior
 */
public class BancoFrame extends AbstractDialog implements FocusListener, MouseListener {
	
	protected DefaultModelTabela   modelList     = null;
	protected JTable        tableList     = null;
	protected int           HEIGHT_LIST   = 200;
	protected int           HEIGHT_MNT    = 195;
	protected UjrTextField  txtNome       = new UjrTextField();
	protected UjrTextField  txtCodigo     = new UjrTextField();
	protected UjrNumberField  txtDiaVencVisa       = new UjrNumberField();
	protected UjrNumberField  txtDiaVencMastercard = new UjrNumberField();
	protected ScorecardManager scorecardManager = (ScorecardManager)Util.getBean("scorecardManager");
	private JButton btnExcluir;
	private JButton btnNovo;
	private JButton btnOk;
	private JButton btnSair;
	private JButton btnCancelar;
	private Banco   loadedBanco;
	
	public BancoFrame(JFrame owner) {
		super(owner);
		this.title = "Bancos";
		this.createUI();
		this.componentsReady();
	}

	protected void createUI() {
		this.width = 600;
		this.height = 450;
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
		int LBL_WIDTH = 160;
		
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
		JLabel lblNome = new JLabel("Nome:");
		lblNome.setBounds(10,Y,LBL_WIDTH,20);
		txtNome.setBounds(LBL_WIDTH,Y,300,20);
		txtNome.setEnabled(false);
		
		Y += 30;
		JLabel lblDiaVencVisa = new JLabel("Dia Venc. Visa:");
		lblDiaVencVisa.setBounds(10,Y,LBL_WIDTH,20);
		txtDiaVencVisa.setBounds(LBL_WIDTH,Y,40,20);
		txtDiaVencVisa.setEnabled(false);
		
		Y += 30;
		JLabel lblDiaVencMastercard = new JLabel("Dia Venc. Mastercard:");
		lblDiaVencMastercard.setBounds(10,Y,LBL_WIDTH,20);
		txtDiaVencMastercard.setBounds(LBL_WIDTH,Y,40,20);
		txtDiaVencMastercard.setEnabled(false);
		
		panel.add(btnOk);
		panel.add(btnCancelar);
		panel.add(txtCodigo);
		panel.add(lblCodigo);
		panel.add(lblNome);
		panel.add(lblDiaVencVisa);
		panel.add(lblDiaVencMastercard);
		panel.add(txtNome);
		panel.add(txtDiaVencVisa);
		panel.add(txtDiaVencMastercard);
		
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
		Util.setToolTip(this, btnNovo, "Criar novo banco");
		Util.setToolTip(this, btnExcluir, "Excluir o banco selecionado");
		Util.setToolTip(this, btnSair, "Sair");
		btnExcluir.setEnabled(false);
		btnNovo.setEnabled(true);
		btnSair.setEnabled(true);
		
		btnNovo.addFocusListener(this);
		btnExcluir.addFocusListener(this);
		btnSair.addFocusListener(this);
		
		panel.add(btnExcluir);
		panel.add(btnNovo);
		panel.add(btnSair);
	}

	private void layOutTableList() {
		TableColumn idColumn    = this.tableList.getColumnModel().getColumn(0);
		TableColumn nomeColumn  = this.tableList.getColumnModel().getColumn(1);
		TableColumn diaVencVisaColumn  = this.tableList.getColumnModel().getColumn(2);
		TableColumn diaVencMastercardColumn  = this.tableList.getColumnModel().getColumn(3);
		
		DefaultTableCellRenderer idRenderer = new DefaultTableCellRenderer();
		DefaultTableCellRenderer nomeRenderer = new DefaultTableCellRenderer();
		DefaultTableCellRenderer diaVencVisaRenderer = new DefaultTableCellRenderer();
		DefaultTableCellRenderer diaVencMastercardRenderer = new DefaultTableCellRenderer();
		
		idRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		nomeRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		diaVencVisaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		diaVencMastercardRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		
		idColumn.setCellRenderer(idRenderer);
		nomeColumn.setCellRenderer(nomeRenderer);
		diaVencVisaColumn.setCellRenderer(diaVencVisaRenderer);
		diaVencMastercardColumn.setCellRenderer(diaVencMastercardRenderer);
		
		idColumn.setPreferredWidth(50);
		nomeColumn.setPreferredWidth(250);
		diaVencVisaColumn.setPreferredWidth(120);
		diaVencMastercardColumn.setPreferredWidth(120);
	}
	
	private void loadList() {
		this.modelList = 
			new DefaultModelTabela(null,
				new Object[]{ "Código","Nome", "Dia Venc. Visa", "Dia Venc. Mastercard" });
		
		List<Banco> bancos = this.scorecardManager.listarBanco();
		Collections.sort(bancos,BancoOrdenador.NOME);
		for (Banco banco : bancos) {
			Object row[] = new Object[]{banco.getId(), banco.getNome(), banco.getDiaVencimentoVisa(), banco.getDiaVencimentoMastercard()};
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
			if ( this.loadedBanco != null ) {
				boolean isAllOk = this.isAllOk();
				if ( isAllOk ) {
					this.loadedBanco.setNome(this.txtNome.getText());
					this.loadedBanco.setDiaVencimentoVisa(this.diaVencVisa);
					this.loadedBanco.setDiaVencimentoMastercard(this.diaVencMastercard);
					this.scorecardManager.saveBanco(this.loadedBanco);
					this.loadedBanco = null;
				}
			} else {
				boolean isAllOk = this.isAllOk();
				if ( isAllOk ) {
					Banco banco = new Banco();
					banco.setNome(this.txtNome.getText());
					banco.setDiaVencimentoMastercard(this.diaVencMastercard);
					banco.setDiaVencimentoVisa(this.diaVencVisa);
					this.scorecardManager.saveBanco(banco);
				}
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
				if ( !this.scorecardManager.isBancoRemovable(loadedBanco) ) {
					JOptionPane.showMessageDialog(this,"Este Banco possui Conta(s) Corrente(s) associadas.\n Exclusão não permitida!","Atenção",JOptionPane.WARNING_MESSAGE);
				} else {
					this.scorecardManager.deleteBanco(this.loadedBanco);
					this.loadedBanco = null;
					this.resetScreen();
				}
			}
		}
	}

	private int diaVencVisa = 0, diaVencMastercard = 0;
	
	private boolean isAllOk() {
		boolean isAllOk = true;
		
		try {
			this.diaVencVisa = Integer.parseInt(this.txtDiaVencVisa.getText());
			this.diaVencMastercard = Integer.parseInt(this.txtDiaVencMastercard.getText());
		} catch (NumberFormatException e) {
			this.diaVencVisa = 0;
			this.diaVencMastercard = 0;
		}
		
		if ( diaVencVisa < 1 || diaVencVisa > 31 ) {
			JOptionPane.showMessageDialog(this,"Dia Vencimento Cartão Visa inválido!","Atenção",JOptionPane.WARNING_MESSAGE);
			isAllOk = false;
		}
		if ( diaVencMastercard < 1 || diaVencMastercard > 31 ) {
			JOptionPane.showMessageDialog(this,"Dia Vencimento Cartão Mastercard inválido!","Atenção",JOptionPane.WARNING_MESSAGE);
			isAllOk = false;
		}
		return isAllOk;
	}
	
	public void focusGained(FocusEvent evt) {
		if ( evt.getSource() instanceof JButton ) {
			getRootPane().setDefaultButton((JButton)evt.getSource());
		}
	}

	public void focusLost(FocusEvent evt) {
		if ( evt.getSource() instanceof JButton ) {
			getRootPane().setDefaultButton(null);
		}
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
	}
	
	public static void main(String[] args) {
		BancoFrame bancoFrame = new BancoFrame(null);
		bancoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		bancoFrame.setVisible(true);
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
			this.loadedBanco = this.scorecardManager.getBancoPorId(id);
			this.prepararAlteracaoExclusao();
		}
	}
	
	private void prepararAlteracaoExclusao() {
		this.btnCancelar.setEnabled(true);
		this.btnOk.setEnabled(true);
		this.btnExcluir.setEnabled(true);
		this.txtNome.setEnabled(true);
		this.txtDiaVencVisa.setEnabled(true);
		this.txtDiaVencMastercard.setEnabled(true);
		this.btnNovo.setEnabled(false);
		this.txtCodigo.setText("" + this.loadedBanco.getId());
		this.txtNome.setText(this.loadedBanco.getNome());
		this.txtDiaVencVisa.setText(String.valueOf(this.loadedBanco.getDiaVencimentoVisa()));
		this.txtDiaVencMastercard.setText(String.valueOf(this.loadedBanco.getDiaVencimentoMastercard()));
		this.txtNome.selectAll();
		this.txtNome.requestFocus();
		this.btnSair.setEnabled(false);
		this.setTitle("Bancos - Alterando: " + this.loadedBanco.getId() + "-" + this.loadedBanco.getNome());
	}
	
	private void prepararInsercao() {
		this.btnCancelar.setEnabled(true);
		this.btnOk.setEnabled(true);
		this.btnExcluir.setEnabled(false);
		this.txtNome.setEnabled(true);
		this.btnNovo.setEnabled(false);
		this.txtDiaVencVisa.setEnabled(false);
		this.txtDiaVencMastercard.setEnabled(false);
		this.txtCodigo.setText("");
		this.txtNome.setText("");
		this.txtNome.requestFocus();
		this.btnSair.setEnabled(false);
		this.setTitle("Bancos - Inserindo... ");
	}
	
	private void resetScreen() {
		this.btnCancelar.setEnabled(false);
		this.btnOk.setEnabled(false);
		this.btnExcluir.setEnabled(false);
		this.txtNome.setEnabled(false);
		this.btnNovo.setEnabled(true);
		this.btnExcluir.setEnabled(false);
		this.txtCodigo.setText("");
		this.txtNome.setText("");
		this.txtDiaVencVisa.setText("");
		this.txtDiaVencMastercard.setText("");
		this.btnSair.setEnabled(true);
		this.setTitle("Bancos");
		
		this.loadList();
		this.tableList.setModel(this.modelList);
		this.layOutTableList();
		this.tableList.clearSelection();
		
		this.loadedBanco = null;
	}
}
