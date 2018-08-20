package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingWorker;

import br.ujr.components.gui.tabela.DefaultModelTabela;
import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.UtilGUI;

public class ConfirmarPassivo extends SwingWorker<String, String> {
		private BankPanel    bankPanel;
		private String       actionCommand;
		private LoadingFrame loadingFrame;
		private int          rowSelected;
	
		public ConfirmarPassivo(BankPanel bankPanel, String actionCommand) {
			this.bankPanel     = bankPanel;
			UtilGUI.coverBlinder(bankPanel.getOwner());
			this.actionCommand = actionCommand;
			this.loadingFrame = new LoadingFrame(2);
		}
		protected String doInBackground() throws Exception {
			this.loadingFrame.showLoadinFrame();
			if (actionCommand.indexOf("CHEQUE") != -1) {
				publish(new String[]{"Carregando Cheque"});
				Cheque cheque   = bankPanel.getSelectedCheque();
				if ( cheque != null ) {
					rowSelected     = bankPanel.tableCheque.getSelectedRow();
					Parcela parcela = cheque.getParcela(bankPanel.getChequeSelectedParcelaId());
					parcela.setEfetivado(!parcela.isEfetivado());
					publish(new String[]{"Salvando Cheque: " + Util.formatCurrency(parcela.getValor(),false) + " - " + cheque.getHistorico()});
					bankPanel.scorecardManager.savePassivo(cheque);
				}
			} else
			if (actionCommand.indexOf("CARTAO") != -1) {
				Integer keyCartaoContratado       = Integer.parseInt(actionCommand.split("_")[2]);
				CartaoContratado cartaoContratado = bankPanel.scorecardManager.getCartaoContratado(keyCartaoContratado);
				
				JTable             tableCartao      = bankPanel.tableCartoes.get(cartaoContratado);
				DefaultModelTabela tableModelCartao = bankPanel.tableModelCartoes.get(cartaoContratado);
				int rows[] = tableCartao.getSelectedRows();
				for(int i : rows) {
					publish(new String[]{"Carregando Cartão"});
					int cartaoId    = ((Integer)tableModelCartao.getValueAt(i, 5)).intValue();
					Cartao cartao   = (Cartao)bankPanel.scorecardManager.getPassivoPorId(cartaoId);
					int parcelaId   = ((Integer)tableModelCartao.getValueAt(i, 6)).intValue();
					Parcela parcela = cartao.getParcela(parcelaId);
					parcela.setEfetivado(!parcela.isEfetivado());
					String  msg     = parcela.isEfetivado() ? "Confirmando Débito: " : "Cancelando Débito: ";
					publish(new String[]{msg + Util.formatCurrency(parcela.getValor(),false) + " - " + cartao.getHistorico()});
					bankPanel.scorecardManager.savePassivo(cartao);
				}
			} else
			if (actionCommand.indexOf("DEBITO") != -1) {
				publish(new String[]{"Carregando Débito"});
				DebitoCC debito = bankPanel.getSelectedDebito();
				if ( debito != null ) {
					Parcela parcela = debito.getParcela(bankPanel.getDebitoSelectedParcelaId());
					rowSelected = bankPanel.tableDebito.getSelectedRow();
					parcela.setEfetivado(!parcela.isEfetivado());
					publish(new String[]{"Salvando Débito: " + Util.formatCurrency(parcela.getValor(),false) + " - " + debito.getHistorico()});
					bankPanel.scorecardManager.savePassivo(debito);
				}
			} else
			if (actionCommand.indexOf("SAQUE") != -1) {
				publish(new String[]{"Carregando Saque"});
				Saque saque = bankPanel.getSelectedSaque();
				if ( saque != null ) {
					Parcela parcela = saque.getParcela(bankPanel.getSaqueSelectedParcelaId());
					rowSelected = bankPanel.tableSaque.getSelectedRow();
					parcela.setEfetivado(!parcela.isEfetivado());
					publish(new String[]{"Salvando Saque: " + Util.formatCurrency(parcela.getValor(),false) + " - " + saque.getHistorico()});
					bankPanel.scorecardManager.savePassivo(saque);
				}
			}
			return null;
		}
		
		@Override
		protected void done() {
			publish(new String[]{"Finalizado"});
			if (actionCommand.indexOf("CHEQUE") != -1) {
				bankPanel.updateViewCheque();
				bankPanel.updateViewPeriodo();
				bankPanel.tableCheque.setRowSelectionInterval(rowSelected, rowSelected);
			} else
			if (actionCommand.indexOf("CARTAO") != -1) {
				Integer keyCartaoContratado       = Integer.parseInt(actionCommand.split("_")[2]);
				CartaoContratado cartaoContratado = bankPanel.scorecardManager.getCartaoContratado(keyCartaoContratado);
				bankPanel.updateViewCartao(cartaoContratado);
				bankPanel.updateViewPeriodo();
				bankPanel.tableCartoes.get(cartaoContratado).setRowSelectionInterval(rowSelected, rowSelected);
			} else
			if (actionCommand.indexOf("DEBITO") != -1) {
				bankPanel.updateViewDebito();
				bankPanel.updateViewPeriodo();
				bankPanel.tableDebito.setRowSelectionInterval(rowSelected, rowSelected);
			} else
			if (actionCommand.indexOf("SAQUE") != -1) {
				bankPanel.updateViewSaque();
				bankPanel.updateViewPeriodo();
				bankPanel.tableSaque.setRowSelectionInterval(rowSelected, rowSelected);
			}
			loadingFrame.dispose();
			UtilGUI.uncoverBlinder(bankPanel.getOwner());
		}
		
		@Override
		protected void process(List<String> chunks) {
			for (String string : chunks) {
				this.loadingFrame.incrementProgressValue();
				this.loadingFrame.setMessage(string);
			}
		}
	}