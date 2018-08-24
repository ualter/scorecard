package br.ujr.scorecard.importing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.ativo.saldoanterior.SaldoAnterior;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;

/**
 * Executa a importação da Planilha de Contas, interpretando e traduzindo o movimento.
 * Utiliza o objeto MovimentoMensal para encapsular os movimento mensais carregados.
 * @author Ualter
 */
public class Importador {
	
	private HSSFWorkbook workBook;
	
	int[]   indexCellIgnore = new int[]{5,10};
	int[]   indexCellDates  = new int[]{1,6};
	int[]   indexCellValues = new int[]{2,8,11};
	int[]   indexCellHist   = new int[]{3,9,12};
	int[]   indexCellNrCq   = new int[]{4};
	int[]   indexCellIdCart = new int[]{7};

	private String flatFile;
	private boolean debug = false;
	
	
	public Importador(String file) {
		try {
			this.workBook = new HSSFWorkbook( new FileInputStream(file) );
		} catch (FileNotFoundException e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		} catch (IOException e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		}
	}
	
	private boolean isCellIdCartao(short num) {
		return this.isPresent(num, indexCellIdCart);
	}
	private boolean isCellNrCq(short num) {
		return this.isPresent(num, indexCellNrCq);
	}
	private boolean isCellHist(short num) {
		return this.isPresent(num, indexCellHist);
	}
	private boolean isCellIgnore(short num) {
		return this.isPresent(num, indexCellIgnore);
	}
	private boolean isCellValues(short num) {
		return this.isPresent(num, indexCellValues);
	}
	private boolean isCellDate(short num) {
		return this.isPresent(num, indexCellDates);
	}
	private boolean isPresent(short num, int[] cache) {
		for (int i = 0; i < cache.length; i++) {
			if ( num == cache[i] ) {
				return true;
			}
		}
		return false;
	}
	public void createFlatFile() {
		int                 totalALer    = 107;
		int                 totalLinhas  = 107;
		System.out.println("Total de Linhas a serem lidas: " + totalALer);
		int                 endCell      = 12;
		SimpleDateFormat    sdf          = new SimpleDateFormat("dd/MM/yyyy");
		String              currentMonth = null;
		
		HSSFSheet           sheetLasts   = this.workBook.getSheetAt(0);
		ArrayList<String[]> listRow      = new ArrayList<String[]>();
		
		/**
		 * Lendo Planilha Excel com a Carga e trazudindo para uma sequência de linhas representado por uma collection to tipo List, 
		 * que contêm uma coleção de Array de Strings, que representa as colunas na linha
		 */
		for(Iterator iteratorRow = sheetLasts.rowIterator(); iteratorRow.hasNext();) {
			HSSFRow           row     = (HSSFRow) iteratorRow.next();
			
			ArrayList<String> listCell = new ArrayList<String>();
			
			for (Iterator iteratorCell = row.cellIterator(); iteratorCell.hasNext();) {
				
				HSSFCell          cell     = (HSSFCell) iteratorCell.next();
				int               cellType = cell.getCellType();
				
				if ( cell.getCellNum() > endCell ) 
					break;
				
				if ( cell.getCellNum() == 0 ) {
					if ( cell.toString().equals("Mês") ) {
						break;
					}
					if ( currentMonth == null || ( !cell.toString().trim().equals("") && !currentMonth.equals(cell.toString()) ) ) {
						currentMonth = cell.toString();
					}
					listCell.add( StringUtils.rightPad(currentMonth,20) );
				} else
				if ( isCellDate(cell.getCellNum()) ) {
					if ( HSSFCell.CELL_TYPE_NUMERIC == cellType ) {
						double d = cell.getNumericCellValue();
						listCell.add( sdf.format(HSSFDateUtil.getJavaDate(d)) );
					} else {
						try {
							Date date = sdf.parse(cell.toString());
							listCell.add( sdf.format(date));
						} catch (ParseException e) {
							if ( cell.toString().equals("") ) {
								listCell.add( StringUtils.rightPad(cell.toString(),10) );
							} else {
								if ( !cell.toString().trim().equals("") ) {
									Date date = convertToDate(currentMonth.substring(currentMonth.indexOf("/")+1), cell.toString());
									listCell.add( sdf.format(date) );
								} else {
									listCell.add(StringUtils.rightPad(cell.toString(),10));
								}
							}
						}
					}
				} else
				if ( isCellValues(cell.getCellNum()) ) {
					listCell.add( StringUtils.rightPad(cell.toString(),20) );
				} else
				if ( isCellHist(cell.getCellNum()) ) {
					listCell.add( StringUtils.rightPad(cell.toString(),50) );
				} else
				if ( isCellNrCq(cell.getCellNum()) ) {
					listCell.add( StringUtils.rightPad(cell.toString(),8) );
				} else
				if ( isCellIdCartao(cell.getCellNum()) ) {
					listCell.add( StringUtils.rightPad(cell.toString(),1) );
				} else	
				if ( isCellIgnore(cell.getCellNum()) ) {
				} else {
					listCell.add( cell.toString() );
				}
			}
			String[] strCells = new String[listCell.size()];
			listCell.toArray(strCells);
			for (int i = 1; i < strCells.length; i++) {
				String string = strCells[i];
				if ( string != null && !string.trim().equals("") ) {
					listRow.add(strCells);
					break;
				}
			}
			
			if ( --totalLinhas < 1 ) 
				break;
			if ( debug ) System.out.println("Lido linha: " + (totalALer - totalLinhas) );
		}
		
		TradutorContas tradutorContas = new TradutorContas();
		/**
		 * Carregando coleção do objeto do tipo MovimentoMensal do que foi extraído 
		 * da planilha Excel no código acima ( List<String[]> )
		 */
		TreeMap<String,MovimentoMensal> ht = new TreeMap<String,MovimentoMensal>();
		for (String[] strings : listRow) {
			MovimentoMensal importingLine = null;
			String referencia = Importador.traduzirReferencia(strings[0].trim());
			if ( !ht.containsKey(referencia) ) {
				importingLine = new MovimentoMensal(tradutorContas, referencia, strings[0].trim());
				ht.put(referencia, importingLine);
			}
			importingLine = ht.get(referencia);
			importingLine.add(strings);
		}
		
		FileWriter outScorecardCarga;
		try {
			flatFile = "c:/temp/CargaScorecard.txt";
			outScorecardCarga = new FileWriter(flatFile);
			for (String keyMovimentoMensal : ht.keySet()) {
				MovimentoMensal il = ht.get(keyMovimentoMensal);
				if (debug) System.out.println(keyMovimentoMensal);
					
				for (MovimentoMensal.Cheque cheque : il.getCheques()) {
					outScorecardCarga.write(il.getReferencia() + "\t");
					outScorecardCarga.write("CHEQUE\t");
					outScorecardCarga.write(cheque.getConta() + "\t");
					outScorecardCarga.write(cheque.getData() + "\t");
					outScorecardCarga.write(cheque.getValor() + "\t");
					outScorecardCarga.write(cheque.getParcela() + "\t");
					outScorecardCarga.write(cheque.getHistorico() + "\t");
					outScorecardCarga.write(cheque.getChave() + "\t");
					outScorecardCarga.write(cheque.getNumero());
					outScorecardCarga.write("\n");
				}
				outScorecardCarga.flush();
				
				for (MovimentoMensal.Cartao cartao : il.getCartoes()) {
					outScorecardCarga.write(il.getReferencia() + "\t");
					if ( "V".equals(cartao.getCartao()) ) {
						outScorecardCarga.write("VISA  \t");
					} else
					if ( "M".equals(cartao.getCartao()) ) {
						outScorecardCarga.write("MASTER\t");
					} else
					if ( "E".equals(cartao.getCartao()) ) {
						outScorecardCarga.write("ELECTR\t");
					} else
					if ( "S".equals(cartao.getCartao()) ) {
						outScorecardCarga.write("SAQUE \t");
					}	
					outScorecardCarga.write(cartao.getConta() + "\t");
					outScorecardCarga.write(cartao.getData() + "\t");
					outScorecardCarga.write(cartao.getValor() + "\t");
					outScorecardCarga.write(cartao.getParcela() + "\t");
					outScorecardCarga.write(cartao.getHistorico() + "\t");
					outScorecardCarga.write(cartao.getChave());
					outScorecardCarga.write("\n");
				}
				outScorecardCarga.flush();
				
				for (MovimentoMensal.Debito debito : il.getDebitos()) {
					outScorecardCarga.write(il.getReferencia() + "\t");
					outScorecardCarga.write("DEBITO\t");
					outScorecardCarga.write(debito.getConta() + "\t");
					outScorecardCarga.write(debito.getData() + "\t");
					outScorecardCarga.write(debito.getValor() + "\t");
					outScorecardCarga.write(debito.getParcela() + "\t");
					outScorecardCarga.write(debito.getHistorico() + "\t");
					outScorecardCarga.write(debito.getChave());
					outScorecardCarga.write("\n");
				}
				
				outScorecardCarga.write("\n");
				outScorecardCarga.flush();
			}
			outScorecardCarga.flush();
			outScorecardCarga.close();
		} catch (Exception e) {
			throw new RuntimeException("[ERROR] " + e.getMessage(),e);
		}
	}

