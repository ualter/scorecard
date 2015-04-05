package br.ujr.scorecard.model.conta;

import java.util.List;

public interface ContaDAO {
	
	public Conta save(Conta conta);
	public void remove(Conta conta);
	public Conta findById(int id);
	public List<Conta> findByDescricao(String descricao);
	public List<Conta> findByNivel(String nivel);
	public List<Conta> list();

}
