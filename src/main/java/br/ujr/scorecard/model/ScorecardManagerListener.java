package br.ujr.scorecard.model;

import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.transferencia.Transferencia;

public interface ScorecardManagerListener {
	
	public void actionPassivo(Passivo passivo);
	public void actionOrcamento(Orcamento orcamento);
	public void actionAtivo(Ativo ativo);
	public void actionTransferencia(Transferencia transferencia);
	public void actionConsistirSaldosAnteriores(SaldoProcessadoEvent event);

}
