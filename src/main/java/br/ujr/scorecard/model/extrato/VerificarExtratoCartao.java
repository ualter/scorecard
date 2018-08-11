package br.ujr.scorecard.model.extrato;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.ujr.scorecard.util.ScorecardProperties;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;

public class VerificarExtratoCartao {
	
	private File extrato;
	private List<LinhaExtratoCartao> linhas;
	private String valorTotal;
	private Map<String,Integer> valoresVerificados = new HashMap<String,Integer>();
	
	public String getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(String valorTotal) {
		this.valorTotal = valorTotal;
	}

	public List<LinhaExtratoCartao> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<LinhaExtratoCartao> linhas) {
		this.linhas = linhas;
	}

	public VerificarExtratoCartao(String pathFile) {
		this.extrato = new File(pathFile);
		loadLinhas();
	}
	
	public boolean isExtratoValido() {
		if ( !this.extrato.exists() ) {
			return false;
		}
		return true;
	}
	
	public void loadLinhas() {
		String strValorDolar = ScorecardProperties.getProperty(ScorecardPropertyKeys.ValorDolar);
		float  vlrDolar      = Float.parseFloat(strValorDolar);
		
		this.linhas = new ArrayList<LinhaExtratoCartao>();
		boolean goAhead = true;
		if ( this.isExtratoValido() ) {
			BufferedReader reader = null;	
			try {
				reader = new BufferedReader(new FileReader(this.extrato));
				String line = null;
				int    idx  = 0;
				while ( (line = reader.readLine()) != null ) {
					idx++;

					if ( (line.indexOf("TOTAL") != -1 || 
						  line.indexOf("Total") != -1 || 
						  line.indexOf("total") != -1) && idx > 15 ) {
						String valor = line.substring(60,69).trim();
						this.setValorTotal(valor);
						break;
					}
					
					goAhead = true;
					
					if ( goAhead && idx <= 18 )                                   goAhead = false;
					if ( goAhead && line.trim().equals("") )                      goAhead = false;
					if ( goAhead && line.substring(0,8).trim().equals("") )       goAhead = false;
					if ( goAhead && line.indexOf("PGTO DEBITO CONTA 0411") != -1) goAhead = false;
						
					if ( goAhead ) {
						String data      = line.substring(0,8).trim();
						String historico = line.substring(9,48).trim();
						String pais      = line.substring(48,51).trim();
						String reais     = line.substring(61,69).trim();
						String dolar     = line.substring(74).trim();

						LinhaExtratoCartao linhaExtratoCartao = new LinhaExtratoCartao();
						linhaExtratoCartao.setData(data);
						linhaExtratoCartao.setHistorico(historico);
						linhaExtratoCartao.setPais(pais);
						
						if ( StringUtils.equals(reais.trim(),"0,00") ) {
							BigDecimal bdDolar    = new BigDecimal(Util.cleanNumber(dolar),MathContext.DECIMAL32);
							BigDecimal bdVlrDolar = new BigDecimal(vlrDolar,MathContext.DECIMAL32);
							reais = Util.formatCurrency(bdDolar.multiply(bdVlrDolar));
						}
						
						linhaExtratoCartao.setValor(reais);

						this.linhas.add(linhaExtratoCartao);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if ( reader != null ) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void resetVerificacaoValores() {
		this.valoresVerificados = new HashMap<String,Integer>();
	}
	
	public boolean exist(String valor) {
		/**
		 * Na existência de valores iguais, deve-se verificar 
		 * também a incidência deles, caso seja a 4.a vez, 
		 * então a pesquisa pelo valor similar deve encerrar-se 
		 * somente no 4.o loop.
		 */
		if ( this.valoresVerificados.containsKey(valor) ) {
			Integer qtde = this.valoresVerificados.get(valor);
			qtde = new Integer(qtde.intValue() + 1);
			this.valoresVerificados.put(valor, qtde);
		} else {
			this.valoresVerificados.put(valor, new Integer(1));
		}
		int stopWhen = this.valoresVerificados.get(valor).intValue();
		
		int     index = 0;
		boolean found = false;
		for (LinhaExtratoCartao linha : linhas) {
			if ( linha.getValor().equalsIgnoreCase(valor)) {
				index++;
				linha.setConferido(true);
				if ( index == stopWhen ) {
					return true;
				}
			}
		}
		return found;
	}
	
	public List<LinhaExtratoCartao> getLinhasNaoConferidas() {
		ArrayList<LinhaExtratoCartao> result = new ArrayList<LinhaExtratoCartao>();
		for (LinhaExtratoCartao linha : linhas) {
			if ( !linha.isConferido() ) {
				result.add(linha);
			}
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		VerificarExtratoCartao verificarExtratoCartao = new VerificarExtratoCartao("C:\\Temp\\extratocartao.txt");
		verificarExtratoCartao.getLinhas();
	}

}
