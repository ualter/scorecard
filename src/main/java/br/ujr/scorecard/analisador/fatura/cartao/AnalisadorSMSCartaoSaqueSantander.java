package br.ujr.scorecard.analisador.fatura.cartao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;

public class AnalisadorSMSCartaoSaqueSantander extends AnalisadorSMSCartaoSantander {
	
	
	public static void main(String[] args) {
		Date hoje = Calendar.getInstance().getTime();
		Cartao.Operadora operadora = Cartao.Operadora.MASTERCARD;
		AnalisadorSMSCartaoSaqueSantander a =  new AnalisadorSMSCartaoSaqueSantander(hoje, operadora);
	}
	
	public AnalisadorSMSCartaoSaqueSantander(Date mesAnoRefencia, Cartao.Operadora operadora) {
		super(mesAnoRefencia, operadora); 
	}
	
	public List<LinhaLancamento> getLista() {
		return lista;
	}

	public void setLista(List<LinhaLancamento> lista) {
		this.lista = lista;
	}
	
	protected String encontrarData(String linha) {
		int begin = linha.indexOf("efetuado em") + 11;
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
		return linha.substring(begin+3);
	}
	
	protected String encontrarDescricao(String linha) {
		return "Saque Lucimar";
	}

	@Override
	public String getIdentificadorLancamento() {
		return "Saque";
	}

	@Override
	public Conta isInDatabaseAlready(String valor, String descricao) {
		/**
		 * 867 - Saques
		 * 868 - Saques Ualter
		 * 869 - Saques Lucimar
		 */
		int id = 867;
		if ( descricao.indexOf("Santander") != -1) {
			id = 869;
		} else {
			id = 868;
		}
		ScorecardBusinessDelegate bd = ScorecardBusinessDelegate.getInstance();
		return bd.getContaPorId(id);
	}

	@Override
	protected String encontrarValorDolar(String linha) {
		return "0.00";
	}

}
