package br.ujr.scorecard.model.reports.totalcontacontabil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.util.Util;

public class TotalContaContabilValue {
	
	private String  descricao;
	private String  nivel;
	private TreeMap<Long,BigDecimal> valorPorMes = new TreeMap<Long,BigDecimal>();
	private double  valorTotalAsDouble;
	private List<TotalContaContabilValuePassivo> passivos = new ArrayList<TotalContaContabilValuePassivo>();
	
	public List<TotalContaContabilValuePassivo> getPassivos() {
		return passivos;
	}
	public void setPassivos(List<TotalContaContabilValuePassivo> passivos) {
		this.passivos = passivos;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String contaContabil) {
		this.descricao = contaContabil;
	}
	public String getValorTotal() {
		return Util.formatCurrency(new BigDecimal(this.getValorTotalAsDouble()),10,false,0);
	}
	public double getValorTotalAsDouble() {
		return valorTotalAsDouble;
	}
	public void setValorTotalAsDouble(double valorTotalAsDouble) {
		this.valorTotalAsDouble = valorTotalAsDouble;
	}
	public void addValorTotal(BigDecimal valorTotal) {
		BigDecimal vlr = new BigDecimal(this.valorTotalAsDouble);
		vlr = vlr.add(valorTotal, MathContext.DECIMAL32);
		this.valorTotalAsDouble = vlr.doubleValue();
	}
	public String getNivel() {
		return nivel;
	}
	public void setNivel(String contaContabilNivel) {
		this.nivel = contaContabilNivel;
	}
	
	public void addValorPorMes(long ref, BigDecimal valor) {
		if ( this.valorPorMes.containsKey(new Long(ref))) {
			BigDecimal vlr    = this.valorPorMes.get(new Long(ref));
			BigDecimal newVlr = vlr.add(valor);
			this.valorPorMes.put(new Long(ref),newVlr);
		} else {
			this.valorPorMes.put(new Long(ref), valor);
		}
	}
	
	private String[] valorPorMesArray = null;
	public String[] getValorPorMesArray() {
		if ( valorPorMesArray == null ) {
			valorPorMesArray = new String[this.getValorPorMes().size()];
			int index = 0;
			for (BigDecimal valor : this.valorPorMes.values()) {
				if ( valor.doubleValue() == 0 ) {
					valorPorMesArray[index++] = "";
				} else {
					valorPorMesArray[index++] = Util.formatCurrency(valor);
				}
			}
		}
		return valorPorMesArray;
	}
	
	private BigDecimal[] valorPorMesBigDecimalArray = null;
	public BigDecimal[] getValorPorMesBigDecimalArray() {
		if ( valorPorMesBigDecimalArray == null ) {
			valorPorMesBigDecimalArray = new BigDecimal[this.getValorPorMes().size()];
			int index = 0;
			for (BigDecimal valor : this.valorPorMes.values()) {
				if ( valor.doubleValue() == 0 ) {
					valorPorMesBigDecimalArray[index++] = new BigDecimal(0);
				} else {
					valorPorMesBigDecimalArray[index++] = valor;
				}
			}
		}
		return valorPorMesBigDecimalArray;
	}
	
	public String[] getMesesArray() {
		String[] array = new String[this.getValorPorMes().size()]; 
		int index = 0;
		for (Long ref : this.valorPorMes.keySet()) {
			String descMes = Util.getMesDescricao(new Integer(("" + ref).substring(4,6)) - 1);
			String ano     = ("" + ref).substring(0,4);
			array[index++] = descMes.substring(0,3) + "/" + ano;
		}
		return array;
	}
	
	public TreeMap<Long, BigDecimal> getValorPorMes() {
		return valorPorMes;
	}
	public void setValorPorMes(TreeMap<Long, BigDecimal> valorPorMes) {
		this.valorPorMes = valorPorMes;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TotalContaContabilValue) {
			TotalContaContabilValue that = (TotalContaContabilValue) obj;
			return this.getNivel().equals(that.getNivel());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.getNivel().hashCode();
	}
	@Override
	public String toString() {
		return "\"" + this.getNivel() + "\" - " + this.getDescricao();
	}
	
