package br.ujr.scorecard.analisador.fatura.cartao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.Util;

public class AnalisadorSMSCartaoTransacaoSantander extends AnalisadorSMSCartaoSantander {
	
	
	public AnalisadorSMSCartaoTransacaoSantander(Date mesAnoRefencia, CartaoContratado cartaoContratado) {
		super(mesAnoRefencia, cartaoContratado); 
	}
	
	public List<LinhaLancamento> getLista() {
		return lista;
	}

	public void setLista(List<LinhaLancamento> lista) {
		this.lista = lista;
	}
	
	protected String encontrarData(String linha) {
		int begin = linha.indexOf("aprovada em") + 11;
		String data = StringUtils.trim(linha.substring(begin, begin + 9));
		if ( data.length() == 8 ) {
			StringBuffer sb = new StringBuffer(data);
			sb.insert(6, "20");
			data = sb.toString();
		} else
		if ( data.length() == 10 ) {
		} else {
			throw new RuntimeException("Formato de data não esperado... \"" + data + "\"");
		}
		return data;
	}
	
	/**
	 * Busca o valor dentro da linha
	 * @param linha
	 * @return
	 */
	protected String encontrarValor(String linha) {
		int begin = linha.indexOf("R$ ");
		int end   = linha.indexOf("aprovada");
		return linha.substring(begin+3, end-1);
	}
	
	protected String encontrarValorDolar(String linha) {
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
	
	protected String encontrarDescricao(String linha) {
		Pattern pattern = Pattern.compile(" as \\d\\d:\\d\\d ");
		Matcher matcher = pattern.matcher(linha);
		if ( matcher.find() ) {
			int end   = matcher.end();
			return linha.substring(end);
		}
		throw new RuntimeException("Não foi encontrado a descrição na linha \"" + linha + "\"");
	}

	@Override
	public String getIdentificadorLancamento() {
		return "Transacao";
	}

	@Override
	public Conta isInDatabaseAlready(String valor, String descricao) {
		long refInicial = Util.extrairReferencia(this.mesAnoRefencia);
		long refFinal   = Util.extrairReferencia(this.mesAnoRefencia);
		/*
		 * Verifica a existencia desta linha da fatura na base de dados
		 */
		Cartao cartao = new Cartao();
		cartao.setOperadora(cartaoContratado.getCartaoOperadora());
		cartao.setContaCorrente(santander);
		Parcela parcela = new Parcela();
		parcela.setValor( Util.parseCurrency(valor) );
		cartao.addParcela(parcela);
		Set<Cartao> cartoes = this.bd.getCartaoPorFiltro(refInicial, refFinal, cartao, null);
		if ( cartoes.size() > 0 ) {
			return cartoes.iterator().next().getConta();
		}
		return null;
	}

}
