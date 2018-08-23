/*
 * ScoreCardFacade.java created on 27/02/2005, 18:58:16
 */

package br.ujr.scorecard.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

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

/**
 * Scorecard Facade Specification
 * @author Ualter
 */
public interface ScorecardManager
{
    public Conta saveConta(Conta conta);

    public void deleteConta(Conta conta);

    public String getContaProximoNivel();
    
    public String getContaProximoNivel(String crt);

    public Conta getContaPorId(int id);

    public List<Conta> getContasPorNivel(String nivel);

    public List<Conta> getContaPorDescricao(String descricao);
    
    public List<Conta> listarContas(ContaOrdenador<Conta> contaComparator);

    public void savePassivo(Passivo passivo);

    public void deletePassivo(Passivo passivo);
    
    public List<CartaoContratado>  getCartoesContaCorrente(ContaCorrente contaCorrente);

    public Set<Passivo> getPassivoPorHistorico(String historico);
    
    public Set<Cartao> getCartaoPorFiltro(long referenciaInicial, long referenciaFinal, Cartao cartao, Date dataMovimento);
    
    public Set<Passivo> getPassivoPorValor(ContaCorrente cc, float valor);
    
    public Set<Passivo> getPassivoPorValor(float valor);
    
    public Passivo getPassivoPorId(int id);

    public Set<Passivo> getPassivosPorReferencia(ContaCorrente contaCorrente, long referencia);
    
    public Set<Passivo> getPassivosPorReferencia(ContaCorrente contaCorrente,long referenciaInicial, long referenciaFinal);
    
    public Set<Passivo> getEspecificoPassivoPorReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal);

    public Cheque getChequePorNumero(ContaCorrente contaCorrente, String numero);

    public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Cartao.Operadora enumOperadora, long referenciaInicial, long referenciaFinal);

    public void saveAtivo(Ativo ativo);

    public void deleteAtivo(Ativo ativo);

    public Ativo getAtivoPorId(int id);

    public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, long referencia);
    
    public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);
    
    public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal);

    public SaldoAnterior getSaldoAnterior(ContaCorrente contaCorrente, long referencia);
    
    public BigDecimal getSaldoAnteriorPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);

    public Salario getSalario(ContaCorrente contaCorrente, long referencia);
    
    public BigDecimal getSalarioPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);
    
    public void saveObservacao(Observacao obs);

    public List<Observacao> getObservacao(String descricao);
    
    public Observacao getObservacao(int id);

    public void deleteObservacao(Observacao obs);
    
    public ResumoPeriodo getResumoPeriodo(ContaCorrente contaCorrente, Date dateInicial, Date dateFinal, boolean considerarOrcamento);

    /**
     * Reuni as informações de movimento de um período encapsulando em objeto Resumo
     * @param referenciaInicial mês e ano no formato YYYYMM do início do período
     * @param referenciaFinal   mês e ano no formato YYYYMM do final  do período
     * @param considerarOrcamento retirar do saldo previsto o valor de orçamento previsto ainda não realizado (o restante) ?
     * @return objeto Resumo com informações do movimento do período desejado
     */
    public ResumoPeriodo getResumoPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, boolean considerarOrcamento);
    /**
     * Resumo GERAL do Periodo, inclui todas as Contas Correntes Cadastradas
     * @param contaCorrente
     * @param referenciaInicial
     * @param referenciaFinal
     * @return
     */
    public ResumoPeriodo getResumoPeriodo(long referenciaInicial, long referenciaFinal);
    
    public void addScorecardManagerListener(ScorecardManagerListener listener);
    
    public void removeScorecardManagerListener(ScorecardManagerListener listener);
    
    // Temporário (Retirar)
    public Session getHibernateSession();
    
    public void ordenarPassivos(List<Passivo> passivos, PassivoOrdenador ordenador);
    
    public void ordenarParcelas(List<Parcela> passivos, ParcelaOrdenador ordenador);
    
    public void saveContaCorrente(ContaCorrente contaCorrente);
    
    public void deleteContaCorrente(ContaCorrente contaCorrente);
    
    public ContaCorrente getContaCorrentePorId(int id);
    
    public List<ContaCorrente> getContaCorrentePorDescricao(String descricao);
    
    public List<ContaCorrente> getContaCorrentePorBanco(Banco banco);
    
    public List<ContaCorrente> listarContaCorrente();
    
    public void saveBanco(Banco Banco);
    
    public void deleteBanco(Banco Banco);
    
    public boolean isBancoRemovable(Banco banco);
    
    public Banco getBancoPorId(int id);
    
    public List<Banco> getBancoPorNome(String nome);
    
    public List<Banco> listarBanco();
    
    public boolean saveOrcamento(Orcamento orcamento);
    
    public void deleteOrcamento(Orcamento orcamento);
    
    public Orcamento getOrcamentoPorId(int id);
    
    public Set<Orcamento> getOrcamentoPorDescricao(String descricao);
    
    public Set<Orcamento> getOrcamentosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);
    
    public void saveTransferencia(Transferencia transferencia);

    public void deleteTransferencia(Transferencia transferencia);

    public Transferencia getTransferenciaPorId(int id);
    
    public List<Transferencia> getTransferenciasPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal);
    
    public int getMavenTests();
    
    public void setMavenTests(int bool);
    
    public boolean isContaCorrenteRemovable(ContaCorrente contaCorrente);
    
    /**
     * Verificar e Gravar Saldo Anterior do seis meses passados, se estes não existerem gravados
     * Não refaz, apenas cria se não existirem ainda!
     */
    public void consistirSaldosAnteriores(ContaCorrente contaCorrente);
    
    /**
     * Processa Saldos de acordo com as referencias inicial e final, opcionalmente os reprocessa if(force == true)
     * @param contaCorrente
     * @param refIni
     * @param refFim
     * @param force
     */
    public void consistirSaldosAnteriores(ContaCorrente contaCorrente, long refIni, long refFim, boolean force);
    
    public BigDecimal calcularSaldo(ContaCorrente contaCorrente, long referencia);
    
    public Set<Passivo> getPassivosPorNiveisContaContabil(ContaCorrente contaCorrente, String[] niveis, boolean incluirDescendentes, long refIni, long refFim);
    
    public Set<Passivo> getPassivosAssociadosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta);
    
    public List<CartaoContratado> listarCartoesContaCorrente(ContaCorrente contaCorrente);
    
    public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, Class clazz, Date referenciaInicial, Date referenciaFinal);
    
    public List<Transferencia> getTransferenciasPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal);
    
    public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Operadora enumOperadora, Date referenciaInicial, Date referenciaFinal);
    
    public Set<Passivo> getEspecificoPassivoPorReferencia(ContaCorrente contaCorrente, Class clazz, java.util.Date referenciaInicial, java.util.Date referenciaFinal);
    
    public Set<Orcamento> getOrcamentosPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal);

	public CartaoContratado getCartaoContratado(Integer keyCartaoContratado);
	
	public List<CartaoContratado> getCartaoContratado(ContaCorrente contaCorrente);

}