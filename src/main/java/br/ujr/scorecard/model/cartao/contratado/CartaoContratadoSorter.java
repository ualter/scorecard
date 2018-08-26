package br.ujr.scorecard.model.cartao.contratado;

import java.util.Comparator;

public class CartaoContratadoSorter implements Comparator<CartaoContratado> {

	public static CartaoContratadoSorter NOME  = new CartaoContratadoSorter(CartaoContratadoSorter.Sort.NOME);
	public static CartaoContratadoSorter ORDEM = new CartaoContratadoSorter(CartaoContratadoSorter.Sort.ORDEM);

	public enum Sort {
		ID, NOME, CONTA_CORRENTE, ORDEM
	}

	public Sort sort;

	public CartaoContratadoSorter(Sort sort) {
		this.sort = sort;
	}

	@Override
	public int compare(CartaoContratado o1, CartaoContratado o2) {

		if (o1 == null && o2 == null)
			return 0;
		if (o1 != null && o2 == null)
			return 1;
		if (o1 == null && o2 != null)
			return -1;

		switch (this.sort) {
			case ID: {
				if (o1.getId() > o2.getId())
					return 1;
				if (o1.getId() < o2.getId())
					return -1;
				if (o1.getId() == o2.getId())
					return 0;
			}
			case NOME: {
				return o1.getNome().compareTo(o2.getNome());
			}
			case CONTA_CORRENTE: {
				if (o1.getContaCorrente() == null || o2.getContaCorrente() == null)
					throw new RuntimeException("MUST NOT exist a CartaoContratado object without ContaCorrente");
				if (o1.getContaCorrente().getId() > o2.getContaCorrente().getId())
					return 1;
				if (o1.getContaCorrente().getId() < o2.getContaCorrente().getId())
					return -1;
				if (o1.getContaCorrente().getId() == o2.getContaCorrente().getId())
					return 0;
			}
			case ORDEM: {
				if (o1.getOrdem() > o2.getOrdem())
					return 1;
				if (o1.getOrdem() < o2.getOrdem())
					return -1;
				if (o1.getOrdem() == o2.getOrdem())
					return 0;
			}
			default: {
				return -1;
			}
		}
	}

}