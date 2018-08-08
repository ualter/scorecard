package br.ujr.scorecard.gui.view.screen.passivo;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.field.UjrTextField;
import br.ujr.components.gui.tabela.UjrTabelaParcela;
import br.ujr.components.gui.tabela.UjrTabelaParcelaModel;
import br.ujr.components.gui.tabela.UjrTabelaParcelaModelCheque;
import br.ujr.scorecard.gui.view.screen.ContaFrame;
import br.ujr.scorecard.gui.view.screen.DataCellEditor;
import br.ujr.scorecard.gui.view.screen.cellrenderer.DataTableCellRenderer;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.util.Util;

/**
 * @author ualter.junior
 */
public abstract class PassivoFrame extends AbstractDialog implements FocusListener, TableModelListener {

	protected JLabel                      lblDataMovimento  = new JLabel("Dt Movimento:");
	protected JDateChooser                txtDataMovimento  = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
	protected JLabel                      lblConta          = new JLabel("Conta:");
	protected JLabel                      lblHistorico      = new JLabel("Histórico:");
	protected UjrComboBox                 txtConta          = new UjrComboBox();
	protected UjrTextField                txtHistorico      = new UjrTextField();
	protected JButton                     btnOk             = new JButton(new ImageIcon("images/salvar.png"));
	protected JButton                     btnCancela        = new JButton(new ImageIcon("images/cancel.png"));
	protected UjrTabelaParcelaModel       modelParcelas     = null;
	protected UjrTabelaParcela            tabParcelas       = null;
	protected JButton                     btnParcelaInserir = new JButton(new ImageIcon("images/add.png"));
	protected JButton                     btnParcelaRemover = new JButton(new ImageIcon("images/remove.png"));
	protected JButton                     btnContas         = new JButton(new ImageIcon("images/search.png"));
	protected JButton                     btnFFParcelas     = new JButton(new ImageIcon("images/ff.png"));
	protected JButton                     btnREWParcelas    = new JButton(new ImageIcon("images/rew.png"));
	protected boolean                     isUpdate          = false;
	protected ContaCorrente               contaCorrente     = null;
	protected JLabel                      lblTotalParcelas  = new JLabel("R$ 0,00");
	protected double                      totalParc         = 0;
	protected Date                        periodoDataIni    = null;
	
	private static Logger logger = Logger.getLogger(PassivoFrame.class);

