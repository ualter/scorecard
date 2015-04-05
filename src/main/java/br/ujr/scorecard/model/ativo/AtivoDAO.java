package br.ujr.scorecard.model.ativo;

import java.util.List;
import java.util.Set;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.Passivo;

public interface AtivoDAO {
	
	public Ativo save(Ativo ativo);
	public void remove(Ativo ativo);
	public Ativo findById(int id);
	public List<Ativo> findByDescricao(String descricao);
	public List<Ativo> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);
	public List<Ativo> findByReferencia(ContaCorrente contaCorrente, Class ativoClass, long referenciaInicial, long referenciaFinal);
	public List<Ativo> findByContaContabil(Conta conta);

}
