package br.ujr.scorecard.model.ativo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.persistence.BusinessObject;
import br.ujr.scorecard.model.transferencia.Transferencia;

/**
 * Ativo
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public abstract class Ativo extends BusinessObject
{
	protected ContaCorrente contaCorrente;
	protected long          referencia;
	protected Date          dataMovimento;
	protected BigDecimal    valor;
	protected String        historico;
	protected Conta         conta;
	protected Transferencia transferencia;
	
	public Conta getConta() {
		return conta;
	}
	public void setConta(Conta conta) {
		this.conta = conta;
	}
	public Date getDataMovimento()
	{
		return dataMovimento;
	}
	public String getHistorico()
	{
		return historico;
	}
	public long getReferencia()
	{
		return referencia;
	}
	public BigDecimal getValor()
	{
		return valor;
	}
	public void setDataMovimento(Date date)
	{
		dataMovimento = date;
		this.setReferenciaByDataMovimento(dataMovimento);
	}
	public void setDataMovimento(String strDate)
	{
		this.setDataMovimento(this.convertDateString(strDate));
	}
	public void setHistorico(String string)
	{
		historico = string;
	}
	public void setReferencia(long l)
	{
		referencia = l;
	}
	public void setValor(float f)
	{
		valor = new BigDecimal(f);
	}
	public void setValor(BigDecimal f)
	{
		valor = f;
	}
	
	private void setReferenciaByDataMovimento(Date dataMovimento)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataMovimento);
		DecimalFormat df = new DecimalFormat("00");
		String mes        = df.format((cal.get(Calendar.MONTH) + 1));
		df.applyPattern("0000");
		String ano        = df.format(cal.get(Calendar.YEAR));
		long   referencia = Long.parseLong(ano + mes);
		this.setReferencia(referencia);
	}
	
    //	Suport Methods
	
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
	
	
    public String toString()
    {
        return "Tipo: " + this.getClass().getName() + ", Histórico:" + this.getHistorico() + ", Valor:" + this.getValor() + ", " + this.getReferencia();
    }
    
	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}
	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
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
	public void setReferencia(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		
		String mes = df2.format(calendar.get(Calendar.MONTH) + 1);
		String ano = df4.format(calendar.get(Calendar.YEAR));
		
		this.referencia = new Long(ano+mes);
	}
	
	public Transferencia getTransferencia() {
		return transferencia;
	}
	public void setTransferencia(Transferencia transferencia) {
		this.transferencia = transferencia;
	}
	
	public boolean isOrigemTransferencia() {
		if ( this.getTransferencia() != null ) {
			return true;
		}
		return false;
	}
}
