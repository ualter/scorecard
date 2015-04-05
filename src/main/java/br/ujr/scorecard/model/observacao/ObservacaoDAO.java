package br.ujr.scorecard.model.observacao;

import java.util.List;

public interface ObservacaoDAO {
	
	public Observacao save(Observacao observacao);
	public void remove(Observacao observacao);
	public Observacao findById(int id);
	public List<Observacao> findByDescricao(String descricao);

}
