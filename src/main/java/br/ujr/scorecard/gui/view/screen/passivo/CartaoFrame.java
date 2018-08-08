package br.ujr.scorecard.gui.view.screen.passivo;

import java.util.Date;

import javax.swing.JFrame;

import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;

/**
 * @author ualter.junior
 */
public class CartaoFrame extends AbstractCartaoFrame {

	public CartaoFrame(JFrame owner, ContaCorrente contaCorrente, CartaoContratado cartaoContratado, Date periodoDataIni) {
		super(owner, contaCorrente, cartaoContratado, periodoDataIni);
	}

	public CartaoFrame(JFrame owner, ContaCorrente contaCorrente, CartaoContratado cartaoContratado, Date periodoDataIni, Cartao cartao) {
		super(owner, contaCorrente, cartaoContratado, periodoDataIni, cartao);
	}

	@Override
	public Operadora getOperadora() {
		return this.cartaoContratado.getCartaoOperadora();
	}

	@Override
	public String getTituloNomeOperadora() {
		return this.cartaoContratado.getNome();
	}

}
