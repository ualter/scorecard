package br.ujr.scorecard.analisador.extrato.contacorrente.deutsche;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
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
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;


public class AnalisadorExtratoCCDeutsche {

	private List<LinhaExtratoContaCorrenteDeutsche> extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteDeutsche>();
	private ScorecardBusinessDelegate businessDelegate = ScorecardBusinessDelegate.getInstance();
	private ContaCorrente cc;
	private Set<Passivo> passivos;
	private List<Ativo> ativos;
	private long referencia;
	private String conteudo;
	

	private static Logger logger = Logger.getLogger(AnalisadorExtratoCCDeutsche.class);
	
	public static Date foundInThePath() {
		/*try {
			String name = ScorecardProperties.getProperty(ScorecardPropertyKeys.ArquivoExtratoSantander);
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
		return null;*/
		return Calendar.getInstance().getTime();
	}
	
	public AnalisadorExtratoCCDeutsche(long referencia) {
		this.cc = businessDelegate.getContaCorrentePorId(ScorecardPropertyKeys.IdCCDeutsche);
		this.passivos = businessDelegate.getPassivosPorReferencia(cc, referencia);
		this.ativos = businessDelegate.getAtivosPorReferencia(cc, referencia);
		this.referencia = referencia;
		this.conteudo = Util.getClipBoarContent();
	}
	
