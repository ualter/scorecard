package br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell;

import java.util.List;

public interface AnalisadorExtratoBanSabadell {

	List<LinhaExtratoContaCorrenteBanSabadell> getLancamentosNaoExistentesBaseDados();

	List<LinhaExtratoContaCorrenteBanSabadell> getLancamentosExistentesBaseDados();
	
	public String analisarExtrato();

}