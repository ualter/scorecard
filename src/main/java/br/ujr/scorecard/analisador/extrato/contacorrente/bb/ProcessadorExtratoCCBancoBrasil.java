package br.ujr.scorecard.analisador.extrato.contacorrente.bb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.ScorecardProperties;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;


public class ProcessadorExtratoCCBancoBrasil {

	private String pathExtrato = "";
	private List<LinhaExtratoContaCorrenteBancoBrasil> extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteBancoBrasil>();
	private ScorecardManager scorecardManager = (ScorecardManager)Util.getBean("scorecardManager");
	private ContaCorrente cc;
	private Set<Passivo> passivos;
	private List<Ativo> ativos;
	private long referencia;
	private LinhaExtratoContaCorrenteBancoBrasil saldoAnterior;
	private LinhaExtratoContaCorrenteBancoBrasil saldo;
	
	public LinhaExtratoContaCorrenteBancoBrasil getSaldoAnterior() {
		return saldoAnterior;
	}

	public void setSaldoAnterior(LinhaExtratoContaCorrenteBancoBrasil saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}

	public LinhaExtratoContaCorrenteBancoBrasil getSaldo() {
		return saldo;
	}

	public void setSaldo(LinhaExtratoContaCorrenteBancoBrasil saldo) {
		this.saldo = saldo;
	}
	public String getPathExtrato() {
		return pathExtrato;
	}

	private static Logger logger = Logger.getLogger(ProcessadorExtratoCCBancoBrasil.class);
	
	public static Date foundInThePath() {
		try {
			String name = ScorecardProperties.getProperty(ScorecardPropertyKeys.ArquivoExtratoCC_BB);
			File dir = new File(name.substring(0,name.lastIndexOf("/")));
			name = name.substring(name.lastIndexOf("/") + 1,name.lastIndexOf("-$MESANO"));
			if ( dir.exists() ) {
				for(File file : dir.listFiles()) {
					if (file.getName().contains(name)) {
						String anoMes = file.getName().substring(file.getName().lastIndexOf("-")+1,file.getName().lastIndexOf("."));
						return Util.parseDate("01" + "/" + anoMes.substring(4) + "/" + anoMes.substring(0,4));
					}
				}
			}
		} catch (Throwable e) {
			Log.warn(e);
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public ProcessadorExtratoCCBancoBrasil(long referencia) {
		String file      = ScorecardProperties.getProperty(ScorecardPropertyKeys.ArquivoExtratoCC_BB);
		this.pathExtrato = file.replaceAll("\\$MESANO", referencia + "");;
		this.cc = scorecardManager.getContaCorrentePorId(ScorecardPropertyKeys.IdCCBancoBrasil);
		this.passivos = scorecardManager.getPassivosPorReferencia(cc, referencia);
		this.ativos = scorecardManager.getAtivosPorReferencia(cc, referencia);
		this.referencia = referencia;
	}
	
	/**
	 *  Verifica os lançamentos que estão no Extrato MAS NÃO estão na Base de dados
	 */
	public List<LinhaExtratoContaCorrenteBancoBrasil> getLancamentosNaoExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteBancoBrasil> listaNaoEncontrados = new ArrayList<LinhaExtratoContaCorrenteBancoBrasil>();
		for(LinhaExtratoContaCorrenteBancoBrasil linha : this.extratoContaCorrente) {
			
			BigDecimal valorExtrato = linha.getValorAsBigDecimal();
			
			if ( !linha.isAtivo() && (!linha.isSaldo() || !linha.isSaldoAnterior()) ) {
				boolean    encontrado   = false;
				
				for(Passivo passivo : passivos) {
					for(Parcela parcela : passivo.getParcelas(referencia, referencia)) {
						float valorParcela = parcela.getValor().floatValue() * -1;
						if ( valorParcela == valorExtrato.floatValue()  ) {
							/**
							 * Como o valor do saque normalmente eh igual, utiliza-se tambem o parametro data
							 * para confirmar se o registro do extrato esta mesmo lancado na base de dados 
							 */
							if ( passivo instanceof Saque ) {
								 if ( passivo.getDataMovimento().compareTo(linha.getDataAsDate()) == 0 ) {
									 encontrado = true;
									 break;
								 }
							} else {
								encontrado = true;
								break;
							}
						}
					}
					if ( encontrado ) break;
				}
				
				if ( !encontrado ) {
					listaNaoEncontrados.add(linha);
				}
			} else 
			if ( linha.isAtivo() && !linha.isSaldo() && !linha.isSaldoAnterior() ) {
				boolean    encontrado   = false;
				for(Ativo ativo : ativos) {
					float valor = valorExtrato.floatValue();
					if ( valor < 0 ) {
						valor *= -1;
					}
					/**
					 * Como o valor do Depósito poder ser igual, utiliza-se tambem o parametro data
					 * para confirmar se o registro do extrato esta mesmo lancado na base de dados 
					 */
					if ( ativo.getValor().floatValue() == valor  ) {
						if ( ativo instanceof Deposito ) {
							if ( !((Deposito)ativo).isOrigemTransferencia() ) {
								if ( ativo.getDataMovimento().compareTo(linha.getDataAsDate()) == 0 ) {
									 encontrado = true;
									 break;
								 }
							} else {
								encontrado = true;
								break;
							}
						} else {
							encontrado = true;
							break;
						}
					}
					if ( encontrado ) break;
				}
				
				if ( !encontrado ) {
					listaNaoEncontrados.add(linha);
				}
			}
		}
		Collections.sort(listaNaoEncontrados);
		return listaNaoEncontrados;
	}
	/**
	 *  Verifica os lançamentos que estão no Extrato E TAMBÉM QUE ESTÃO na Base de dados
	 */
	public List<LinhaExtratoContaCorrenteBancoBrasil> getLancamentosExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteBancoBrasil> listaEncontrados = new ArrayList<LinhaExtratoContaCorrenteBancoBrasil>();
		for(LinhaExtratoContaCorrenteBancoBrasil linha : this.extratoContaCorrente) {
			
			if ( !linha.isSaldo() && !linha.isSaldoAnterior() ) {
				BigDecimal valorExtrato = linha.getValorAsBigDecimal();
				boolean    encontrado   = false;
				
				for(Passivo passivo : passivos) {
					for(Parcela parcela : passivo.getParcelas(referencia, referencia)) {
						if ( (parcela.getValor().floatValue() * -1) == valorExtrato.floatValue() ) {
							encontrado = true;
							break;
						}
					}
					if ( encontrado ) break;
				}
				
				if ( encontrado ) {
					listaEncontrados.add(linha);
				}
			}
		}
		Collections.sort(listaEncontrados);
		return listaEncontrados;
	}
	
