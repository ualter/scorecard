package br.ujr.scorecard.gui.view.screen;

import java.util.Date;

import javax.swing.JFrame;

import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.cc.ContaCorrente;

public class DepositoFrame extends AbstractAtivoFrame {

	public DepositoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Ativo ativo) {
		super(owner, contaCorrente, periodoDataIni, ativo);
	}

	public DepositoFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		super(owner, contaCorrente, periodoDataIni);
	}

	@Override
	public String getTitulo() {
		return "DEPÓSITO";
	}

	@Override
	public Ativo newAtivo() {
		return new Deposito();
	}

}
