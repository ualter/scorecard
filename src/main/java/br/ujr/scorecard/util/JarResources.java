package br.ujr.scorecard.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class JarResources {

	private static JarResources me; 
		
	public  boolean debugOn = false;
	private Hashtable<String,Integer> htSizes = new Hashtable<String,Integer>();
	private Hashtable<String,byte[]> htJarContents = new Hashtable<String,byte[]>();
	private String jarFileName;
	
	private static Logger logger = Logger.getLogger(JarResources.class);
	
	public synchronized static JarResources getInstance() {
		String pathFolder = ScorecardProperties.getProperty(ScorecardPropertyKeys.PathInstallation);
		String pathFile   = null;
		File folder = new File(pathFolder);
		for (File f : folder.listFiles()) {
			if ( f.getName().indexOf(".jar") != -1 ) {
				pathFile = f.getAbsolutePath();
			}
		}
		
		File file = new File(pathFile);
		if ( !file.exists() ) {
			RuntimeException runtimeException = new RuntimeException("Caminho para arquivo JAR não encontrado");
			logger.error(runtimeException);
			throw runtimeException; 
		}
		
		if (me == null) {
			me = new JarResources(pathFile);
		}
		return me;
	}

	public JarResources(String jarFileName) {
		this.jarFileName = jarFileName;
		init();
	}

	private void init() {
		try {
			ZipFile zf = new ZipFile(jarFileName);
			Enumeration e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				htSizes.put(ze.getName(), new Integer((int) ze.getSize()));
			}
			zf.close();
			
			FileInputStream     fis = new FileInputStream(jarFileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream      zis = new ZipInputStream(bis);
			
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					continue;
				}
				
				if (debugOn) {
					System.out.println("ze.getName()=" + ze.getName() + ","
							+ "getSize()=" + ze.getSize());
				}
				
				int size = (int) ze.getSize();
				// -1 means unknown size.
				if (size == -1) {
					size = ((Integer) htSizes.get(ze.getName())).intValue();
				}
				
				byte[] b     = new byte[(int) size];
				int    rb    = 0;
				int    chunk = 0;
				while (((int) size - rb) > 0) {
					chunk = zis.read(b, rb, (int) size - rb);
					if (chunk == -1) {
						break;
					}
					rb += chunk;
				}
				// add to internal resource hashtable
				htJarContents.put(ze.getName(), b);
				if (debugOn) {
					System.out.println(ze.getName() + "  rb=" + rb + ",size="
							+ size + ",csize=" + ze.getCompressedSize());
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("done.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getResource(String name) {
		return this.htJarContents.get(name);
	}
	
	public Integer getResourceSize(String name) {
		return this.htSizes.get(name);
	}

}
