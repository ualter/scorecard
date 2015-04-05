package br.ujr.scorecard.model.banco;

import java.util.Comparator;

public class BancoOrdenador implements Comparator<Banco> {

	public static BancoOrdenador ID   = new BancoOrdenador(Sort.ID);
	public static BancoOrdenador NOME = new BancoOrdenador(Sort.NOME);
	
	public enum Sort {
		ID, NOME;
	}
	
	private Sort sort;
	
	private BancoOrdenador(Sort sorter) {
		this.sort = sorter;
	}
	
	public int compare(Banco o1, Banco o2) {
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
