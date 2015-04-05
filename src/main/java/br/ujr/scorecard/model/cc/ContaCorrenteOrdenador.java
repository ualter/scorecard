package br.ujr.scorecard.model.cc;

import java.util.Comparator;

public class ContaCorrenteOrdenador implements Comparator<ContaCorrente> {

	public static ContaCorrenteOrdenador ID   = new ContaCorrenteOrdenador(Sort.ID);
	public static ContaCorrenteOrdenador DESCRICAO = new ContaCorrenteOrdenador(Sort.DESCRICAO);
	public static ContaCorrenteOrdenador ORDEM = new ContaCorrenteOrdenador(Sort.ORDEM);
	
	public enum Sort {
		ID, DESCRICAO, ORDEM;
	}
	
	private Sort sort;
	
	private ContaCorrenteOrdenador(Sort sorter) {
		this.sort = sorter;
	}
	
	public int compare(ContaCorrente o1, ContaCorrente o2) {
		switch (this.sort) {
			case ID: {
				if ( o1.getId() > o2.getId() ) return 1;
				if ( o1.getId() < o2.getId() ) return -1;
				return 0;
			}
			case DESCRICAO: {
				return o1.getDescricao().compareTo(o2.getDescricao());
			}
			case ORDEM: {
				if ( o1.getOrdem() > o2.getOrdem() ) return  1;
				if ( o1.getOrdem() < o2.getOrdem() ) return -1;
				return 0;
			}
		}
		return -1;
	}

}
