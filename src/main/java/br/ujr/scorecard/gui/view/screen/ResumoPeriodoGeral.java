package br.ujr.scorecard.gui.view.screen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.scorecard.gui.view.screen.cellrenderer.ResumoTableCellRenderer;
import br.ujr.scorecard.model.ResumoPeriodo;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

@SuppressWarnings("serial")
public class ResumoPeriodoGeral extends JPanel implements MouseListener {
	
	private ScorecardManager scorecardBusinessDelegate;
	private Date dataInicial;
	private Date dataFinal;
	private JPanel panelParent;
	private JFrame frameParent;
	private DefaultModelTabela tableModelResumo;
	private JLabel lblPeriodoResumo;
	
	private ResumoPeriodo getResumoPeriodo() {
		ResumoPeriodo resumoPeriodo = this.scorecardBusinessDelegate
						.getResumoPeriodo(Util.extrairReferencia(dataInicial), Util.extrairReferencia(dataFinal));
		return resumoPeriodo;
	}
	
	public ResumoPeriodoGeral(JFrame frameParent, JPanel panelParent, ScorecardManager scorecardBusinessDelegate) {
		this.frameParent = frameParent;
		this.panelParent = panelParent;
		this.scorecardBusinessDelegate = scorecardBusinessDelegate;
		this.setUpDataInicialFinal();
		this.createUI();
	}

	private void createUI() {
		ResumoPeriodo resumoPeriodo = getResumoPeriodo();
		String        estipendio    = Util.formatCurrency(resumoPeriodo.getSalario());
		String        margin        = "\u0020\u0020\u0020";
		tableModelResumo = new DefaultModelTabela(
							new Object[][]{
									{margin + "Saldo Anterior",Util.formatCurrency(resumoPeriodo.getSaldoAnterior()) + margin},
									{margin + "Cheques",Util.formatCurrency(resumoPeriodo.getCheques()) + margin},
									{margin + "Visa",Util.formatCurrency(resumoPeriodo.getVisa()) + margin},
									{margin + "Visa Electron",Util.formatCurrency(resumoPeriodo.getElectron()) + margin},
									{margin + "Mastercard",Util.formatCurrency(resumoPeriodo.getMastercard()) + margin},
									{margin + "Saques",Util.formatCurrency(resumoPeriodo.getSaques()) + margin},
									{margin + "Débitos",Util.formatCurrency(resumoPeriodo.getDebitosCC()) + margin},
									{margin + "Despesas",Util.formatCurrency(resumoPeriodo.getDespesas()) + margin},
									{margin + "Depósitos",Util.formatCurrency(resumoPeriodo.getDepositos()) + margin},
									{margin + "Ivestimentos",Util.formatCurrency(resumoPeriodo.getInvestimentos()) + margin},
									{margin + "Transferências",Util.formatCurrency(resumoPeriodo.getTransferencias()) + margin},
									{margin + "Estipêndios",estipendio + margin},
									{margin + "Saldo Previsto",Util.formatCurrency(resumoPeriodo.getSaldoPrevisto()) + margin},
									{margin + "Saldo Prev. dia 20",Util.formatCurrency(resumoPeriodo.getSaldoPrevistoAteDia20()) + margin},
									{margin + "Saldo Real",Util.formatCurrency(resumoPeriodo.getSaldoReal()) + margin},
									},
					        new Object[]{"Descrição","Valor"});
		JTable tableResumo = new JTable(tableModelResumo){
			private static final long serialVersionUID = -7192115991350831533L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return false;
			}
		};
		
