package br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.util.Util;

public class LinhaExtratoContaCorrenteBanSabadell implements Comparable<LinhaExtratoContaCorrenteBanSabadell>{
	
		private static Logger logger = Logger.getLogger(LinhaExtratoContaCorrenteBanSabadell.class);
	
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