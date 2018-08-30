package br.ujr.scorecard.analisador.fatura.cartao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import br.ujr.scorecard.config.ScorecardConfigBootStrap;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertiesUtil;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;

public class AnalisadorFaturaCartaoBancSabadell {
	
	private String conteudo;
	private List<LinhaLancamento> lista = new ArrayList<LinhaLancamento>();
	private Date mesAnoRefencia;
	private CartaoContratado cartaoContratado;
	private ContaCorrente bancSabadell;
	private ScorecardManager bd;
	private static Logger logger = Logger.getLogger(AnalisadorFaturaCartaoBancSabadell.class);
	
	public static void main(String[] args) {
//		Date hoje = Calendar.getInstance().getTime();
//		ScorecardManager manager = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
//		ContaCorrente cc = manager.getContaCorrentePorId(Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCBanSabadell));
//		List<CartaoContratado> listCartaoContratado = manager.getCartaoContratado(cc);
//		AnalisadorFaturaCartaoBancSabadell a =  new AnalisadorFaturaCartaoBancSabadell(hoje, listCartaoContratado.get(0));
	}
	
	public AnalisadorFaturaCartaoBancSabadell(Date mesAnoRefencia, CartaoContratado cartaoContratado) {
		try {
			
			this.bd = (ScorecardManager)ScorecardConfigBootStrap.getBean("scorecardManager");
			this.bancSabadell = bd.getContaCorrentePorId(Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCBanSabadell));
			
			this.mesAnoRefencia   = mesAnoRefencia;
			this.cartaoContratado = cartaoContratado;
			
			String result[]       = loadConteudo();
			this.conteudo         = result[1];
			if (!result[0].equalsIgnoreCase("OK")) {
				throw new RuntimeException(result[1]);
			}
			
		} catch (Throwable e) {
			logger.error(e.getMessage());
			StringBuffer sb = new StringBuffer("*** Problemas na captura de dados de Arquivo CSV ***");
			sb.append("\n\"" + this.conteudo + "\"\n\n");
			e.printStackTrace();
			throw new RuntimeException(sb.toString(), e);
		} 
	}
	
	private String[] loadConteudo() {
		String pathCsvFile = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.BancSabadellCsvPath);
		if ( pathCsvFile == null ) {
			throw new RuntimeException("Não foi encontrado a configuração de PATH para os arquivos CSV, chave=" + ScorecardPropertyKeys.BancSabadellCsvPath);
		}
		Path       path         = Paths.get(pathCsvFile);
		List<Path> listFilesCsv = null;
		try {
		   listFilesCsv = Files.walk(path)
                               .filter(s -> s.toString().endsWith(".csv"))
                               .map(Path::toAbsolutePath)
                               .sorted()
                               .collect(Collectors.toList());
		} catch(FileNotFoundException e) {
			String msg = "Não foi encontrado nenhum arquivo CSV no diretório: " + pathCsvFile; 
			logger.warn(msg);
			return new String[] {"KO",msg};
		} catch(IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(),e);
		}
		if ( listFilesCsv.size() == 0 ) {
			String msg = "Não foi encontrado nenhum arquivo CSV no diretório: " + pathCsvFile; 
			logger.warn(msg);
			return new String[] {"KO",msg};
		}
		
		
		MappingAnalisador mappingAnalisador = new MappingAnalisador();
		long refInicial = Util.extrairReferencia(this.mesAnoRefencia);
		long refFinal   = Util.extrairReferencia(this.mesAnoRefencia);
		this.setLista(new ArrayList<LinhaLancamento>());
		
		Pattern pattern = Pattern.compile("^\\d{2}-\\w{3}-\\d{4}\\|"); // Search for Pattern: 08-Aug-2018
		Stream<String> lines = null;
		final Map<String,String> values = new HashMap<String,String>();
		try {
			lines = Files.lines(listFilesCsv.get(0));
			lines.filter( pattern.asPredicate() ) 
			     .forEach(line -> {
			    	 
			        String[] columns   = line.split("\\|");
			    	String   data      = columns[0];
			    	String   historico = columns[1];
			    	String   banco     = columns[2];
			    	String   valor     = columns[3];
			    	String   moeda     = columns[4];
			    	
			    	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy",new Locale("en", "US"));
			    	Date dataMovimento;
					try {
						dataMovimento = sdf.parse(data.trim());
					} catch (ParseException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
			    	data = Util.formatDate(dataMovimento);
			    	 
			    	/*
					 * Verifica a existencia desta linha da fatura na base de dados
					 */
					Cartao cartao = new Cartao();
					cartao.setCartaoContratado(this.cartaoContratado);
					cartao.setContaCorrente(bancSabadell);
					Parcela parcela = new Parcela();
					parcela.setValor( Util.parseCurrency(valor) );
					cartao.addParcela(parcela);
					
					Set<Cartao> cartoes              = this.bd.getCartaoPorFiltro(refInicial, refFinal, cartao, dataMovimento);
					boolean     isInDatabaseAlready  = cartoes.size() > 0;
			    	 
			    	LinhaLancamento linhaFatura = new LinhaLancamento(isInDatabaseAlready,data,historico,valor,"0");
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
							.checkMappingDescricaoVsContaContabil(historico, linhaFatura);
					}
					
					this.getLista().add(linhaFatura);
			    	 
			     });
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if ( lines != null ) {
				lines.close();
			}
		}
		
		return new String[] {"OK",null};
	}

	public List<LinhaLancamento> getLista() {
		return lista;
	}

	public void setLista(List<LinhaLancamento> lista) {
		this.lista = lista;
	}


}
