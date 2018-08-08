package br.ujr.scorecard.model.reports.resumomensal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.ujr.scorecard.model.ResumoPeriodo;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.reports.AbstractReport;
import br.ujr.scorecard.model.reports.ReportManagerListener;
import br.ujr.scorecard.util.Util;

public class ResumoMensal extends AbstractReport {
	
	public String getJasperFileName() {
		return "ResumoMensal.jasper";
	}
	
	public Collection execute() {
		List<ResumoMensalValue> result = new ArrayList<ResumoMensalValue>();
		
		ContaCorrente conta = (ContaCorrente)this.criteria[0];
		long          ini   = Util.extrairReferencia((Date)this.criteria[1]);
		long          fim   = Util.extrairReferencia((Date)this.criteria[2]);
		
		/**
		 * Calculando Total de Registros
		 */
		int progress = 0;
		for (long i = ini; i <= fim; i = Util.computeReferencia(i, 1)) {
			for(ReportManagerListener listener : listeners) {
				listener.reportMaxProgress(++progress);
			}
		}
		
		/**
		 * Processamento do Relatório 
		 */
		progress = 0;
		for (long i = ini; i <= fim; i = Util.computeReferencia(i, 1)) {
			
			for(ReportManagerListener listener : listeners) {
				listener.reportProgress("",++progress);
			}
			
			ResumoPeriodo resumo =  this.scorecardManager.getResumoPeriodo(conta, i, i, true);
			
			ResumoMensalValue value = new ResumoMensalValue();
			
			String mesAno =  ("" + resumo.getReferenciaInicial()).substring(0, 4) +  "/" + 
				Util.getMesDescricao(Integer.parseInt(("" + resumo.getReferenciaInicial()).substring(4)) - 1);
			value.setMesAno(mesAno);
			
			int tam = 11;
			value.setSaldoAnterior(Util.formatCurrency(resumo.getSaldoAnterior(),tam,false,0));
			value.setCheques(Util.formatCurrency(resumo.getCheques(),tam,false,0));
			value.setVisa(Util.formatCurrency(resumo.getVisa(),tam,false,0));
			value.setElectron(Util.formatCurrency(resumo.getElectron(),tam,false,0));
			value.setMastercard(Util.formatCurrency(resumo.getMastercard(),tam,false,0));
			value.setSaques(Util.formatCurrency(resumo.getSaques(),tam,false,0));
			value.setDebitos(Util.formatCurrency(resumo.getDebitosCC(),tam,false,0));
			value.setInvestimentos(Util.formatCurrency(resumo.getInvestimentos(),tam,false,0));
			value.setDepositos(Util.formatCurrency(resumo.getDepositos(),tam,false,0));
			value.setEstipendio(Util.formatCurrency(resumo.getSalario(),tam,false,0));
			value.setTransferencias(Util.formatCurrency(resumo.getTransferencias(),tam,false,0));
			
			BigDecimal totalDespesas = new BigDecimal(0);
			totalDespesas = totalDespesas.add(resumo.getCheques());
			totalDespesas = totalDespesas.add(resumo.getVisa());
			totalDespesas = totalDespesas.add(resumo.getElectron());
			totalDespesas = totalDespesas.add(resumo.getMastercard());
			totalDespesas = totalDespesas.add(resumo.getSaques());
			totalDespesas = totalDespesas.add(resumo.getDebitosCC());
			value.setTotalDespesa(Util.formatCurrency(totalDespesas,tam,false,0));
			
			BigDecimal saldo = new BigDecimal(0);
			saldo = saldo.add(resumo.getSaldoAnterior());
			saldo = saldo.add(resumo.getDepositos());
			saldo = saldo.add(resumo.getSalario());
			saldo = saldo.subtract(resumo.getInvestimentos());
			saldo = saldo.subtract(totalDespesas);
			value.setSaldoAsDouble(saldo.doubleValue());
			value.setSaldo(Util.formatCurrency(saldo,tam,false,0));
			
			result.add(value);
		}
		return result;
	}
}