	/**
	 *  Verifica os lançamentos que estão no Extrato MAS NÃO estão na Base de dados
	 */
	public List<LinhaExtratoContaCorrenteDeutsche> getLancamentosNaoExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteDeutsche> listaNaoEncontrados = new ArrayList<LinhaExtratoContaCorrenteDeutsche>();
		for(LinhaExtratoContaCorrenteDeutsche linha : this.extratoContaCorrente) {
			BigDecimal valorExtrato = linha.getValorAsBigDecimal();
			if ( !linha.isAtivo()  ) {
				boolean    encontrado   = false;
				
				for(Passivo passivo : passivos) {
					for(Parcela parcela : passivo.getParcelas(referencia, referencia)) {
						float valorParcela = parcela.getValor().floatValue() * -1;
						if ( valorParcela == valorExtrato.floatValue()  ) {
							if ( passivo.getDataMovimento().compareTo(linha.getDataOperacaoAsDate()) == 0 ) {
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
			if ( linha.isAtivo()  ) {
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
					if ( ativo instanceof Deposito ) {
						if ( !((Deposito)ativo).isOrigemTransferencia() ) {
							if ( ativo.getDataMovimento().compareTo(linha.getDataOperacaoAsDate()) == 0 ) {
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
	public List<LinhaExtratoContaCorrenteDeutsche> getLancamentosExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteDeutsche> listaEncontrados = new ArrayList<LinhaExtratoContaCorrenteDeutsche>();
		for(LinhaExtratoContaCorrenteDeutsche linha : this.extratoContaCorrente) {
			
			if ( !linha.isSaldoAnterior() ) {
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
	
	public String analisarExtrato() {
		this.extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteDeutsche>();
		
		MappingAnalisadorDeutsche mappingAnalisadorDeutsche = new MappingAnalisadorDeutsche();
		try {
			int i = 0;
			String dataOperacao;
			String dataValor;
			String historico;
			String valor;
			String saldo;
			String[] linhas = this.conteudo.split("\\n");
	    	for (String linhaClip : linhas) {
	    		i++;
	    		String columns[] = linhaClip.split("\\t");
	    		if (columns.length > 4 ) {
	    			
	    			// Copy-Paste the content of Deutsche-Bank Extrato 
	    			// gets the first line without the TAB, thats why
	    			// its treat differently
	    			
	    			
	    			if ( i == 1 ) {
	    				dataOperacao = columns[0];
	    				dataValor    = columns[1];
	    				historico    = columns[2];
	    				valor        = columns[3];
	    				saldo        = columns[4];
	    			}  else {
	    				dataOperacao = columns[1];
	    				dataValor    = columns[2];
	    				historico    = columns[3];
	    				valor        = columns[4];
	    				saldo        = columns[5];
	    			}

	    			if ( StringUtils.isBlank(valor) ) {
	    				valor = "0,00";
	    			}
	    			if ( StringUtils.isBlank(saldo) ) {
	    				saldo = "0,00";
	    			}
	    			
	    			LinhaExtratoContaCorrenteDeutsche linha = new LinhaExtratoContaCorrenteDeutsche();
					linha.setDataOperacao(dataOperacao.replaceAll("\\.", "/"));
					linha.setDataValor(dataValor.replaceAll("\\.", "/"));
					linha.setHistorico(StringUtils.trim(historico));
					linha.setValor(Util.cleanNumber(valor));
					linha.setSaldo(Util.cleanNumber(saldo));
					
					if ( linha.getHistorico().contains("TARJETA") )  {
						linha.setTipo("Visa Electron");
					}
					mappingAnalisadorDeutsche.checkMappingDescricaoVsContaContabil(linha.getHistorico(),linha);

					this.extratoContaCorrente.add(linha);
					
	    		} else {
	    			logger.info(this.conteudo);
	    			return this.conteudo;
	    		}
	    	}
		} catch (Exception e) {
			logger.fatal(e);
			return this.conteudo;
		}
		return null;
	}
	
	public static class LinhaExtratoContaCorrenteDeutsche implements Comparable<LinhaExtratoContaCorrenteDeutsche>{
		private String dataOperacao;
		private String dataValor;
		private String historico;
		private String valor;
		private String saldo;
		private String tipo;
		private Conta contaContabil;
		
		public LinhaExtratoContaCorrenteDeutsche() {
		}
		
		private String cleanValue(String vlr) {
			return vlr.replaceAll("\"", "");
		}
		public String getDataOperacao() {
			return dataOperacao;
		}
		public Date getDataOperacaoAsDate() {
			return Util.parseDate(this.getDataOperacao());
		}
		public void setDataOperacao(String dataOp) {
			dataOp = cleanValue(dataOp);
			this.dataOperacao = dataOp;
		}
		public String getSaldo() {
			return saldo;
		}

		public String getDataValor() {
			return dataValor;
		}
		public void setDataValor(String dataValor) {
			this.dataValor = dataValor;
		}
		public void setSaldo(String saldo) {
			this.saldo = saldo;
		}
		public String getHistorico() {
			return historico;
		}
		public void setHistorico(String historico) {
			this.historico = cleanValue(historico);
		}

		public String getValor() {
			return valor;
		}
		public void setValor(String valor) {
			this.valor = cleanValue(valor);
		}
		
		
		public String getTipo() {
			return tipo;
		}

		
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}

		public Conta getContaContabil() {
			return contaContabil;
		}
		
		public void setContaContabil(Conta contaContabil) {
			this.contaContabil = contaContabil;
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
		public BigDecimal getSaldoAsBigDecimal() {
			try {
				if ( StringUtils.equalsIgnoreCase("Valor", this.getSaldo()) ) {
					return new BigDecimal(0);
				}
				return new BigDecimal(this.getSaldo().trim());
			} catch (RuntimeException e) {
				logger.fatal(e);
				System.out.println("Problemas Conversao Valor: " + this.getValor());
				throw e;
			}
			
		}
		public boolean isCheque() {
			if ( StringUtils.contains(this.historico,"Cheque")) {
				return true;
			}
			return false;
		}
		public boolean isSaldoAnterior() {
			if ( StringUtils.contains(this.historico,"SALDO ANTERIOR") ) {
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
			if ( StringUtils.contains(this.historico,"Saque")) {
				return true;
			}
			return false;
		}
		public boolean isPassivo() {
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
			return   StringUtils.rightPad(this.dataOperacao, 13)
				   + separador
				   + StringUtils.rightPad(this.dataValor, 13)
				   + separador
				   + StringUtils.rightPad(this.historico, 65)
				   + separador
				   + StringUtils.rightPad(this.valor, 30);
		}
		
		public int compareTo(LinhaExtratoContaCorrenteDeutsche o) {
			return this.getHistorico().compareTo(o.getHistorico());
		}
		
	}
	
	public static Transferencia converterLinhaTransferencia(LinhaExtratoContaCorrenteDeutsche linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Transferencia transferencia = new Transferencia();
		transferencia.setConta(conta);
		transferencia.setContaCorrente(contaCorrente);
		transferencia.setDataMovimento(linha.getDataOperacaoAsDate());
		transferencia.setHistorico(linha.getHistorico());
		transferencia.setReferencia(linha.getDataOperacaoAsDate());
		transferencia.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		return transferencia;
	}
	public static Ativo converterLinhaAtivo(LinhaExtratoContaCorrenteDeutsche linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
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
		ativo.setDataMovimento(linha.getDataOperacaoAsDate());
		ativo.setHistorico(linha.getHistorico());
		ativo.setReferencia(linha.getDataOperacaoAsDate());
		ativo.setContaCorrente(contaCorrente);
		return ativo;
	}
	public static Passivo converterLinhaPassivo(LinhaExtratoContaCorrenteDeutsche linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
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
			parcela.setNumeroCheque(linha.getHistorico());
		} else
		if ( StringUtils.equalsIgnoreCase("Débito",tipo)) {
			passivo = new DebitoCC();
		}
		
		passivo.setContaCorrente(contaCorrente);
		passivo.setHistorico(linha.getHistorico());
		passivo.setDataMovimento(linha.getDataOperacaoAsDate());
		passivo.setConta(conta);
		parcela.setDataVencimento(linha.getDataOperacao());
		parcela.setEfetivado(true);
		parcela.setNumero(1);
		parcela.setReferencia(linha.getDataOperacaoAsDate());
		parcela.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		passivo.addParcela(parcela);
		return passivo;
	}

}
