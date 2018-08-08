
package br.ujr.scorecard.gui.view.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXPanel;

import br.ujr.components.gui.combo.UjrComboBox;
import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.field.JTextFieldDateEditor;
import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.gui.view.utils.AbstractDialog;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.util.Util;

/**
 * Replicar Orçamentos para outros meses
 * @author ualter.junior
 */
public class ReplicarOrcamentoFrame extends AbstractDialog implements FocusListener, MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092445806277556773L;
	
	protected JLabel                      lblOrigem = new JLabel("Origem:");
	protected JDateChooser                txtRefOrigem = new JDateChooser("MM/yyyy","##/####",'_');
	protected JLabel                      lblDestino = new JLabel("Destino:");
	protected JDateChooser                txtRefDestino = new JDateChooser("MM/yyyy","##/####",'_');
	protected DefaultModelTabela                 tableModelOrcamento;
	protected JTable                      tableOrcamento;
	protected JButton                     btnOk             = new JButton();
	protected JButton                     btnCancela        = new JButton();
	protected JButton                     btnContas         = new JButton();
	protected Date                        periodoDataIni    = null;
	protected JLabel                      lblContaCorrente  = new JLabel("Conta Corrente:");
	protected UjrComboBox                 txtContaCorrente  = new UjrComboBox();
	private JXPanel blinder;

	private JLabel lblTotalOrcadoOrigem;
	private JLabel lblTotalRealizadoOrigem;
	private JLabel lblTotalRestanteOrigem;
	
	private BigDecimal totalOrcadoOrigem;
	private BigDecimal totalRealizadoOrigem;
	private BigDecimal totalRestanteOrigem;

	public ReplicarOrcamentoFrame(JFrame owner, Date periodoDataIni) {
		super(owner);
		this.periodoDataIni = periodoDataIni;
		this.title = this.getTitulo();
		
		this.createUI();
		
		this.btnOk.setIcon(new ImageIcon(Util.loadImage(this, "salvar.png")));
		this.btnCancela.setIcon(new ImageIcon(Util.loadImage(this, "cancel.png")));
		this.btnContas.setIcon(new ImageIcon(Util.loadImage(this, "search.png")));
	}
	
	public String getTitulo() {
		return "Replicar Orçamentos";
	}
	
	protected void createUI() {
		this.width = 680;
		this.height = 485;
		super.createUI();
		
		// Panels, painéis containers para os componentes
		JPanel panContaCorrente = new JPanel();
		JPanel panOrigem        = new JPanel();
		JPanel panBtnAcoes      = new JPanel();
		JPanel panDestino       = new JPanel();
		// Setting Layouts, configurando o Layout criado anteriormente para os
		// painéis
		panOrigem.setLayout(null);
		panBtnAcoes.setLayout(null);
		panContaCorrente.setLayout(null);
		panDestino.setLayout(null);
		// Borders
		panOrigem.setBorder(BorderFactory.createEtchedBorder());
		panBtnAcoes.setBorder(BorderFactory.createEtchedBorder());
		panContaCorrente.setBorder(BorderFactory.createEtchedBorder());
		panDestino.setBorder(BorderFactory.createEtchedBorder());

		buildPanelOrigem(panOrigem);
		buildPanelBtnAcoes(panBtnAcoes);
		buildPanelContaCorrente(panContaCorrente);
		buildPanelDestino(panDestino);

		panBtnAcoes.setBounds(10, 10, 657, 65);
		panContaCorrente.setBounds(10, 82, 657, 55);
		panOrigem.setBounds(10, 144, 657, 236);
		panDestino.setBounds(10, 390, 657, 55);
		
		this.btnCancela.addActionListener(this);
		this.btnOk.addActionListener(this);
		this.btnContas.addActionListener(this);
		
		this.btnCancela.setActionCommand("CANCELAR");
		this.btnOk.setActionCommand("OK");
		this.btnContas.setName("Conta");
		this.btnContas.setActionCommand("CONTAS");
		
		Util.setToolTip(this,btnOk, "Confirmar Replicação de Orçamentos");
		Util.setToolTip(this,btnCancela, "Cancelar Replicação");

		blinder = new JXPanel();
		blinder.setBackground(Color.BLACK);
		blinder.setBounds(0, 0, width, height);
		blinder.setAlpha(0.5f);
		blinder.setOpaque(true);
		blinder.setVisible(false);
		panMain.add(blinder);
		
		panMain.add(panOrigem);
		panMain.add(panBtnAcoes);
		panMain.add(panContaCorrente);
		panMain.add(panDestino);

		this.txtRefOrigem.setDate(this.periodoDataIni);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.periodoDataIni);
		cal.add(Calendar.MONTH,1);
		this.txtRefDestino.setDate(cal.getTime());
		
		this.setFocusTraversalPolicy(new InternalFocusManager(txtContaCorrente));
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
		
		panBtnAcoes.add(btnOk);
		panBtnAcoes.add(btnCancela);
	}
	
	protected int getIndexColumnEfetivado() {
		return UtilTableCells.DEFAULT_COLUMN_EFETIVADO - 1;
	}
	
	private void buildPanelContaCorrente(JPanel panContaCorrente) {
		int X          = 10;
		int Y          = 16;
		int WIDTH_LBLS = 105;
		
		lblContaCorrente.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblContaCorrente.getWidth();
		txtContaCorrente.setBounds(X, Y, 300, 20);
		txtContaCorrente.getEditor().getEditorComponent().setName("txtContaCorrente");
		txtContaCorrente.setEditable(false);
		txtContaCorrente.setName("txtContaCorrente");
		txtContaCorrente.addFocusListener(this);
		List<ContaCorrente> ccs = this.scorecardBusiness.listarContaCorrente();
		for (ContaCorrente corrente : ccs) {
			txtContaCorrente.addItem(corrente);
		}
		
		panContaCorrente.add(txtContaCorrente);
		panContaCorrente.add(lblContaCorrente);
	}
	
	private void buildPanelDestino(JPanel panDestino) {
		int X          = 10;
		int Y          = 16;
		int WIDTH_LBLS = 60;
		
		lblDestino.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblDestino.getWidth();
		txtRefDestino.setBounds(X, Y, 84, 20);
		txtRefDestino.setFont(new Font("Courier New",Font.PLAIN,13));
		((JTextFieldDateEditor)txtRefDestino.getDateEditor()).setName("txtRefDestino");
		
		panDestino.add(txtRefDestino);
		panDestino.add(lblDestino);
	}
	
	private void buildPanelOrigem(JPanel panOrcamento) {
		int X          = 10;
		int Y          = 14;
		int WIDTH_LBLS = 48;
		int SPACE_VERT = 30;
		
		lblOrigem.setBounds(X, Y, WIDTH_LBLS, 20);
		X += lblOrigem.getWidth();
		txtRefOrigem.setBounds(X, Y, 84, 20);
		txtRefOrigem.setFont(new Font("Courier New",Font.PLAIN,13));
		((JTextFieldDateEditor)txtRefOrigem.getDateEditor()).addFocusListener(this);
		((JTextFieldDateEditor)txtRefOrigem.getDateEditor()).setName("txtRefOrigem");
		
		int X_TOTAIS = 4;
		int Y_TOTAIS = 1;
		JPanel panelTotais = new JPanel();
		panelTotais.setBackground(Color.GRAY);
		panelTotais.setLayout(null);
		panelTotais.setBounds(lblOrigem.getWidth() + txtRefOrigem.getWidth() + 20,Y-3,493,24);
		panelTotais.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JLabel lblLabelTotal = new JLabel("TOTAIS");
		lblLabelTotal.setForeground(Color.YELLOW);
		lblLabelTotal.setFont(new Font("Verdana",Font.BOLD,9));
		lblLabelTotal.setBounds(X_TOTAIS,Y_TOTAIS + 1,40,20);
		panelTotais.add(lblLabelTotal);
		
		X_TOTAIS += lblLabelTotal.getWidth() + 5;
		JLabel lblLabelOrcado = new JLabel("Orçado:");
		lblLabelOrcado.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelOrcado.setForeground(Color.WHITE);
		lblLabelOrcado.setBounds(X_TOTAIS,Y_TOTAIS,52,20);
		panelTotais.add(lblLabelOrcado);
		X_TOTAIS += lblLabelOrcado.getWidth();
		lblTotalOrcadoOrigem = new JLabel("R$ 0,00");
		lblTotalOrcadoOrigem.setFont(new Font("Verdana",Font.BOLD,11));
		lblTotalOrcadoOrigem.setBounds(X_TOTAIS,Y_TOTAIS,85,20);
		lblTotalOrcadoOrigem.setForeground(Color.YELLOW);
		panelTotais.add(lblTotalOrcadoOrigem);
		
		X_TOTAIS += lblTotalOrcadoOrigem.getWidth() + 1;
		JLabel lblLabelRealizado = new JLabel("Realizado:");
		lblLabelRealizado.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelRealizado.setForeground(Color.WHITE);
		lblLabelRealizado.setBounds(X_TOTAIS,Y_TOTAIS,70,20);
		panelTotais.add(lblLabelRealizado);
		X_TOTAIS += lblLabelRealizado.getWidth();
		lblTotalRealizadoOrigem = new JLabel("R$ 0,00");
		lblTotalRealizadoOrigem.setFont(new Font("Verdana",Font.BOLD,11));
		lblTotalRealizadoOrigem.setBounds(X_TOTAIS,Y_TOTAIS,85,20);
		lblTotalRealizadoOrigem.setForeground(Color.YELLOW);
		panelTotais.add(lblTotalRealizadoOrigem);
		
		X_TOTAIS += lblTotalRealizadoOrigem.getWidth() + 1;
		JLabel lblLabelRestante = new JLabel("Restante:");
		lblLabelRestante.setFont(new Font("Verdana",Font.BOLD,11));
		lblLabelRestante.setForeground(Color.WHITE);
		lblLabelRestante.setBounds(X_TOTAIS,Y_TOTAIS,62,20);
		panelTotais.add(lblLabelRestante);
		X_TOTAIS += lblLabelRestante.getWidth();
		lblTotalRestanteOrigem = new JLabel("R$ 32.000,00");
		lblTotalRestanteOrigem.setFont(new Font("Verdana",Font.BOLD,11));
		lblTotalRestanteOrigem.setBounds(X_TOTAIS,Y_TOTAIS,85,20);
		lblTotalRestanteOrigem.setForeground(Color.YELLOW);
		panelTotais.add(lblTotalRestanteOrigem);
		panOrcamento.add(panelTotais);
		
		X  = 10;
		Y += SPACE_VERT;	
		this.tableModelOrcamento = new DefaultModelTabela(null,new Object[]{"Ref.","Orçado","Realizado","Restante","Conta","ID" });
		this.tableOrcamento = new JTable(this.tableModelOrcamento){
			private static final long serialVersionUID = 4634987309460177002L;
			@Override
			public boolean isCellEditable(int row, int col) {
				if ( col == 0 ) {
					return true;
				}
				return false;
			}
		};
		this.tableOrcamento.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tableOrcamento.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.tableOrcamento.setPreferredScrollableViewportSize(new Dimension(400, 80));
		this.tableOrcamento.setFont(new Font("Courier New",Font.PLAIN,12));
		this.tableOrcamento.addMouseListener(this);
		this.layOutTableOrcamentoOrigem();
		JScrollPane jScrollPane = new JScrollPane(tableOrcamento);
		JLabel lblOrcamentos = new JLabel("Orçamentos",SwingConstants.CENTER);
		lblOrcamentos.setFont(new Font("Verdana",Font.BOLD,10));
		lblOrcamentos.setOpaque(true);
		lblOrcamentos.setBackground(Color.GRAY);
		lblOrcamentos.setForeground(Color.WHITE);
		lblOrcamentos.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		jScrollPane.setBounds(X, Y, 637, 180);

		panOrcamento.add(lblOrcamentos);
		panOrcamento.add(jScrollPane);
		
		panOrcamento.add(lblOrigem);
		panOrcamento.add(txtRefOrigem);
	}

	private class OrcamentoTableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 2418901921193844216L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			
			boolean isChosen = Boolean.valueOf(table.getValueAt(row, 0).toString());
			
			this.setForeground(table.getForeground());
			this.setBackground(!isChosen ? table.getBackground() : Color.YELLOW);
			
			this.setFont(new Font("Courier New",Font.PLAIN,11));
			this.setText(String.valueOf(value));
			this.setBorder(table.getBorder());
			
			if ( column == 1 ) {
				layOutCell(value, new Color(0, 128, 0));
			} else
			if ( column == 2 ) {
				layOutCell(value, Color.BLACK);
			} else
			if ( column == 3 ) {
				Color color = Color.BLUE;
				if ( StringUtils.contains(String.valueOf(value), "-") ) {
					color = Color.RED;
				}
				layOutCell(value, color);
			}
			
			return this;
		}

		private void layOutCell(Object value, Color color) {
			String valor = String.valueOf(value);
			valor        = StringUtils.leftPad(valor, 9);
			valor        = " R$ " + valor + " ";
			this.setValue(valor);
			this.setForeground(color);
			this.setFont(new Font("Courier New",Font.BOLD,11));
		}

	}
	
	private class SelecionarTableCellRenderer extends JCheckBox implements TableCellRenderer {
		private static final long serialVersionUID = 6594333725114220143L;
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			boolean selected = ((Boolean)value).booleanValue();
			
			//Color background = isSelected ? table.getBackground() : (selected ? Color.YELLOW : table.getBackground());
			Color background = selected ? Color.YELLOW : Color.WHITE;
			
			this.setForeground(table.getForeground());
			this.setBackground(background);
			this.setBorder(table.getBorder());
			this.setFont(table.getFont());
			this.setSelected(selected);
			this.setHorizontalAlignment(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
			return this;
		}
	}
	
	private void layOutTableOrcamentoOrigem() {
		tableOrcamento.removeColumn(tableOrcamento.getColumnModel().getColumn(5));
		
		tableOrcamento.setPreferredScrollableViewportSize(new Dimension(350, 80));
		TableColumn selecionarColumn                = tableOrcamento.getColumnModel().getColumn(0);
		TableColumn orcadoColumn                    = tableOrcamento.getColumnModel().getColumn(1);
		TableColumn realizadoColumn                 = tableOrcamento.getColumnModel().getColumn(2);
		TableColumn restanteColumn                  = tableOrcamento.getColumnModel().getColumn(3);
		TableColumn contaColumn                     = tableOrcamento.getColumnModel().getColumn(4);
		
		TableCellRenderer        referenciaRenderer = new SelecionarTableCellRenderer();
		DefaultTableCellRenderer orcadoRenderer     = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer realizadoRenderer  = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer restanteRenderer   = new OrcamentoTableCellRenderer();
		DefaultTableCellRenderer contaRenderer      = new OrcamentoTableCellRenderer();
		
		orcadoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		realizadoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		restanteRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		contaRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		
		selecionarColumn.setCellRenderer(referenciaRenderer);
		orcadoColumn.setCellRenderer(orcadoRenderer);
		realizadoColumn.setCellRenderer(realizadoRenderer);
		restanteColumn.setCellRenderer(restanteRenderer);
		contaColumn.setCellRenderer(contaRenderer);
		
		selecionarColumn.setPreferredWidth(28);
		orcadoColumn.setPreferredWidth(100);
		realizadoColumn.setPreferredWidth(100);
		restanteColumn.setPreferredWidth(100);
		contaColumn.setPreferredWidth(290);
	}

	public class InternalFocusManager extends FocusTraversalPolicy {

		private Component firstFocus = null;
		
		public InternalFocusManager(Component firstFocus) {
			this.firstFocus = firstFocus;
		}
		
		public Component getComponentAfter(Container ctn, Component comp) {
			String componentName = comp.getName();
			if ( componentName == null ) {
				System.out.println("Ops... getComponentAfter retornou nulo Component " + comp);
			} else {
				if (componentName.equals("txtContaCorrente")) {
					return (JFormattedTextField)txtRefOrigem.getDateEditor();
				} else
				if (componentName.equals("txtRefOrigem")) {
					return (JFormattedTextField)txtRefDestino.getDateEditor();
				} else
				if (componentName.equals("txtRefDestino")) {
					return btnOk;
				} else	
				if (componentName.equals("btnOk")) {
					return btnCancela;
				} else
				if (componentName.equals("btnCancela")) {
					return txtContaCorrente;
				}
			}
			return null;
		}

		public Component getComponentBefore(Container ctn, Component comp) {
			String componentName = comp.getName();
			if (componentName == null) {
				System.out.println("Ops... getComponentAfter retornou nulo Component " + comp);
			} else {
				if (componentName.equals("txtContaCorrente")) {
					return btnCancela;
				} else
				if (componentName.equals("txtRefOrigem")) {
					return txtContaCorrente;
				} else
				if (componentName.equals("txtRefDestino")) {
					return (JFormattedTextField)txtRefOrigem.getDateEditor();
				} else	
				if (componentName.equals("btnOk")) {
					return (JFormattedTextField)txtRefDestino.getDateEditor();
				} else
				if (componentName.equals("btnCancela")) {
					return btnOk;
				}
			}
			return null;
		}

		public Component getDefaultComponent(Container ctn) {
			return this.firstFocus;
		}

		public Component getFirstComponent(Container ctn) {
			return this.firstFocus;
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
			this.savePassivoConstante();
		}
	}
	
	private class CarregarOrcamentosOrigem extends SwingWorker<String, Object[]> {
		
		private LoadingFrame loadingFrame;
		private ReplicarOrcamentoFrame frame;
		private ContaCorrente contaCorrente;
		private Date ref;
		private BigDecimal totalOrcadoOrigem    = new BigDecimal(0);
		private BigDecimal totalRealizadoOrigem = new BigDecimal(0);
		private BigDecimal totalRestanteOrigem  = new BigDecimal(0);
		
		public CarregarOrcamentosOrigem(ReplicarOrcamentoFrame frame) {
			this.frame         = frame;
			this.contaCorrente = (ContaCorrente)frame.txtContaCorrente.getSelectedItem();
			this.ref           = frame.txtRefOrigem.getDate();
		}
		
		@Override
		protected String doInBackground() throws Exception {
			loadingFrame = new LoadingFrame();
			loadingFrame.showLoadinFrame();
			Set<Orcamento> orcamentos = scorecardBusiness.getOrcamentosPorReferencia(contaCorrente, ref, ref);
			loadingFrame.setMaxProgress(orcamentos.size());

			Object row[];
			for(Orcamento orcamento : orcamentos) {
				row = new Object[]{
						true,
						Util.formatCurrency(orcamento.getOrcado()),
						Util.formatCurrency(orcamento.getRealizado()),
						Util.formatCurrency(orcamento.getOrcado().subtract(orcamento.getRealizado())),
						" " + orcamento.getContaAssociada().getNivel() + " " + orcamento.getContaAssociada().getDescricao(),
						orcamento.getId()
				};
				totalOrcadoOrigem = totalOrcadoOrigem.add(orcamento.getOrcado());
				totalRealizadoOrigem = totalRealizadoOrigem.add(orcamento.getRealizado());
				publish(new Object[]{row});
				Thread.sleep(100);
			}
			totalRestanteOrigem = totalOrcadoOrigem.subtract(totalRealizadoOrigem);
			loadingFrame.dispose();
			return null;
		}

		@Override
		protected void done() {
			frame.lblTotalOrcadoOrigem.setText(Util.formatCurrency(totalOrcadoOrigem,false));
			frame.lblTotalRealizadoOrigem.setText(Util.formatCurrency(totalRealizadoOrigem,false));
			frame.lblTotalRestanteOrigem.setText(Util.formatCurrency(totalRestanteOrigem,false));
		}

		@Override
		protected void process(List<Object[]> chunks) {
			for (Object[] orcamentos : chunks) {
				Object[] rowOrcamento = (Object[])orcamentos[0];
				frame.tableModelOrcamento.addRow(rowOrcamento);
				loadingFrame.incrementProgressValue();
				loadingFrame.setMessage("Carregando Orçamento " + rowOrcamento[4]);
			}
		}
		
	}
	
	private class ReplicarOrcamentos extends SwingWorker<String, Orcamento> {
		
		private LoadingFrame loadingFrame;
		private ReplicarOrcamentoFrame frame;
		private ContaCorrente contaCorrente;
		private Date refOrigem;
		private Date refDestino;
		private ArrayList<Orcamento> orcamentos = new ArrayList<Orcamento>();
		
		
		public ReplicarOrcamentos(ReplicarOrcamentoFrame frame) {
			this.frame           = frame;
			this.contaCorrente   = (ContaCorrente)frame.txtContaCorrente.getSelectedItem();
			this.refOrigem       = frame.txtRefOrigem.getDate();
			this.refDestino      = frame.txtRefDestino.getDate();
			loadingFrame = new LoadingFrame();
			int total = 0;
			for(int i = 0; i < frame.tableModelOrcamento.getRowCount(); i++ ) {
				if ( ((Boolean)frame.tableModelOrcamento.getValueAt(i, 0)).booleanValue() ) {
					Orcamento orcamento = new Orcamento();
					orcamento.setId((Integer)frame.tableModelOrcamento.getValueAt(i, 5));
					orcamentos.add(orcamento);
					total++;
				}
				
			}
			loadingFrame.setMaxProgress(total);
			
			this.frame.btnCancela.setEnabled(false);
			this.frame.btnOk.setEnabled(false);
			this.frame.txtRefDestino.setEnabled(false);
			this.frame.txtRefOrigem.setEnabled(false);
			this.frame.txtContaCorrente.setEnabled(false);
		}
		
		@Override
		protected String doInBackground() throws Exception {
			loadingFrame.showLoadinFrame();
			
			for (Orcamento orc : orcamentos) {
				Orcamento orcamento = scorecardBusiness.getOrcamentoPorId(orc.getId());
				
				orcamento.setId(0);
				orcamento.setReferencia(this.refDestino);
				
				publish(new Orcamento[]{orcamento});
				
				scorecardBusiness.saveOrcamento(orcamento);
			}
			
			loadingFrame.dispose();
			return null;
		}

		@Override
		protected void done() {
			this.frame.btnCancela.setEnabled(true);
			this.frame.btnOk.setEnabled(true);
			this.frame.txtRefDestino.setEnabled(true);
			this.frame.txtRefOrigem.setEnabled(true);
			this.frame.txtContaCorrente.setEnabled(true);
		}

		@Override
		protected void process(List<Orcamento> chunks) {
			for (Orcamento orcamento : chunks) {
				loadingFrame.setMessage("Replicando orçamento: " +orcamento.getDescricao() + " - " + Util.formatCurrency(orcamento.getOrcado()));
				loadingFrame.incrementProgressValue();
			}
		}
	}
	
	protected void savePassivoConstante() {
		if ( isValidValueOfFields() ) {
			ReplicarOrcamentos task = new ReplicarOrcamentos(this);
			task.execute();
		}
	}
	
	public boolean isValidValueOfFields() {
		if ( this.txtRefOrigem.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Origem Inválida !","Atenção",JOptionPane.ERROR_MESSAGE);
			((JFormattedTextField)this.txtRefOrigem.getDateEditor()).requestFocus();
			return false;
		} else
		if ( this.txtRefDestino.getDate() == null ) {
			JOptionPane.showMessageDialog(this,"Destino Inválido !","Atenção",JOptionPane.ERROR_MESSAGE);
			((JFormattedTextField)this.txtRefDestino.getDateEditor()).requestFocus();
			return false;
		}	
		int datas = this.txtRefDestino.getDate().compareTo(this.txtRefOrigem.getDate()); 
		if ( datas == 0 || datas == -1 ) {
			JOptionPane.showMessageDialog(this,"Referência de Destino deve ser maior que a de Origem !","Atenção",JOptionPane.ERROR_MESSAGE);
			((JFormattedTextField)this.txtRefDestino.getDateEditor()).requestFocus();
			return false;
		}
		return true;
	}

	public void focusGained(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtRefOrigem") ) {
			this.resetTableOrcamentoOrigem();
		} else
		if ( component.getName().equals("txtContaCorrente") ) {
			this.resetTableOrcamentoOrigem();
		}
	}

	private void resetTableOrcamentoOrigem() {
		if ( this.tableModelOrcamento.getRowCount() > 0 ) {
			while (this.tableModelOrcamento.getRowCount() > 0) {
				this.tableModelOrcamento.removeRow(0);
			}
		}
		this.lblTotalOrcadoOrigem.setText("R$ 0,00");
		this.lblTotalRealizadoOrigem.setText("R$ 0,00");
		this.lblTotalRestanteOrigem.setText("R$ 0,00");
	}
	

	public void focusLost(FocusEvent evt) {
		JComponent component = (JComponent)evt.getComponent();
		if ( component.getName().equals("txtRefOrigem") ) {
			this.loadOrcamentoOrigem();
			Calendar cal = Calendar.getInstance();
			cal.setTime(txtRefOrigem.getDate());
			cal.add(Calendar.MONTH,1);
			this.txtRefDestino.setDate(cal.getTime());
		}
	}
	
	private void loadOrcamentoOrigem() {
		if ( this.txtRefOrigem.getDate() != null && this.txtContaCorrente.getSelectedItem() != null ) {
			CarregarOrcamentosOrigem task = new CarregarOrcamentosOrigem(this);
			task.execute();
		}
	}

	@Override
	public void windowOpened(WindowEvent evt) {
		txtContaCorrente.requestFocusInWindow();
	}
	
	public static void main(String[] args) {
		ReplicarOrcamentoFrame p = new ReplicarOrcamentoFrame(null,Util.today());
		p.setVisible(true);
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
		if ( e.getSource() instanceof JTable) {
			this.tableOrcamento.repaint();
		}
	}

}
