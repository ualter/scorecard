package br.ujr.scorecard.model.reports.totalcontacontabil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaContabilNivelOrdenador;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.reports.AbstractReport;
import br.ujr.scorecard.model.reports.ReportManagerListener;
import br.ujr.scorecard.util.Util;

public class TotalContaContabil extends AbstractReport {

	public static final int SINTETICO = 1;
	public static final int ANALITICO = 2;
	public static final int GRAFICO   = 3;
	
	protected int tipo = SINTETICO;
	protected ContaCorrente contaCorrente;
	protected Date refIni;
	protected Date refFim;
	protected String[] niveis;
	protected int tipoGrafico;
	protected Boolean efetivados;
	
	@SuppressWarnings("unchecked")
	public Collection execute() {
		contaCorrente = (ContaCorrente)criteria[0];
		refIni        = (Date)criteria[1];
		refFim        = (Date)criteria[2];
		niveis        = (String[])criteria[3];
		tipo          = ((Integer)criteria[4]).intValue();
		tipoGrafico   = ((Integer)criteria[5]).intValue();
		efetivados    = (Boolean)criteria[6];
		Set<Passivo> passivos = this.scorecardBusinessDelegate
									.getPassivosPorNiveisContaContabil(contaCorrente, niveis, true,Util.extrairReferencia(refIni), Util.extrairReferencia(refFim));
		
		List<TotalContaContabilValue> values = null;
		
		switch(tipo) {
			case 1: 
				values = this.executeSintetico(passivos); 
				break;
			case 2:
				values = this.executeAnalitico(passivos);
				break;
			case 3:
				values = this.executeGrafico(passivos);
				break;
		}
		return values;
	}

	private List<TotalContaContabilValue> executeSintetico(Set<Passivo> passivos) {
		/**
		 * Cálculando Total por Conta Contábil dos Passivos existentes 
		 * retornados para as Contas Contábeis requeridas
		 */
		Map<Conta,TotalContaContabilValue> map = new HashMap<Conta,TotalContaContabilValue>();
		
		/**
		 * Informando Total de Registros
		 */
		for(ReportManagerListener listener : listeners) {
			listener.reportMaxProgress(passivos.size());
		}
		
		BigDecimal totalGeral                = new BigDecimal(0);
		TreeMap<Long,BigDecimal> totalPorMes = new TreeMap<Long,BigDecimal>();
		
		int progress = 0;
		for(Passivo p : passivos) {
			/**
			 * Informando Registro Processado
			 */
			for(ReportManagerListener listener : listeners) {
				listener.reportProgress(p.getHistorico(), ++progress);
			}
			
			if ( !map.containsKey(p.getConta()) ) {
				TotalContaContabilValue totalContaContabilValue = criarValueObject(p.getConta());
				map.put(p.getConta(),totalContaContabilValue);
			}
			
			TotalContaContabilValue totalContaContabilValue = map.get(p.getConta());
			BigDecimal valor = new BigDecimal(0,MathContext.DECIMAL32);
			for(Parcela parcela : p.getParcelas()) {
				if (   parcela.getReferencia() >= Util.extrairReferencia(refIni) 
					&& parcela.getReferencia() <= Util.extrairReferencia(refFim) ) {
					
					if ( efetivados == null || efetivados.booleanValue() == parcela.isEfetivado() ) {
						totalContaContabilValue.addValorPorMes(parcela.getReferencia(), parcela.getValor());
						valor = valor.add(parcela.getValor());
						
						/**
						 * Calculo do Total Geral (todos os passivos) por Mês
						 */
						Long ref = new Long(parcela.getReferencia());
						BigDecimal total = new BigDecimal(0);
						if ( totalPorMes.containsKey(ref) ) {
							total = totalPorMes.get(ref);
						}
						total = total.add(parcela.getValor());
						totalPorMes.put(ref, total);
					}
				}
			}
			totalGeral = totalGeral.add(valor);
			totalContaContabilValue.addValorTotal(valor);
		}
		
		//TODO: Inserir ATIVOS (como Investimentos) no relatório.
		/**
		 * Buscar os ativos dentro da referência requisitada e adicionar no Map
		 * principal Map<Conta,TotalContaContabilValue> map
		 * Não esquecer de somar em totalGeral e em totalPorMes
		 */
		
		List<TotalContaContabilValue> values = new ArrayList<TotalContaContabilValue>(map.values());
		
		/**
		 * Inserindo Contas Contábeis sem débitos para disposição hierárquica completa
		 * das contas que possuem débitos
		 */
		this.inserirContasContabeisVazias(passivos, values);
		
		/**
		 * Ordenação Níveis, de modo que:
		 * 1.1.1 < 1.0
		 * 1.2   < 1.0
		 * 1.1.2 < 1.0
		 * 10.1  > 1.0
		 * 1.2.1 > 1.0
		 */
		this.ordenarRegistros(values);
		
		/**
		 * Colocando valores zerados para os meses em que não há movimento para que posicionamento termine correto no relatório. 
		 * Ex:  JAN/2008 FEV/2008 MAR/2008 ABR/2008
		 *                 10,00            40,00
		 * Caso não seje feito isto, o relatário ficaria:
		 *      JAN/2008 FEV/2008 MAR/2008 ABR/2008
		 *         10,00   40,00
		 */
		this.inserirValoresMesesVazios(values);
		
		/**
		 * Identação 
		 */
		this.identarRegistros(values);
		
		/**
		 * Cabeçalho para os meses escolhidos
		 */
		this.construirCabecalho(totalGeral, totalPorMes);
		return values;
	}

