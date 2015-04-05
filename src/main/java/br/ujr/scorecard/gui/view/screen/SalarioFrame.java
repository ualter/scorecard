package br.ujr.scorecard.gui.view.screen;

import java.util.Date;

import javax.swing.JFrame;

import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.cc.ContaCorrente;

public class SalarioFrame extends AbstractAtivoFrame {

	public SalarioFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Ativo ativo) {
		super(owner, contaCorrente, periodoDataIni, ativo);
	}

	public SalarioFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni) {
		super(owner, contaCorrente, periodoDataIni);
	}

	@Override
	public String getTitulo() {
		return "ESTIPÊNDIO";
	}

	@Override
	public Ativo newAtivo() {
		return new Salario();
	}

}
