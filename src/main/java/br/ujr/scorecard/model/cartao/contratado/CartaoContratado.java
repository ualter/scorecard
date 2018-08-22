package br.ujr.scorecard.model.cartao.contratado;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.persistence.BusinessObject;

public class CartaoContratado extends BusinessObject implements Comparable<CartaoContratado> {
	
	private Integer operadora;
	private String nome;
	private ContaCorrente contaCorrente;
	private String logo;
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logoFile) {
		this.logo = logoFile;
	}

	public Integer getOperadora() {
		return operadora;
	}

	public void setOperadora(Integer operadora) {
		this.operadora = operadora;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public boolean equals(Object obj)
	{
	    if ( obj instanceof CartaoContratado )
	    {
	    	CartaoContratado contaCorrente = (CartaoContratado)obj;
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

	public int compareTo(CartaoContratado obj) {
		 int result = -1;
        if ( obj instanceof CartaoContratado )
        {
        	CartaoContratado that = (CartaoContratado)obj;
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
		return this.nome;
		//return "CartaoContratado [operadora=" + operadora + ", nome=" + nome + ", contaCorrente=" + contaCorrente + "]";
	}
	
	public Cartao.Operadora getCartaoOperadora() {
		return Cartao.Operadora.values()[this.getOperadora()];
	}


}
