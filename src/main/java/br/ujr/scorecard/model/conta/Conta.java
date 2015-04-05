
package br.ujr.scorecard.model.conta;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.ujr.scorecard.model.persistence.BusinessObject;

/**
 * Conta de um movimento Passivo ou Ativo 
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Conta extends BusinessObject implements Comparable
{
	private String nivel;
	private String descricao;
	private Set<Conta> contasFilhos = new HashSet<Conta>();
	private Conta contaPai; 
	
	public Conta()
	{
	}
	
	public Conta(String nivel, String descricao)
	{
	    this.nivel = nivel;
	    this.descricao = descricao;
	}
	
	public String getDescricao()
	{
		return descricao;
	}

	public void setDescricao(String string)
	{
		descricao = string;
	}

	public Conta getContaPai()
	{
		return contaPai;
	}

	public Set<Conta> getContasFilhos()
	{
		return contasFilhos;
	}

	public void setContaPai(Conta conta)
	{
		contaPai = conta;
	}

	public void setContasFilhos(Set<Conta> setFilhos)
	{
		contasFilhos = setFilhos;
	}
	
	public String getNivel()
	{
		return nivel;
	}

	public void setNivel(String s)
	{
		nivel = s;
	}

	public Conta addContaFilho(Conta conta)
	{
		conta.setContaPai(this);
		this.contasFilhos.add(conta);
		return conta;
	}
	public void removeContaFilho(Conta conta)
	{
		if ( this.contasFilhos != null ) 
		{
			this.contasFilhos.remove(conta);
		}
	}

	public boolean equals(Object obj)
	{
	    if ( obj instanceof Conta )
	    {
	        return (this.id == ((Conta)obj).getId());
	    }
	    return false;
	}
	
	public int toStringMode = 0; 
    public String toString()
    {
    	if ( toStringMode == 1 ) {
	    	return StringUtils.rightPad(this.descricao,5) + " [" + this.nivel + "]";
    	}
    	String codigo    = this.nivel;
    	String descricao = this.descricao;
    	return  codigo + " - " + descricao;
    }
    

    public int compareTo(Object obj)
    {
        int result = -1;
        if ( obj instanceof Conta )
        {
            Conta that = (Conta)obj;
            if ( this.id > that.getId() )
            {
                result = 1;
            }
            else if ( this.id < that.getId() )
            {
                result = -1;
            }
            else
            {
                result = 0;
            }
        }
        return result;
    }

	@Override
	public int hashCode() {
		return this.id;
	}
    
    
}
