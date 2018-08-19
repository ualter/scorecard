package br.ujr.scorecard.gui.view.screen.bankpanel;

import javax.swing.SwingWorker;

import br.ujr.scorecard.gui.view.screen.LoadingFrame;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.util.Util;

public class AdiarDataVencimentoCartao extends SwingWorker<String, String> {
	private BankPanel frame;
	private Cartao cartao;
	private CartaoContratado cartaoContratado;
	private LoadingFrame loadingFrame;

	public AdiarDataVencimentoCartao(BankPanel frame, Cartao cartao, CartaoContratado cartaoContratado) {
		this.frame = frame;
		this.cartao = cartao;
		this.cartaoContratado = cartaoContratado;
		this.loadingFrame = new LoadingFrame(true);
		this.loadingFrame.setMessage("Adiando vencimento " + Util.formatCurrency(cartao.getValorTotal(), false) + " - "
				+ cartao.getHistorico());
		this.loadingFrame.showLoadinFrame();
	}

	protected String doInBackground() throws Exception {
		this.frame.scorecardManager.savePassivo(cartao);
		return null;
	}

	protected void done() {
		this.loadingFrame.dispose();
		this.frame.updateViewCartao(this.cartaoContratado);
	}
}