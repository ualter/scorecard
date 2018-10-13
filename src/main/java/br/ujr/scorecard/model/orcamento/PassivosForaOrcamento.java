package br.ujr.scorecard.model.orcamento;

import java.math.BigDecimal;
import java.util.Set;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.Passivo;

public class PassivosForaOrcamento {
	
	private ContaCorrente contaCorrente;
	private String contaContabil;
	private BigDecimal total;
	private Set<Passivo> passivos;
	
	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}
	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}
	public String getContaContabil() {
		return contaContabil;
	}
	public void setContaContabil(String contaContabil) {
		this.contaContabil = contaContabil;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Set<Passivo> getPassivos() {
		return passivos;
	}
	public void setPassivos(Set<Passivo> passivos) {
		this.passivos = passivos;
	}
	
	

}
