package br.ujr.scorecard.gui.view.screen.passivo;

import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;

import br.ujr.components.gui.field.JDateChooser;
import br.ujr.components.gui.tabela.UjrTabelaParcelaCheque;
import br.ujr.components.gui.tabela.UjrTabelaParcelaModelCheque;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;

/**
 * @author ualter.junior
 */
public class ChequeFrame extends PassivoFrame {

	private static final long serialVersionUID = -6561438310932958547L;
	protected Cheque                loadedCheque      = null;
	private Banco banco = null;

	public ChequeFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		this(owner,contaCorrente,periodoDataIni,null);
		this.banco = contaCorrente.getBanco();
		
	}
	
	public ChequeFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Cheque cheque) {
		super(owner,contaCorrente,periodoDataIni);
		this.title        = MessageManager.getMessage(MessagesEnum.TituloFrameCheque,
													 contaCorrente.getBanco(),contaCorrente);
		this.loadedCheque = cheque;
		this.isUpdate     = this.loadedCheque != null ? true : false;
		
		if  ( this.isUpdate ) {
			this.txtDataMovimento.setDate(this.loadedCheque.getDataMovimento());
			this.txtConta.setSelectedItem(this.loadedCheque.getConta());
			this.txtHistorico.setText(this.loadedCheque.getHistorico());
			// Parcelas
			Object data[][] = new Object[this.loadedCheque.getParcelas().size()][5];
			int row = 0;
			int col = 0;
			List<Parcela> parcelas = new ArrayList<Parcela>(this.loadedCheque.getParcelas());
			Collections.sort(parcelas,ParcelaOrdenador.DATA_VENCIMENTO);
			for(Parcela parcela : parcelas) {
				data[row][col++] = Util.formatDate(parcela.getDataVencimento());
				data[row][col++] = Util.formatCurrency(parcela.getValor(),true);
				//data[row][col++] = parcela.getNumero();
				data[row][col++] = parcela.getNumeroCheque();
				data[row][col++] = parcela.isEfetivado();
				data[row][col++] = parcela.getId();
				row++; col = 0;
				totalParc += parcela.getValor().doubleValue();
			}
			this.modelParcelas = new UjrTabelaParcelaModelCheque(data);
		} else {
			this.modelParcelas = new UjrTabelaParcelaModelCheque();
		}
		
		this.createUI();
	}
	

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					ContaCorrente cc = ((ScorecardManager)Util.getBean("scorecardManager")).getContaCorrentePorId(Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCSantander));
					
					JFrame frame = new JFrame();
					frame.setLayout(null);
					frame.setBounds(10, 10, 300, 600);
					JPanel panel = new JPanel();
					panel.setLayout(null);
					
					JDateChooser dt = new JDateChooser("dd/MM/yyyy","##/##/####",'_');
					dt.setBounds(10, 10, 100, 30);
					panel.add(dt);
					
					frame.setContentPane(panel);
					frame.setVisible(true);
				
					ChequeFrame cheque = new ChequeFrame(frame,cc,new java.util.Date());
					cheque.setVisible(true);
				} catch (Throwable e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void savePassivo() {
		if ( isValidValueOfFields() ){
			Cheque cheque = new Cheque();
			if ( this.isUpdate ) {
				cheque = this.loadedCheque;
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.txtDataMovimento.getDate());
			
			Conta conta = (Conta)this.txtConta.getSelectedItem();
			cheque.setContaCorrente(this.getContaCorrente());
			cheque.setDataMovimento(cal.getTime());
			cheque.setConta(conta);
			cheque.setHistorico(this.txtHistorico.getText());
			cheque.getParcelas().removeAll(cheque.getParcelas());
			
			for (int i = 0; i < this.tabParcelas.getModel().getRowCount(); i++) {
				/* In case need the Parcela's ID, here you are!
					if (this.tabParcelas.getModel().getValueAt(i, 4) != null) {
						int id = ((Integer)this.tabParcelas.getModel().getValueAt(i, 4)).intValue();
					}
				*/
				String data = (String)this.tabParcelas.getModel().getValueAt(i, 0);
				String valor = (String)this.tabParcelas.getModel().getValueAt(i, 1);
				String numero = (String)this.tabParcelas.getModel().getValueAt(i, 2);
				boolean efetivado = ((Boolean)this.tabParcelas.getModel().getValueAt(i, 3)).booleanValue();
				Parcela parcela = new Parcela();
				parcela.setDataVencimento(data);
				parcela.setValor(Util.parseCurrency(valor));
				parcela.setNumeroCheque(numero);
				parcela.setEfetivado(efetivado);
				cheque.addParcela(numero, parcela);
			}
			
			UtilGUI.coverBlinder(this);
			new SalvarCheque(this, cheque).execute();
		}
	}
	
	private class SalvarCheque extends SwingWorker<String, String> {
		private ChequeFrame frame;
		private Cheque cheque;
		private LoadingFrame loadingFrame;
		public SalvarCheque(ChequeFrame frame, Cheque cheque) {
			this.frame = frame;
			this.cheque = cheque;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(cheque.getValorTotal(), false) + " - " + cheque.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			frame.scorecardBusiness.savePassivo(cheque);
			return null;
		}
		protected void done() {
			this.loadingFrame.dispose();
			frame.dispose();
		}
	}
	
	public void focusGained(FocusEvent evt) {
		super.focusGained(evt);
		if ( evt.getSource() instanceof JTextField ) {
			JTextField field = (JTextField)evt.getSource();
			if ( StringUtils.isBlank(field.getText()) ) {
				if (this.txtConta.getEditor().getItem() != null) {
					Conta conta = (Conta) this.txtConta.getSelectedItem();
					if ( conta != null ) {
						field.setText(conta.getDescricao());
					}
				}
			}
			field.selectAll();
		}
	}

	@Override
	protected void initiateTabelaParcelaModel() {
		this.tabParcelas = new UjrTabelaParcelaCheque(modelParcelas,txtConta,txtHistorico);
	}

	@Override
	protected void removePassivoIdColumn() {
		this.getTabParcelas().removeColumn(this.getTabParcelas().getColumnModel().getColumn(4));
	}
	
	@Override
	protected void layOutColumnsTableParcela() {
		super.layOutColumnsTableParcela();
		this.getTabParcelas().getColumnModel().getColumn(0).setPreferredWidth(50);
		this.getTabParcelas().getColumnModel().getColumn(1).setPreferredWidth(180);
		this.getTabParcelas().getColumnModel().getColumn(2).setPreferredWidth(80);
		this.getTabParcelas().getColumnModel().getColumn(3).setPreferredWidth(30);
	}

	@Override
	protected Object[] getNewRow() {
		String[] newRow = this.getNewRowDateVlr();
		return new Object[]{newRow[0],newRow[1],"",new Boolean(false),0};
	}

	@Override
	protected Banco getBanco() {
		return this.banco;
	}

}