	private List<TotalContaContabilValue> executeAnalitico(Set<Passivo> passivos) {
		/**
		 * Cálculando Total por Conta Contábil dos Passivos existentes 
		 * retornados para as Contas Contábeis requeridas
		 */
		Map<Conta,TotalContaContabilValue> map = new HashMap<Conta,TotalContaContabilValue>();
		
		/**
		 * Informando Total de Registros
		 */
		for(ReportManagerListener listener : listeners) {
			listener.reportMaxProgress(passivos.size());
		}
		
		BigDecimal totalGeral                = new BigDecimal(0);
		TreeMap<Long,BigDecimal> totalPorMes = new TreeMap<Long,BigDecimal>();
		
		int progress = 0;
		for(Passivo passivo : passivos) {
			/**
			 * Informando Registro Processado
			 */
			for(ReportManagerListener listener : listeners) {
				listener.reportProgress(passivo.getHistorico(), ++progress);
			}
			
			if ( !map.containsKey(passivo.getConta()) ) {
				TotalContaContabilValue totalContaContabilValue = criarValueObject(passivo.getConta());
				map.put(passivo.getConta(),totalContaContabilValue);
			}
			
			TotalContaContabilValue totalContaContabilValue = map.get(passivo.getConta());
			BigDecimal valor = new BigDecimal(0,MathContext.DECIMAL32);
			for(Parcela parcela : passivo.getParcelas()) {
				if (   parcela.getReferencia() >= Util.extrairReferencia(refIni) 
					&& parcela.getReferencia() <= Util.extrairReferencia(refFim) ) {
					
					if ( efetivados == null || efetivados.booleanValue() == parcela.isEfetivado() ) {
						totalContaContabilValue.addValorPorMes(parcela.getReferencia(), parcela.getValor());
						valor = valor.add(parcela.getValor());

						/**
						 * Calculo do Total Geral (todos os passivos) por Mês
						 */
						Long ref = new Long(parcela.getReferencia());
						BigDecimal total = new BigDecimal(0);
						if ( totalPorMes.containsKey(ref) ) {
							total = totalPorMes.get(ref);
						}
						total = total.add(parcela.getValor());
						totalPorMes.put(ref, total);

						totalContaContabilValue.addPassivo(passivo, parcela);
					}
				}
				
			}
			totalGeral = totalGeral.add(valor);
			totalContaContabilValue.addValorTotal(valor);
		}
		List<TotalContaContabilValue> values = new ArrayList<TotalContaContabilValue>(map.values());
		
		//this.inserirContasContabeisVazias(passivos, values);
		
		/**
		 * Ordenação Níveis, de modo que:
		 * 1.1.1 < 1.0
		 * 1.2   < 1.0
		 * 1.1.2 < 1.0
		 * 10.1  > 1.0
		 * 1.2.1 > 1.0
		 */
		this.ordenarRegistros(values);
		
		/**
		 * Ordenar Passivos
		 */
		for (TotalContaContabilValue value : values) {
			value.sortPassivos();
		}
		
		//this.inserirValoresMesesVazios(values);
		//this.identarRegistros(values);
		this.construirCabecalho(totalGeral, totalPorMes);
		
		return values;
	}
	
