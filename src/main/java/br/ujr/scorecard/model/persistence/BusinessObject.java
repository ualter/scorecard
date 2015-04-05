package br.ujr.scorecard.model.persistence;

/**
 * Objeto de negócio persistente
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public abstract class BusinessObject
{
	
	protected int id;
	
	public int getId()
	{
		return id;
	}

	public void setId(int i)
	{
		id = i;
	}

}