	public PassivoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		super(owner);
		this.periodoDataIni = periodoDataIni;
		this.contaCorrente  = contaCorrente;
	}
	
	protected void createUI() {
		super.createUI();
		// Panels, painéis containers para os componentes
		JPanel panMovimento  = new JPanel();
		JPanel panConta      = new JPanel();
		JPanel panParcelas   = new JPanel();
		JPanel panBtnAcoes   = new JPanel();
		JPanel panHistorico  = new JPanel();
		JPanel panTotalParcs = new JPanel();
		// Setting Layouts, configurando o Layout criado anteriormente para os
		// painéis
		panMovimento.setLayout(null);
		panConta.setLayout(null);
		panParcelas.setLayout(null);
		panBtnAcoes.setLayout(null);
		panHistorico.setLayout(null);
		panTotalParcs.setLayout(null);
		// Borders
		panMovimento.setBorder(BorderFactory.createEtchedBorder());
		panConta.setBorder(BorderFactory.createEtchedBorder());
		panParcelas.setBorder(BorderFactory.createEtchedBorder());
		panBtnAcoes.setBorder(BorderFactory.createEtchedBorder());
		panHistorico.setBorder(BorderFactory.createEtchedBorder());
		panTotalParcs.setBorder(BorderFactory.createEtchedBorder());

		buildPanelMovimento(panMovimento);
		buildPanelConta(panConta);
		buildPanelParcelas(panParcelas);
		buildPanelBtnAcoes(panBtnAcoes);
		buildPanelHistorico(panHistorico);
		buildPanelTotalParces(panTotalParcs);

		int y = 10;
		panBtnAcoes.setBounds(10, y, 636, 65);
		y += 75;
		panMovimento.setBounds(10, y, 230, 50);
		panConta.setBounds(248, y, 398, 50);
		y += 60;
		panParcelas.setBounds(10, y, 636, 240);
		y += 250;
		panHistorico.setBounds(266, y, 380, 50);
		panTotalParcs.setBounds(10, y, 247, 50);
				
		this.txtConta.getEditor().getEditorComponent().addFocusListener(this);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnParcelaInserir.addActionListener(this);
		this.btnParcelaRemover.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnParcelaInserir.setIcon(new ImageIcon(Util.loadImage(this, "add.png")));
		this.btnParcelaRemover.setIcon(new ImageIcon(Util.loadImage(this, "remove.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
		Util.setToolTip(this,btnParcelaInserir, "Inserir Novas Parcelas");
		Util.setToolTip(this,btnParcelaRemover, "Remover Parcela Selecionada");
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnParcelaInserir.setActionCommand("INSERIR");
		this.btnParcelaRemover.setActionCommand("REMOVER");
		this.btnParcelaInserir.setName("btnParcelaInserir");
		this.btnParcelaRemover.setName("btnParcelaRemover");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");

		panMain.add(panMovimento);
		panMain.add(panBtnAcoes);
		panMain.add(panConta);
		panMain.add(panParcelas);
		panMain.add(panHistorico);
		panMain.add(panTotalParcs);

		
		if ( this.txtDataMovimento.getDate() == null ) {
			this.txtDataMovimento.setDate(Util.buildDateFromReferencia(this.periodoDataIni));
		}
		this.setFocusTraversalPolicy(new InternalFocusManager(this.getTabParcelas()));
		
		this.modelParcelas.addTableModelListener(this);
		this.showTotalParcelas();
		this.componentsReady();
	}

	protected void showTotalParcelas() {
		this.lblTotalParcelas.setText(Util.formatCurrency(new BigDecimal(this.totalParc),false));
	}
	
	private void buildPanelTotalParces(JPanel panTotalParcs) {
		JLabel lbl = new JLabel("Total:");
		lbl.setBounds(10, 14, 100, 20);
	    panTotalParcs.add(lbl);
	    
	    Font font = new Font("Courier New", Font.BOLD,17);
	    lblTotalParcelas.setHorizontalAlignment(SwingConstants.RIGHT);
	    lblTotalParcelas.setBounds(57, 14, 173, 20);
	    lblTotalParcelas.setFont(font);
	    
	    panTotalParcs.add(lblTotalParcelas);
	}

	private void buildPanelHistorico(JPanel panHistorico) {
		
		lblHistorico.setBounds(10, 14, 120, 20);
		txtHistorico.setBounds(68, 15, 300, 20);
		txtHistorico.setName("txtHistorico");
		txtHistorico.addFocusListener(this);
		txtHistorico.setFont(new Font("Courier New",Font.PLAIN,13));
		
		panHistorico.add(lblHistorico);
		panHistorico.add(txtHistorico);
	}

	/**
	 * @param panBtnAcoes
	 */
	private void buildPanelBtnAcoes(JPanel panBtnAcoes) {
		int x = (this.width - 100) / 2;
		btnOk.setBounds(x, 9, 50, 45);
		btnCancela.setBounds(x+50, 9, 50, 45);

		btnOk.setName("btnOk");
		btnCancela.setName("btnCancela");
		
		btnOk.addFocusListener(this);
		btnCancela.addFocusListener(this);
		
		panBtnAcoes.add(btnOk);
		panBtnAcoes.add(btnCancela);
	}
	
	private void buildPanelParcelas(JPanel panParcelas) {
		this.initTableParcelas();
			
		JScrollPane scrollPane = new JScrollPane(this.getTabParcelas());
		this.getTabParcelas().setName("tabParcelas");

		btnParcelaInserir.setBounds(10, 8, 25, 25);
		btnParcelaRemover.setBounds(39, 8, 25, 25);
		btnParcelaInserir.setMnemonic('I');
		btnParcelaRemover.setMnemonic('R');
		
		btnParcelaInserir.addFocusListener(this);
		btnParcelaRemover.addFocusListener(this);

		JLabel labParcelas = new JLabel("Parcelas");
		labParcelas.setBounds(70, 5, 100, 30);
		scrollPane.setBounds(10, 40, 616, 190);
		
		btnREWParcelas.setBounds(126, 8, 25, 25);
		btnFFParcelas.setBounds(152, 8, 25, 25);
		btnFFParcelas.setActionCommand("FAST_FOWARD_PARCELAS");
		btnREWParcelas.setActionCommand("REWIND_PARCELAS");
		btnREWParcelas.setIcon(new ImageIcon(Util.loadImage(this, "rew.png")));
		btnFFParcelas.setIcon(new ImageIcon(Util.loadImage(this, "ff.png")));
		btnREWParcelas.addActionListener(this);
		btnFFParcelas.addActionListener(this);
		Util.setToolTip(this,btnREWParcelas, "Retroceder em 1 mês os vencimentos das parcelas");
		Util.setToolTip(this,btnFFParcelas, "Avançar em 1 mês os vencimentos das parcelas");

		panParcelas.add(btnParcelaInserir);
		panParcelas.add(btnParcelaRemover);
		panParcelas.add(btnREWParcelas);
		panParcelas.add(btnFFParcelas);
		panParcelas.add(labParcelas);
		panParcelas.add(scrollPane);
	}
	
	private void initTableParcelas() {
		this.initiateTabelaParcelaModel();
		this.removePassivoIdColumn();
		this.getTabParcelas().setPreferredScrollableViewportSize(new Dimension(600,70));
		this.getTabParcelas().setSurrendersFocusOnKeystroke(true);
		this.layOutColumnsTableParcela();
	}

	protected int getIndexColumnEfetivado() {
		return UtilTableCells.DEFAULT_COLUMN_EFETIVADO - 1;
	}
	
	protected void layOutColumnsTableParcela() {
		this.getTabParcelas().getColumnModel().getColumn(0).setPreferredWidth(50);
		this.getTabParcelas().getColumnModel().getColumn(1).setPreferredWidth(320);
		this.getTabParcelas().getColumnModel().getColumn(2).setPreferredWidth(35);
		
		TableColumn       dataColumn    = this.getTabParcelas().getColumnModel().getColumn(0);
		TableCellRenderer dataRenderer  = new DataTableCellRenderer(this.getIndexColumnEfetivado());
		dataColumn.setCellRenderer(dataRenderer);
		dataColumn.setCellEditor(new DataCellEditor());
	}

	private void buildPanelConta(JPanel panConta) {
		this.txtConta.setEditable(false);
		this.txtConta.setMaximumRowCount(20);
		
		lblConta.setBounds(10, 14, 120, 20);
		txtConta.setBounds(53, 14, 300, 20);

		txtConta.getEditor().getEditorComponent().setName("txtConta");
		txtConta.setName("txtConta");
		
		List<Conta> list = this.scorecardBusiness.listarContas(ContaOrdenador.Descricao);
		for (Conta conta : list) {
			conta.toStringMode = 1;
			txtConta.addItem(conta); 
		}
		if ( !this.isUpdate ) {
			txtConta.setSelectedItem(null);
		}
		
		btnContas.setName("btnConta");
		btnContas.setBounds(355,14,20,20);
		
		panConta.add(btnContas);
		panConta.add(lblConta);
		panConta.add(txtConta);
	}

	private void buildPanelMovimento(JPanel panMovimento) {
		lblDataMovimento.setBounds(10, 14, 120, 20);
		txtDataMovimento.setBounds(109, 14, 110, 20);
		txtDataMovimento.setFont(new Font("Courier New",Font.PLAIN,13));
		txtDataMovimento.setName("txtDataMovimento");

		panMovimento.add(lblDataMovimento);
		panMovimento.add(txtDataMovimento);
	}

	public class InternalFocusManager extends FocusTraversalPolicy {

		private JTable table;
		
		public InternalFocusManager(JTable table) {
			this.table = table;
		}
		
		public Component getComponentAfter(Container ctn, Component comp) {
			String componentName = comp.getName();
			if ( componentName == null ) {
				// txtDataMovimento
				if ( comp.getWidth() == 88 ) {
					return txtConta;
				}
			} else {
				if (componentName.equals("txtDataMovimento")) {
					return txtConta;
				} else if (componentName.equals("txtConta")) {
					return btnParcelaInserir;
				} else if (componentName.equals("btnParcelaInserir")) {
					return btnParcelaRemover;	
				} else if (componentName.equals("btnParcelaRemover")) {
					return this.table;
				} else if (componentName.equals("tabParcelas")) {
					return txtHistorico;
				} else if (componentName.equals("txtHistorico")) {
					return btnOk;	
				} else if (componentName.equals("btnOk")) {
					return btnCancela;
				} else if (componentName.equals("btnCancela")) {
					return (JFormattedTextField)txtDataMovimento.getDateEditor();
				}
			}
			return null;
		}

		public Component getComponentBefore(Container ctn, Component comp) {
			String componentName = comp.getName();
			if (componentName == null) {
				// txtDataMovimento
				if ( comp.getWidth() == 88 ) {
					return btnCancela;
				}
			} else {
				if (componentName.equals("txtDataMovimento")) {
					return btnCancela;
				} else if (componentName.equals("txtConta")) {
					return (JFormattedTextField)txtDataMovimento.getDateEditor();
				} else if (componentName.equals("btnParcelaInserir")) {
					return txtConta;
				} else if (componentName.equals("btnParcelaRemover")) {
					return btnParcelaInserir;
				} else if (componentName.equals("tabParcelas")) {
					return btnParcelaRemover;
				} else if (componentName.equals("txtHistorico")) {
					return this.table;
				} else if (componentName.equals("btnOk")) {
					return txtHistorico;
				} else if (componentName.equals("btnCancela")) {
					return btnOk;
				}
			}
			return null;
		}

		public Component getDefaultComponent(Container ctn) {
			return txtDataMovimento;
		}

		public Component getFirstComponent(Container ctn) {
			return txtDataMovimento;
		}

		public Component getLastComponent(Container ctn) {
			return btnCancela;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().indexOf("CANCELAR") != -1) {
			this.dispose();
		} else
		if ( e.getActionCommand().indexOf("OK") != -1) {
			this.savePassivo();
		} else
		if ( e.getActionCommand().indexOf("INSERIR") != -1) {
			Object[] row = this.getNewRow();
			this.getModelParcelas().addRow(row);
			this.getTabParcelas().requestFocusInWindow();
			this.getTabParcelas().changeSelection(this.getModelParcelas().getRowCount() - 1, 1, true, false);
			Robot r;
			try {
				r = new Robot();
				r.keyPress(KeyEvent.VK_ENTER);
			} catch (AWTException e1) {
				logger.warn(e);
			}
		} else
		if ( e.getActionCommand().indexOf("REMOVER") != -1) {
			int rows[] = this.getTabParcelas().getSelectedRows();
			for (int i = 0; i < rows.length; i++) {
				this.getModelParcelas().removeRow(rows[i]);
			}
		} else
		if ( e.getActionCommand().indexOf("CONTA") != -1) {
			ContaFrame contaFrame = new ContaFrame(this);
			contaFrame.setVisible(true);
			if ( contaFrame.getConta() != null ) {
				this.txtConta.getModel().setSelectedItem(contaFrame.getConta());
			}
		} else
		if ( e.getActionCommand().indexOf("FAST_FOWARD_PARCELAS") != -1) {
			for(int i = 0; i < this.modelParcelas.getRowCount(); i++) {
				Date dataVencimento = Util.parseDate((String)this.modelParcelas.getValueAt(i, 0));
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataVencimento);
				cal.add(Calendar.MONTH,1);
				this.modelParcelas.setValueAt(Util.formatDate(cal.getTime()),i,0);
			}
		} else
		if ( e.getActionCommand().indexOf("REWIND_PARCELAS") != -1) {
			for(int i = 0; i < this.modelParcelas.getRowCount(); i++) {
				Date dataVencimento = Util.parseDate((String)this.modelParcelas.getValueAt(i, 0));
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataVencimento);
				cal.add(Calendar.MONTH,-1);
				this.modelParcelas.setValueAt(Util.formatDate(cal.getTime()),i,0);
			}
		}
	}
	
	protected abstract Banco getBanco();
	protected abstract Object[] getNewRow();
	protected abstract void savePassivo();
	
	public boolean isValidValueOfFields() {
		if ( this.txtDataMovimento.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Data de movimento inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtDataMovimento.requestFocus();
			return false;
		}
		if ( this.txtConta.getSelectedItem() == null || !(this.txtConta.getSelectedItem() instanceof Conta) ) {
			JOptionPane.showMessageDialog(this,"Escolha a Conta Contábil !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtConta.requestFocus();
			return false;
		}
		if ( this.txtHistorico.getText() == null ) {
			JOptionPane.showMessageDialog(this,"Digite o histórico !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.txtHistorico.requestFocus();
			return false;
		}
		if ( this.getTabParcelas().getRowCount() < 1 ) {
			JOptionPane.showMessageDialog(this,"Deve existir ao menos uma parcela !","Atenção",JOptionPane.ERROR_MESSAGE);
			this.btnParcelaInserir.requestFocus();
			return false;
		}
		return true;
	}

	public void focusGained(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtHistorico")) {
			txtHistorico.selectAll();
		} else
		if ( component instanceof JButton ) {
			getRootPane().setDefaultButton((JButton)component);
		}
	}

	public void focusLost(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtConta") ) {
			if ( StringUtils.isBlank(txtHistorico.getText()) ) {
				Conta conta = (Conta) this.txtConta.getEditor().getItem();
				if ( conta != null ) {
					txtHistorico.setText(conta.getDescricao());
				}
			}
		} else
		if ( component instanceof JButton ) {
			getRootPane().setDefaultButton(null);
		}
	}
	
	@Override
	public void windowOpened(WindowEvent evt) {
		((JTextFieldDateEditor)this.txtDataMovimento.getDateEditor()).requestFocus();
	}
	
	protected abstract void initiateTabelaParcelaModel();
	protected abstract void removePassivoIdColumn();
	
	public UjrTabelaParcelaModel getModelParcelas() {
		return this.modelParcelas;
	}
	protected UjrTabelaParcela getTabParcelas() {
		return this.tabParcelas;
	}
	
	protected String[] getNewRowDateVlr() {
		String date     = Util.formatDate(Util.buildDateFromReferencia(this.periodoDataIni));
		String vlr      = "0,00";
		int    rowCount = this.getModelParcelas().getRowCount(); 
		if ( rowCount > 0 ) {
			date = (String)this.getModelParcelas().getValueAt(rowCount-1, 0);
			vlr  = (String)this.getModelParcelas().getValueAt(rowCount-1, 1);
			
			Date ddate = Util.parseDate(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(ddate);
			cal.add(Calendar.MONTH, 1);
			date = Util.formatDate(cal.getTime());
			
		}
		return new String[]{date,vlr};
	}
	
	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public void tableChanged(TableModelEvent evt) {
		if ( evt.getSource() instanceof UjrTabelaParcelaModelCheque ) {
			UjrTabelaParcelaModelCheque m = (UjrTabelaParcelaModelCheque)evt.getSource();
			totalParc = 0;
			for(int i = 0; i < m.getRowCount(); i++) {
				double vlr = Util.parseCurrency((String)m.getValueAt(i, 1)).doubleValue();
				totalParc += vlr;
			}
		} else 
		if ( evt.getSource() instanceof UjrTabelaParcelaModel ) {
			UjrTabelaParcelaModel m = (UjrTabelaParcelaModel)evt.getSource();
			totalParc = 0;
			for(int i = 0; i < m.getRowCount(); i++) {
				double vlr = Util.parseCurrency((String)m.getValueAt(i, 1)).doubleValue();
				totalParc += vlr;
			}
		}
		showTotalParcelas();
	}

	public JDateChooser getTxtDataMovimento() {
		return txtDataMovimento;
	}

	public void setTxtDataMovimento(JDateChooser txtDataMovimento) {
		this.txtDataMovimento = txtDataMovimento;
	}

	public UjrTextField getTxtHistorico() {
		return txtHistorico;
	}

	public void setTxtHistorico(UjrTextField txtHistorico) {
		this.txtHistorico = txtHistorico;
	}

}
