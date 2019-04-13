package br.ujr.scorecard.analisador.extrato.contacorrente.bansabadell;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import br.ujr.scorecard.util.Util;
import br.ujr.scorecard.util.properties.ScorecardPropertiesUtil;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;


public class AnalisadorExtratoCCExcelBanSabadell extends AbstractAnalisadorExtratoBanSabadell {

	private static Logger logger = Logger.getLogger(AnalisadorExtratoCCExcelBanSabadell.class);
	private static final DecimalFormat    NUMBER_FORMAT = new DecimalFormat("0.00");
	private static final SimpleDateFormat DATE_TIME_FORMATTER6 = new SimpleDateFormat("ddMMyyyy");	
	
	public AnalisadorExtratoCCExcelBanSabadell(long referencia) {
		this.cc         = scorecardManager.getContaCorrentePorId( Integer.parseInt(ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.IdCCBanSabadell)) );
		this.passivos   = scorecardManager.getPassivosPorReferencia(cc, referencia);
		this.ativos     = scorecardManager.getAtivosPorReferencia(cc, referencia);
		this.referencia = referencia;
		NUMBER_FORMAT.applyLocalizedPattern("0,00");
	}
	
	/**
	 * Formato Esperado: Excel
	 * @return
	 */
	public String analisarExtrato() {
		String pathExcelFile = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.BancSabadellExcelPath);
		if ( pathExcelFile == null ) {
			throw new RuntimeException("Não foi encontrado a configuração de PATH para os arquivos N43, chave=" + ScorecardPropertyKeys.BancSabadellExcelPath);
		}
		
		Path       path         = Paths.get(pathExcelFile);
		List<Path> listFilesExcel = null;
		try {
		   listFilesExcel = Files.walk(path)
                               .filter(s -> s.toString().endsWith(".xls"))
                               .map(Path::toAbsolutePath)
                               .sorted()
                               .collect(Collectors.toList());
		} catch (NoSuchFileException e) {
			String msg = "Não foi encontrado nenhum arquivo Excel no diretório: " + pathExcelFile;
			logger.warn(msg);
			return msg;
		} catch(IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(),e);
		}
		if ( listFilesExcel.size() == 0 ) {
			String msg = "Não foi encontrado nenhum arquivo Excel no diretório: " + pathExcelFile; 
			logger.warn(msg);
			return msg;
		}
		
		MappingAnalisadorBanSabadell mappingAnalisadorBanSabadell = new MappingAnalisadorBanSabadell();
		this.extratoContaCorrente   = new ArrayList<LinhaExtratoContaCorrenteBanSabadell>();
		
		Workbook wb = null;
		try {
			FileInputStream excelFile = new FileInputStream(listFilesExcel.get(0).toFile());
			wb = new HSSFWorkbook(excelFile);
			Sheet sheet = wb.getSheetAt(0);
			
			AtomicInteger indexRow = new AtomicInteger();
			sheet.forEach(excelRow -> {
				LinhaExtratoContaCorrenteBanSabadell linhaExtratoContaCorrenteBanSabadell = new LinhaExtratoContaCorrenteBanSabadell();
				
				if ( indexRow.incrementAndGet() > 6 ) {
				
					AtomicInteger indexCell = new AtomicInteger();
					excelRow.forEach(excelCell -> {
						
						indexCell.incrementAndGet();
						// FECHA OPER
						if ( indexCell.intValue() == 1 ) {
						} else
						// CONCEPTO	
						if ( indexCell.intValue() == 2 ) {
							linhaExtratoContaCorrenteBanSabadell.setHistorico(excelCell.getStringCellValue());
						} else
						// FECHA VALOR	
						if ( indexCell.intValue() == 3 ) {
							Date dt = excelCell.getDateCellValue();
							linhaExtratoContaCorrenteBanSabadell.setDataOperacao( DATE_TIME_FORMATTER5.format(  dt.toInstant().atOffset(OffsetDateTime.now().getOffset()).toLocalDateTime() ) );
							//linhaExtratoContaCorrenteBanSabadell.setDataOperacao( DATE_TIME_FORMATTER6.format(  dt ) );
						} else
						// IMPORTE	
						if ( indexCell.intValue() == 4 ) {
							double vlr = excelCell.getNumericCellValue();
							boolean negative = vlr > 1 ? false : true;
							if ( negative ) {
								vlr *= -1;
							}
							linhaExtratoContaCorrenteBanSabadell.setValor(
									NUMBER_FORMAT.format(vlr).replaceAll(",", ""),
									negative ? "1" : "0");
						} else
						// SALDO	
						if ( indexCell.intValue() == 5 ) {
						} else
						// REFERENCIA1	
						if ( indexCell.intValue() == 6 ) {
						} else
						// REFERENCIA2	
						if ( indexCell.intValue() == 7 ) {
						}
					});
					
					mappingAnalisadorBanSabadell.checkMappingDescricaoVsContaContabil(linhaExtratoContaCorrenteBanSabadell.getHistorico(),linhaExtratoContaCorrenteBanSabadell);
					this.extratoContaCorrente.add(linhaExtratoContaCorrenteBanSabadell);
				}
			});
			
		} catch (IOException e) {
			logger.error(e);
			throw new RuntimeException(e.getMessage(),e);
		} finally {
			if ( wb != null ) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	public static void main(String[] args) {
		AnalisadorExtratoCCExcelBanSabadell analisador = new AnalisadorExtratoCCExcelBanSabadell(201904);
		String status = analisador.analisarExtrato();
		if (status == null) {
			List<LinhaExtratoContaCorrenteBanSabadell> listaNaoEncontrados = analisador.getLancamentosNaoExistentesBaseDados();
			for (LinhaExtratoContaCorrenteBanSabadell linha : listaNaoEncontrados) {
				System.out.println(linha);
				
				Object row[] = new Object[] {
						Util.formatDate(linha.getDataOperacaoAsDate()), 
						linha.getHistorico(), 
						linha.getValor(), 
						(linha.getTipo() != null ? linha.getTipo() : ""),
						(linha.getContaContabil() != null ? linha.getContaContabil() : ""),
						linha
				};
			}
		} else {
			System.out.println("Problemas na leitura do arquivo:\n\n" + status);
		}
		
	}

}
