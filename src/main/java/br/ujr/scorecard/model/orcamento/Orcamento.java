package br.ujr.scorecard.model.orcamento;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.persistence.BusinessObject;

/**
 * Orçamento
 * Estimativa de custo, gasto de futuro Passivo
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Orcamento extends BusinessObject {
	
	protected ContaCorrente contaCorrente;
	protected long          referencia;
	protected BigDecimal    orcado;
	protected Conta         contaAssociada;
	protected String        descricao;
	protected BigDecimal    realizado;
	
	public Orcamento() {
		super();
	}

	public Conta getContaAssociada() {
		return contaAssociada;
	}

	public void setContaAssociada(Conta contaAssociada) {
		this.contaAssociada = contaAssociada;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public long getReferencia() {
		return referencia;
	}
	public Date getReferenciaAsDate() {
		Calendar calendar = Calendar.getInstance();
		String   ref      = String.valueOf(this.getReferencia());
		String   mes      = ref.substring(4);
		String   ano      = ref.substring(0, 4);
		calendar.set(Calendar.MONTH,Integer.parseInt(mes));
		calendar.set(Calendar.YEAR,Integer.parseInt(ano));
		return calendar.getTime();
	}
	public String getMesAno() {
		String ref = String.valueOf(this.getReferencia());
		ref        = ref.substring(4) + "/" + ref.substring(0, 4);
		return ref;
	}

	public void setReferencia(long referencia) {
		this.referencia = referencia;
	}

	public BigDecimal getOrcado() {
		return orcado;
	}

	public void setOrcado(BigDecimal valor) {
		this.orcado = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public void setReferencia(String referencia) {
		this.referencia = new Long(referencia);
	}
	
	public void setReferencia(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		
		String mes = df2.format(calendar.get(Calendar.MONTH) + 1);
		String ano = df4.format(calendar.get(Calendar.YEAR));
		
		this.referencia = new Long(ano+mes);
	}

	public BigDecimal getRealizado() {
		if ( this.realizado == null ) this.realizado = new BigDecimal(0);
		return realizado;
	}

	public void setRealizado(BigDecimal realizado) {
		this.realizado = realizado;
	}
	
	public void setRealizado(double realizado) {
		this.realizado = new BigDecimal(realizado);
	}
	
	public boolean equals(Object obj)
	{
	    if ( obj instanceof Orcamento )
	    {
	    	Orcamento orcamento = (Orcamento)obj;
	        if ( this.id == orcamento.getId() )
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
	
	@Override
	public String toString() {
		return "[" + this.getReferencia() + ", " + this.getContaAssociada().getDescricao() + ", " + this.getOrcado() + "]";
	}
	

}
