package br.ujr.scorecard.model.passivo.parcela;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Calendar;

import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.persistence.BusinessObject;
import br.ujr.scorecard.util.Util;

/**
 * Representa uma parcela do Passivo
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Parcela extends BusinessObject
{
	protected long       referencia;
	protected int        numero;
	protected Date       dataVencimento;
	protected BigDecimal valor;
	protected boolean    efetivado;
	protected boolean    cheque;
	protected String     numeroCheque;
	protected Passivo    passivo;
	
    public Parcela()
	{
	}
    
	public Parcela(String dataVencimento, float valor)
	{
		this.setReferenciaByDataVencimento(dataVencimento);
		this.setDataVencimento(dataVencimento);
		this.setValor(valor);
	}
	
	public Date getDataVencimento()
	{
		return dataVencimento;
	}

	public boolean isEfetivado()
	{
		return efetivado;
	}

	public BigDecimal getValor()
	{
		return valor;
	}

	public void setDataVencimento(Date date)
	{
		this.setReferenciaByDataVencimento(date);
		dataVencimento = date;
	}
	public void setDataVencimento(String strDate)
	{
		this.setDataVencimento(this.convertDateString(strDate));
	}

	public void setEfetivado(boolean b)
	{
		efetivado = b;
	}

	public void setValor(BigDecimal f)
	{
		valor = f;
	}
	public void setValor(float f)
	{
		valor = new BigDecimal(f);
	}
	public int getNumero()
	{
		return numero;
	}
	public void setNumero(int i)
	{
		numero = i;
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
	
	private void setReferenciaByDataVencimento(Date dataVencimento)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataVencimento);
		DecimalFormat df = new DecimalFormat("00");
		String mes        = df.format((cal.get(Calendar.MONTH) + 1));
		df.applyPattern("0000");
		String ano        = df.format(cal.get(Calendar.YEAR));
		long   referencia = Long.parseLong(ano + mes);
		this.setReferencia(referencia);
	}
	private void setReferenciaByDataVencimento(String dataVencimento)
	{
		String mes        = dataVencimento.substring(3,5);
		String ano        = dataVencimento.substring(6,10);
		long   referencia = Long.parseLong(ano + mes);
		this.setReferencia(referencia);
	}
	
	public Passivo getPassivo()
    {
        return this.passivo;
    }
    public void setPassivo(Passivo passivo)
    {
        this.passivo = passivo;
    }
    
    public String toString()
    {
        return "[" + this.getId() + "] Conta: " + this.getPassivo().getConta().getNivel() + "-" + 
        			this.getPassivo().getConta().getDescricao() + ", Valor:" + 
        			this.getValor() + ", Referência:" + this.getReferencia();
    }
    
	public long getReferencia()
	{
		return referencia;
	}

	public void setReferencia(long l)
	{
		referencia = l;
	}
	public boolean isCheque() {
		return cheque;
	}
	public void setCheque(boolean cheque) {
		this.cheque = cheque;
	}
	public String getNumeroCheque() {
		return numeroCheque;
	}
	public void setNumeroCheque(String numeroCheque) {
		this.numeroCheque = numeroCheque;
	}
	
	public boolean equals(Object obj)
	{
	    if ( obj instanceof Passivo )
	    {
	        Parcela parcela = (Parcela)obj;
	        if ( this.id == parcela.id )
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
	
	public int getNumeroParcelas() {
		return this.getPassivo().getTotalParcelas();
	}
	
	public String getLabelNumeroTotalParcelas() {
		return Util.formatNumber("00",this.getNumero()) + "/" + Util.formatNumber("00",this.getNumeroParcelas());
	}
	
	public java.util.Date getReferenciaAsDate() {
		Calendar calendar = Calendar.getInstance();
		String   ref      = String.valueOf(this.getReferencia());
		String   mes      = ref.substring(4);
		String   ano      = ref.substring(0, 4);
		calendar.set(Calendar.MONTH,Integer.parseInt(mes) - 1);
		calendar.set(Calendar.YEAR,Integer.parseInt(ano));
		return calendar.getTime();
	}
	
	public void setReferencia(java.util.Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		
		String mes = df2.format(calendar.get(Calendar.MONTH) + 1);
		String ano = df4.format(calendar.get(Calendar.YEAR));
		
		this.referencia = new Long(ano+mes);
	}
	

}