	private List<TotalContaContabilValue> executeGrafico(Set<Passivo> passivos) {
		/**
		 * Cálculando Total por Conta Contábil dos Passivos existentes 
		 * retornados para as Contas Contábeis requeridas
		 */
		Map<Conta,TotalContaContabilValue> map = new HashMap<Conta,TotalContaContabilValue>();
		
		/**
		 * Informando Total de Registros
		 */
		for(ReportManagerListener listener : listeners) {
			listener.reportMaxProgress(passivos.size());
		}
		
		BigDecimal totalGeral                = new BigDecimal(0);
		TreeMap<Long,BigDecimal> totalPorMes = new TreeMap<Long,BigDecimal>();
		
		int progress = 0;
		for(Passivo p : passivos) {
			/**
			 * Informando Registro Processado
			 */
			for(ReportManagerListener listener : listeners) {
				listener.reportProgress(p.getHistorico(), ++progress);
			}
			
			if ( !map.containsKey(p.getConta()) ) {
				TotalContaContabilValue totalContaContabilValue = criarValueObject(p.getConta());
				map.put(p.getConta(),totalContaContabilValue);
			}
			
			TotalContaContabilValue totalContaContabilValue = map.get(p.getConta());
			BigDecimal valor = new BigDecimal(0,MathContext.DECIMAL32);
			for(Parcela parcela : p.getParcelas()) {
				if (   parcela.getReferencia() >= Util.extrairReferencia(refIni) 
					&& parcela.getReferencia() <= Util.extrairReferencia(refFim) ) {
					
					if ( efetivados == null || efetivados.booleanValue() == parcela.isEfetivado() ) {
						totalContaContabilValue.addValorPorMes(parcela.getReferencia(), parcela.getValor());
						valor = valor.add(parcela.getValor());

						/**
						 * Calculo do Total Geral (todos os passivos) por Mês
						 */
						Long ref = new Long(parcela.getReferencia());
						BigDecimal total = new BigDecimal(0);
						if ( totalPorMes.containsKey(ref) ) {
							total = totalPorMes.get(ref);
						}
						total = total.add(parcela.getValor());
						totalPorMes.put(ref, total);
					}
				}
			}
			totalGeral = totalGeral.add(valor);
			totalContaContabilValue.addValorTotal(valor);
		}
		List<TotalContaContabilValue> values = new ArrayList<TotalContaContabilValue>(map.values());
		
		/**
		 * Inserindo Contas Contábeis sem débitos para disposição hierárquica completa
		 * das contas que possuem débitos
		 */
		//this.inserirContasContabeisVazias(passivos, values);
		
		/**
		 * Ordenação Níveis, de modo que:
		 * 1.1.1 < 1.0
		 * 1.2   < 1.0
		 * 1.1.2 < 1.0
		 * 10.1  > 1.0
		 * 1.2.1 > 1.0
		 */
		this.ordenarRegistros(values);
		
		/**
		 * Colocando valores zerados para os meses em que não há movimento para que posicionamento termine correto no relatório. 
		 * Ex:  JAN/2008 FEV/2008 MAR/2008 ABR/2008
		 *                 10,00            40,00
		 * Caso não seje feito isto, o relatário ficaria:
		 *      JAN/2008 FEV/2008 MAR/2008 ABR/2008
		 *         10,00   40,00
		 */
		this.inserirValoresMesesVazios(values);
		
		/**
		 * Identação 
		 */
		//this.identarRegistros(values);
		
		/**
		 * Cabeçalho para os meses escolhidos
		 */
		this.construirCabecalho(totalGeral, totalPorMes);
		return values;
	}

	/**
	 * Cabeçalho para os meses escolhidos
	 */
	@SuppressWarnings("unchecked")
	private void construirCabecalho(BigDecimal totalGeral, TreeMap<Long, BigDecimal> totalPorMes) {
		int mesIni = Util.extrairMesReferencia(refIni);
		int mesFim = Util.extrairMesReferencia(refFim);
		int anoIni = Util.extrairAnoReferencia(refIni);
		int anoFim = Util.extrairAnoReferencia(refFim);
		
		List cabecalhoMeses = new ArrayList();
		for (long i = Util.extrairReferencia(refIni); i <= Util.extrairReferencia(refFim); i = Util.computeReferencia(i, 1)) {
			String mes   = Util.getMesDescricao(Integer.parseInt(String.valueOf(i).substring(4)) - 1).substring(0,3);
			String ano   = String.valueOf(i).substring(0,4);
			String label = mes  + "/" + ano;
			cabecalhoMeses.add(label);
		}
		this.getParameters().put("CABEC", cabecalhoMeses);
		
		this.getParameters().put("CONTA_CORRENTE", this.contaCorrente != null ? this.contaCorrente.getNumero() + " - " + this.contaCorrente.getDescricao() : "Geral");
		
		String paramRefIni = Util.getMesDescricao(mesIni-1) + "/" + anoIni;
		String paramRefFim = Util.getMesDescricao(mesFim-1) + "/" + anoFim;
		this.getParameters().put("REF_INI",paramRefIni);
		this.getParameters().put("REF_FIM",paramRefFim);
		
		this.getParameters().put("VLRTOTAL",Util.formatCurrency(totalGeral));
		
		ArrayList<String> totalPorMesFormatted = new ArrayList<String>();
		for (long i = Util.extrairReferencia(refIni); i <= Util.extrairReferencia(refFim); i = Util.computeReferencia(i, 1)) {
			BigDecimal vlr = totalPorMes.get(i);
			if ( vlr == null ) {
				vlr = new BigDecimal(0);
			}
			totalPorMesFormatted.add(Util.formatCurrency(vlr));
		}
		this.getParameters().put("VLRTOTALMENSAL", totalPorMesFormatted);
		
		ArrayList<BigDecimal> totalPorMesBigDecimal = new ArrayList<BigDecimal>();
		for (long i = Util.extrairReferencia(refIni); i <= Util.extrairReferencia(refFim); i = Util.computeReferencia(i, 1)) {
			BigDecimal vlr = totalPorMes.get(i);
			if ( vlr == null ) {
				vlr = new BigDecimal(0);
			}
			totalPorMesBigDecimal.add(vlr);
		}
		this.getParameters().put("VLRTOTALMENSAL_BD", totalPorMesBigDecimal);
		this.getParameters().put("TIPO_GRAFICO", new Integer(tipoGrafico));
	}

