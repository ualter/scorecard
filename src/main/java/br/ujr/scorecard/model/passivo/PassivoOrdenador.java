package br.ujr.scorecard.model.passivo;

import java.util.Comparator;
import java.util.Date;

import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.Util;

public class PassivoOrdenador implements Comparator<Passivo> {
	
	public static PassivoOrdenador PARCELA_DATA_VENCIMENTO = new PassivoOrdenador(Sorter.PARCELA_DATA_VENCIMENTO);
	
	public enum Sorter {
		PARCELA_DATA_VENCIMENTO;
	}
	
	private Sorter sort;
	private long referenciaInicial;
	private long referenciaFinal;
	
	private PassivoOrdenador(Sorter sort) {
		this.sort = sort;
	}
	
	public int compare(Passivo passivoA, Passivo passivoB) {
		int result = this.compareNullable(passivoA, passivoB); 
		if ( result < 2) {
			return result;
		}
		
		switch (this.sort) {
			case PARCELA_DATA_VENCIMENTO:
				return this.sortByParcelaDataVencimento(passivoA, passivoB);
			default:
				return -1;
		}
	}

	private int sortByParcelaDataVencimento(Passivo passivoA, Passivo passivoB) {
		int result = 1;
		
		if ( passivoA.getParcelas(this.getReferenciaInicial(),this.getReferenciaFinal()).size() <= 0 ) {
			return -1;
		}
		if ( passivoB.getParcelas(this.getReferenciaInicial(),this.getReferenciaFinal()).size() <= 0 ) {
			return 1;
		}
		
		if ( this.getReferenciaInicial() == 0 || this.getReferenciaFinal() == 0 ) {
			throw new RuntimeException("Referência Inicial e Final devem ser atribuídos para ordenação de Passivos");
		}
		
		Parcela parcelaA = passivoA.getParcelas(this.getReferenciaInicial(),this.getReferenciaFinal()).get(0);
		Parcela parcelaB = passivoB.getParcelas(this.getReferenciaInicial(),this.getReferenciaFinal()).get(0);
		
		result = this.compareNullable(parcelaA, parcelaB); 
		if ( result < 2) {
			return result;
		}
		
		Date vencimentoA = parcelaA.getDataVencimento();
		Date vencimentoB = parcelaB.getDataVencimento();
		
		result = this.compareNullable(vencimentoA, vencimentoB); 
		if ( result < 2) {
			return result;
		}
		
		result = vencimentoA.compareTo(vencimentoB);
		
		/**
		 * Desempate por ID
		 */
		if ( result == 0 ) {
			int idA = passivoA.getId();
			int idB = passivoB.getId();
			if ( idA > idB ) {
				result = 1;
			} else
			if ( idA < idB ) {
				result = -1;
			}
		}
		return result;
	}

	public long getReferenciaFinal() {
		return referenciaFinal;
	}

	public void setReferenciaFinal(Date referenciaFinal) {
		this.setReferenciaFinal(Util.extrairReferencia(referenciaFinal));
	}
	public void setReferenciaFinal(long referenciaFinal) {
		this.referenciaFinal = referenciaFinal;
	}

	public long getReferenciaInicial() {
		return referenciaInicial;
	}

	public void setReferenciaInicial(Date referenciaInicial) {
		this.setReferenciaInicial(Util.extrairReferencia(referenciaInicial));
	}
	public void setReferenciaInicial(long referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}
	
	private int compareNullable(Object a, Object b) {
		int result = 99;
		if ( a == null && b == null ) return  0;
		if ( a != null && b == null ) return  1;
		if ( a == null && b != null ) return -1;
		return result;
	}

	

}
