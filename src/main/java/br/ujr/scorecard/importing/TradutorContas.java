package br.ujr.scorecard.importing;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Interpreta os históricos existentes no legado de planilhas Excel
 * relacionando a Contas do Plano de Contas
 * @author Ualter
 */
public class TradutorContas {
	
	private HashMap<String,String> mapConta      = new HashMap<String,String>();
	private HashMap<String,String> mapParcela    = new HashMap<String,String>();
	private HashMap<String,String> mapChave      = new HashMap<String,String>();
	private FileWriter             outHistoricos = null;
	
	public TradutorContas() {
		this.load();
	}

	private void startFile() {
		try {
			this.outHistoricos = new FileWriter("c:/temp/HistoricosNaoEncontrados_" + new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss").format(new Date()) + ".txt");
		} catch (IOException e) {
			throw new RuntimeException(e.toString(),e);
		}
	}
	
	public void load() {
		try {
			String       file     = "c:/Documents and Settings/Ualter/My Documents/Planilhas/Carga Scorecard.xls";
			HSSFWorkbook workBook = new HSSFWorkbook( new FileInputStream(file) );
			HSSFSheet    sheet    = workBook.getSheetAt(1);
			for(Iterator iteratorRow = sheet.rowIterator(); iteratorRow.hasNext();) {
				HSSFRow row = (HSSFRow) iteratorRow.next();
				
				Iterator iteratorCell  = row.cellIterator();
				HSSFCell cellHistorico = iteratorCell.hasNext() ? (HSSFCell)iteratorCell.next() : null;
				HSSFCell cellConta     = iteratorCell.hasNext() ? (HSSFCell)iteratorCell.next() : null;
				HSSFCell cellParcela   = iteratorCell.hasNext() ? (HSSFCell)iteratorCell.next() : null;
				HSSFCell cellChave     = iteratorCell.hasNext() ? (HSSFCell)iteratorCell.next() : null;
				
				if ( cellHistorico != null && cellConta != null ) {
					String historico = cellHistorico.toString();
					String conta     = cellConta.toString();
					String parcela   = cellParcela != null ? cellParcela.toString() : null;
					String chave     = cellChave   != null ? cellChave.toString() : null;  
					
					mapConta.put(historico.trim().toUpperCase(),conta);
					if ( parcela != null ) {
						mapParcela.put(historico.trim().toUpperCase(), parcela);
						//System.out.println(historico.replaceAll(parcela,"").replaceAll("-","") + " - " + conta + " - " + parcela);
					}
					
					if ( chave != null ) {
						mapChave.put(historico.trim().toUpperCase(), chave);
						//System.out.println(historico.replaceAll(parcela,"").replaceAll("-","") + " - " + conta + " - " + parcela);
					}
				}
			}
			
			if ( this.outHistoricos != null ) {
				this.outHistoricos.flush();
				this.outHistoricos.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("[ERROR " + e.toString() + "] - TradutorContas.load");
		}
		
	}
	
	public String getConta(String historico) {
		String result = this.mapConta.get(historico.trim().toUpperCase());
		if (result == null && historico != null && !historico.trim().equals(""))this.writeMissingHistorico(historico);
		return result;
	}

	private void writeMissingHistorico(String historico) {
		if ( this.outHistoricos == null ) {
			this.startFile();
		}
		try {
			this.outHistoricos.write(historico.trim().toUpperCase());
			this.outHistoricos.write("\n");
			this.outHistoricos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e.toString(),e);
		}
	}
	public String getParcela(String historico) {
		String result = this.mapParcela.get(historico.trim().toUpperCase());
		if (result == null && historico != null && !historico.trim().equals("")) return "1/1";
		return result;
	}
	
	public String getChave(String historico) {
		String result = this.mapChave.get(historico.trim().toUpperCase());
		return result == null ? "#" : result;
	}

	public static void main(String[] args) {
		new TradutorContas();
	}
}