	public Boolean getShowMes01() {
		return this.getValorPorMesArray().length >= 1;
	}
	public Boolean getShowMes02() {
		return this.getValorPorMesArray().length >= 2;
	}
	public Boolean getShowMes03() {
		return this.getValorPorMesArray().length >= 3;
	}
	public Boolean getShowMes04() {
		return this.getValorPorMesArray().length >= 4;
	}
	public Boolean getShowMes05() {
		return this.getValorPorMesArray().length >= 5;
	}
	public Boolean getShowMes06() {
		return this.getValorPorMesArray().length >= 6;
	}
	public Boolean getShowMes07() {
		return this.getValorPorMesArray().length >= 7;
	}
	public Boolean getShowMes08() {
		return this.getValorPorMesArray().length >= 8;
	}
	public Boolean getShowMes09() {
		return this.getValorPorMesArray().length >= 9;
	}
	public Boolean getShowMes10() {
		return this.getValorPorMesArray().length >= 10;
	}
	public Boolean getShowMes11() {
		return this.getValorPorMesArray().length >= 11;
	}
	public Boolean getShowMes12() {
		return this.getValorPorMesArray().length >= 12;
	}
	
	public void addPassivo(Passivo passivo, Parcela parcelaPassivo) {
		String tipoPassivo = "";
		if (passivo instanceof Cheque) {
			tipoPassivo = "CHEQUE Nº " + parcelaPassivo.getNumeroCheque();
		} else
		if (passivo instanceof Cartao) {
			Cartao c = (Cartao)passivo;
			switch (c.getEnumOperadora()) {
				case MASTERCARD:    tipoPassivo = "MASTERCARD"; break;
				case VISA:          tipoPassivo = "VISA"; break;
				case VISA_ELECTRON: tipoPassivo = "VISA ELECTRON"; break;
			}
		} else
		if (passivo instanceof DebitoCC) {
			tipoPassivo = "DÉBITO";
		} else
		if (passivo instanceof Saque) {
			tipoPassivo = "SAQUE";
		} else {
			tipoPassivo = passivo.getClass().getName();
		}
		
		String descParcela = parcelaPassivo.getLabelNumeroTotalParcelas();
		this.addPassivo(parcelaPassivo.getReferencia(), 
					    parcelaPassivo.getDataVencimento(),
				        parcelaPassivo.getValor(),
				        passivo.getHistorico(), tipoPassivo, descParcela);
	}
	public void addPassivo(long ref, Date vencimento, BigDecimal valor, String historico, String tipoPassivo, String descricaoParcela) {
		String vlr = Util.formatCurrency(valor, 10, true, 0);
		this.addPassivo(ref,Util.formatDate(vencimento),vlr,historico,tipoPassivo,descricaoParcela);
	}
	public void addPassivo(long ref, String vencimento, String valor, String historico, String tipoPassivo, String descricaoParcela) {
		TotalContaContabilValuePassivo totalContaContabilValuePassivo = new TotalContaContabilValuePassivo(this);
		totalContaContabilValuePassivo.setReferencia(ref);
		
		String mes = String.valueOf(Integer.parseInt(String.valueOf(ref).substring(4)) - 1);
		String ano = String.valueOf(ref).substring(0,4);
		totalContaContabilValuePassivo.setDescricaoReferencia(Util.getMesDescricao(mes).substring(0, 3) + "/" + ano);
		
		totalContaContabilValuePassivo.setVencimento(vencimento);
		totalContaContabilValuePassivo.setValor(valor);
		totalContaContabilValuePassivo.setHistorico(historico);
		totalContaContabilValuePassivo.setTipoPassivo(tipoPassivo);
		totalContaContabilValuePassivo.setDescricaoParcela(descricaoParcela);
		this.addPassivo(totalContaContabilValuePassivo);
		
	}
	
	public void addPassivo(TotalContaContabilValuePassivo totalContaContabilValuePassivo) {
		this.getPassivos().add(totalContaContabilValuePassivo);
	}
	
	public void sortPassivos() {
		Collections.sort(this.passivos);
	}
	
}
