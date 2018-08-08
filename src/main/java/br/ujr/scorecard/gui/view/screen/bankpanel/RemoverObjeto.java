package br.ujr.scorecard.gui.view.screen.bankpanel;

import java.util.List;

import javax.swing.SwingWorker;

import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.UtilGUI;

public class RemoverObjeto extends SwingWorker<String, String> {
	
	private BankPanel     bankPanel;
	private String        actionCommand;
	private LoadingFrame  loadingFrame;
	private Passivo       passivo;
	private Ativo         ativo;
	private Orcamento     orcamento;
	private Transferencia transferencia;
	
	public RemoverObjeto(BankPanel bankPanel, String actionCommand, Ativo ativo) {
		this.bankPanel     = bankPanel;
		UtilGUI.coverBlinder(bankPanel.getOwner());
		this.actionCommand = actionCommand;
		this.loadingFrame  = new LoadingFrame(1);
		this.loadingFrame.setMessage("Excluindo: " + ativo.getHistorico());
		this.ativo         = ativo;
	}
	public RemoverObjeto(BankPanel bankPanel, String actionCommand, Passivo passivo) {
		this.bankPanel     = bankPanel;
		UtilGUI.coverBlinder(bankPanel.getOwner());
		this.actionCommand = actionCommand;
		this.loadingFrame  = new LoadingFrame(1);
		this.loadingFrame.setMessage("Excluindo: " + passivo.getHistorico());
		this.passivo       = passivo;
	}
	public RemoverObjeto(BankPanel bankPanel, String actionCommand, Orcamento orcamento) {
		this.bankPanel     = bankPanel;
		UtilGUI.coverBlinder(bankPanel.getOwner());
		this.actionCommand = actionCommand;
		this.loadingFrame  = new LoadingFrame(1);
		this.loadingFrame.setMessage("Excluindo: " + orcamento.getDescricao());
		this.orcamento     = orcamento;
	}
	public RemoverObjeto(BankPanel bankPanel, String actionCommand, Transferencia transferencia) {
		this.bankPanel     = bankPanel;
		UtilGUI.coverBlinder(bankPanel.getOwner());
		this.actionCommand = actionCommand;
		this.loadingFrame  = new LoadingFrame(1);
		this.loadingFrame.setMessage("Excluindo: " + transferencia.getHistorico());
		this.transferencia     = transferencia;
	}
	protected String doInBackground() throws Exception {
		this.loadingFrame.showLoadinFrame();
		if ( passivo != null ) {
			bankPanel.scorecardManager.deletePassivo(passivo);
		} else
		if ( ativo != null ) {
			publish(new String[]{"Excluindo"});
			bankPanel.scorecardManager.deleteAtivo(ativo);
		} else 
		if ( orcamento != null ) {
			publish(new String[]{"Excluindo"});
			bankPanel.scorecardManager.deleteOrcamento(orcamento);	
		} else
		if ( transferencia != null ) {
			publish(new String[]{"Excluindo"});
			bankPanel.scorecardManager.deleteTransferencia(transferencia);
		}
		return null;
	}
	
	@Override
	protected void done() {
		this.loadingFrame.incrementProgressValue();
		if (actionCommand.indexOf("CHEQUE") != -1) {
			bankPanel.updateViewCheque();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("VISA") != -1) {
			bankPanel.updateViewVisaCredito();
			bankPanel.updateViewVisaElectron();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("MASTERCARD") != -1) {
			bankPanel.updateViewMastercard();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("DEBITO") != -1) {
			bankPanel.updateViewDebito();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("ORCAMENTO") != -1) {
			bankPanel.updateViewOrcamento();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("INVESTIMENTO") != -1) {
			bankPanel.updateViewInvestimento();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("DEPOSITO") != -1) {
			bankPanel.updateViewDeposito();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("SALARIO") != -1) {
			bankPanel.updateViewSalario();
			bankPanel.updateViewPeriodo();
		} else
		if (actionCommand.indexOf("TRANSFERENCIA") != -1) {
			/*bankPanel.updateViewTransferencia();
			bankPanel.updateViewSalario();
			bankPanel.updateViewDeposito();
			bankPanel.updateViewInvestimento();
			bankPanel.updateViewPeriodo(); */
		} else
		if (actionCommand.indexOf("SAQUE") != -1) {
			bankPanel.updateViewSaque();
			bankPanel.updateViewPeriodo();
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