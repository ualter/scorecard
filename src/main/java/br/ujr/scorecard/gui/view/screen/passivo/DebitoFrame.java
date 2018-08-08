package br.ujr.scorecard.gui.view.screen.passivo;

import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.lang3.StringUtils;

import br.ujr.components.gui.tabela.UjrTabelaParcela;
import br.ujr.components.gui.tabela.UjrTabelaParcelaModel;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.gui.view.screen.cellrenderer.UtilTableCells;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

/**
 * @author ualter.junior
 */
public class DebitoFrame extends PassivoFrame {

	private static final long serialVersionUID = -6561438310932958547L;
	
	protected DebitoCC loadedDebito = null;
	protected Banco banco = null;

	public DebitoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataInicial) {
		this(owner,contaCorrente,periodoDataInicial, null);
		this.banco = contaCorrente.getBanco();
	}
	
	public String getTitle() {
		return "Débito";
	}
	
	public DebitoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataInicial, DebitoCC cartao) {
		super(owner, contaCorrente, periodoDataInicial);
		this.title             = this.getTitle();
		this.loadedDebito      = cartao;
		this.isUpdate          = this.loadedDebito != null ? true : false;
		
		if  ( this.isUpdate ) {
			this.txtDataMovimento.setDate(this.loadedDebito.getDataMovimento());
			this.txtConta.setSelectedItem(this.loadedDebito.getConta());
			this.txtHistorico.setText(this.loadedDebito.getHistorico());
			// Parcelas
			Object data[][] = new Object[this.loadedDebito.getParcelas().size()][5];
			int row = 0;
			int col = 0;
			List<Parcela> parcelas = new ArrayList<Parcela>(this.loadedDebito.getParcelas());
			Collections.sort(parcelas,ParcelaOrdenador.DATA_VENCIMENTO);
			for(Parcela parcela : parcelas) {
				data[row][col++] = Util.formatDate(parcela.getDataVencimento());
				data[row][col++] = Util.formatCurrency(parcela.getValor(),true);
				data[row][col++] = parcela.isEfetivado();
				data[row][col++] = parcela.getId();
				row++; col = 0;
				totalParc += parcela.getValor().doubleValue();
			}
			this.modelParcelas = new UjrTabelaParcelaModel(data);
		} else {
			this.modelParcelas = new UjrTabelaParcelaModel();
		}
		
		this.createUI();
	}
	
	protected void savePassivo() {
		if ( isValidValueOfFields() ){
			DebitoCC debito = new DebitoCC();
			if ( this.isUpdate ) {
				debito = this.loadedDebito;
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.txtDataMovimento.getDate());
			
			Conta conta = (Conta)this.txtConta.getSelectedItem();
			debito.setContaCorrente(this.getContaCorrente());
			debito.setDataMovimento(cal.getTime());
			debito.setConta(conta);
			debito.setHistorico(this.txtHistorico.getText());
			debito.getParcelas().removeAll(debito.getParcelas());
			
			for (int i = 0; i < this.tabParcelas.getModel().getRowCount(); i++) {
				/* In case need the Parcela's ID, here you are!
					if (this.tabParcelas.getModel().getValueAt(i, 4) != null) {
						int id = ((Integer)this.tabParcelas.getModel().getValueAt(i, 4)).intValue();
					}
				*/
				String data = (String)this.tabParcelas.getModel().getValueAt(i, 0);
				String valor = (String)this.tabParcelas.getModel().getValueAt(i, 1);
				boolean efetivado = ((Boolean)this.tabParcelas.getModel().getValueAt(i, 2)).booleanValue();
				Parcela parcela = new Parcela();
				parcela.setDataVencimento(data);
				parcela.setValor(Util.parseCurrency(valor));
				parcela.setEfetivado(efetivado);
				debito.addParcela(parcela);
			}
			UtilGUI.coverBlinder(this);
			new SalvarDebito(this,debito).execute();
		}
	}
	
	private class SalvarDebito extends SwingWorker<String, String> {
		private DebitoFrame frame;
		private Passivo debito;
		private LoadingFrame loadingFrame;
		public SalvarDebito(DebitoFrame frame, Passivo saque) {
			this.frame = frame;
			this.debito = saque;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(saque.getValorTotal(), false) + " - " + saque.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			frame.scorecardBusiness.savePassivo(debito);
			return null;
		}
		protected void done() {
			this.loadingFrame.dispose();
			frame.dispose();
		}
	}
	
	public void focusGained(FocusEvent evt) {
		if ( evt.getSource() instanceof JTextField ) {
			JTextField field = (JTextField)evt.getSource();
			if ( StringUtils.isBlank(field.getText()) ) {
				Conta conta = (Conta) this.txtConta.getEditor().getItem();
				if ( conta != null ) {
					field.setText(conta.getDescricao());
				}
			}
			field.selectAll();
		}
	}

	@Override
	protected void initiateTabelaParcelaModel() {
		this.tabParcelas = new UjrTabelaParcela(modelParcelas,txtConta,txtHistorico);
	}

	@Override
	protected void removePassivoIdColumn() {
		this.getTabParcelas().removeColumn(this.getTabParcelas().getColumnModel().getColumn(3));
	}

	@Override
	protected Object[] getNewRow() {
		String[] newRow = this.getNewRowDateVlr();
		return new Object[]{newRow[0],newRow[1],new Boolean(false),0};
	}
	
	@Override
	protected int getIndexColumnEfetivado() {
		return UtilTableCells.DEFAULT_COLUMN_EFETIVADO - 2;
	}

	@Override
	protected Banco getBanco() {
		return this.banco;
	}
	
}
