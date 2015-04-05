package br.ujr.scorecard.model.passivo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.model.persistence.BusinessObject;


/**
 * Passivo
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public abstract class Passivo extends BusinessObject
{
	protected ContaCorrente contaCorrente;
	protected Date dataMovimento;
	protected String historico;
	protected Conta conta;
	protected int totalParcelas;
	protected Set<Parcela> parcelas = new HashSet<Parcela>();
	
	public Passivo() 
	{
	}
	
	public Date getDataMovimento()
	{
		return dataMovimento;
	}

	public String getHistorico()
	{
		return historico;
	}

	public Conta getConta()
	{
		return conta;
	}

	public void setDataMovimento(Date date)
	{
		dataMovimento = date;
	}
	public void setDataMovimento(java.util.Date date)
	{
		dataMovimento = new Date(date.getTime());
	}
	public void setDataMovimento(String date)
	{
		this.setDataMovimento(this.convertDateString(date));
	}
	
	public void setHistorico(String string)
	{
		historico = string;
	}

	public void setConta(Conta natureza)
	{
		this.conta = natureza;
	}

	/**
	 * Converte uma string no formato dd/mm/yyyy para java.sql.Date 
	 * com o orcado correspondete
	 */
	private Date convertDateString(String strDate)
	{
		int dia = Integer.parseInt(strDate.substring(0,2));
		int mes = Integer.parseInt(strDate.substring(3,5));
		int ano = Integer.parseInt(strDate.substring(6,10));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH,dia);
		cal.set(Calendar.MONTH,--mes);
		cal.set(Calendar.YEAR,ano);
		Date date = new Date(cal.getTimeInMillis());
		return date; 
	}

	public Set<Parcela> getParcelas(ParcelaOrdenador parcelaOrdenador) {
		TreeSet<Parcela> treeSet = new TreeSet<Parcela>(parcelaOrdenador);
		treeSet.addAll(this.parcelas);
		return treeSet;
	}
	
	public Set<Parcela> getParcelas()
	{
		return parcelas;
	}
	public List<Parcela> getParcelas(long referenciaInicial, long referenciaFinal)
	{
	    List<Parcela> filtradas = new ArrayList<Parcela>();
	    for(Parcela parcela : this.parcelas) 
	    {
	    	if (parcela.getReferencia() >= referenciaInicial && parcela.getReferencia() <= referenciaFinal)
	        {
	            filtradas.add(parcela);
	        }
	    }
		return filtradas;
	}
	public void setParcelas(Set<Parcela> set)
	{
		this.parcelas = set;
	}
	public void addParcela(Parcela parcela)
	{
		parcela.setNumero(this.parcelas.size() + 1);
		parcela.setPassivo(this);
		this.parcelas.add(parcela);
	}
	public Parcela getParcela() 
	{
		return this.parcelas.iterator().next();
	}
	public boolean equals(Object obj)
	{
	    if ( obj instanceof Passivo )
	    {
	        Passivo passivo = (Passivo)obj;
	        if ( this.id == passivo.id )
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
	
	public String toString()
    {
        String clazz = this.getClass().getName();
        StringBuffer parcelas = new StringBuffer("\n*** PARCELAS ****\n");
        for (Iterator iParcelas = this.getParcelas().iterator(); iParcelas.hasNext();) {
			Parcela p = (Parcela) iParcelas.next();
			parcelas.append(p.getReferencia() + "|" + p.getDataVencimento() + "|" + p.getValor() + "|" + p.getNumero() + "\n");
		}
        parcelas.append("\n");
        return "[" + this.getId() + "] Tipo: " + clazz + ", Histórico:" + this.getHistorico() + parcelas.toString();
    }
	
	public BigDecimal getValorTotal() {
		BigDecimal total = new BigDecimal(0);
		for (Parcela parcela : this.parcelas) {
			total = total.add(parcela.getValor());
		}
		return total;
	}
	
	public void cleanParcelas() {
		this.setParcelas(new HashSet<Parcela>());
	}
	
	public Parcela getParcela(int idParcela) {
		for(Parcela parc : parcelas) {
			if ( parc.getId() == idParcela ) {
				return parc;
			}
		}
		return null;
	}

	public int getTotalParcelas() {
		return totalParcelas;
	}

	public void setTotalParcelas(int totalParcelas) {
		this.totalParcelas = totalParcelas;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	

}
