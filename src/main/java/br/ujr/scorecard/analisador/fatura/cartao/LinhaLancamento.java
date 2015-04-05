package br.ujr.scorecard.analisador.fatura.cartao;

import br.ujr.scorecard.model.conta.Conta;

public class LinhaLancamento {
		
		private boolean checked;
		private String data;
		private String descricao;
		private String valor;
		private String valorDolar;
		private Conta contaContabil;
		
		public LinhaLancamento() {
		}
		public LinhaLancamento(boolean confere, String data, String descricao, String valor, String valorDolar) {
			this.checked = confere;
			this.data = data;
			this.descricao = descricao;
			this.valor = valor;
			this.valorDolar = valorDolar;
		}
		
		public boolean isConfere() {
			return checked;
		}
		public void setConfere(boolean confere) {
			this.checked = confere;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getDescricao() {
			return descricao;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = valor;
		}
		public String getValorDolar() {
			return valorDolar;
		}
		public void setValorDolar(String valorDolar) {
			this.valorDolar = valorDolar;
		}
		public String toString() {
			return data + "\t" + valor + "\t" + valorDolar + "\t" + descricao;
		}
		public Conta getContaContabil() {
			return contaContabil;
		}
		public void setContaContabil(Conta contaContabil) {
			this.contaContabil = contaContabil;
		}
	}