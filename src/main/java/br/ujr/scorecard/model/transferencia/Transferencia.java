package br.ujr.scorecard.model.transferencia;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.persistence.BusinessObject;

public class Transferencia extends BusinessObject {
	
	protected ContaCorrente contaCorrente;
	protected long          referencia;
	protected Date          dataMovimento;
	protected BigDecimal    valor;
	protected String        historico;
	protected Conta         conta;
	protected Ativo         ativoTransferido;
	
	private   int           ativoOrfao;

	public int getAtivoOrfao() {
		return ativoOrfao;
	}

	public void setAtivoOrfao(int idAtivoOrfao) {
		this.ativoOrfao = idAtivoOrfao;
	}

	public Ativo getAtivoTransferido() {
		return ativoTransferido;
	}

	public void setAtivoTransferido(Ativo ativoSalvo) {
		if ( ativoSalvo.getContaCorrente() == null ) {
			throw new RuntimeException("Conta Corrente de Ativo Inválida");
		}
		if ( ativoSalvo.getContaCorrente().equals(this.getContaCorrente())) {
			StringBuffer msg = new StringBuffer("O Ativo criado "); 
			msg.append("deve ser obrigatoriamente para OUTRA conta corrente, caracterizando assim uma transferência");
			throw new RuntimeException(msg.toString());
		}
		this.ativoTransferido = ativoSalvo;
		this.ativoTransferido.setTransferencia(this);
	}
	
	public void setAtivoTransferido(ContaCorrente contaCorrenteDestino, Conta contaContabilDestino, Class classAtivo, String historico) {
		if ( this.getValor() == null || this.getDataMovimento() == null ) {
			StringBuffer msg = new StringBuffer("A transferência deve ser configurada antes do Ativo para reuso das informações"); 
			throw new RuntimeException(msg.toString());
		}
		if ( this.getAtivoTransferido() != null ) {
			this.setAtivoOrfao(this.getAtivoTransferido().getId());
		}
		Ativo ativo = null;
		try {
			ativo = (Ativo) classAtivo.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		fillAtivo(contaCorrenteDestino, contaContabilDestino, ativo);
		ativo.setHistorico(historico);
		this.setAtivoTransferido(ativo);
	}

	private void fillAtivo(ContaCorrente contaCorrenteDestino, Conta contaContabilDestino, Ativo ativo) {
		ativo.setContaCorrente(contaCorrenteDestino);
		ativo.setConta(contaContabilDestino);
		ativo.setDataMovimento(this.getDataMovimento());
		ativo.setReferencia(this.getReferencia());
		ativo.setValor(this.getValor());
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public ContaCorrente getContaCorrente() {
		return contaCorrente;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
		if ( this.getAtivoTransferido() != null ) {
			this.getAtivoTransferido().setDataMovimento(dataMovimento);
		}
	}
	

	public String getHistorico() {
		return historico;
	}

	public void setHistorico(String historico) {
		this.historico = historico;
	}

	public long getReferencia() {
		return referencia;
	}

	public void setReferencia(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		
		String mes = df2.format(calendar.get(Calendar.MONTH) + 1);
		String ano = df4.format(calendar.get(Calendar.YEAR));
		
		this.setReferencia(new Long(ano+mes));
	}
	
	public void setReferencia(long referencia) {
		this.referencia = referencia;
		if ( this.getAtivoTransferido() != null ) {
			this.getAtivoTransferido().setReferencia(referencia);
		}
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
		if ( this.getAtivoTransferido() != null ) {
			this.getAtivoTransferido().setValor(valor);
		}
	}
	public void setValor(double valor) {
		this.valor = new BigDecimal(valor);
		if ( this.getAtivoTransferido() != null ) {
			this.getAtivoTransferido().setValor(new BigDecimal(valor));
		}
	}
	
	public String getMesAno() {
		String ref = String.valueOf(this.getReferencia());
		ref        = ref.substring(4) + "/" + ref.substring(0, 4);
		return ref;
	}
	
	public String getDestino() {
		Banco         banco = this.getAtivoTransferido().getContaCorrente().getBanco();
		ContaCorrente cc    = this.getAtivoTransferido().getContaCorrente();
		StringBuffer msg = new StringBuffer();
		msg.append(cc.getDescricao()).append(" ").append(cc.getNumero());
		return msg.toString();
	}
	public Date getReferenciaAsDate() {
		Calendar calendar = Calendar.getInstance();
		String   ref      = String.valueOf(this.getReferencia());
		String   mes      = "" + (Integer.parseInt(ref.substring(4)) - 1);
		String   ano      = ref.substring(0, 4);
		calendar.set(Calendar.MONTH,Integer.parseInt(mes));
		calendar.set(Calendar.YEAR,Integer.parseInt(ano));
		return calendar.getTime();
	}

}
