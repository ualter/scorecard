package br.ujr.scorecard.model.extrato;

public class LinhaExtratoCartao {
	
	private String data;
	private String historico;
	private String pais;
	private String valor;
	private boolean conferido;
	
	public boolean isConferido() {
		return conferido;
	}
	public void setConferido(boolean conferido) {
		this.conferido = conferido;
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
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String toString() {
		return data + ", " + historico + ", " + pais + ", " + valor;
	}
	
	
	

}
