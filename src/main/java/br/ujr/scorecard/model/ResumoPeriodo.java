package br.ujr.scorecard.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Encapsula o movimento de um per�odo, resumo.
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class ResumoPeriodo
{
	private long  referenciaInicial;
	private long  referenciaFinal;
	private BigDecimal saldoAnterior;
	private BigDecimal cheques;
	private BigDecimal visa;
	private BigDecimal electron;
	private BigDecimal mastercard;
	private BigDecimal saques;
	private BigDecimal debitosCC;
	private BigDecimal investimentos;
	private BigDecimal depositos;
	private BigDecimal salario;
	private BigDecimal transferencias;
	private String observacao;
	private BigDecimal saldoPrevisto;
	private BigDecimal saldoReal;
	private BigDecimal saldoPrevistoAteDia20;
	
	public BigDecimal getSaldoPrevisto() {
		return saldoPrevisto;
	}
	public void setSaldoPrevisto(BigDecimal saldoPrevisto) {
		this.saldoPrevisto = saldoPrevisto;
	}
	public BigDecimal getSaldoReal() {
		return saldoReal;
	}
	public void setSaldoReal(BigDecimal saldoReal) {
		this.saldoReal = saldoReal;
	}
	public ResumoPeriodo()
	{
	}
	public ResumoPeriodo(long referenciaInicial, long referenciaFinal)
	{
	    this.referenciaInicial = referenciaInicial;
	    this.referenciaFinal   = referenciaFinal;
	}
	public BigDecimal getCheques()
	{
		return cheques;
	}

	public BigDecimal getDebitosCC()
	{
		return debitosCC;
	}

	public BigDecimal getDepositos()
	{
		return depositos;
	}

	public BigDecimal getElectron()
	{
		return electron;
	}

	public BigDecimal getSalario()
	{
		return salario;
	}

	public BigDecimal getInvestimentos()
	{
		return investimentos;
	}

	public BigDecimal getMastercard()
	{
		return mastercard;
	}

	public String getObservacao()
	{
		return observacao;
	}

	public long getReferenciaInicial()
	{
		return referenciaInicial;
	}
	
	public long getReferenciaFinal() {
		return referenciaFinal;
	}
	
	public BigDecimal getSaldoAnterior()
	{
		return saldoAnterior;
	}

	public BigDecimal getSaques()
	{
		return saques;
	}

	public BigDecimal getVisa()
	{
		return visa;
	}

	public void setCheques(BigDecimal f)
	{
		cheques = f;
	}

	public void setDebitosCC(BigDecimal f)
	{
		debitosCC = f;
	}

	public void setDepositos(BigDecimal f)
	{
		depositos = f;
	}

	public void setElectron(BigDecimal f)
	{
		electron = f;
	}

	public void setSalario(BigDecimal f)
	{
		salario = f;
	}

	public void setInvestimentos(BigDecimal f)
	{
		investimentos = f;
	}

	public void setMastercard(BigDecimal f)
	{
		mastercard = f;
	}

	public void setObservacao(String string)
	{
		observacao = string;
	}

	public void setReferenciaInicial(long l)
	{
		referenciaInicial = l;
	}
	
	public void setReferenciaFinal(long referenciaFinal) {
		this.referenciaFinal = referenciaFinal;
	}
	
	public void setSaldoAnterior(BigDecimal f)
	{
		saldoAnterior = f;
	}

	public void setSaques(BigDecimal f)
	{
		saques = f;
	}

	public void setVisa(BigDecimal f)
	{
		visa = f;
	}
	
	public BigDecimal getDespesas() {
		BigDecimal total = new BigDecimal(0);
		total = total.add(this.getCheques());
		total = total.add(this.getVisa());
		total = total.add(this.getElectron());
		total = total.add(this.getMastercard());
		total = total.add(this.getSaques());
		total = total.add(this.getDebitosCC());
		return total;
	}
	public BigDecimal getTransferencias() {
		return transferencias;
	}
	public void setTransferencias(BigDecimal transferencias) {
		this.transferencias = transferencias;
	}
	public BigDecimal getSaldoPrevistoAteDia20() {
		return saldoPrevistoAteDia20;
	}
	public void setSaldoPrevistoAteDia20(BigDecimal saldoPrevistoAteDia20) {
		this.saldoPrevistoAteDia20 = saldoPrevistoAteDia20;
	}
}
