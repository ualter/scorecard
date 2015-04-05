package br.ujr.scorecard.model.banco;

import br.ujr.scorecard.model.persistence.BusinessObject;

public class Banco extends BusinessObject implements Comparable<Banco> {
	
	private String nome;
	private Integer diaVencimentoVisa;
	private Integer diaVencimentoMastercard;
	private boolean ativo;
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getDiaVencimentoVisa() {
		return diaVencimentoVisa;
	}

	public void setDiaVencimentoVisa(Integer diaVencimentoVisa) {
		this.diaVencimentoVisa = diaVencimentoVisa;
	}

	public Integer getDiaVencimentoMastercard() {
		return diaVencimentoMastercard;
	}

	public void setDiaVencimentoMastercard(Integer diaVencimentoMastercard) {
		this.diaVencimentoMastercard = diaVencimentoMastercard;
	}

	public boolean equals(Object obj)
	{
	    if ( obj instanceof Banco )
	    {
	    	Banco banco = (Banco)obj;
	        if ( this.id == banco.getId() )
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

	public int compareTo(Banco o) {
		if ( this.getId() > o.getId() ) return  1;
		if ( this.getId() < o.getId() ) return -1;
		return 0;
	}
	
	public String toString() {
		return this.getNome();
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
