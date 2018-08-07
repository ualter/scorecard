package br.ujr.scorecard.model.cc;

import org.apache.commons.lang.StringUtils;

import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.persistence.BusinessObject;

public class ContaCorrente extends BusinessObject implements Comparable<ContaCorrente> {
	
	private String descricao;
	private String numero;
	private Banco banco;
	private Integer ordem;
	private boolean cheque;
	
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Banco getBanco() {
		return banco;
	}

	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean equals(Object obj)
	{
	    if ( obj instanceof ContaCorrente )
	    {
	    	ContaCorrente contaCorrente = (ContaCorrente)obj;
	        if ( this.id == contaCorrente.id )
	        {
	            return true;
	        }
	        else
	        {
	            return false;
	        }
	    }
	    return false;
	}
	
	public int hashCode() 
	{
		return this.id * 31;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int compareTo(ContaCorrente obj) {
		 int result = -1;
        if ( obj instanceof ContaCorrente )
        {
        	ContaCorrente that = (ContaCorrente)obj;
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
	public String toString() {
		StringBuffer sb = new StringBuffer("");
		sb.append(StringUtils.rightPad(this.getNumero(),12));
		sb.append(this.getDescricao());
		return sb.toString();
	}

	public boolean isCheque() {
		return cheque;
	}

	public void setCheque(boolean cheque) {
		this.cheque = cheque;
	}
	
	

}
