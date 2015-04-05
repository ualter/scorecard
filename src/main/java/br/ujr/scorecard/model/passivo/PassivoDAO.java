package br.ujr.scorecard.model.passivo;

import java.util.Set;

import br.ujr.scorecard.model.cc.ContaCorrente;

public interface PassivoDAO {
	
	public Passivo save(Passivo passivo);
	public void remove(Passivo passivo);
	public Passivo findById(int id);
	public Set<Passivo> findByHistorico(String historico);
	public Set<Passivo> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);
	public Set<Passivo> findSpecificByReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal);
	public Set<Passivo> findByContaContabilId(int contaId);
	public Set<Passivo> findByContaContabilNiveis(ContaCorrente contaCorrente, String[] niveis, boolean incluirDescendentes, long refIni, long refFim);
	public Set<Passivo> findByValor(ContaCorrente contaCorrente, float valor);

}

