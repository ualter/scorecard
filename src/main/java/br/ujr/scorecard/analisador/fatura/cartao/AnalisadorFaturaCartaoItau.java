package br.ujr.scorecard.analisador.fatura.cartao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;

public class AnalisadorFaturaCartaoItau {
	
	private String conteudo;
	private List<LinhaLancamento> lista = new ArrayList<LinhaLancamento>();
	private Date mesAnoRefencia;
	private Cartao.Operadora operadora;
	private ContaCorrente itau;
	private ScorecardBusinessDelegate bd;
	
	public static void main(String[] args) {
		Date hoje = Calendar.getInstance().getTime();
		Cartao.Operadora operadora = Cartao.Operadora.VISA;
		AnalisadorFaturaCartaoItau a =  new AnalisadorFaturaCartaoItau(hoje, operadora);
	}
	
	public AnalisadorFaturaCartaoItau(Date mesAnoRefencia, Cartao.Operadora operadora) {
		try {
			
			this.bd = ScorecardBusinessDelegate.getInstance();
			this.itau = bd.getContaCorrentePorId(ScorecardPropertyKeys.IdCCItau);
			
			this.mesAnoRefencia = mesAnoRefencia;
			this.operadora = operadora;
			this.conteudo = Util.getClipBoarContent();
			
			this.traduzirConteudo();
		} catch (Throwable e) {
			StringBuffer sb = new StringBuffer("*** Problemas na captura de dados do ClipBoard *** \n\nConteúdo encontrado:");
			sb.append("\n\"" + this.conteudo + "\"\n\n");
			System.out.println(sb.toString());
			e.printStackTrace();
			throw new RuntimeException(sb.toString(), e);
		} 
	}
	
	public List<LinhaLancamento> getLista() {
		return lista;
	}

	public void setLista(List<LinhaLancamento> lista) {
		this.lista = lista;
	}

	private void traduzirConteudo()  {
		long refInicial = Util.extrairReferencia(this.mesAnoRefencia);
		long refFinal   = Util.extrairReferencia(this.mesAnoRefencia);
		
		this.setLista(new ArrayList<LinhaLancamento>());
		String[] linhas = this.conteudo.split("\\n");
    	for (String linha : linhas) {
    		
    		String vlrs[] = linha.split("\\t");
    		
    		
			String data       = vlrs[0];
			String valor      = vlrs[3];
			String valorDolar = "0";
			String descricao  = vlrs[1];
			
			if ( data.length() == 5 ) {
				data += "/2014";
			}
			
			/*
			 * Verifica a existencia desta linha da fatura na base de dados
			 */
			Cartao cartao = new Cartao();
			cartao.setOperadora(operadora);
			cartao.setContaCorrente(itau);
			Parcela parcela = new Parcela();
			parcela.setValor( Util.parseCurrency(valor) );
			cartao.addParcela(parcela);
			Set<Cartao> cartoes = this.bd.getCartaoPorFiltro(refInicial, refFinal, cartao);
			boolean isInDatabaseAlready  = cartoes.size() > 0;
			
			LinhaLancamento linhaFatura = new LinhaLancamento(isInDatabaseAlready,data, descricao, valor, valorDolar);
			
			/*
			 * Busca a conta contabil ja gravada para o registro que existe, registro este que representa a linha na fatura
			 */
			if ( isInDatabaseAlready ) {
				Conta conta = cartoes.iterator().next().getConta();
				linhaFatura.setContaContabil(conta);
			}
			
			this.getLista().add(linhaFatura);
		}
		
	}
	
	/**
	 * Busca o valor dentro da linha
	 * @param linha
	 * @return
	 */
	private String encontrarValor(String linha) {
		int posicao = linha.length() - 1;
		boolean startValor = false;
		boolean endValor   = false;
		StringBuffer valor = new StringBuffer();
		String caract = null;
		while ( posicao > 0 ) {
			caract = linha.substring(posicao, posicao+1);
			/*
			 * Procura pelo espaco anterior ao 0,00 e comeca a considerar o valor
			 */
			if ( !startValor && caract.indexOf(" ") != -1 ) {
				startValor = true;
			}
			/*
			 * Se ja encontrado a marca que inicia o valor, concatenar o caracter
			 * para compor o valor, e somente quando o caracter for numerico ou igual ao a "," ou "."
			 */
			if ( startValor && (caract.equals(",") || caract.equals(".") || StringUtils.isNumeric(caract)) ) {
				valor.insert(0, caract);
			}
			/*
			 * Quando a composicao do valor ja tiver sido iniciada, e o caracter for igual a ","
			 * e o caracter nao for mais numerico, entao cessar a composicao do valor (chegou ao fim do valor)
			 */
			if ( valor.length() > 0 && !caract.equals(",") && !caract.equals(".") && !StringUtils.isNumeric(caract) ) {
				return valor.toString().trim();
			}
			posicao--;
		}
		throw new RuntimeException("Valor nao encontrado na linha:\"" + linha + "\"");
	}
	
	private String encontrarValorDolar(String linha) {
		int posicao = linha.length() - 1;
		StringBuffer valor = new StringBuffer();
		String caract = null;
		while ( posicao > 0 ) {
			caract = linha.substring(posicao, posicao+1);
			if ( caract.indexOf(" ") != -1 ) {
				return valor.toString().trim();
			}
			valor.insert(0, caract);
			posicao--;
		}
		throw new RuntimeException("Valor do Dolar nao encontrado na linha:\"" + linha + "\"");
	}
	
	private String encontrarDescricao(String linha) {
		int posicao = linha.length() - 1;
		StringBuffer valor = new StringBuffer();
		
		/*
		 * Encontrar o primeito valor da linha para a partir da sua esquerda ate o fim da data
		 * considerar como valor da descricao
		 */
		Pattern pattern = Pattern.compile("(([0-9]){1,3}\\.){0,}([0-9]){1,3}\\,([0-9][0-9])");
		Matcher matcher = pattern.matcher(linha);
		if ( matcher.find() ) {
			int finalPosDescricao = matcher.start();
			return linha.substring(11, finalPosDescricao-1).trim();
		} 
		
		throw new RuntimeException("Valor do Dolar nao encontrado na linha:\"" + linha + "\"");
	}

}
