package br.ujr.scorecard.model;

import java.math.BigDecimal;
import java.util.Comparator;

import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;

public class ResumoPeriodoTotalCartao  implements Comparable<ResumoPeriodoTotalCartao> {
	
	private String           keyTotalCartao;
	private ContaCorrente    contaCorrente;
	private CartaoContratado cartaoContratado;
	private BigDecimal       total;
	
	public ResumoPeriodoTotalCartao(ContaCorrente contaCorrente, CartaoContratado cartaoContratado) {
		super();
		this.contaCorrente = contaCorrente;
		this.cartaoContratado = cartaoContratado;
		this.computeKey();
	}
	
	public ResumoPeriodoTotalCartao(ContaCorrente contaCorrente, CartaoContratado cartaoContratado, BigDecimal total) {
		super();
		this.contaCorrente = contaCorrente;
		this.cartaoContratado = cartaoContratado;
		this.total = total;
		this.computeKey();
	}
	
	private void computeKey() {
		this.keyTotalCartao = (this.contaCorrente != null ? this.contaCorrente.getId() : "0") + "_" + this.getCartaoContratado().getId();
	}
	
	@Override
	public int hashCode() {
		return this.keyTotalCartao.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResumoPeriodoTotalCartao other = (ResumoPeriodoTotalCartao) obj;
		if (keyTotalCartao == null) {
			if (other.keyTotalCartao != null)
				return false;
		} else if (!keyTotalCartao.equals(other.keyTotalCartao))
			return false;
		return true;
	}

	public String getKeyTotalCartao() {
		return keyTotalCartao;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public CartaoContratado getCartaoContratado() {
		return cartaoContratado;
	}

	public void setCartaoContratado(CartaoContratado cartaoContratado) {
		this.cartaoContratado = cartaoContratado;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}


	@Override
	public int compareTo(ResumoPeriodoTotalCartao o) {
		if ( this.getContaCorrente() != null && o.getContaCorrente() == null ) return  1;
		if ( this.getContaCorrente() == null && o.getContaCorrente() != null ) return -1;
		if ( this.getContaCorrente() == null && o.getContaCorrente() == null ) {
			return
					this.getCartaoContratado().getContaCorrente().getBanco().compareTo(
							o.getCartaoContratado().getContaCorrente().getBanco());
		}
		
		if ( this.getContaCorrente().getId() == o.getContaCorrente().getId() ) {
			 return compareCartaoContratado(o);
		} else {
			if ( this.getContaCorrente().getId() >  o.getContaCorrente().getId() ) return  1;
			if ( this.getContaCorrente().getId() <  o.getContaCorrente().getId() ) return -1;
			return 0;
		}
	}

	private int compareCartaoContratado(ResumoPeriodoTotalCartao o) {
		if ( this.getCartaoContratado().getId() > o.getCartaoContratado().getId() ) return  1;
		if ( this.getCartaoContratado().getId() < o.getCartaoContratado().getId() ) return -1;
		return 0;
	}

	@Override
	public String toString() {
		return "ResumoPeriodoTotalCartao [keyTotalCartao=" + keyTotalCartao + ", total=" + total + ", cartaoContratado="
				+ cartaoContratado + ", contaCorrente=" + contaCorrente + "]";
	}
	
	

}
