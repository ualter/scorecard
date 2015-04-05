package br.ujr.scorecard.importing;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.util.Util;

/**
 * Reinicia todo o plano de contas, apagando o atual e carregado o que foi desenhado 
 * na planilha c:\Documents and Settings\Ualter\My Documents\Planilhas\Plano de Contas.xls 
 * @author Ualter
 */
public class ImportadorPlanoContas {
	
	ScorecardManager manager;
	
	public ImportadorPlanoContas() {
		this.manager = (ScorecardManager)Util.getBean("scorecardManager");
	}
	
	public void importar() {
		this.limparPlanoContas();
		this.importarPlanoContas();
	}
	
	private void limparPlanoContas() {
		Session session = manager.getHibernateSession();
		Transaction tx = session.beginTransaction();
		
		Query qAtivoDelete = session.createQuery("Delete From Ativo");
		Query qPassivoDelete = session.createQuery("Delete From Passivo");
		Query qContaDelete = session.createQuery("Delete From Conta");
		
		System.out.println(qAtivoDelete.executeUpdate() + " Ativo(s) deleted.");
		System.out.println(qPassivoDelete.executeUpdate() + " Passivo(s) deleted.");
		System.out.println(qContaDelete.executeUpdate() + " Conta(s) deleted.");
		
		tx.commit();
		session.close();
		
		
	}

	private void importarPlanoContas() {
		try {
			HSSFWorkbook workBook = new HSSFWorkbook( new FileInputStream("c:/Documents and Settings/Ualter/My Documents/Planilhas/Plano de Contas.xls") );
			HSSFSheet    sheet    = workBook.getSheetAt(0);
			
			StringBuffer modulo    = new StringBuffer();
			String       lastNivel = null;
			
			for(Iterator iteratorRow = sheet.rowIterator(); iteratorRow.hasNext();) {
				HSSFRow row = (HSSFRow)iteratorRow.next();
				
				HSSFCell cell     = (HSSFCell)row.getCell((short)0);
				String   contaStr = cell.getRichStringCellValue().getString();
				
				String nivel = contaStr.substring(0,contaStr.indexOf("."));
				if ( lastNivel == null ) {
					lastNivel = nivel;
					modulo = new StringBuffer();
				}
				
				if ( !nivel.equals(lastNivel) ) {
					// Gravar Aqui
					this.gravarContas(modulo.toString());
					modulo = new StringBuffer();
					lastNivel = nivel;
				}
				modulo.append(contaStr).append("\n");
			}
			
			// Gravar Aqui
			this.gravarContas(modulo.toString());
			
		} catch (Exception e) {
			throw new RuntimeException("[ERROR]: " + e.getMessage(),e);
		}
	}

	private void gravarContas(String string) {
		StringTokenizer stkConta = new StringTokenizer(string,"\n");
		TreeMap<String,String> map = new TreeMap<String,String>();
		while (stkConta.hasMoreElements()) {
			StringTokenizer stkNivelDescricao = new StringTokenizer((String)stkConta.nextElement()," - ");
			String nivel     = (String)stkNivelDescricao.nextElement();
			String descricao = (String)stkNivelDescricao.nextElement();
			map.put(nivel,descricao);
		}
		
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key     = iterator.next();
			String descr   = map.get(key);
			String pai     = key.substring(0,key.lastIndexOf("."));
			boolean isRaiz = key.substring(key.length()-1,key.length()).equals("0") ? true : false;
			
			System.out.println("\nPai: " + pai);
			System.out.println(key + " - " + descr  + (isRaiz ? " - RAIZ" : "") );
			
			if ( isRaiz ) {
				Conta conta = new Conta(key,descr);
				this.manager.saveConta(conta);
				
				System.out.print("Conta Raiz: " + conta.getNivel() + "-" + conta.getDescricao());
				System.out.println(" - SAVED");
			} else {
				Conta contaFilho = new Conta(key,descr);
				if ( pai.indexOf(".") == -1 ) pai += ".0";
				Conta contaPai   = this.manager.getContasPorNivel(pai).iterator().next();
				contaPai.addContaFilho(contaFilho);
				this.manager.saveConta(contaPai);
				
				System.out.print("Conta: " + contaFilho.getNivel() + "-" + contaFilho.getDescricao());
				System.out.print(" - Pai: " + contaPai.getNivel() + "-" + contaPai.getDescricao());
				System.out.println(" - SAVED");
			}
		}
	}

	public static void main(String[] args) {
		ImportadorPlanoContas importadorPlanoContas = new ImportadorPlanoContas();
		importadorPlanoContas.importar();
		System.out.println("\nOk!\n");
	}

}