	public static String traduzirReferencia(String referencia) {
		int    pos = referencia.indexOf("/");
		String mes = referencia.substring(0, pos);
		String ano = referencia.substring(pos+1);
		return converterReferencia(mes, ano);
	}

	private static String converterReferencia(String mes, String ano) {
		String ref = "";
		if ( "JANEIRO".equalsIgnoreCase(mes)) {
			ref = "01";
		} else
		if ( "FEVEREIRO".equalsIgnoreCase(mes)) {
			ref = "02";
		} else	
		if ( "MARÇO".equalsIgnoreCase(mes)) {
			ref = "03";
		} else
		if ( "ABRIL".equalsIgnoreCase(mes)) {
			ref = "04";
		} else	
		if ( "MAIO".equalsIgnoreCase(mes)) {
			ref = "05";
		} else
		if ( "JUNHO".equalsIgnoreCase(mes)) {
			ref = "06";
		} else		
		if ( "JULHO".equalsIgnoreCase(mes)) {
			ref = "07";
		} else
		if ( "AGOSTO".equalsIgnoreCase(mes)) {
			ref = "08";
		} else
		if ( "SETEMBRO".equalsIgnoreCase(mes)) {
			ref = "09";
		} else	
		if ( "OUTUBRO".equalsIgnoreCase(mes)) {
			ref = "10";
		} else
		if ( "NOVEMBRO".equalsIgnoreCase(mes)) {
			ref = "11";
		} else
		if ( "DEZEMBRO".equalsIgnoreCase(mes)) {
			ref = "12";
		}	
		return ano.trim() + ref.trim();
	}

