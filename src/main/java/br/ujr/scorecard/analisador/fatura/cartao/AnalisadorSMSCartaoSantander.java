package br.ujr.scorecard.analisador.fatura.cartao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;

public abstract class AnalisadorSMSCartaoSantander {
	
	protected String conteudo;
	protected List<LinhaLancamento> lista = new ArrayList<LinhaLancamento>();
	protected Date mesAnoRefencia;
	protected Cartao.Operadora operadora;
	protected ContaCorrente santander;
	protected ScorecardManager bd;
	
	
	public AnalisadorSMSCartaoSantander(Date mesAnoRefencia, Cartao.Operadora operadora) {
		try {
			
			this.bd = (ScorecardManager)Util.getBean("scorecardManager");
			this.santander = bd.getContaCorrentePorId(ScorecardPropertyKeys.IdCCSantander);
			
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

	public abstract String getIdentificadorLancamento();
	public abstract Conta isInDatabaseAlready(String valor, String descricao);
	
	protected void traduzirConteudo()  {
		String data;
		String valor;
		String valorDolar;
		String descricao;
		
		this.setLista(new ArrayList<LinhaLancamento>());
		String[] linhas = this.conteudo.split("\\n");
    	for (String linha : linhas) {
    		
    		if ( linha.indexOf(this.getIdentificadorLancamento()) != -1 ) {
    			data       = this.encontrarData(linha);
    			valor      = this.encontrarValor(linha);
    			valorDolar = "0";
    			descricao  = this.encontrarDescricao(linha);
    			
    			/*
    			 * Verifica a existencia desta linha da fatura na base de dados
    			 */
    			Conta conta = this.isInDatabaseAlready(valor, descricao);
    			boolean isInDatabaseAlready  = (conta != null);
    			
    			LinhaLancamento linhaFatura = new LinhaLancamento(isInDatabaseAlready,data, descricao, valor, valorDolar);
    			/*
    			 * Busca a conta contabil ja gravada para o registro que existe, registro este que representa a linha na fatura
    			 */
    			if ( isInDatabaseAlready ) {
    				linhaFatura.setContaContabil(conta);
    			}
    			
    			this.getLista().add(linhaFatura);
    		}
		}
		
	}
	
	protected abstract String encontrarData(String linha);
	
	/**
	 * Busca o valor dentro da linha
	 * @param linha
	 * @return
	 */
	protected abstract String encontrarValor(String linha);
	
	protected abstract String encontrarValorDolar(String linha);
	
	protected abstract String encontrarDescricao(String linha);


}
