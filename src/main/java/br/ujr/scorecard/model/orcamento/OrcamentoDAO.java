package br.ujr.scorecard.model.orcamento;

import java.util.List;
import java.util.Set;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.Passivo;

public interface OrcamentoDAO {
	
	public boolean save(Orcamento passivo);
	public void remove(Orcamento passivo);
	public Orcamento findById(int id);
	public Set<Orcamento> findByDescricao(String descricao);
	public Set<Orcamento> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, List<PassivosForaOrcamento> listaPassivosForaOrcamento);
	public Set<Passivo> listPassivosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta);
	public Set<Passivo> listPassivosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta, boolean includeChildren);

}