	private Date convertToDate(String ano, String string) {
		String result = string.replaceAll("\\.", "/");
		if ( result.length() == 5 ) {
			result += "/" + ano;
		} else
		if ( result.length() == 4 ) {
			int pos = result.indexOf("/");
			if ( pos == 2 ) {
				String dia = result.substring(0, 2);
				String mes = "0" + result.substring(3);
				result = dia + "/" + mes + "/" + ano;
			}
		} else {
			if (debug) System.out.println(string + " - " + result);
		}
		
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(result);
		} catch (ParseException e) {
			throw new RuntimeException("[ERROR] " + e.getMessage() + ", " + result,e);
		}
	}
	
	public void doImport() {
		ScorecardManager manager = (ScorecardManager)Util.getBean("scorecardManager");
		
		ContaCorrente contaCorrente = manager.getContaCorrentePorId(Util.getInstance().getIdContaCorrenteBanco(ScorecardPropertyKeys.IdCCSantander));
		if ( contaCorrente == null ) {
			throw new RuntimeException("Conta corrente Ualter Jr. não encontrada.");
		}
		System.out.println("Conta Corrente: " + contaCorrente.getDescricao());
		
		this.importarPassivos(manager, contaCorrente);
		this.importarAtivos(manager, contaCorrente);
		this.importSaldoInicial(manager, contaCorrente);	
		manager.consistirSaldosAnteriores(contaCorrente,200506L,200804L,true);
		
	}

	private void importSaldoInicial(ScorecardManager manager, ContaCorrente contaCorrente) {
		/**
		 * Saldo Inicial 2005/05
		 */
		
		System.out.println("*** SALDO INICIAL");
		
		Conta contaContabilSaldosAnteriories = null;
		List<Conta> contas = manager.getContaPorDescricao("Saldos Anteriores");
		if ( contas != null && contas.size() > 0 ) {
			contaContabilSaldosAnteriories = contas.get(0);
		} else {
			String msg = "Não existe a Conta Contábil \"Saldo Anteriores\" cadastrada para cobrir a geração automática dos saldos anteriores.";
			throw new RuntimeException(msg);
		}
		
		String file = "c:/Documents and Settings/Ualter/My Documents/Planilhas/Carga Scorecard.xls";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			HSSFWorkbook        workBook    = new HSSFWorkbook( new FileInputStream(file) );
			HSSFSheet           sheetLasts  = this.workBook.getSheetAt(2);
			
			HSSFRow  row  = sheetLasts.getRow(27);
			
			HSSFCell c = row.getCell((short)0);
			String ref = c.toString().toUpperCase();
			int    pos = ref.indexOf("/");
			String ano = ref.substring(0, pos);
			String mes = ref.substring(pos+1);
			ref = converterReferencia(mes, ano);
			ano = ref.substring(0,4);
			mes = ref.substring(4,6);
			Date referencia = null;
			try {
				referencia = sdf.parse("01" + "/" + mes + "/" + ano);
			} catch (ParseException e) {
				throw new RuntimeException("Data " + "01" + "/" + mes + "/" + ano + " invalida.");
			}
			
			c = row.getCell((short)1);
			System.out.println(c.getNumericCellValue());
			SaldoAnterior saldo = new SaldoAnterior();
			saldo.setConta(contaContabilSaldosAnteriories);
			saldo.setDataMovimento(referencia);
			saldo.setContaCorrente(contaCorrente);
			saldo.setHistorico("Saldo Anterior");
			saldo.setReferencia(referencia);
			saldo.setValor(new BigDecimal(c.getNumericCellValue()));
			
			System.out.println("Saldo Inicial " + ref + " " + saldo.getValor());
			
			manager.saveAtivo(saldo);
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		} catch (IOException e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		}
	}
	
	private void importarAtivos(ScorecardManager manager, ContaCorrente contaCorrente) {
		
		System.out.println("*** ATIVOS");
		
		String file = "c:/Documents and Settings/Ualter/My Documents/Planilhas/Carga Scorecard.xls";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			HSSFWorkbook        workBook    = new HSSFWorkbook( new FileInputStream(file) );
			HSSFSheet           sheetLasts  = this.workBook.getSheetAt(2);
			
			Conta contaContabilSalario      = manager.getContaPorId(853);
			Conta contaContabilDeposito     = manager.getContaPorId(854);
			Conta contaContabilInvestimento = manager.getContaPorId(815);
			
			System.out.println("Conta Contábil para Depósito:" + contaContabilDeposito.getNivel() + "-" + contaContabilDeposito.getDescricao());
			System.out.println("Conta Contábil para Salário:" + contaContabilSalario.getNivel() + "-" + contaContabilSalario.getDescricao());
			System.out.println("Conta Contábil para Investimento:" + contaContabilInvestimento.getNivel() + "-" + contaContabilInvestimento.getDescricao());
			
			short CELL_REFERENTE    = 0;
			short CELL_INVESTIMENTO = 8;
			short CELL_DEPOSITO     = 9;
			short CELL_SALARIO      = 11;
			
			HSSFCell c = null;
			int index = 0;
			String ref = "";
			BigDecimal investimento = null;
			BigDecimal deposito     = null;
			BigDecimal salario      = null;
			Date       referencia   = null;
			
			for(Iterator iteratorRow = sheetLasts.rowIterator(); iteratorRow.hasNext();) {
				HSSFRow row = (HSSFRow) iteratorRow.next();
				
				System.out.println("Importando Ativos a partir da Linha 65 (2008/Junho)");
				if (index > 65) {
					
					c = row.getCell(CELL_REFERENTE);
					if (c != null) {
						ref = c.toString().toUpperCase();
						int    pos = ref.indexOf("/");
						if ( StringUtils.isNotBlank(ref) ) {
							String ano = ref.substring(0, pos);
							String mes = ref.substring(pos+1);
							ref = converterReferencia(mes, ano);
							ano = ref.substring(0,4);
							mes = ref.substring(4,6);
							try {
								referencia = sdf.parse("01" + "/" + mes + "/" + ano);
							} catch (ParseException e) {
								throw new RuntimeException("Data " + "01" + "/" + mes + "/" + ano + " invalida.");
							}
						} else {
							ref = null;
						}
					}
					
					if ( ref != null ) {
						
						Ativo ativo = null;
						
						c = row.getCell(CELL_INVESTIMENTO);
						if (c != null) {
							investimento = new BigDecimal(c.getNumericCellValue());
							if ( investimento.doubleValue() >  0 ) {
								ativo = new Investimento();
								ativo.setConta(contaContabilInvestimento);
								ativo.setHistorico("Fundo Renda Fixa");
								ativo.setValor(investimento);
								ativo.setDataMovimento(referencia);
								ativo.setContaCorrente(contaCorrente);
								ativo.setReferencia(referencia);
								manager.saveAtivo(ativo);
							}
						}
						
						c = row.getCell(CELL_DEPOSITO);
						if (c != null) {
							deposito = new BigDecimal(c.getNumericCellValue());
							if ( deposito.doubleValue() > 0 ) {
								ativo = new Deposito();
								ativo.setConta(contaContabilDeposito);
								ativo.setHistorico("Depósito");
								ativo.setValor(deposito);
								ativo.setDataMovimento(referencia);
								ativo.setContaCorrente(contaCorrente);
								ativo.setReferencia(referencia);
								manager.saveAtivo(ativo);
							}
						}
						
						c = row.getCell(CELL_SALARIO);
						if (c != null) {
							salario = new BigDecimal(c.getNumericCellValue(),MathContext.DECIMAL32);
							if ( salario.doubleValue() > 0 ) {
								ativo = new Salario();
								ativo.setConta(contaContabilSalario);
								ativo.setHistorico("Salário");
								ativo.setValor(salario);
								ativo.setDataMovimento(referencia);
								ativo.setContaCorrente(contaCorrente);
								ativo.setReferencia(referencia);
								manager.saveAtivo(ativo);
							}
						}
					}
				}
				index++;
			}
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		} catch (IOException e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		}
		
	}

	private void importarPassivos(ScorecardManager manager, ContaCorrente contaCorrente) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		float totalLinhas = 3493;
		DecimalFormat perc = new DecimalFormat("#00");
		
		System.out.println("*** PASSIVOS");
		String lastLabelPercent = null;
		
		HashMap<String,Passivo> passivosParcelados = new HashMap<String,Passivo>();
		
		File file = new File(this.flatFile);
		if ( file.exists() ) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = null;
				int indexLine = 0;
				while ((line = reader.readLine()) != null) {
					++indexLine;
					if ( debug ) 
						System.out.println("Importando Linha: " + (indexLine) + " ");
					
					float  percentual = (indexLine / totalLinhas) * 100;
					String labelPerc  = "Percentual " + perc.format(percentual) + "%";
					if ( !StringUtils.equals(labelPerc, lastLabelPercent) ) {
						System.out.println(labelPerc);
						lastLabelPercent = labelPerc;
					}
					
					if ( StringUtils.isNotBlank(line)) {
						
						String[] dados        = line.split("\t");
						
						if ( dados.length < 8 ) {
							throw new RuntimeException("Problema com a linha: " + line);
						}
						
						String   referencia    = dados[0];
						String   tipo          = dados[1];
						String   conta         = dados[2];
						String   data          = dados[3];
						String   valor         = dados[4];
						String   parcela       = dados[5];
						String   historico     = dados[6];
						String   chave         = dados[7];
						String   numeroCheque  = "CHEQUE".equals(tipo) ? dados[8] : null;
						
						// Montando Data
						String diaRef   = data.substring(0,2);
						String mesRef   = referencia.substring(4,6);
						String anoRef   = referencia.substring(0,4);
						
						if ( "CHEQUE".equals(tipo) ) {
							if ( StringUtils.isBlank(numeroCheque) ) {
								throw new RuntimeException("Linha de Cheque: " + line + " , está sem n. de cheque");
							}
						}
						
						/*if ( Integer.parseInt(mesRef) > 6 ) {
							break;
						}*/
						
						Passivo passivo = null;
						if ( isPassivoParcelado(parcela) ) {
							if ( !passivosParcelados.containsKey(chave) ) {
								passivo = this.criarPassivo(manager,contaCorrente, tipo, historico, diaRef, mesRef, anoRef, conta);
								passivosParcelados.put(chave, passivo);
							}
							passivo = passivosParcelados.get(chave);
							
							/**
							 * Várias Parcelas (Desmenbrar todas e Gravá-las nos meses corretos
							 */
							// Parcelas
							StringTokenizer stk = new StringTokenizer(parcela,"/");
							if ( stk.countTokens() == 0 ) {
								throw new RuntimeException("Problema com parse de Parcela, linha:" + line);
							}
							int atual;
							int total;
							try {
								atual = Integer.parseInt((String)stk.nextElement());
								total = Integer.parseInt((String)stk.nextElement());
							} catch (NumberFormatException e) {
								throw new RuntimeException("Parcela nao numerica " + parcela + " , linha:" + line);
							}
							// Data Vencimento de Referência (Parcela em foco)
							
							if ( Integer.parseInt(diaRef) > 28 && Integer.parseInt(mesRef) == 2 ) {
								/**
								 * Ultima dia de Fevereiro (desprezando ano bissexto
								 */
								diaRef = "28";
							}
							Date             dataVenc       = sdf.parse(diaRef + "/" + mesRef + "/" + anoRef);
							Calendar         dataVencimento = Calendar.getInstance();
							dataVencimento.setTime(dataVenc);
							
							Parcela parcelaPassivo = new Parcela();
							parcelaPassivo.setDataVencimento(new java.sql.Date(dataVencimento.getTimeInMillis()));
							parcelaPassivo.setEfetivado(true);
							parcelaPassivo.setReferencia(Long.parseLong(referencia));
							parcelaPassivo.setValor(converterParaBigDecimal(valor));
							parcelaPassivo.setNumero(atual);
							if ( "CHEQUE".equals(tipo) ) {
								parcelaPassivo.setCheque(true);
								parcelaPassivo.setNumeroCheque(numeroCheque);
								((Cheque)passivo).addParcela(numeroCheque, parcelaPassivo);
							} else {
								passivo.addParcela(parcelaPassivo);
							}
							passivo.setTotalParcelas(total);
						} else {
							/**
							 * Apenas 1 parcela
							 */
							passivo = this.criarPassivo(manager,contaCorrente, tipo, historico, diaRef, mesRef, anoRef, conta);
							Parcela parcelaPassivo = new Parcela();
							if ( Integer.parseInt(diaRef) > 28 && Integer.parseInt(mesRef) == 2 ) {
								/**
								 * Ultima dia de Fevereiro (desprezando ano bissexto
								 */
								diaRef = "28";
							}
							parcelaPassivo.setDataVencimento(diaRef + "/" + mesRef + "/" + anoRef);
							parcelaPassivo.setEfetivado(true);
							parcelaPassivo.setReferencia(Long.parseLong(referencia));
							BigDecimal vlr = converterParaBigDecimal(valor);
							parcelaPassivo.setValor(vlr);
							parcelaPassivo.setNumero(1);
							if ( "CHEQUE".equals(tipo) ) {
								parcelaPassivo.setCheque(true);
								parcelaPassivo.setNumeroCheque(numeroCheque);
								((Cheque)passivo).addParcela(numeroCheque, parcelaPassivo);
							} else {
								passivo.addParcela(parcelaPassivo);
							}
							if ( debug ) System.out.println(passivo);
							manager.savePassivo(passivo);
						}
					}
				}
				
				for (Iterator i = passivosParcelados.values().iterator(); i.hasNext();) {
					Passivo passivo = (Passivo) i.next();
					manager.savePassivo(passivo);
				}
				
				System.out.println("END!!!");
				
			} catch (Exception e) {
				throw new RuntimeException(e.toString(),e);
			}
		} else {
			System.out.println("Não foi encontrado o arquivo " + this.flatFile);
		}
	}

	private boolean isPassivoParcelado(String parcela) {
		return !StringUtils.equals("1/1", parcela);
	}

	private Passivo criarPassivo(ScorecardManager manager, ContaCorrente contaCorrente, String tipo, String historico, String diaRef, String mesRef, String anoRef, String conta) {
		// Identificando Conta Contábil
		List<Conta> list = manager.getContasPorNivel(conta);
		if ( list.size() == 0 ) {
			throw new RuntimeException("Não encontrada Conta Contabil: " + conta);
		}
		Conta contaContabil = list.iterator().next();
		
		// Criando passivo
		Passivo passivo = null;
		if ( "CHEQUE".equals(tipo.trim()) ) {
			passivo = new Cheque();
		} else
		if ( "VISA".equals(tipo.trim()) ) {
			passivo = new Cartao();
			((Cartao)passivo).setOperadora(Operadora.VISA);
		} else
		if ( "MASTER".equals(tipo.trim()) ) {
			passivo = new Cartao();
			((Cartao)passivo).setOperadora(Operadora.MASTERCARD);
		} else
		if ( "ELECTR".equals(tipo.trim()) ) {
			passivo = new Cartao();
			((Cartao)passivo).setOperadora(Operadora.VISA_ELECTRON);
		} else
		if ( "DEBITO".equals(tipo.trim()) ) {
			passivo = new DebitoCC();
		} else
		if ( "SAQUE".equals(tipo.trim()) ) {
			passivo = new Saque();
		}
		
		if ( passivo == null ) {
			throw new RuntimeException("Não encontrado tipo = " + tipo);
		}
			
		// Atribuindo Valores do Passivo
		passivo.setDataMovimento(diaRef + "/" + mesRef + "/" + anoRef);
		passivo.setHistorico(historico);
		passivo.setConta(contaContabil);
		passivo.setContaCorrente(contaCorrente);
		passivo.setTotalParcelas(1);
		return passivo;
	}

	private BigDecimal converterParaBigDecimal(String valor) {
		BigDecimal vlr = null;
		try {
			vlr = new BigDecimal(valor);
		} catch (NumberFormatException e) {
			throw new RuntimeException("Numero " + valor + ", não compreendido para parser.");
		}
		return vlr;
	}
	
	public String cleanHistoricoFromParcela(String historico) {
		Pattern p = Pattern.compile("-{0,1}\\s[\\d]{1,2}/[\\d]{1,2}");
		Matcher m = p.matcher(historico);
		historico = m.replaceAll("");
		return historico;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String file = "c:/Documents and Settings/Ualter/My Documents/Planilhas/Carga Scorecard.xls";
		Importador importingExcel = new Importador(file);
		throw new RuntimeException("Proteção para não rodar sem querer");
		//importingExcel.createFlatFile();
		//importingExcel.doImport();
	}

}
