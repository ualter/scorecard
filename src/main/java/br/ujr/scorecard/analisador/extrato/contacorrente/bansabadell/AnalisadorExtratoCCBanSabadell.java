package br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.scorecard.config.ScorecardConfigBootStrap;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertiesUtil;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;


public class AnalisadorExtratoCCBanSabadell {

	private List<LinhaExtratoContaCorrenteBanSabadell> extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
	private ScorecardManager scorecardManager = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
	private ContaCorrente cc;
	private Set<Passivo> passivos;
	private List<Ativo> ativos;
	private long referencia;
	private String conteudo;
	

	private static Logger logger = Logger.getLogger(AnalisadorExtratoCCBanSabadell.class);
	private static final DateTimeFormatter DATE_TIME_FORMATTER  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DATE_TIME_FORMATTER2 = DateTimeFormatter.ofPattern("dd/M/yyyy");
	private static final DateTimeFormatter DATE_TIME_FORMATTER3 = DateTimeFormatter.ofPattern("d/MM/yyyy");
	private static final DateTimeFormatter DATE_TIME_FORMATTER4 = DateTimeFormatter.ofPattern("d/M/yyyy");
	
	
	public AnalisadorExtratoCCBanSabadell(long referencia) {
		this.cc         = scorecardManager.getContaCorrentePorId( Integer.parseInt(ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.IdCCBanSabadell)) );
		this.passivos   = scorecardManager.getPassivosPorReferencia(cc, referencia);
		this.ativos     = scorecardManager.getAtivosPorReferencia(cc, referencia);
		this.referencia = referencia;
		//this.conteudo   = Util.getClipBoarContent();
	}
	
	/**
	 *  Verifica os lançamentos que estão no Extrato MAS NÃO estão na Base de dados
	 */
	public List<LinhaExtratoContaCorrenteBanSabadell> getLancamentosNaoExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteBanSabadell> listaNaoEncontrados = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
		for(LinhaExtratoContaCorrenteBanSabadell linha : this.extratoContaCorrente) {
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
					if ( ativo instanceof Deposito || ativo instanceof Salario ) {
						if ( !ativo.isOrigemTransferencia() ) {
							if ( ativo.getDataMovimento().compareTo(linha.getDataOperacaoAsDate()) == 0 ) {
								if ( ativo.getValor().compareTo(linha.getValorAsBigDecimal()) == 0 ) {
									encontrado = true;
									break;
								}
							 }
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
	public List<LinhaExtratoContaCorrenteBanSabadell> getLancamentosExistentesBaseDados() {
		List<LinhaExtratoContaCorrenteBanSabadell> listaEncontrados = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
		for(LinhaExtratoContaCorrenteBanSabadell linha : this.extratoContaCorrente) {
			
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
	
	/**
	 * Formato Esperado: N43, check PDF
	 * @return
	 */
	public String analisarExtrato() {
		String pathN43Files = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.BancSabadellN43Path);
		if ( pathN43Files == null ) {
			throw new RuntimeException("Não foi encontrado a configuração de PATH para os arquivos N43, chave=" + ScorecardPropertyKeys.BancSabadellN43Path);
		}
		
		Path       path         = Paths.get(pathN43Files);
		List<Path> listFilesN43 = null;
		try {
		   listFilesN43 = Files.walk(path)
                               .filter(s -> s.toString().endsWith(".n43"))
                               .map(Path::toAbsolutePath)
                               .sorted()
                               .collect(Collectors.toList());
		} catch (NoSuchFileException e) {
			String msg = "Não foi encontrado nenhum arquivo N43 no diretório: " + pathN43Files;
			logger.warn(msg);
			return msg;
		} catch(IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(),e);
		}
		if ( listFilesN43.size() == 0 ) {
			String msg = "Não foi encontrado nenhum arquivo N43 no diretório: " + pathN43Files; 
			logger.warn(msg);
			return msg;
		}
		
		MappingAnalisadorBanSabadell mappingAnalisadorBanSabadell = new MappingAnalisadorBanSabadell();
		this.extratoContaCorrente   = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
		Stream<String> lines        = null;
		final Map<String,String> values = new HashMap<String,String>();
		try {
			lines = Files.lines(listFilesN43.get(0));
			lines.filter(line -> line.startsWith("22") || line.startsWith("23"))
			     .forEach(line -> {
			    	 
			    	 if ( line.startsWith("22") ) {
			    		 values.put("dataOperacao", extractDataFromLine(line));
			    		 values.put("valor", line.substring(28,42));
			    		 values.put("negativePositive", line.substring(27,28));
			    	 } else
		    		 if ( line.startsWith("23") ) {
		    			 LinhaExtratoContaCorrenteBanSabadell linhaExtratoContaCorrenteBanSabadell = new LinhaExtratoContaCorrenteBanSabadell();
		    			 linhaExtratoContaCorrenteBanSabadell.setDataOperacao(values.get("dataOperacao"));
		    			 linhaExtratoContaCorrenteBanSabadell.setValor(values.get("valor"), values.get("negativePositive"));
		    			 linhaExtratoContaCorrenteBanSabadell.setHistorico(line.substring(4,42));
		    			 mappingAnalisadorBanSabadell.checkMappingDescricaoVsContaContabil(linhaExtratoContaCorrenteBanSabadell.getHistorico(),linhaExtratoContaCorrenteBanSabadell);
		    			 this.extratoContaCorrente.add(linhaExtratoContaCorrenteBanSabadell);
		    			 values.clear();
		    		 }
			    	 
			     });
						 
			
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if ( lines != null ) {
				lines.close();
			}
		}

		return null;
	}

	private String extractDataFromLine(String line) {
		String dt    = line.substring(10, 16);
		 String year  = "20" + dt.substring(0,2);
		 String month = dt.substring(2,4);
		 String day   = dt.substring(4);
		 dt           = day + month + year;
		return dt;
	}

	private String convertDate(String dataValor) {
		String dt = "";
		try {
			dt = DATE_TIME_FORMATTER.format(DATE_TIME_FORMATTER.parse(dataValor.replaceAll("\\.", "/")));
		} catch (DateTimeParseException e) {
			try {
				dt = DATE_TIME_FORMATTER.format(DATE_TIME_FORMATTER2.parse(dataValor.replaceAll("\\.", "/")));
			} catch (DateTimeParseException e1) {
				try {
					dt = DATE_TIME_FORMATTER.format(DATE_TIME_FORMATTER3.parse(dataValor.replaceAll("\\.", "/")));
				} catch (DateTimeParseException e2) {
					dt = DATE_TIME_FORMATTER.format(DATE_TIME_FORMATTER4.parse(dataValor.replaceAll("\\.", "/")));
				}
			}
		}
		return dt;
	}
	
	public static class LinhaExtratoContaCorrenteBanSabadell implements Comparable<LinhaExtratoContaCorrenteBanSabadell>{
		private String dataOperacao;
		private String historico;
		private String valor;
		private String tipo;
		private Conta contaContabil;
		private boolean isNegative;
		
		public LinhaExtratoContaCorrenteBanSabadell() {
		}
		
		public String getDataOperacao() {
			return dataOperacao;
		}
		public String getDataOperacaoFormatted() {
			return Util.formatDate(this.getDataOperacaoAsDate());
		}
		public Date getDataOperacaoAsDate() {
			int day   = Integer.parseInt(this.getDataOperacao().substring(0,2));
			int month = Integer.parseInt(this.getDataOperacao().substring(2,4));
			int year  = Integer.parseInt(this.getDataOperacao().substring(4,8));
			return Util.parseDate(day,month,year);
		}
		public void setDataOperacao(String dataOp) {
			this.dataOperacao = dataOp;
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
		public void setValor(String valor, String negativoPositive) {
			this.valor = valor;
			if ( StringUtils.equalsIgnoreCase(negativoPositive, "1") ) {
				this.isNegative = true;
			}
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
				if ( StringUtils.isNotBlank(this.valor) ) { 
					String vlrInteger  = this.valor.substring(0, this.valor.length() - 2);
					String vlrFraction = this.valor.substring(this.valor.length() - 2);
					BigDecimal result  = new BigDecimal(vlrInteger + "." + vlrFraction);
					if ( this.isNegative ) {
						return result.multiply(new BigDecimal(-1));
					}
					return result;
				} else {
					return new BigDecimal(0);
				}
			} catch (RuntimeException e) {
				logger.fatal(e);
				System.out.println("Problemas Conversao Valor: (14 posicoes, duas ultimas sao cents)" + this.getValor());
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
				   + StringUtils.rightPad("" + this.getDataOperacaoAsDate(), 65)
				   + separador
				   + StringUtils.rightPad(this.historico, 65)
				   + separador
				   + StringUtils.rightPad(this.valor, 14)
				   + separador
				   + StringUtils.rightPad("" + this.getValorAsBigDecimal(), 14);
		}
		
		public int compareTo(LinhaExtratoContaCorrenteBanSabadell o) {
			return this.getHistorico().compareTo(o.getHistorico());
		}
		
	}
	
	public static Transferencia converterLinhaTransferencia(LinhaExtratoContaCorrenteBanSabadell linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Transferencia transferencia = new Transferencia();
		transferencia.setConta(conta);
		transferencia.setContaCorrente(contaCorrente);
		transferencia.setDataMovimento(linha.getDataOperacaoAsDate());
		transferencia.setHistorico(linha.getHistorico());
		transferencia.setReferencia(linha.getDataOperacaoAsDate());
		transferencia.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		return transferencia;
	}
	public static Ativo converterLinhaAtivo(LinhaExtratoContaCorrenteBanSabadell linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Ativo ativo = null;
		if ( StringUtils.equalsIgnoreCase("Depósito",tipo) ) {
			ativo = new Deposito();
			ativo.setValor(linha.getValorAsBigDecimal());
		} else
		if ( StringUtils.equalsIgnoreCase("Investimento",tipo) ) {
			ativo = new Investimento();
			ativo.setValor(linha.getValorAsBigDecimal());
		} else
		if ( StringUtils.equalsIgnoreCase("Estipêndios",tipo) ) {
			ativo = new Salario();
			ativo.setValor(linha.getValorAsBigDecimal());
		}
		ativo.setConta(conta);
		ativo.setDataMovimento(linha.getDataOperacaoAsDate());
		ativo.setHistorico(linha.getHistorico());
		ativo.setReferencia(linha.getDataOperacaoAsDate());
		ativo.setContaCorrente(contaCorrente);
		return ativo;
	}
	public static Passivo converterLinhaPassivo(LinhaExtratoContaCorrenteBanSabadell linha, Conta conta, ContaCorrente contaCorrente, String tipo) {
		Passivo passivo = null;
		Parcela parcela = new Parcela();
		Cartao  cartao  = new Cartao();
		
		ScorecardManager scorecardManager = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
		List<CartaoContratado> listCartaoContratados = scorecardManager.getCartoesContaCorrente(contaCorrente);
		
		listCartaoContratados.stream()
		                     .filter(cc -> cc.getNome().equalsIgnoreCase(tipo))
		                     .forEach(cc -> {
            cartao.setCartaoContratado(cc);
			cartao.setOperadora(cc.getCartaoOperadora());
		});
		
		
		if ( cartao.getCartaoContratado() != null ) {
			passivo = cartao;
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
		parcela.setDataVencimento(linha.getDataOperacaoFormatted());
		parcela.setEfetivado(true);
		parcela.setNumero(1);
		parcela.setReferencia(linha.getDataOperacaoAsDate());
		parcela.setValor(linha.getValorAsBigDecimal().multiply(new BigDecimal(-1)));
		passivo.addParcela(parcela);
		return passivo;
	}

}
