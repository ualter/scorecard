package br.ujr.scorecard.analisador.extrato.contacorrente.deutsche;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;

import org.jfree.xml.generator.model.ManualMappingInfo;

import br.ujr.scorecard.analisador.extrato.contacorrente.deutsche.AnalisadorExtratoCCDeutsche.LinhaExtratoContaCorrenteDeutsche;
import br.ujr.scorecard.gui.view.ScorecardBusinessDelegate;
import br.ujr.scorecard.model.conta.Conta;

public class MappingAnalisadorDeutsche {
	
	public void checkMappingDescricaoVsContaContabil(String descricao, LinhaExtratoContaCorrenteDeutsche linhaExtratoDeutsche) {
		ScorecardBusinessDelegate bd = ScorecardBusinessDelegate.getInstance();
		
		BufferedReader buffReader = null;
		try {
			
			InputStream stream = Thread.currentThread().getClass().getResourceAsStream("/mapping-contacontabil-analise-deutsche-extrato.txt");
			Reader file = new InputStreamReader(stream);
			
			LinkedHashMap<String, Mappings> mappingsContaContabil = new LinkedHashMap<String, Mappings>();
			buffReader = new BufferedReader(file);
			String line = buffReader.readLine();
			while (line != null) {
				String key          = line.split("=")[0];
				String valueAndTipo = line.split("=")[1];
				String value        = valueAndTipo.split("@")[0];
				String tipo         = valueAndTipo.split("@")[1];
				mappingsContaContabil.put(key, new Mappings(new Integer(value), tipo.trim()));
				line = buffReader.readLine();
			}
			
			Conta conta = null;
			for(String keyWord : mappingsContaContabil.keySet()) {
				if ( descricao.toUpperCase().contains(keyWord.toUpperCase()) ) {
					Integer idConta = mappingsContaContabil.get(keyWord).getContaContabil();
					conta = bd.getContaPorId(idConta.intValue());
					linhaExtratoDeutsche.setContaContabil(conta);
					linhaExtratoDeutsche.setTipo(mappingsContaContabil.get(keyWord).getTipo());
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
	
	private static class Mappings {
		
		private Integer contaContabil;
		private String  tipo;
		
		public Mappings(Integer contaContabil, String tipo) {
			super();
			this.contaContabil = contaContabil;
			this.tipo = tipo;
		}

		
		public Integer getContaContabil() {
			return contaContabil;
		}

		
		public void setContaContabil(Integer contaContabil) {
			this.contaContabil = contaContabil;
		}

		
		public String getTipo() {
			return tipo;
		}

		
		public void setTipo(String tipo) {
			this.tipo = tipo;
		}
		
	}

}
