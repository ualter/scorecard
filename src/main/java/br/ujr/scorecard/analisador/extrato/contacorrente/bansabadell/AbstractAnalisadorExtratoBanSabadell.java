package br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

public abstract class AbstractAnalisadorExtratoBanSabadell implements AnalisadorExtratoBanSabadell {
	
	protected List<LinhaExtratoContaCorrenteBanSabadell> extratoContaCorrente = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
	protected ScorecardManager scorecardManager = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
	protected ContaCorrente cc;
	protected Set<Passivo> passivos;
	protected List<Ativo> ativos;
	protected long referencia;
	protected String conteudo;
	
	protected static final DateTimeFormatter DATE_TIME_FORMATTER  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	protected static final DateTimeFormatter DATE_TIME_FORMATTER2 = DateTimeFormatter.ofPattern("dd/M/yyyy");
	protected static final DateTimeFormatter DATE_TIME_FORMATTER3 = DateTimeFormatter.ofPattern("d/MM/yyyy");
	protected static final DateTimeFormatter DATE_TIME_FORMATTER4 = DateTimeFormatter.ofPattern("d/M/yyyy");
	protected static final DateTimeFormatter DATE_TIME_FORMATTER5 = DateTimeFormatter.ofPattern("ddMMyyyy");

	public AbstractAnalisadorExtratoBanSabadell() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell.AnalisadorExtratoBanSabadell#getLancamentosNaoExistentesBaseDados()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell.AnalisadorExtratoBanSabadell#getLancamentosExistentesBaseDados()
	 */
	@Override
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
	
	protected String extractDataFromLine(String line) {
		String dt    = line.substring(10, 16);
		 String year  = "20" + dt.substring(0,2);
		 String month = dt.substring(2,4);
		 String day   = dt.substring(4);
		 dt           = day + month + year;
		return dt;
	}
	
	protected String convertDate(String dataValor) {
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