		tableResumo.setFont(new Font("Verdana",Font.PLAIN,11));
		tableResumo.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
			}
		});
		
		tableResumo.setPreferredScrollableViewportSize(new Dimension(380, 180));
		TableColumn descricaoColumn                = tableResumo.getColumnModel().getColumn(0);
		TableColumn valorColumn                    = tableResumo.getColumnModel().getColumn(1);
		DefaultTableCellRenderer descricaoRenderer = new ResumoTableCellRenderer();
		DefaultTableCellRenderer valorRenderer     = new ResumoTableCellRenderer();
		
		descricaoRenderer.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
		valorRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
				
		valorColumn.setCellRenderer(valorRenderer);
		descricaoColumn.setCellRenderer(descricaoRenderer);
		
		valorColumn.setPreferredWidth(35);
		descricaoColumn.setPreferredWidth(75);
		
		int X = 385;
		int Y = 119;
		JScrollPane jScrollPane = new JScrollPane(tableResumo);
		String lblPeriodoResumoText = this.updateLabelResumo();
		lblPeriodoResumo = new JLabel(lblPeriodoResumoText,SwingConstants.CENTER);
		lblPeriodoResumo.setFont(new Font("Verdana",Font.BOLD,12));
		lblPeriodoResumo.setOpaque(true);
		lblPeriodoResumo.setBackground(Color.GRAY);
		lblPeriodoResumo.setForeground(Color.WHITE);
		lblPeriodoResumo.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		lblPeriodoResumo.setName("PERIODO");
		lblPeriodoResumo.setBounds(X, Y, 249, 30);
		lblPeriodoResumo.addMouseListener(this);
		
		Y += 34;
		jScrollPane.setBounds(X, Y, 250, 304);
		panelParent.add(lblPeriodoResumo);
		panelParent.add(jScrollPane);
	}
	
	public String updateLabelResumo() {
		String lblPeriodoResumoText = null;
		Calendar today  = Calendar.getInstance();
		Calendar inicio = Calendar.getInstance();
		Calendar fim    = Calendar.getInstance();
		inicio.setTime(dataInicial);
		fim.setTime(dataFinal);
		
		if ( (inicio.get(Calendar.MONTH) == fim.get(Calendar.MONTH)) &&
			 ( inicio.get(Calendar.DAY_OF_MONTH) == 1  && 
					 (fim.get(Calendar.DAY_OF_MONTH) == 30 || fim.get(Calendar.DAY_OF_MONTH) == 31 || 
							 ((fim.get(Calendar.DAY_OF_MONTH) == 29 || fim.get(Calendar.DAY_OF_MONTH) == 28) && inicio.get(Calendar.MONTH) == 1)) 
			 ) 	
		    ) {
				String meses[] = new String[]{"J A N E I R O",
											  "F E V E R E I R O",
											  "M A R Ç O",
											  "A B R I L",
											  "M A I O",
											  "J U N H O",
											  "J U L H O",
											  "A G O S T O",
											  "S E T E M B R O",
											  "O U T U B R O",
											  "N O V E M B R O",
											  "D E Z E M B R O"};
				
				if ( inicio.get(Calendar.YEAR) != today.get(Calendar.YEAR) ) {
					lblPeriodoResumoText = meses[inicio.get(Calendar.MONTH)] + "    -    " + inicio.get(Calendar.YEAR);
				} else {
					lblPeriodoResumoText = meses[inicio.get(Calendar.MONTH)];
				}
		} else {
			lblPeriodoResumoText = new StringBuffer(Util.formatDate(this.dataInicial)).append(" \u0020\u0020 à \u0020\u0020 ").append(Util.formatDate(this.dataFinal)).toString();
		}
		return lblPeriodoResumoText;
	}
	
	private void setUpDataInicialFinal() {
		int mesAtual = Calendar.getInstance().get(Calendar.MONTH) + 1;
    	int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
    	int diaFinal = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
    	
    	this.dataInicial = Util.parseDate(1, mesAtual, anoAtual);
    	this.dataFinal   = Util.parseDate(diaFinal, mesAtual, anoAtual);
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
		if ( "PERIODO".equalsIgnoreCase(nameComponent)) {
			PeriodoFrame periodoResumoFrame = new PeriodoFrame(this.frameParent,this.dataInicial,this.dataFinal);
			periodoResumoFrame.setVisible(true);
			this.dataInicial    = periodoResumoFrame.getPeriodoDataInicial();
			this.dataFinal      = periodoResumoFrame.getPeriodoDataFinal();
			this.update(false);
		}
	}
	
	public void update(boolean backgroundUpdate) {
		UpdateViewTask task = new UpdateViewTask(this,backgroundUpdate);
		task.execute();
	}
	
	private class UpdateViewTask extends SwingWorker<String, String> {
		private ResumoPeriodoGeral frame  = null;
		private LoadingFrame loadingFrame; 
		private ResumoPeriodo resumoPeriodo;
		private boolean backgroundUpdate = false;
		
		public UpdateViewTask(ResumoPeriodoGeral frame, boolean backgroundUpdate) {
			this.frame            = frame;
			this.backgroundUpdate = backgroundUpdate;
			if (!backgroundUpdate) {
				loadingFrame = new LoadingFrame(15);
			}
		}
		
		@Override
		protected String doInBackground() throws Exception {
			if (!backgroundUpdate) {
				UtilGUI.coverBlinder(frame.frameParent);
				loadingFrame.showLoadinFrame();
				loadingFrame.setStatus("Resumo do Período: " + Util.formatDate(frame.dataInicial) + " até " + Util.formatDate(frame.dataFinal),1);
				frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			}
			resumoPeriodo = frame.getResumoPeriodo();
			String margin = "\u0020\u0020\u0020";
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Saldo Anterior",2);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaldoAnterior()) + margin,0, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Cheque",3);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getCheques()) + margin,1, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Visa Crédito",4);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getVisa()) + margin,2, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Visa Electron",5);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getElectron()) + margin,3, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Mastercard",6);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getMastercard()) + margin,4, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Saques",7);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaques()) + margin,5, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Débitos",8);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getDebitosCC()) + margin,6, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Despesas",9);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getDespesas()) + margin,7, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Depósitos",10);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getDepositos()) + margin,8, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Investimentos",11);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getInvestimentos()) + margin,9, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Transferências",12);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getTransferencias()) + margin,10, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Salário",13);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSalario()) + margin,11, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Saldo Previsto",14);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaldoPrevisto()) + margin,12, 1);
			if (!backgroundUpdate)loadingFrame.setStatus("Carregando Saldo Real",15);
			frame.tableModelResumo.setValueAt(Util.formatCurrency(resumoPeriodo.getSaldoReal()) + margin,13, 1);
			return null;
		}
		
		@Override
		protected void done() {
			if (!backgroundUpdate) {
				loadingFrame.dispose();
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				UtilGUI.uncoverBlinder(frame.frameParent);
			}
			frame.lblPeriodoResumo.setText(frame.updateLabelResumo());
		}
	}

}
