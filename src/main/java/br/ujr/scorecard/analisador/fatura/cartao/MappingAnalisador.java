package br.ujr.scorecard.analisador.fatura.cartao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;

import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.model.conta.Conta;

public class MappingAnalisador {
	
	public void checkMappingDescricaoVsContaContabil(String descricao, LinhaLancamento linhaFatura) {
		ScorecardBusinessDelegate bd = ScorecardBusinessDelegate.getInstance();
		
		BufferedReader buffReader = null;
		try {
			
			InputStream stream = Thread.currentThread().getClass().getResourceAsStream("/mapping-contacontabil-descricaofatura.txt");
			Reader file = new InputStreamReader(stream);
			
			LinkedHashMap<String, Integer> mappings = new LinkedHashMap<String, Integer>();
			buffReader = new BufferedReader(file);
			String line = buffReader.readLine();
			while (line != null) {
				String key   = line.split("=")[0];
				String value = line.split("=")[1];
				mappings.put(key, new Integer(value));
				line = buffReader.readLine();
			}
			
			Conta conta = null;
			for(String keyWord : mappings.keySet()) {
				if ( descricao.toUpperCase().contains(keyWord.toUpperCase()) ) {
					Integer idConta = mappings.get(keyWord);
					conta = bd.getContaPorId(idConta.intValue());
					linhaFatura.setContaContabil(conta);
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Nao encontrado o arquivo de mapping mapping-contacontabil-descricaofatura.txt");
		} finally {
			if ( buffReader != null )
				try {
					buffReader.close();
				} catch (IOException e) {
					// Ignore
				}
		}
	}

}
