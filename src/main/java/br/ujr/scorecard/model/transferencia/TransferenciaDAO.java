package br.ujr.scorecard.model.transferencia;

import java.util.List;

import br.ujr.scorecard.model.cc.ContaCorrente;

public interface TransferenciaDAO {
	
	public Transferencia save(Transferencia Transferencia);
	public void remove(Transferencia Transferencia);
	public Transferencia findById(int id);
	public List<Transferencia> findByDescricao(String descricao);
	public List<Transferencia> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);

}
