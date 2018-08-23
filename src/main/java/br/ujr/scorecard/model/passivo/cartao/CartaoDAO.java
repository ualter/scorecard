package br.ujr.scorecard.model.passivo.cartao;

import java.util.Date;
import java.util.Set;

import br.ujr.scorecard.model.cc.ContaCorrente;

public interface CartaoDAO {
	
	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Cartao.Operadora enumOperadora, long referenciaInicial, long referenciaFinal);
	public Set<Cartao> getCartaoPorFiltro(long referenciaInicial, long referenciaFinal, Cartao cartao, Date dataMovimento);

}
