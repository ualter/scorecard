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
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.util.MessageManager;
import br.ujr.scorecard.util.MessagesEnum;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;


/**
 * @author ualter.junior
 */
public abstract class AbstractCartaoFrame extends PassivoFrame {

	private static final long serialVersionUID = -6561438310932958547L;
	
	protected Cartao loadedCartao = null;
	protected Banco banco = null;
	protected CartaoContratado cartaoContratado;

	public AbstractCartaoFrame(JFrame owner, ContaCorrente contaCorrente, CartaoContratado cartaoContratado, Date periodoDataIni) {
		this(owner, contaCorrente, cartaoContratado, periodoDataIni, null);
		this.banco = contaCorrente.getBanco();
	}
	
	public abstract String getTituloNomeOperadora();
	
	public String getTitle() {
		return MessageManager.getMessage(MessagesEnum.TituloFrameCartao,
				new String[]{this.getTituloNomeOperadora()},
				new Object[]{this.contaCorrente,this.contaCorrente.getBanco()});
	}
	
	public AbstractCartaoFrame(JFrame owner, ContaCorrente contaCorrente, CartaoContratado cartaoContratado, Date periodoDataIni, Cartao cartao) {
		super(owner, contaCorrente, periodoDataIni);
		this.cartaoContratado  = cartaoContratado;
		this.loadedCartao      = cartao;
		this.isUpdate          = this.loadedCartao != null ? true : false;
		this.banco             = contaCorrente.getBanco();
		this.title             = this.getTitle();
		
		
		if  ( this.isUpdate ) {
			this.txtDataMovimento.setDate(this.loadedCartao.getDataMovimento());
			this.txtConta.setSelectedItem(this.loadedCartao.getConta());
			this.txtHistorico.setText(this.loadedCartao.getHistorico());
			// Parcelas
			Object data[][] = new Object[this.loadedCartao.getParcelas().size()][5];
			int row = 0;
			int col = 0;
			List<Parcela> parcelas = new ArrayList<Parcela>(this.loadedCartao.getParcelas());
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
	
	public Banco getBanco() {
		return this.banco;
	}
	
	public abstract Cartao.CartaoCatalogo getOperadora();
	
	protected void savePassivo() {
		if ( isValidValueOfFields() ){
			Cartao cartao = new Cartao();
			if ( this.isUpdate ) {
				cartao = this.loadedCartao;
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.txtDataMovimento.getDate());
			
			Conta conta = (Conta)this.txtConta.getSelectedItem();
			cartao.setContaCorrente(this.getContaCorrente());
			cartao.setDataMovimento(cal.getTime());
			cartao.setConta(conta);
			cartao.setOperadora(this.getOperadora());
			cartao.setHistorico(this.txtHistorico.getText());
			cartao.getParcelas().removeAll(cartao.getParcelas());
			cartao.setCartaoContratado(this.cartaoContratado);
			
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
				cartao.addParcela(parcela);
			}
			UtilGUI.coverBlinder(this);
			new SalvarCartao(this,cartao).execute();
		}
	}
	
	private class SalvarCartao extends SwingWorker<String, String> {
		private AbstractCartaoFrame frame;
		private Cartao cartao;
		private LoadingFrame loadingFrame;
		public SalvarCartao(AbstractCartaoFrame frame, Cartao cartao) {
			this.frame = frame;
			this.cartao = cartao;
			this.loadingFrame = new LoadingFrame(true);
			this.loadingFrame.setMessage("Salvando " + Util.formatCurrency(cartao.getValorTotal(), false) + " - " + cartao.getHistorico());
			this.loadingFrame.showLoadinFrame();
		}
		protected String doInBackground() throws Exception {
			frame.scorecardBusiness.savePassivo(cartao);
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
	
	public String getFirstDateVencimento(Banco banco, Cartao.CartaoCatalogo operadora) {
		Calendar today = Calendar.getInstance();
		switch(operadora) {
			case VISA:
				today.set(Calendar.DAY_OF_MONTH,banco.getDiaVencimentoVisa());
				break;
			case MASTERCARD:
				today.set(Calendar.DAY_OF_MONTH,banco.getDiaVencimentoMastercard());
				break;
		}
		
		/*
		 * A data de vencimento deve estar dentro do mês corrente 
		 * No caso de ser diferente, e o cálculo resultar em uma data no mês seguinte, 
		 * por não existir o dia no mês corrente (como Fevereiro, por exemplo, não existe o dia 29 em ano não bisexto...)
		 * Retornar o último dia do mês corrente
		 */
		if ( today.get(Calendar.MONTH) != Calendar.getInstance().get(Calendar.MONTH) ) {
			today = Calendar.getInstance();
			today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DATE));
		}
		
		return Util.formatDate(today.getTime());
	}
	
	protected String getFirstDataVencimento() {
		return this.getFirstDateVencimento(this.getBanco(), this.getOperadora());
	}
	
	protected String[] getNewRowDateVlr() {
		String date     = this.getFirstDataVencimento();
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
	
}
