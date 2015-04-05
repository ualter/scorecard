package br.ujr.scorecard.model.cc;

import java.util.List;

import br.ujr.scorecard.model.banco.Banco;

public interface ContaCorrenteDAO {
	
	public ContaCorrente save(ContaCorrente conta);
	public void remove(ContaCorrente conta);
	public ContaCorrente findById(int id);
	public List<ContaCorrente> findByDescricao(String descricao);
	public List<ContaCorrente> list();
	public boolean isRemovable(ContaCorrente cc);

}