	/**
	 * Identação 
	 */
	private void identarRegistros(List<TotalContaContabilValue> values) {
		for(TotalContaContabilValue v : values) {
			if ( StringUtils.countMatches(v.getNivel(),".") == 1 && !v.getNivel().endsWith(".0") ) {
				v.setNivel("   " + v.getNivel());
			} else
			if ( StringUtils.countMatches(v.getNivel(),".") == 2 ) {
				v.setNivel("     " + v.getNivel());
			} else 
			if ( StringUtils.countMatches(v.getNivel(),".") == 3 ) {
				v.setNivel("       " + v.getNivel());
			} else	
			if ( StringUtils.countMatches(v.getNivel(),".") == 4 ) {
				v.setNivel("         " + v.getNivel());
			}
		}
	}

	/**
	 * Ordenação Níveis, de modo que:
	 * 1.1.1 < 1.0
	 * 1.2   < 1.0
	 * 1.1.2 < 1.0
	 * 10.1  > 1.0
	 * 1.2.1 > 1.0
	 */
	private void ordenarRegistros(List<TotalContaContabilValue> values) {
		Collections.sort(values, new Comparator<TotalContaContabilValue>() {
			public int compare(TotalContaContabilValue o1, TotalContaContabilValue o2) {
				String   nivel1   = o1.getNivel();
				String   nivel2   = o2.getNivel();
				return ContaContabilNivelOrdenador.compare(nivel1,nivel2);
			}
		});
	}

	private TotalContaContabilValue criarValueObject(Conta conta) {
		TotalContaContabilValue totalContaContabilValue = new TotalContaContabilValue();
		totalContaContabilValue.setDescricao(conta.getDescricao());
		totalContaContabilValue.setNivel(conta.getNivel());
		totalContaContabilValue.setValorTotalAsDouble(0);
		return totalContaContabilValue;
	}
	
	/**
	 * Colocando valores zerados para os meses em que não há movimento para que posicionamento termine correto no relatório. 
	 * Ex:  JAN/2008 FEV/2008 MAR/2008 ABR/2008
	 *                 10,00            40,00
	 * Caso não seje feito isto, o relatário ficaria:
	 *      JAN/2008 FEV/2008 MAR/2008 ABR/2008
	 *         10,00   40,00
	 */
	private void inserirValoresMesesVazios(List<TotalContaContabilValue> values) {
		for(TotalContaContabilValue v : values) {
			for(long i = Util.extrairReferencia(refIni); i <= Util.extrairReferencia(refFim); i = Util.computeReferencia(i, 1)) {
				if ( !v.getValorPorMes().containsKey(new Long(i)) ) {
					v.getValorPorMes().put(new Long(i), new BigDecimal(0));
				}
			}
		}
	}

	/**
	 * Inserindo Contas Contábeis sem débitos para disposição hierárquica completa
	 * das contas que possuem débitos
	 */
	private void inserirContasContabeisVazias(Set<Passivo> passivos, List<TotalContaContabilValue> values) {
		for(Passivo p : passivos) {
			Conta conta =  p.getConta().getContaPai();
			while ( true ) {
				if ( conta == null ) {
					break;
				} else {
					/** 
					 * Caso não tenha sido adicionado antes por não possuir débito (pelo menos direto), 
					 * a inclusão desta Conta Contábil com débito zerado é realizada
					 */
					TotalContaContabilValue totalContaContabilValue = criarValueObject(conta);
					if (!values.contains(totalContaContabilValue) ) {
						values.add(totalContaContabilValue);
					}
					conta = conta.getContaPai();
				}
			}
		}
	}

	public String getJasperFileName() {
		switch (tipo) {
			case 1: return "TotalContabil.jasper";
			case 2: return "TotalContabilAnalitico.jasper";
			case 3: return "TotalContabilGrafico.jasper";
		}
		throw new RuntimeException("Erro na configuração do relatório Total por Conta Contábil");
	}

}
