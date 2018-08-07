package br.ujr.scorecard.model.cartao.contratado;

import java.util.Comparator;

public class CartaoContratadoOrdenador implements Comparator<CartaoContratado> {

	public static CartaoContratadoOrdenador ID   = new CartaoContratadoOrdenador(Sort.ID);
	public static CartaoContratadoOrdenador NOME = new CartaoContratadoOrdenador(Sort.NOME);
	
	public enum Sort {
		ID, NOME;
	}
	
	private Sort sort;
	
	private CartaoContratadoOrdenador(Sort sorter) {
		this.sort = sorter;
	}
	
	public int compare(CartaoContratado o1, CartaoContratado o2) {
		switch (this.sort) {
			case ID: {
				if ( o1.getId() > o2.getId() ) return 1;
				if ( o1.getId() < o2.getId() ) return -1;
				return 0;
			}
			case NOME: {
				return o1.getNome().compareTo(o2.getNome());
			}
		}
		return -1;
	}

}
