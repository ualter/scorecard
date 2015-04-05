package br.ujr.components.gui.tabela;


public class UjrTabelaParcelaModelCheque extends UjrTabelaParcelaModel {
	
	private static final long serialVersionUID = -7339143024591576974L;

	public UjrTabelaParcelaModelCheque() {
		super(null,
			  new String[]{"Vencimento","Valor","Cheque","C/C","ID"});
	}
	
	public UjrTabelaParcelaModelCheque(Object data[][]) {
		super(data,
			  new String[]{"Vencimento","Valor","Cheque","C/C","ID"});
	}

}
