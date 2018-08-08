package br.ujr.scorecard.util;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.JComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Util {

	private static Locale                         locale     = new Locale("pt","BR");
	private	static DecimalFormatSymbols           dfs        = new DecimalFormatSymbols(locale);
	private static DecimalFormat                  df         = new DecimalFormat();
	private static SimpleDateFormat               sdf        = new SimpleDateFormat("dd/MM/yyyy");
	private static ClassPathXmlApplicationContext context;
	private static Util                           me         = new Util();
	private static File                           imgToolTip = null;
	
	 private static Logger logger = Logger.getLogger(Util.class);
	
	private Util() {
	}
	
	public static Util getInstance() {
		return me;
	}
	
	static 
	{
		df.setDecimalFormatSymbols(dfs);
		df.applyLocalizedPattern("###.###.###.##0,00");
		context = new ClassPathXmlApplicationContext("spring.beans.xml");
	}
	
	public static Date today()
	{
		Date dt = new Date(Calendar.getInstance().getTimeInMillis());
		return dt;
	}
	
	public static String today(String pattern) { 
		Date dt = today();
		sdf.applyPattern(pattern);
		return sdf.format(dt);
	}
	
	/**
	 * Converte a Referencia no formato yyyyMM para Data dd/MM/yyyy
	 * @param ref
	 * @return
	 */
	public static java.util.Date referenciaToDate(long ref) {
		int ano  = Integer.parseInt(("" + ref).substring(0, 4));
		int mes  = Integer.parseInt(("" + ref).substring(4));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,ano);
		calendar.set(Calendar.MONTH,mes - 1);
		
		return calendar.getTime();
	}
	
	public static java.util.Date parseDate(String date) {
		int day   = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(3,5));
		int year  = Integer.parseInt(date.substring(6,10));
		return parseDate(day,month,year);
	}
	public static java.util.Date parseDateEightDigits(String date) {
		int day   = Integer.parseInt(date.substring(0,2));
		int month = Integer.parseInt(date.substring(3,5));
		int year  = Integer.parseInt(date.substring(8,10));
		return parseDate(day,month,year);
	}
	public static String formatNumber(String pattern, long number) {
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(number);
	}
	public static String formatReferencia(long ref) {
		return Util.formatReferencia(ref,true,false);
	}
	public static String formatReferencia(long ref, boolean numeric, boolean descricaoMesCompleto) {
		int ano  = Integer.parseInt(("" + ref).substring(0, 4));
		int mes  = Integer.parseInt(("" + ref).substring(4));
		DecimalFormat df4 = new DecimalFormat("0000");
		if ( numeric ) {
			DecimalFormat df2 = new DecimalFormat("00");
			return df2.format(mes) + "/" + df4.format(ano);
		} else {
			String mesDescr = Util.getMesDescricao(mes - 1);
			if ( descricaoMesCompleto ) {
				return mesDescr + "/" + df4.format(ano); 
			} else {
				return mesDescr.substring(0,3) + "/" + df4.format(ano);
			}
		}
		
	}
	public static java.util.Date parseDate(int day, int month, int year) {
		try {
			DecimalFormat df2 = new DecimalFormat("00");
			DecimalFormat df4 = new DecimalFormat("0000");
			sdf.applyPattern("dd/MM/yyyy");
			return sdf.parse(df2.format(day) + "/" + df2.format(month) + "/"+ df4.format(year));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Retorna o Mes e ano contido na data no formato YYYYMM
	 * @param date
	 * @return
	 */
	public static long extrairReferencia(java.util.Date date) {
		StringBuffer sb = new StringBuffer(formatDate(date));
		return Long.parseLong(sb.substring(6, 10) + sb.substring(3, 5));
	}
	public static int extrairMesReferencia(java.util.Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		StringBuffer sb = new StringBuffer(sdf.format(date));
		return Integer.parseInt(sb.toString());
	}
	public static int extrairAnoReferencia(java.util.Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		StringBuffer sb = new StringBuffer(sdf.format(date));
		return Integer.parseInt(sb.toString());
	}
	
	public static String formatDate(java.util.Date date) {
		return sdf.format(date);
	}
	public static String formatCurrency(BigDecimal bd) {
		return Util.formatCurrency(bd,true);
	}
	public static String formatCurrency(BigDecimal bd, boolean semMoeda) {
		return (!semMoeda ? "R$ " : "") + df.format(bd);
	}
	public static String formatCurrency(BigDecimal bd, int tam, boolean sinalMoeda, int spaceEnd) {
		String valor = StringUtils.leftPad(df.format(bd),tam);
		valor        = (sinalMoeda ? "R$" : "") + valor;
		for(int i = 0; i < spaceEnd; i++) {
			valor += " ";
		}
		return valor;
	}
	/**
	 * Retira os separados de grupos de milhar (.)
	 * Substitui o separador de casa decimal de (,) para (.)
	 * @param number
	 * @return
	 */
	public static String cleanNumber(String number) {
		return (number.replaceAll("\\.", "").replaceAll(",", ".")).trim();
	}
	public static BigDecimal parseCurrency(String valor) {
		valor = valor.replaceAll("\\.", "").replaceAll(",", ".");
		try {
			return new BigDecimal(valor.trim());
		} catch (Throwable e) {
			System.out.println(e.getMessage() + "- valor:" + valor);
			Log.error(e.getMessage() + "- valor:" + valor);
			//throw new RuntimeException(e);
			return new BigDecimal(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T getBean(String bean) {
		return (T)context.getBean(bean);
	}
	
	public static void setToolTip(Component comp,JComponent cmp, String msg) {
		Util.setToolTip(comp,cmp, msg, null);
	}
	public static File getImageToolTip() {
		if ( imgToolTip == null ) {
			String pathTemp = System.getProperty("java.io.tmpdir");
			byte[] bytes = JarResources.getInstance().getResource("images/tooltip.png");
			try {
				FileOutputStream out = new FileOutputStream(pathTemp + "tooltip.png");
				out.write(bytes);	
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			imgToolTip = new File(pathTemp + "tooltip.png"); 
		}
		return imgToolTip;
	}
	public static void setToolTip(Component comp, JComponent targetComponent, String msg, String image) {
		if ( StringUtils.isBlank(image) ) {
			image = "file:" + getImageToolTip().getAbsolutePath();
		}
		StringBuffer toolTip = new StringBuffer();
		toolTip.append("<html>");
		toolTip.append("<table style='border:solid 1px black;'>");
		toolTip.append(" <tr>");
		toolTip.append("   <td align='center' valign='middle'>");
		toolTip.append("     <img src=").append(image).append(">");
		toolTip.append("   </td>");
		toolTip.append("   <td align='center' valign='middle'>");
		toolTip.append("       <font style='font-family:Verdana;font-size:8px;color:black'><b>");
		toolTip.append(msg);
		toolTip.append("       </b></font>");
		toolTip.append("   </td>");
		toolTip.append(" </tr>");
		toolTip.append("</html>");
		targetComponent.setToolTipText(toolTip.toString());
	}
	
	public static BufferedImage captureScreenShot(int x, int y, int width, int height) {
		try {
	        Robot robot = new Robot();
	        Rectangle     area          = new Rectangle(x, y, width, height);
	        BufferedImage bufferedImage = robot.createScreenCapture(area);
	    
	        // Capture the whole screen
	        //area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	        
	        return bufferedImage;
	    } catch (AWTException e) {
	    	throw new RuntimeException(e);
	    }
	}
	
	/*public static void writeImage(BufferedImage displayImage, String name) {
		try {
			encodeImage(displayImage, createOutputStream(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
 
	public static FileOutputStream createOutputStream(String outFile) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	
	/*public static void encodeImage(BufferedImage img, FileOutputStream out) {
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
		param.setQuality(1.0f, false);
		encoder.setJPEGEncodeParam(param);
		try {
			encoder.encode(img);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}*/
	
	public static String getMesDescricao(String mes) {
		return getMesDescricao(Integer.parseInt(mes));
	}
	public static String getMesDescricao(int mes) {
		String[] meses = new String[] {
				"JANEIRO",	
				"FEVEREIRO",
				"MARÇO",
				"ABRIL",
				"MAIO",
				"JUNHO",
				"JULHO",
				"AGOSTO",
				"SETEMBRO",
				"OUTUBRO",
				"NOVEMBRO",
				"DEZEMBRO",
		};
		return meses[mes];
	}
	
	/**
	 * Adiciona ou Subtrai meses no valor de Referencia yyyyMM
	 * @param referencia
	 * @param amount
	 * @return
	 */
	public static long computeReferencia(long referencia, int amount) {
		int ano  = Integer.parseInt(("" + referencia).substring(0, 4));
		int mes  = Integer.parseInt(("" + referencia).substring(4));
		
		if ( amount > 0 ) {
			if ( mes == 12 ) {
				mes = 1;
				ano++;
			} else {
				mes++;
			}
		} else {
			if ( mes == 1 ) {
				mes = 12;
				ano--;
			} else {
				mes--;
			}
		}
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		return Long.parseLong( df4.format(ano) + df2.format(mes));
	}
	
	public static Image loadImage(Component component, String imageName) {
		if ( getInstance().getClass().getResource("/images/" + imageName) == null ) {
			String msg = "Image " + "/images/" + imageName + ", nao encontrada!!!";
			logger.warn(msg);
			System.out.println(msg);
		}
		Image image = component.getToolkit().getImage(getInstance().getClass().getResource("/images/" + imageName)); 
		return image;
	}
	
	public static int calcDateDifference(int calendarItem, java.util.Date d1, java.util.Date d2) {
	 int x = 1000;
	 if (calendarItem == Calendar.DAY_OF_MONTH) {
		 x = x * 60 * 60 * 24;
	 } else
	 if (calendarItem == Calendar.HOUR_OF_DAY) {
	    x = x * 60 * 60;
	 } else
	 if (calendarItem == Calendar.MINUTE) {
	    x = x * 60;
	 } else {
	    return 1;
	 }
	 return (int) ((d2.getTime() - d1.getTime()) / (x));
	}
	
	public static java.util.Date buildDateFromReferencia(java.util.Date date) {
		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH,day);
		return cal.getTime();
	}
	
	/**
	 * Calcula se o dia da data informada esta entre os dias inicial e final informados
	 * Sendo que o dia inicial é considerado (inclusive) e o dia final não (exclusive)
	 * Exemplo: dia inicio = 1, dia fim = 20, dia = 1, resultado = true
	 * Exemplo: dia inicio = 1, dia fim = 20, dia = 20, resultado = false
	 * Exemplo: dia inicio = 1, dia fim = 20, dia = 19, resultado = true
	 * @param dt
	 * @param beginDay
	 * @param endDay
	 * @return
	 */
	public static boolean isDateBetweenDays(java.util.Date dt, int beginDay, int endDay) {
		boolean result = false;
		Calendar date             = Calendar.getInstance();
		Calendar dateLimiteMinimo = Calendar.getInstance();
		Calendar dateLimiteMaximo = Calendar.getInstance();
		
		date.setTime(dt);
		dateLimiteMinimo.setTime(dt);
		dateLimiteMaximo.setTime(dt);
		
		dateLimiteMinimo.set(Calendar.DAY_OF_MONTH, beginDay);
		dateLimiteMaximo.set(Calendar.DAY_OF_MONTH, endDay);
		
		int compareMinimo = date.compareTo(dateLimiteMinimo);
		int compareMaximo = date.compareTo(dateLimiteMaximo);
		
		// Data deve estar entre o dia inicial e o dia fim, inclusive o dia inicio e exclusive o dia fim
		// Exemplo: dia inicio = 1, dia fim = 20, dia = 1, resultado = true
		// Exemplo: dia inicio = 1, dia fim = 20, dia = 20, resultado = false
		// Exemplo: dia inicio = 1, dia fim = 20, dia = 19, resultado = true
		if ( (compareMinimo == 1 || compareMinimo == 0) && compareMaximo == -1 ) {
			result = true;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println( isDateBetweenDays(Util.parseDate("20/10/2010"), 1, 20));
	}
	
	public static String getClipBoarContent() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if ( hasTransferableText ) {
          try {
            result = (String)contents.getTransferData(DataFlavor.stringFlavor);
          } catch (UnsupportedFlavorException ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
          } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
          }
        }
        return result;
	}
	
}
