package br.ujr.components.gui;

public class Commons {
	
	public static boolean isNumber(char keyChar) {
		return isNumber(new String(new char[] {keyChar}));
	}
	public static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}		
		return true;
	}

}
