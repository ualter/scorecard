package br.ujr.scorecard.model.reports;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.reports.resumomensal.ResumoMensal;
import br.ujr.scorecard.model.reports.totalcontacontabil.TotalContaContabil;
import br.ujr.scorecard.util.JarResources;
import br.ujr.scorecard.util.properties.ScorecardPropertiesUtil;
import br.ujr.scorecard.util.properties.ScorecardPropertyKeys;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ReportManager {
	
	private static final String PDF_READER;
	private static final String PDF_REPORT_PATH;
	private static final String PDF_CLEAN;
	private static final String PDF_READER_PARAMETERS;
	private ArrayList<ReportManagerListener> listeners = new ArrayList<ReportManagerListener>();
	
	private static Logger logger = Logger.getLogger(ReportManager.class); 
	
	static {
		PDF_READER = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.PdfReader);
		PDF_READER_PARAMETERS = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.PdfReaderParameters);
		PDF_REPORT_PATH = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.PdfReportPath);
		PDF_CLEAN = ScorecardPropertiesUtil.getProperty(ScorecardPropertyKeys.PdfClean);
	}
	
	public ReportManager() {
	}
	
	public void printTotalContaContabil(ContaCorrente contaCorrente, Date periodoInicial, Date periodoFinal, String[] niveis, int tipo, int tipoGrafico, Boolean efetivado) {
		Object[] criteria = new Object[7];
		criteria[0] = contaCorrente;
		criteria[1] = periodoInicial;
		criteria[2] = periodoFinal;
		criteria[3] = niveis;
		criteria[4] = new Integer(tipo);
		criteria[5] = new Integer(tipoGrafico);
		criteria[6] = efetivado;
		
		TotalContaContabil report = new TotalContaContabil();
		report.setListeners(this.listeners);
		report.setCriteria(criteria);
		this.print(report);
	}
	
	public void printResumoMensal(ContaCorrente contaCorrente, Date periodoInicial, Date periodoFinal) {
		Object[] criteria = new Object[3];
		criteria[0] = contaCorrente;
		criteria[1] = periodoInicial;
		criteria[2] = periodoFinal;
		
		ResumoMensal report = new ResumoMensal();
		report.setListeners(this.listeners);
		report.setCriteria(criteria);
		this.print(report);
	}
	
	private void print(Report report) {
		for(ReportManagerListener listener : this.listeners) {
			listener.reportStarted();
		}
		try {
			JarResources jar = JarResources.getInstance();
			Collection   collection  = report.execute();
			
			byte[]       byteFile    = jar.getResource("reports/" + report.getJasperFileName());
			if ( byteFile == null ) {
				RuntimeException e = new RuntimeException("Arquivo " + "reports/" + report.getJasperFileName() + " não encontrado dentro do JAR.");
				logger.error(jar);
				throw e;
			}
			InputStream  jasperFile    = new ByteArrayInputStream(byteFile);
			InputStream  in            = new ByteArrayInputStream(jar.getResource("images/Logo-Scorecard-01.gif"));
			String       subReportPath = "reports/";
			
			/*
			InputStream  jasperFile    = new FileInputStream("C://eclipse-workspace//Scorecard//jasper-reports//TotalContabil//" + report.getJasperFileName());
			InputStream  in            = new FileInputStream("C://eclipse-workspace//Scorecard//src//main//resources-bin//images//Logo-Scorecard-01.gif");
			String       subReportPath = "C://eclipse-workspace//Scorecard//jasper-reports//TotalContabil//";
			*/
			
			String       reportFile    = report.getTargetFile(PDF_REPORT_PATH);
			HashMap      parameters    = report.getParameters();
			parameters.put("SUBREPORT_DIR", subReportPath);
			parameters.put("IMAGE_LOGO", in);
			
			JasperPrint  jasperPrint  = JasperFillManager.fillReport(
					jasperFile, 
					parameters, 
					new JRBeanCollectionDataSource(collection));

			JasperExportManager.exportReportToPdfFile(jasperPrint, reportFile);
			for(ReportManagerListener listener : this.listeners) {
				listener.reportFinished();
			}
			openPDF(reportFile);
			if ( Boolean.parseBoolean(PDF_CLEAN) ) {
				cleanPDF(reportFile);
			}
		} catch (Throwable e) {
			for(ReportManagerListener listener : this.listeners) {
				listener.reportFinished();
			}
			logger.error(e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void cleanPDF(String pdf) {
		File file = new File(pdf);
		file.delete();
	}
	
	private void openPDF(String pdf) {
		try {
			// Get the parameters specifically for the PDF Reader
			String parametersPdfReader[] = PDF_READER_PARAMETERS.split(",");
			
			// Building the Array for the Executing Process with the PDF Reader
			List<String> executerParameters = new ArrayList<String>();
			executerParameters.add(PDF_READER);
			for(String s : parametersPdfReader) {
				executerParameters.add(s);
			}
			executerParameters.add(pdf);
			
			// Converting the List Parameter Execution Process to an Array
			String[] executerParametersArr = new String[executerParameters.size()];
			executerParameters.toArray(executerParametersArr);
			
			// Execute the Process with the PDF Reader
			Process process = Runtime.getRuntime().exec(executerParametersArr);
			process.waitFor();
		} catch (IllegalMonitorStateException stateExcpetion) {
			String msg = "IllegalMonitorStateException at process.wait() execution. Verificar quando puder!!!";
			System.out.println(msg);
			logger.warn(msg, stateExcpetion);
		} catch (Throwable e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
	}
	
	public void addListener(ReportManagerListener listener) {
		this.listeners.add(listener);
	}
	
/*	public static void main(String[] args) throws Exception {
		ReportManager reportManager = new ReportManager();
		ContaCorrente contaCorrente = ScorecardBusinessDelegate.getInstance().getContaCorrentePorId(57);
		String ini = "01/01/2008";
		String fim = "01/05/2008";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date   dIni = sdf.parse(ini);
		Date   dFim = sdf.parse(fim);
		reportManager.printResumoMensal(contaCorrente, dIni, dFim);
		System.out.println("fim!");
	}
*/
}
