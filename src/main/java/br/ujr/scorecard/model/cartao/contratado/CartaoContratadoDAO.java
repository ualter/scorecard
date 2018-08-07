package br.ujr.scorecard.model.cartao.contratado;

import java.util.List;

import br.ujr.scorecard.model.cc.ContaCorrente;

public interface CartaoContratadoDAO {
	
	public CartaoContratado save(CartaoContratado conta);
	public void remove(CartaoContratado conta);
	public CartaoContratado findById(int id);
	public List<CartaoContratado> findByDescricao(String descricao);
	public List<CartaoContratado> findByContaCorrente(ContaCorrente contaCorrente);
	public List<CartaoContratado> list();
	
}
