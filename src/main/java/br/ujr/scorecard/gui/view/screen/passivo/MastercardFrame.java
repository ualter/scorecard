package br.ujr.scorecard.gui.view.screen.passivo;

import java.util.Date;

import javax.swing.JFrame;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;

/**
 * @author ualter.junior
 */
public class MastercardFrame extends AbstractCartaoFrame {
	
	public MastercardFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Cartao cartao) {
		super(owner, contaCorrente, periodoDataIni, cartao);
	}

	public MastercardFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		super(owner, contaCorrente, periodoDataIni);
		this.banco = contaCorrente.getBanco();
	}

	@Override
	public Operadora getOperadora() {
		return Cartao.Operadora.MASTERCARD;
	}

	@Override
	public String getTituloNomeOperadora() {
		return "MASTERCARD";
	}


		
}