	public boolean checkArquivo() {
		File extrato = new File(pathExtrato);
		return extrato.exists();
	}
	public void loadExtrato() {
		this.extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteBancoBrasil>();
		try {
			File extrato = new File(pathExtrato);
			BufferedReader reader = new BufferedReader(new FileReader(extrato));
			String line = null;
			reader.readLine(); //dispensar o cabeçalho
			while ( (line = reader.readLine()) != null) {
				String[] dados = line.split(",");
				
				LinhaExtratoContaCorrenteBancoBrasil linhaExtratoContaCorrente = new LinhaExtratoContaCorrenteBancoBrasil(dados);
				//System.out.println(linhaExtratoContaCorrente);
				
				if ( StringUtils.contains(linhaExtratoContaCorrente.getHistorico(), "S A L D O") ) {
					this.setSaldo(linhaExtratoContaCorrente);
				}
				if ( StringUtils.contains(linhaExtratoContaCorrente.getHistorico(), "Saldo Anterior") ) {
					this.setSaldoAnterior(linhaExtratoContaCorrente);
				}
				
				this.extratoContaCorrente.add(linhaExtratoContaCorrente);
			}
		} catch (Exception e) {
			logger.fatal(e);
			throw new RuntimeException(e);
		}
	}
	
	public static class LinhaExtratoContaCorrenteBancoBrasil implements Comparable<LinhaExtratoContaCorrenteBancoBrasil>{
		private String data;
		private String dependenciaOrigem;
		private String historico;
		private String dataDoBalancete;
		private String numeroDocumento;
		private String valor;
		
		public LinhaExtratoContaCorrenteBancoBrasil() {
		}
		
		public LinhaExtratoContaCorrenteBancoBrasil(String[] valores) {
			this.setData(valores[0]);
			this.setDependenciaOrigem(valores[1]);
			this.setHistorico(valores[2]);
			this.setDataDoBalancete(valores[3]);
			this.setNumeroDocumento(valores[4]);
			this.setValor(valores[5]);
		}
		
