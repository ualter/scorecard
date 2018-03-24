package br.ujr.scorecard.analisador.fatura.cartao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.ScorecardPropertyKeys;
import br.ujr.scorecard.util.Util;

public class AnalisadorFaturaCartaoDeutsche {
	
	private String conteudo;
	private List<LinhaLancamento> lista = new ArrayList<LinhaLancamento>();
	private Date mesAnoRefencia;
	private Cartao.Operadora operadora;
	private ContaCorrente deutsche;
	private ScorecardBusinessDelegate bd;
	private static Logger logger = Logger.getLogger(AnalisadorFaturaCartaoDeutsche.class);
	
	public static void main(String[] args) {
		Date hoje = Calendar.getInstance().getTime();
		Cartao.Operadora operadora = Cartao.Operadora.MASTERCARD;
		AnalisadorFaturaCartaoDeutsche a =  new AnalisadorFaturaCartaoDeutsche(hoje, operadora);
	}
	
	public AnalisadorFaturaCartaoDeutsche(Date mesAnoRefencia, Cartao.Operadora operadora) {
		try {
			
			this.bd = ScorecardBusinessDelegate.getInstance();
			this.deutsche = bd.getContaCorrentePorId(ScorecardPropertyKeys.IdCCDeutsche);
			
			this.mesAnoRefencia = mesAnoRefencia;
			this.operadora = operadora;
			this.conteudo = Util.getClipBoarContent();
			
			this.traduzirConteudo();
		} catch (Throwable e) {
			logger.error(e.getMessage());
			StringBuffer sb = new StringBuffer("*** Problemas na captura de dados do ClipBoard *** \n\nConteúdo encontrado:");
			sb.append("\n\"" + this.conteudo + "\"\n\n");
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
		MappingAnalisador mappingAnalisador = new MappingAnalisador();
		
		long refInicial = Util.extrairReferencia(this.mesAnoRefencia);
		long refFinal   = Util.extrairReferencia(this.mesAnoRefencia);
		
		this.setLista(new ArrayList<LinhaLancamento>());
		String[] linhas = this.conteudo.split("\\n");
    	for (String linha : linhas) {
    		
    		String columns[] = linha.split("\\t");
			String data       = columns[0];
			String descricao  = columns[1];
			String valor      = columns[2];
			
			/*
			 * Verifica a existencia desta linha da fatura na base de dados
			 */
			Cartao cartao = new Cartao();
			cartao.setOperadora(operadora);
			cartao.setContaCorrente(deutsche);
			Parcela parcela = new Parcela();
			parcela.setValor( Util.parseCurrency(valor) );
			cartao.addParcela(parcela);
			Set<Cartao> cartoes = this.bd.getCartaoPorFiltro(refInicial, refFinal, cartao);
			boolean isInDatabaseAlready  = cartoes.size() > 0;
			
			LinhaLancamento linhaFatura = new LinhaLancamento(isInDatabaseAlready,data, descricao, valor, "0");
			
			/*
			 * Busca a conta contabil ja gravada para o registro que existe, registro este que representa a linha na fatura
			 */
			if ( isInDatabaseAlready ) {
				Conta conta = cartoes.iterator().next().getConta();
				linhaFatura.setContaContabil(conta);
			} else {
				/*
				 * Verifica a descricao, e de acordo com o mapping contido em um arquivo
				 * sugere a Conta Contabil definida no mapeamento 
				 */
				mappingAnalisador
					.checkMappingDescricaoVsContaContabil(descricao, linhaFatura);
			}
			
			this.getLista().add(linhaFatura);
		}
		
	}

}
