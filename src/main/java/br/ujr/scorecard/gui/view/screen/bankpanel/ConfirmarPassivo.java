package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.util.List;

import javax.swing.SwingWorker;

import br.ujr.scorecard.gui.view.screen.LoadingFrame;
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
					bankPanel.scorecardBusinessDelegate.savePassivo(cheque);
				}
			} else
			if (actionCommand.indexOf("VISA") != -1) {
				if (actionCommand.indexOf("VISACREDITO") != -1) {
					int rows[] = bankPanel.tableVisaCredito.getSelectedRows();
					for(int i : rows) {
						int cartaoId = ((Integer)bankPanel.tableModelVisaCredito.getValueAt(i, 5)).intValue();
						publish(new String[]{"Carregando Cartão"});
						Cartao cartao = (Cartao)bankPanel.scorecardBusinessDelegate.getPassivoPorId(cartaoId);
						Parcela parcela = cartao.getParcela(bankPanel.getVisaCreditoSelectedParcelaId(i));
						parcela.setEfetivado(!parcela.isEfetivado());
						String msg = parcela.isEfetivado() ? "Confirmando Débito: " : "Cancelando Débito: ";
						publish(new String[]{msg + Util.formatCurrency(parcela.getValor(),false) + " - " + cartao.getHistorico()});
						bankPanel.scorecardBusinessDelegate.savePassivo(cartao);
					}
				} else
				if (actionCommand.indexOf("VISAELECTRON") != -1) {
					publish(new String[]{"Carregando Cartão"});
					Cartao cartao = bankPanel.getSelectedVisaElectron();
					Parcela parcela = cartao.getParcela(bankPanel.getVisaElectronSelectedParcelaId());
					rowSelected = bankPanel.tableVisaElectron.getSelectedRow();
					parcela.setEfetivado(!parcela.isEfetivado());
					publish(new String[]{"Salvando Cartão: " + Util.formatCurrency(parcela.getValor(),false) + " - " + cartao.getHistorico()});
					bankPanel.scorecardBusinessDelegate.savePassivo(cartao);
				}	
			} else
			if (actionCommand.indexOf("MASTERCARD") != -1) {
				int rows[] = bankPanel.tableMastercard.getSelectedRows();
				for (int i : rows) {
					publish(new String[]{"Carregando Cartão"});
					int cartaoId = ((Integer)bankPanel.tableModelMastercard.getValueAt(i, 5)).intValue();
					Cartao cartao = (Cartao)bankPanel.scorecardBusinessDelegate.getPassivoPorId(cartaoId);
					Parcela parcela = cartao.getParcela(bankPanel.getMastercardSelectedParcelaId(i));
					parcela.setEfetivado(!parcela.isEfetivado());
					String msg = parcela.isEfetivado() ? "Confirmando Débito: " : "Cancelando Débito: ";
					publish(new String[]{msg + Util.formatCurrency(parcela.getValor(),false) + " - " + cartao.getHistorico()});
					bankPanel.scorecardBusinessDelegate.savePassivo(cartao);
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
					bankPanel.scorecardBusinessDelegate.savePassivo(debito);
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
					bankPanel.scorecardBusinessDelegate.savePassivo(saque);
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
			if (actionCommand.indexOf("VISA") != -1) {
				if (actionCommand.indexOf("VISACREDITO") != -1) {
					bankPanel.updateViewVisaCredito();
					bankPanel.updateViewPeriodo();
					bankPanel.tableVisaCredito.setRowSelectionInterval(rowSelected, rowSelected);
				} else
				if (actionCommand.indexOf("VISAELECTRON") != -1) {
					bankPanel.updateViewVisaElectron();
					bankPanel.updateViewPeriodo();
					bankPanel.tableVisaElectron.setRowSelectionInterval(rowSelected, rowSelected);	
				}	
			} else
			if (actionCommand.indexOf("MASTERCARD") != -1) {
				bankPanel.updateViewMastercard();
				bankPanel.updateViewPeriodo();
				bankPanel.tableMastercard.setRowSelectionInterval(rowSelected, rowSelected);
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