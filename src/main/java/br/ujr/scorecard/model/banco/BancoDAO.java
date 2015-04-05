package br.ujr.scorecard.model.banco;

import java.util.List;

public interface BancoDAO {
	
	public Banco save(Banco conta);
	public void remove(Banco conta);
	public Banco findById(int id);
	public List<Banco> findByNome(String descricao);
	public List<Banco> list();
	public boolean isBancoRemovable(Banco banco);

}
