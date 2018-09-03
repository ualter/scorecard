package br.ujr.scorecard.analisador.extrato.contacorrente.santander;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.scorecard.config.ScorecardConfigBootStrap;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.CartaoCatalogo;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;


public class AnalisadorExtratoCCSantander {

	private List<LinhaExtratoContaCorrenteSantander> extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteSantander>();
	private ScorecardManager scorecardManager = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
	private ContaCorrente cc;
	private Set<Passivo> passivos;
	private List<Ativo> ativos;
	private long referencia;
	private LinhaExtratoContaCorrenteSantander saldoAnterior;
	private LinhaExtratoContaCorrenteSantander saldo;
	private String conteudo;
	
	public LinhaExtratoContaCorrenteSantander getSaldoAnterior() {
		return saldoAnterior;
	}

	public void setSaldoAnterior(LinhaExtratoContaCorrenteSantander saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}

	public LinhaExtratoContaCorrenteSantander getSaldo() {
		return saldo;
	}

	public void setSaldo(LinhaExtratoContaCorrenteSantander saldo) {
		this.saldo = saldo;
	}
	
	private static Logger logger = Logger.getLogger(AnalisadorExtratoCCSantander.class);
	
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
	
	public AnalisadorExtratoCCSantander(long referencia) {
		this.cc = scorecardManager.getContaCorrentePorId(Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCSantander));
		this.passivos = scorecardManager.getPassivosPorReferencia(cc, referencia);
		this.ativos = scorecardManager.getAtivosPorReferencia(cc, referencia);
		this.referencia = referencia;
		this.conteudo = Util.getClipBoarContent();
	}
	
	/**
	 *  Verifica os lançamentos que estão no Extrato MAS NÃO estão na Base de dados
	 */
	public List<LinhaExtratoContaCorrenteSantander> getLancamentosNaoExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteSantander> listaNaoEncontrados = new ArrayList<LinhaExtratoContaCorrenteSantander>();
		for(LinhaExtratoContaCorrenteSantander linha : this.extratoContaCorrente) {
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
	public List<LinhaExtratoContaCorrenteSantander> getLancamentosExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteSantander> listaEncontrados = new ArrayList<LinhaExtratoContaCorrenteSantander>();
		for(LinhaExtratoContaCorrenteSantander linha : this.extratoContaCorrente) {
			
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
		this.extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteSantander>();
		try {
			int i = 0;
			String[] linhas = this.conteudo.split("\\n");
	    	for (String linhaClip : linhas) {
	    		i++;
	    		String columns[] = linhaClip.split("\\t");
	    		if (columns.length > 6 ) {
	    			String data      = columns[0];
	    			//String docId     = columns[1];
	    			String historico = columns[2];
	    			String docto     = columns[3];
	    			//String foto      = columns[4];
	    			String valor     = columns[5];
	    			String saldo     = columns[6];

	    			/*StringBuffer sb = new StringBuffer();
	    			sb.append(data).append("-");
	    			sb.append(docId).append("-");
	    			sb.append(historico).append("-");
	    			sb.append(docto).append("-");
	    			sb.append(foto).append("-");
	    			sb.append(valor).append("-");
	    			sb.append(saldo).append("-");
	    			System.out.println(sb.toString());*/
	    			
	    			if ( StringUtils.isBlank(valor) ) {
	    				valor = "0,00";
	    			}
	    			if ( StringUtils.isBlank(saldo) ) {
	    				saldo = "0,00";
	    			}
	    			
	    			LinhaExtratoContaCorrenteSantander linha = new LinhaExtratoContaCorrenteSantander();
					linha.setData(data);
					linha.setHistorico(historico);
					linha.setDocumento(docto);
					linha.setValor(Util.cleanNumber(valor));
					linha.setSaldo(Util.cleanNumber(saldo));

					if ( i == 1 ) {
						this.setSaldoAnterior(linha);
					}
					if ( i == (linhas.length - 1) ) {
						linha.setSaldo(true);
						this.setSaldo(linha);
					}
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
	
	public static class LinhaExtratoContaCorrenteSantander implements Comparable<LinhaExtratoContaCorrenteSantander>{
		private String data;
		private String historico;
		private String documento;
		private String valor;
		private String saldo;
		private boolean isSaldo;
		
		public LinhaExtratoContaCorrenteSantander() {
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
			this.data = data;
		}
		public String getSaldo() {
			return saldo;
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
		public boolean isSaldo() {
			return isSaldo;
		}

		public void setSaldo(boolean isSaldo) {
			this.isSaldo = isSaldo;
		}

		public String getDocumento() {
			return cleanValue(documento);
		}
		public void setDocumento(String numeroDocumento) {
			this.documento = cleanValue(numeroDocumento);
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
			return   StringUtils.rightPad(this.data, 13)
				   + separador
				   + StringUtils.rightPad(this.historico, 65)
				   + separador
				   + StringUtils.rightPad(this.documento, 30)
				   + separador
				   + StringUtils.rightPad(this.valor, 30);
		}
		
		public int compareTo(LinhaExtratoContaCorrenteSantander o) {
			return this.getHistorico().compareTo(o.getHistorico());
		}
		
	}
	
	public static Transferencia converterLinhaTransferencia(LinhaExtratoContaCorrenteSantander linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Transferencia transferencia = new Transferencia();
		transferencia.setConta(conta);
		transferencia.setContaCorrente(contaCorrente);
		transferencia.setDataMovimento(linha.getDataAsDate());
		transferencia.setHistorico(linha.getHistorico());
		transferencia.setReferencia(linha.getDataAsDate());
		transferencia.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		return transferencia;
	}
	public static Ativo converterLinhaAtivo(LinhaExtratoContaCorrenteSantander linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
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
	public static Passivo converterLinhaPassivo(LinhaExtratoContaCorrenteSantander linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Passivo passivo = null;
		Parcela parcela = new Parcela();
		
		if ( StringUtils.equalsIgnoreCase("Visa Electron",tipo)) {
			passivo = new Cartao();
			((Cartao)passivo).setOperadora(CartaoCatalogo.VISA_ELECTRON);
		} else
		if ( StringUtils.equalsIgnoreCase("Saque",tipo)) {
			passivo = new Saque();
		} else
		if ( StringUtils.equalsIgnoreCase("Cheque",tipo)) {
			passivo = new Cheque();
			parcela.setCheque(true);
			parcela.setNumeroCheque(linha.getDocumento());
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
