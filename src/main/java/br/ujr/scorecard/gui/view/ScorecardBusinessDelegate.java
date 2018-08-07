package br.ujr.scorecard.gui.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import br.ujr.scorecard.model.ResumoPeriodo;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ScorecardManagerListener;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.ativo.saldoanterior.SaldoAnterior;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.observacao.Observacao;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.PassivoOrdenador;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.Util;

public class ScorecardBusinessDelegate implements ScorecardManager {
	
	private static ScorecardBusinessDelegate me = new ScorecardBusinessDelegate();
	private ScorecardManager manager;
	
	public static ScorecardBusinessDelegate getInstance() {
		return me;
	}
	
	public static void initialize() {
	}
	
	private ScorecardBusinessDelegate() {
		this.manager = (ScorecardManager)Util.getBean("scorecardManager");
	}

	public void deleteAtivo(Ativo ativo) {
		this.manager.deleteAtivo(ativo);
	}

	public void deleteConta(Conta conta) {
		this.manager.deleteConta(conta);
	}

	public void deleteObservacao(Observacao obs) {
		this.manager.deleteObservacao(obs);
	}

	public void deletePassivo(Passivo passivo) {
		this.manager.deletePassivo(passivo);
	}

	public Ativo getAtivoPorId(int id) {
		return this.manager.getAtivoPorId(id);
	}

	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, long referencia) {
		return this.manager.getAtivosPorReferencia(contaCorrente, referencia);
	}
	
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.manager.getAtivosPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	}
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal) {
		return this.manager.getAtivosPorReferencia(contaCorrente, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}
	
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, Class clazz,  long referenciaInicial, long referenciaFinal) {
		return this.manager.getAtivosPorReferencia(contaCorrente, clazz, referenciaInicial, referenciaFinal);
	}
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente,Class clazz, Date referenciaInicial, Date referenciaFinal) {
		return this.manager.getAtivosPorReferencia(contaCorrente, clazz, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}

	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Operadora enumOperadora, long referenciaInicial, long referenciaFinal) {
		return this.manager.getCartaoPorOperadora(contaCorrente, enumOperadora, referenciaInicial, referenciaFinal);
	}
	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Operadora enumOperadora, Date referenciaInicial, Date referenciaFinal) {
		return this.manager.getCartaoPorOperadora(contaCorrente, enumOperadora, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}

	public List<Conta> getContaPorDescricao(String descricao) {
		return this.manager.getContaPorDescricao(descricao);
	}

	public Conta getContaPorId(int id) {
		return this.manager.getContaPorId(id);
	}

	public String getContaProximoNivel() {
		return this.manager.getContaProximoNivel();
	}

	public String getContaProximoNivel(String crt) {
		return this.manager.getContaProximoNivel(crt);
	}

	public List<Conta> getContasPorNivel(String nivel) {
		return this.manager.getContasPorNivel(nivel);
	}

	public List<Observacao> getObservacao(String descricao) {
		return this.manager.getObservacao(descricao);
	}

	public Observacao getObservacao(int id) {
		return this.manager.getObservacao(id);
	}

	public Passivo getPassivoPorId(int id) {
		return this.manager.getPassivoPorId(id);
	}

	public Set<Passivo> getPassivosPorReferencia(ContaCorrente contaCorrente, long referencia) {
		return this.manager.getPassivosPorReferencia(contaCorrente, referencia);
	}
	
	public Set<Passivo> getPassivosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.manager.getPassivosPorReferencia(contaCorrente, referenciaInicial,referenciaFinal);
	}

	public ResumoPeriodo getResumoPeriodo(ContaCorrente contaCorrente, Date dateInicial, Date dateFinal, boolean considerarOrcamento) {
		DecimalFormat df2 = new DecimalFormat("00");
		DecimalFormat df4 = new DecimalFormat("0000");
		Calendar calendarInicial = Calendar.getInstance();
		Calendar calendarFinal = Calendar.getInstance();
		calendarInicial.setTime(dateInicial);
		calendarFinal.setTime(dateFinal);
		long referenciaInicial = Long.parseLong(df4.format(calendarInicial.get(Calendar.YEAR)) + df2.format((calendarInicial.get(Calendar.MONTH))+1));
		long referenciaFinal = Long.parseLong(df4.format(calendarFinal.get(Calendar.YEAR)) + df2.format((calendarFinal.get(Calendar.MONTH))+1));
		return this.manager.getResumoPeriodo(contaCorrente, referenciaInicial, referenciaFinal, considerarOrcamento);
	}
	
	public ResumoPeriodo getResumoPeriodo(long referenciaInicial, long referenciaFinal) {
		return this.getResumoPeriodo(null, referenciaInicial, referenciaFinal, true);
	}
	public ResumoPeriodo getResumoPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, boolean considerarOrcamento) {
		return this.manager.getResumoPeriodo(contaCorrente, referenciaInicial, referenciaFinal, considerarOrcamento);
	}

	public SaldoAnterior getSaldoAnterior(ContaCorrente contaCorrente, long referencia) {
		return this.manager.getSaldoAnterior(contaCorrente, referencia);
	}
	
	public BigDecimal getSaldoAnteriorPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.manager.getSaldoAnteriorPeriodo(contaCorrente, referenciaInicial, referenciaFinal);
	}

	public void saveAtivo(Ativo ativo) {
		this.manager.saveAtivo(ativo);
	}

	public Conta saveConta(Conta conta) {
		return this.manager.saveConta(conta);
	}

	public void saveObservacao(Observacao obs) {
		this.manager.saveObservacao(obs);
	}

	public void savePassivo(Passivo passivo) {
		this.manager.savePassivo(passivo);
	}

	public List<Conta> listarContas(ContaOrdenador<Conta> contaComparator) {
		return this.manager.listarContas(contaComparator);
	}

	public void addScorecardManagerListener(ScorecardManagerListener listener) {
		this.manager.addScorecardManagerListener(listener);
	}

	public void removeScorecardManagerListener(ScorecardManagerListener listener) {
		this.manager.removeScorecardManagerListener(listener);
	}

	public Set<Passivo> getEspecificoPassivoPorReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal) {
		return this.manager.getEspecificoPassivoPorReferencia(contaCorrente, clazz, referenciaInicial, referenciaFinal);
	}
	
	public Set<Passivo> getEspecificoPassivoPorReferencia(ContaCorrente contaCorrente, Class clazz, java.util.Date referenciaInicial, java.util.Date referenciaFinal) {
		return this.manager.getEspecificoPassivoPorReferencia(contaCorrente, clazz, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}

	public Session getHibernateSession() {
		return this.manager.getHibernateSession();
	}

	public void ordenarPassivos(List<Passivo> passivos, PassivoOrdenador ordenador) {
		this.manager.ordenarPassivos(passivos, ordenador);
	}

	public void ordenarParcelas(List<Parcela> passivos, ParcelaOrdenador ordenador) {
		this.manager.ordenarParcelas(passivos, ordenador);
	}

	public Cheque getChequePorNumero(ContaCorrente contaCorrente, String numero) {
		return this.getChequePorNumero(contaCorrente, numero);
	}

	public Salario getSalario(ContaCorrente contaCorrente, long referencia) {
		return this.manager.getSalario(contaCorrente, referencia);
	}

	public BigDecimal getSalarioPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.manager.getSalarioPeriodo(contaCorrente, referenciaInicial, referenciaFinal);
	}

	public void deleteContaCorrente(ContaCorrente contaCorrente) {
		this.manager.deleteContaCorrente(contaCorrente);
	}

	public List<ContaCorrente> getContaCorrentePorDescricao(String descricao) {
		return this.manager.getContaCorrentePorDescricao(descricao);
	}

	public ContaCorrente getContaCorrentePorId(int id) {
		return this.manager.getContaCorrentePorId(id);
	}

	public List<ContaCorrente> listarContaCorrente() {
		return this.manager.listarContaCorrente();
	}	

	public void saveContaCorrente(ContaCorrente contaCorrente) {
		this.manager.saveContaCorrente(contaCorrente);
	}

	public void deleteBanco(Banco banco) {
		this.manager.deleteBanco(banco);
	}

	public Banco getBancoPorId(int id) {
		return this.manager.getBancoPorId(id);
	}

	public List<Banco> getBancoPorNome(String nome) {
		return this.manager.getBancoPorNome(nome);
	}

	public List<Banco> listarBanco() {
		return this.manager.listarBanco();
	}

	public void saveBanco(Banco banco) {
		this.manager.saveBanco(banco);
	}

	public void deleteOrcamento(Orcamento orcamento) {
		this.manager.deleteOrcamento(orcamento);
	}

	public Set<Orcamento> getOrcamentoPorDescricao(String descricao) {
		return this.manager.getOrcamentoPorDescricao(descricao);
	}

	public Orcamento getOrcamentoPorId(int id) {
		return this.manager.getOrcamentoPorId(id);
	}

	public Set<Orcamento> getOrcamentosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.manager.getOrcamentosPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	}
	public Set<Orcamento> getOrcamentosPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal) {
		return this.manager.getOrcamentosPorReferencia(contaCorrente, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}

	public boolean saveOrcamento(Orcamento orcamento) {
		return this.manager.saveOrcamento(orcamento);
	}

	public void deleteTransferencia(Transferencia transferencia) {
		this.manager.deleteTransferencia(transferencia);
	}

	public Transferencia getTransferenciaPorId(int id) {
		return this.manager.getTransferenciaPorId(id);
	}

	public List<Transferencia> getTransferenciasPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.manager.getTransferenciasPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	}
	public List<Transferencia> getTransferenciasPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal) {
		return this.manager.getTransferenciasPorReferencia(contaCorrente, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}

	public void saveTransferencia(Transferencia transferencia) {
		this.manager.saveTransferencia(transferencia);
	}

	public int getMavenTests() {
		return -1;
	}

	public void setMavenTests(int bool) {
	}

	public boolean isBancoRemovable(Banco banco) {
		return this.manager.isBancoRemovable(banco);
	}

	public boolean isContaCorrenteRemovable(ContaCorrente contaCorrente) {
		return this.manager.isContaCorrenteRemovable(contaCorrente);
	}

	public void consistirSaldosAnteriores(ContaCorrente contaCorrente) {
		this.manager.consistirSaldosAnteriores(contaCorrente);
	}

	public BigDecimal calcularSaldo(ContaCorrente contaCorrente, long referencia) {
		return this.manager.calcularSaldo(contaCorrente, referencia);
	}

	public void consistirSaldosAnteriores(ContaCorrente contaCorrente, long refIni, long refFim, boolean force) {
		this.manager.consistirSaldosAnteriores(contaCorrente, refIni, refFim, force);
	}

	public Set<Passivo> getPassivoPorHistorico(String historico) {
		return this.manager.getPassivoPorHistorico(historico);
	}

	public Set<Passivo> getPassivosPorNiveisContaContabil(ContaCorrente contaCorrente, String[] niveis, boolean incluirDescendentes, long refIni, long refFim) {
		return this.manager.getPassivosPorNiveisContaContabil(contaCorrente, niveis, incluirDescendentes, refIni, refFim);
	}
	
	public Set<Passivo> getPassivosAssociadosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta) {
		return this.manager.getPassivosAssociadosOrcamento(contaCorrente, referenciaInicial, referenciaFinal, nivelConta);
	}

	public Set<Passivo> getPassivoPorValor(ContaCorrente cc, float valor) {
		return this.manager.getPassivoPorValor(cc, valor);
	}

	public Set<Passivo> getPassivoPorValor(float valor) {
		return this.getPassivoPorValor(null, valor);
	}
	
	public Set<Cartao> getCartaoPorFiltro(long referenciaInicial, long referenciaFinal, Cartao cartao) {
		return this.manager.getCartaoPorFiltro(referenciaInicial, referenciaFinal, cartao);
	}

	@Override
	public List<CartaoContratado> listarCartoesContaCorrente(ContaCorrente contaCorrente) {
		return this.manager.listarCartoesContaCorrente(contaCorrente);
	}

}
