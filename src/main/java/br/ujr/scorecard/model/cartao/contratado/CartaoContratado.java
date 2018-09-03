package br.ujr.scorecard.model.cartao.contratado;

import org.apache.commons.lang3.StringUtils;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.persistence.BusinessObject;

public class CartaoContratado extends BusinessObject implements Comparable<CartaoContratado> {
	
	private Integer cartao;
	private String nome;
	private ContaCorrente contaCorrente;
	private String logo;
	private String categoria;
	private Integer ordem;
	
	public enum CategoriaCartao {
		DEBITO("D"), CREDITO("C");
		
		private final String categoria;
		
		CategoriaCartao(String categoria) {
			this.categoria = categoria;
		}

		public String getCategoria() {
			return categoria;
		}
	}
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logoFile) {
		this.logo = logoFile;
	}

	public Integer getCartao() {
		return cartao;
	}

	public void setCartao(Integer operadora) {
		this.cartao = operadora;
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
	

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
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
	
	public Cartao.CartaoCatalogo getCartaoOperadora() {
		return Cartao.CartaoCatalogo.values()[this.getCartao()];
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public void setCategoriaCartao(CategoriaCartao categoriaCartao) {
		this.categoria = categoriaCartao.getCategoria();
	}
	
	public CategoriaCartao getCategoriaCartao() {
		if (StringUtils.equalsIgnoreCase(CategoriaCartao.CREDITO.getCategoria(), this.getCategoria())) {
			return CategoriaCartao.CREDITO;
		} else
		if (StringUtils.equalsIgnoreCase(CategoriaCartao.DEBITO.getCategoria(), this.getCategoria())) {
			return CategoriaCartao.DEBITO;
		}
		throw new RuntimeException("Valor de Categoria Cartao desconhecida: \"" + this.getCategoria() + "\"");
	}
	
}
