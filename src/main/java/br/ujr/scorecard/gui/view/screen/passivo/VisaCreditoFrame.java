package br.ujr.scorecard.gui.view.screen.passivo;

import java.util.Date;

import javax.swing.JFrame;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;

/**
 * @author ualter.junior
 */
public class VisaCreditoFrame extends AbstractCartaoFrame {

	public VisaCreditoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		super(owner, contaCorrente, periodoDataIni);
	}

	public VisaCreditoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Cartao cartao) {
		super(owner, contaCorrente, periodoDataIni, cartao);
	}

	@Override
	public Operadora getOperadora() {
		return Cartao.Operadora.VISA;
	}

	@Override
	public String getTituloNomeOperadora() {
		return "VISA";
	}

}