		private String cleanValue(String vlr) {
			return vlr.replaceAll("\"", "");
		}
		public String getData() {
			return data;
		}
		public Date getDataAsDate() {
			return Util.parseDate(this.getData());
		}
		public void setData(String data) {
			data = cleanValue(data);
			int month  = Integer.parseInt(data.substring(0,2));
			int day    = Integer.parseInt(data.substring(3,5));
			int year   = Integer.parseInt(data.substring(6,10));
			
			this.data  = (day < 10 ? "0" : "") + day + "/" + (month < 10 ? "0" : "") + month + "/" + year;
		}
		public String getDependenciaOrigem() {
			return dependenciaOrigem;
		}
		public void setDependenciaOrigem(String dependenciaOrigem) {
			this.dependenciaOrigem = cleanValue(dependenciaOrigem);
		}
		public String getHistorico() {
			return historico;
		}
		public void setHistorico(String historico) {
			this.historico = cleanValue(historico);
		}
		public String getDataDoBalancete() {
			return cleanValue(dataDoBalancete);
		}
		public void setDataDoBalancete(String dataDoBalancete) {
			this.dataDoBalancete = cleanValue(dataDoBalancete);
		}
		public String getNumeroDocumento() {
			return cleanValue(numeroDocumento);
		}
		public void setNumeroDocumento(String numeroDocumento) {
			this.numeroDocumento = cleanValue(numeroDocumento);
		}
		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = cleanValue(valor);
		}
		public BigDecimal getValorAsBigDecimal() {
			try {
				if ( StringUtils.equalsIgnoreCase("Valor", this.getValor()) ) {
					return new BigDecimal(0);
				}
				return new BigDecimal(this.getValor().trim());
			} catch (RuntimeException e) {
				logger.fatal(e);
				System.out.println("Problemas Conversao Valor: " + this.getValor());
				throw e;
			}
			
		}
		public boolean isCheque() {
			if ( StringUtils.contains(this.historico,"Cheque Compensado") &&
				 StringUtils.contains(this.numeroDocumento,"") ) {
				return true;
			}
			return false;
		}
		public boolean isSaldoAnterior() {
			if ( StringUtils.contains(this.historico,"Saldo Anterior") ) {
				return true;
			}
			return false;
		}
		public boolean isSaldo() {
			if ( StringUtils.contains(this.historico,"S A L D O") ) {
				return true;
			}
			return false;
		}
		public boolean isVisaElectron() {
			if ( StringUtils.contains(this.historico,"Compra com Cartão") ) {
				return true;
			}
			return false;
		}
		public boolean isSaque() {
			if ( StringUtils.contains(this.historico,"Saque no Caixa") ||
				 StringUtils.contains(this.historico,"Saque no TAA")) {
				return true;
			}
			return false;
		}
		public boolean isPassivo() {
			if ( StringUtils.contains(this.getHistorico(),"Transf") && StringUtils.contains(this.getHistorico(),"Aplic")) {
				return false;
			} else
			if ( this.getValorAsBigDecimal().floatValue() > 0 ) {
				return false;
			}
			return true;
		}
		public boolean isAtivo() {
			return !this.isPassivo();
		}
		
		public String toString() {
			String separador = " | ";
			return   StringUtils.rightPad(this.data, 13)
				   + separador
				   + StringUtils.rightPad(this.dependenciaOrigem, 30)
				   + separador
				   + StringUtils.rightPad(this.historico, 65)
				   + separador
				   + StringUtils.rightPad(this.dataDoBalancete, 13)
				   + separador
				   + StringUtils.rightPad(this.numeroDocumento, 30)
				   + separador
				   + StringUtils.rightPad(this.valor, 30);
		}
		
		public int compareTo(LinhaExtratoContaCorrenteBancoBrasil o) {
			return this.getHistorico().compareTo(o.getHistorico());
		}
		
	}
	
	public static Transferencia converterLinhaTransferencia(LinhaExtratoContaCorrenteBancoBrasil linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Transferencia transferencia = new Transferencia();
		transferencia.setConta(conta);
		transferencia.setContaCorrente(contaCorrente);
		transferencia.setDataMovimento(linha.getDataAsDate());
		transferencia.setHistorico(linha.getHistorico());
		transferencia.setReferencia(linha.getDataAsDate());
		transferencia.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		return transferencia;
	}
	public static Ativo converterLinhaAtivo(LinhaExtratoContaCorrenteBancoBrasil linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Ativo ativo = null;
		if ( StringUtils.equalsIgnoreCase("Depósito",tipo) ) {
			ativo = new Deposito();
			ativo.setValor(linha.getValorAsBigDecimal());
		} else
		if ( StringUtils.equalsIgnoreCase("Investimento",tipo) ) {
			ativo = new Investimento();
			ativo.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		}
		ativo.setConta(conta);
		ativo.setDataMovimento(linha.getDataAsDate());
		ativo.setHistorico(linha.getHistorico());
		ativo.setReferencia(linha.getDataAsDate());
		ativo.setContaCorrente(contaCorrente);
		return ativo;
	}
	public static Passivo converterLinhaPassivo(LinhaExtratoContaCorrenteBancoBrasil linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Passivo passivo = null;
		Parcela parcela = new Parcela();
		
		if ( StringUtils.equalsIgnoreCase("Visa Electron",tipo)) {
			passivo = new Cartao();
			((Cartao)passivo).setOperadora(Operadora.VISA_ELECTRON);
		} else
		if ( StringUtils.equalsIgnoreCase("Saque",tipo)) {
			passivo = new Saque();
		} else
		if ( StringUtils.equalsIgnoreCase("Cheque",tipo)) {
			passivo = new Cheque();
			parcela.setCheque(true);
			parcela.setNumeroCheque(linha.getNumeroDocumento());
		} else
		if ( StringUtils.equalsIgnoreCase("Débito",tipo)) {
			passivo = new DebitoCC();
		}
		passivo.setContaCorrente(contaCorrente);
		passivo.setHistorico(linha.getHistorico());
		passivo.setDataMovimento(linha.getDataAsDate());
		passivo.setConta(conta);
		parcela.setDataVencimento(linha.getData());
		parcela.setEfetivado(true);
		parcela.setNumero(1);
		parcela.setReferencia(linha.getDataAsDate());
		parcela.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		passivo.addParcela(parcela);
		return passivo;
	}

}
