package br.ujr.scorecard.model.passivo.cheque;

import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.parcela.Parcela;



/**
 * Cheque
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Cheque extends Passivo
{
	public Cheque()
	{
		super();
	}
	
	public void addParcela(String numeroCheque, Parcela parcela)
	{
		parcela.setCheque(true);
		parcela.setNumeroCheque(numeroCheque);
		super.addParcela(parcela);
	}
	
	public Parcela getParcela(String numeroCheque)
	{
	    for(Parcela parcela : this.parcelas) 
	    {
	    	if (parcela.getNumeroCheque().trim().equalsIgnoreCase(numeroCheque.trim()))
	        {
	            return parcela;
	        }
	    }
		return null;
	}
	
    /**
     * @see br.ualter.scorecard.components.passivo.Passivo#addParcela(br.ualter.scorecard.components.passivo.Parcela)
     */
    public void addParcela(Parcela parcela)
    {
    	
    	String msg = "Para parcelas de cheque, deve ser informado o nº do mesmo." 
    		+ ",  Ref:" + parcela.getReferencia() + ", Valor:" + parcela.getValor(); 
        throw new RuntimeException(msg);
    }
}
