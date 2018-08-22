package br.ujr.scorecard.util;

public interface ScorecardPropertyKeys {
	
	public static String ArquivoFaturaMastercard = "arquivo.fatura.mastercard";
	public static String ArquivoFaturaVisa       = "arquivo.fatura.visa";
	public static String ValorDolar              = "valor.dolar";
	public static String PathInstallation        = "path.installation";
	public static String PdfReader               = "pdf.reader";
	public static String PdfReaderParameters     = "pdf.reader.parameters";
	public static String PdfReportPath           = "pdf.report.path";
	public static String PdfClean                = "pdf.clean";
	public static String ScorecardVersion        = "scorecard.version";
	public static String ArquivoExtratoCC_BB     = "arquivo.extrato.contacorrete.bancodobrasil";
	public static String ArquivoExtratoSantander = "arquivo.extrato.contacorrete.santander";
	public static String TabResumoGeral          = "tab.resumo.geral";
	
	public static String BancSabadellN43Path     = "banc.sabadell.n43.path";
	public static String BancSabadellN43Ext      = "banc.sabadell.n43.ext";
	
	public static String IdBanSabadell = "banc.sabadell.id";
	
	//TODO: Send this values to scorecard.properties
	public static int IdCCBancoBrasil  = 64;
	public static int IdCCSantander    = 66;
	public static int IdCCItau         = 67;
	public static int IdCCDeutsche     = 69;
	public static int IdSANTANDER      = 59;
	public static int IdITAU           = 60; 
	public static int IdDeutsche       = 62;
	
}
