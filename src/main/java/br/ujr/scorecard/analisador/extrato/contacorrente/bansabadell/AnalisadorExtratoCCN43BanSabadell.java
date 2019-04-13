package br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import br.ujr.scorecard.util.properties.ScorecardPropertiesUtil;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;


public class AnalisadorExtratoCCN43BanSabadell extends AbstractAnalisadorExtratoBanSabadell {


	private static Logger logger = Logger.getLogger(AnalisadorExtratoCCN43BanSabadell.class);
	
	
	public AnalisadorExtratoCCN43BanSabadell(long referencia) {
		this.cc         = scorecardManager.getContaCorrentePorId( Integer.parseInt(ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.IdCCBanSabadell)) );
		this.passivos   = scorecardManager.getPassivosPorReferencia(cc, referencia);
		this.ativos     = scorecardManager.getAtivosPorReferencia(cc, referencia);
		this.referencia = referencia;
	}
	
	/**
	 * Formato Esperado: N43, check PDF
	 * @return
	 */
	public String analisarExtrato() {
		String pathN43Files = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.BancSabadellN43Path);
		if ( pathN43Files == null ) {
			throw new RuntimeException("Não foi encontrado a configuração de PATH para os arquivos N43, chave=" + ScorecardPropertyKeys.BancSabadellN43Path);
		}
		
		Path       path         = Paths.get(pathN43Files);
		List<Path> listFilesN43 = null;
		try {
		   listFilesN43 = Files.walk(path)
                               .filter(s -> s.toString().endsWith(".n43"))
                               .map(Path::toAbsolutePath)
                               .sorted()
                               .collect(Collectors.toList());
		} catch (NoSuchFileException e) {
			String msg = "Não foi encontrado nenhum arquivo N43 no diretório: " + pathN43Files;
			logger.warn(msg);
			return msg;
		} catch(IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(),e);
		}
		if ( listFilesN43.size() == 0 ) {
			String msg = "Não foi encontrado nenhum arquivo N43 no diretório: " + pathN43Files; 
			logger.warn(msg);
			return msg;
		}
		
		MappingAnalisadorBanSabadell mappingAnalisadorBanSabadell = new MappingAnalisadorBanSabadell();
		this.extratoContaCorrente   = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
		Stream<String> lines        = null;
		final Map<String,String> values = new HashMap<String,String>();
		try {
			lines = Files.lines(listFilesN43.get(0), Charset.forName("ISO-8859-1"));
			lines.filter(line -> line.startsWith("22") || line.startsWith("23"))
			     .forEach(line -> {
			    	 
			    	 if ( line.startsWith("22") ) {
			    		 values.put("dataOperacao", extractDataFromLine(line));
			    		 values.put("valor", line.substring(28,42));
			    		 values.put("negativePositive", line.substring(27,28));
			    	 } else
		    		 if ( line.startsWith("23") ) {
		    			 LinhaExtratoContaCorrenteBanSabadell linhaExtratoContaCorrenteBanSabadell = new LinhaExtratoContaCorrenteBanSabadell();
		    			 linhaExtratoContaCorrenteBanSabadell.setDataOperacao(values.get("dataOperacao"));
		    			 linhaExtratoContaCorrenteBanSabadell.setValor(values.get("valor"), values.get("negativePositive"));
		    			 linhaExtratoContaCorrenteBanSabadell.setHistorico(line.substring(4,60));
		    			 mappingAnalisadorBanSabadell.checkMappingDescricaoVsContaContabil(linhaExtratoContaCorrenteBanSabadell.getHistorico(),linhaExtratoContaCorrenteBanSabadell);
		    			 this.extratoContaCorrente.add(linhaExtratoContaCorrenteBanSabadell);
		    			 values.clear();
		    		 }
			    	 
			     });
						 
			
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if ( lines != null ) {
				lines.close();
			}
		}
		return null;
	}

}
