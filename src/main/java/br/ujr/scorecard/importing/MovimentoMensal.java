package br.ujr.scorecard.importing;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa todo o movimento mensal ocorrido em uma interpretação da importação
 * da Planilha de Contas
 * @author Ualter
 */
public class MovimentoMensal {
	
	private TradutorContas tradutorContas;
	private String referencia;
	private String referenciaDescritivo;
	private List<MovimentoMensal.Cheque> cheques = new ArrayList<MovimentoMensal.Cheque>();
	private List<MovimentoMensal.Cartao> cartoes = new ArrayList<MovimentoMensal.Cartao>();
	private List<MovimentoMensal.Debito> debitos = new ArrayList<MovimentoMensal.Debito>();
	
	public MovimentoMensal(TradutorContas tradutorContas, String referencia, String referenciaDescritivo) {
		this.referencia           = referencia;
		this.referenciaDescritivo = referenciaDescritivo;
		this.tradutorContas       = tradutorContas;
	}
	
	public List<MovimentoMensal.Cheque> getCheques() {
		return this.cheques;
	}
	public List<MovimentoMensal.Cartao> getCartoes() {
		return this.cartoes;
	}
	public List<MovimentoMensal.Debito> getDebitos() {
		return this.debitos;
	}
	
	public void add(String[] string) {
		MovimentoMensal.Cheque cheque = new MovimentoMensal.Cheque();
		MovimentoMensal.Cartao cartao = new MovimentoMensal.Cartao();
		MovimentoMensal.Debito debito = new MovimentoMensal.Debito();
		
		if ( !this.isEmpty(string, 1, 4) ) {
			cheque.setData(string[1].trim());
			cheque.setValor(string[2].trim());
			cheque.setHistorico(string[3].trim());
			cheque.setNumero(string[4].trim());
			cheque.setConta(this.tradutorContas.getConta(cheque.getHistorico()));
			cheque.setParcela(this.tradutorContas.getParcela(cheque.getHistorico()));
			cheque.setChave(this.tradutorContas.getChave(cheque.getHistorico()));
			this.cheques.add(cheque);
		}
		
		if ( !this.isEmpty(string, 5, 8) ) {
			cartao.setData(string[5].trim());
			cartao.setCartao(string[6].trim());
			cartao.setValor(string[7].trim());
			cartao.setHistorico(string[8].trim());
			cartao.setConta(this.tradutorContas.getConta(cartao.getHistorico()));
			cartao.setParcela(this.tradutorContas.getParcela(cartao.getHistorico()));
			cartao.setChave(this.tradutorContas.getChave(cartao.getHistorico()));
			this.cartoes.add(cartao);
		}
		
		if ( !this.isEmpty(string, 9, 10) ) {
			debito.setValor(string[9].trim());
			debito.setHistorico(string[10].trim());
			debito.setConta(this.tradutorContas.getConta(debito.getHistorico()));
			debito.setParcela(this.tradutorContas.getParcela(debito.getHistorico()));
			debito.setChave(this.tradutorContas.getChave(debito.getHistorico()));
			
			String anoMes = this.getReferencia();
			String ano    = anoMes.substring(0, 4);
			String mes    = anoMes.substring(4,6);
			String dia    = "15";
			debito.setData(dia + "/" + mes + "/" + ano);
			this.debitos.add(debito);
		}
	}
	
	public class Cheque {
		String data;
		String valor;
		String historico;
		String numero;
		String conta;
		String parcela;
		String chave;
		
		public String getChave() {
			return chave;
		}
		public void setChave(String chave) {
			this.chave = chave;
		}
		public String getParcela() {
			return parcela;
		}
		public void setParcela(String parcela) {
			this.parcela = parcela;
		}
		public String getConta() {
			return conta;
		}
		public void setConta(String conta) {
			this.conta = conta;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getNumero() {
			return numero;
		}
		public void setNumero(String numero) {
			this.numero = numero;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		public String getHistorico() {
			return historico;
		}
		public void setHistorico(String historico) {
			this.historico = historico;
		}
		public String toString() {
			return "[" + this.data + ", " + this.valor + ", " + this.historico + ", " + this.numero + "]";
		}
	}
	public class Cartao {
		String data;
		String cartao;
		String valor;
		String historico;
		String conta;
		String parcela;
		String chave;
		
		public String getChave() {
			return chave;
		}
		public void setChave(String chave) {
			this.chave = chave;
		}
		public String getConta() {
			return conta;
		}
		public void setConta(String conta) {
			this.conta = conta;
		}
		public String getCartao() {
			return cartao;
		}
		public void setCartao(String cartao) {
			this.cartao = cartao;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getHistorico() {
			return historico;
		}
		public void setHistorico(String historico) {
			this.historico = historico;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		public String toString() {
			return "[" + this.data + ", " + this.cartao + ", " + this.valor + ", " + this.historico + "]";
		}
		public String getParcela() {
			return parcela;
		}
		public void setParcela(String parcela) {
			this.parcela = parcela;
		}
	}
	public class Debito {
		String valor;
		String historico;
		String conta;
		String data;
		String parcela;
		String chave;
		
		public String getChave() {
			return chave;
		}
		public void setChave(String chave) {
			this.chave = chave;
		}
		public String getParcela() {
			return parcela;
		}
		public void setParcela(String parcela) {
			this.parcela = parcela;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getConta() {
			return conta;
		}
		public void setConta(String conta) {
			this.conta = conta;
		}
		public String getHistorico() {
			return historico;
		}
		public void setHistorico(String historico) {
			this.historico = historico;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		public String toString() {
			return "[" + this.valor + ", " + this.historico + "]";
		}
		
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.referencia).append("\n");
		
		sb.append("CHEQUES\n");
		for (MovimentoMensal.Cheque cheque : cheques) {
			sb.append(cheque.toString()).append("\n");
		}
		
		sb.append("\n");
		
		sb.append("CARTOES\n");
		for (MovimentoMensal.Cartao cartao : cartoes) {
			sb.append(cartao.toString()).append("\n");
		}
		
		sb.append("\n");
		
		sb.append("DEBITOS\n");
		for (MovimentoMensal.Debito debito : debitos) {
			sb.append(debito.toString()).append("\n");
		}
		
		return sb.append("\n").toString();
	}
	
	private boolean isEmpty(String[] strings, int start, int end) {
		for (int i = start; i < end; i++) {
			if ( !strings[i].trim().equals("") ) {
				return false;
			}
		}
		return true;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getReferenciaDescritivo() {
		return referenciaDescritivo;
	}

	public void setReferenciaDescritivo(String referenciaString) {
		this.referenciaDescritivo = referenciaString;
	}
}

