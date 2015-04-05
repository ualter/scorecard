package br.ujr.scorecard.gui.view.screen.passivo;

import java.util.Date;

import javax.swing.JFrame;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;
import br.ujr.scorecard.util.Util;

/**
 * @author ualter.junior
 */
public class VisaElectronFrame extends AbstractCartaoFrame {

	public VisaElectronFrame(JFrame owner, ContaCorrente contaCorrente,Date periodoDataIni) {
		super(owner, contaCorrente, periodoDataIni);
	}
	public VisaElectronFrame(JFrame owner, ContaCorrente contaCorrente, Date periodoDataIni, Cartao cartao) {
		super(owner, contaCorrente, periodoDataIni, cartao);
	}

	@Override
	public Operadora getOperadora() {
		return Cartao.Operadora.VISA_ELECTRON;
	}
	@Override
	public String getTituloNomeOperadora() {
		return "VISA ELECTRON";
	}
	@Override
	public String getFirstDataVencimento() {
		return Util.formatDate(this.txtDataMovimento.getDate())
				
				;
	}

}
