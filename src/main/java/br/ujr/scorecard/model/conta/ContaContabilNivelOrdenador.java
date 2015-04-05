package br.ujr.scorecard.model.conta;


public class ContaContabilNivelOrdenador  {

	public static int compare(String o1, String o2) {
		String   nivel1   = o1;
		String   nivel2   = o2;
		String[] niveis01 = nivel1.split("\\."); 
		String[] niveis02 = nivel2.split("\\.");
		
		int index = 0;
		int result;
		while (true) {
			result = compareNiveis(niveis01[index],niveis02[index]);
			if ( result == 0 ) {
				index++;
				if ( index >= niveis01.length  || index >= niveis02.length ) {
					if ( nivel1.length() > nivel2.length() ) {
						result =  1;
					} else
					if ( nivel1.length() < nivel2.length() ) {
						result = -1;
					} else {
						result = 0;
					}
					break;
				}
			} else {
				break;
			}
		}
		return result;
	}
	
	private static int compareNiveis(String n1, String n2) {
		int fn1    = Integer.parseInt(n1);
		int fn2    = Integer.parseInt(n2);
		int result = 0;
		
		if ( fn1 > fn2 ) {
			result = 1;
		} else
		if ( fn1 < fn2 ) {
			result = -1;
		}
		return result;
	}

}
