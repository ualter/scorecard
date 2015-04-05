package br.ujr.scorecard.model.passivo.parcela;

import java.util.Comparator;
import java.util.Date;

public class ParcelaOrdenador implements Comparator<Parcela> {
	
	public static ParcelaOrdenador DATA_VENCIMENTO = new ParcelaOrdenador(Atributos.DATA_VENCIMENTO);
	public static ParcelaOrdenador DATA_LANCAMENTO = new ParcelaOrdenador(Atributos.DATA_LANCAMENTO);

	private final Atributos atributo;
	
	public enum Atributos 
	{ 
		DATA_VENCIMENTO, DATA_LANCAMENTO 
	}
	
	private ParcelaOrdenador(Atributos atributo) {
		this.atributo = atributo;
	}
	
	public int compare(Parcela parcelaA, Parcela parcelaB) 
	{
		int result = -1;
		if (parcelaA == null && parcelaB == null) {
			result = 0;
		} else if (parcelaA == null || parcelaB == null) {
			result = -1;
		} else {
			switch (this.atributo) 
			{
				case DATA_VENCIMENTO: {
					if ( parcelaA.getDataVencimento() == null && parcelaB.getDataVencimento() == null ) {
						result = 0;
					} else 
					if ( parcelaA.getDataVencimento() == null || parcelaB.getDataVencimento() == null ) {
						result = -1;
					} 
					else
					{
						result = parcelaA.getDataVencimento().compareTo(parcelaB.getDataVencimento());
					}
					break;
				}
				case DATA_LANCAMENTO: {
					Date d1 = parcelaA.getPassivo().getDataMovimento();
					Date d2 = parcelaB.getPassivo().getDataMovimento();
					if ( d1 == null && d2 == null ) {
						result = 0;
					} else 
					if ( d1 == null || d2 == null ) {
						result = -1;
					} 
					else
					{
						result = d1.compareTo(d2);
					}
					break;
				}
				default: {
					break;
				}
			}
		}
		
		/**
		 * Desempate
		 */
		if ( result == 0 ) {
			int idA = parcelaA.getId();
			int idB = parcelaB.getId();
			if ( idA > idB ) {
				result = 1;
			} else
			if ( idA < idB ) {
				result = -1;
			}
		}
		
		return result;
	}

}
