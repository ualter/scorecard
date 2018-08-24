package br.ujr.scorecard.analisador.fatura.cartao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import br.ujr.scorecard.config.ScorecardConfigUtil;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;

public class AnalisadorFaturaCartaoDeutsche {
	
	private String conteudo;
	private List<LinhaLancamento> lista = new ArrayList<LinhaLancamento>();
	private Date mesAnoRefencia;
	private CartaoContratado cartaoContratado;
	private ContaCorrente deutsche;
	private ScorecardManager bd;
	private static Logger logger = Logger.getLogger(AnalisadorFaturaCartaoDeutsche.class);
	
	public static void main(String[] args) {
//		Date hoje = Calendar.getInstance().getTime();
//		Cartao.Operadora operadora = Cartao.Operadora.MASTERCARD;
//		AnalisadorFaturaCartaoDeutsche a =  new AnalisadorFaturaCartaoDeutsche(hoje, operadora);
	}
	
	public AnalisadorFaturaCartaoDeutsche(Date mesAnoRefencia, CartaoContratado cartaoContratado) {
		try {
			
			this.bd = (ScorecardManager)ScorecardConfigUtil.getBean("scorecardManager");
			this.deutsche = bd.getContaCorrentePorId(Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCDeutsche));
			
			this.mesAnoRefencia = mesAnoRefencia;
			this.cartaoContratado = cartaoContratado;
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		this.setLista(new ArrayList<LinhaLancamento>());
		String[] linhas = this.conteudo.split("\\n");
    	for (String linha : linhas) {
    		
    		String columns[] = linha.split("\\t");
			String data       = columns[0];
			String descricao  = columns[1];
			String valor      = columns[2];
			
			Date dataMovimento = null;
			try {
				dataMovimento = sdf.parse(data);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			 * Verifica a existencia desta linha da fatura na base de dados
			 */
			Cartao cartao = new Cartao();
			cartao.setCartaoContratado(cartaoContratado);
			cartao.setContaCorrente(deutsche);
			Parcela parcela = new Parcela();
			parcela.setValor( Util.parseCurrency(valor) );
			cartao.addParcela(parcela);
			Set<Cartao> cartoes = this.bd.getCartaoPorFiltro(refInicial, refFinal, cartao, dataMovimento);
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
