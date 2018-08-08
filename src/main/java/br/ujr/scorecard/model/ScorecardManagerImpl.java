package br.ujr.scorecard.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.AtivoDAO;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.ativo.saldoanterior.SaldoAnterior;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.banco.BancoDAO;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.cartao.contratado.CartaoContratadoDAO;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.cc.ContaCorrenteDAO;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.conta.ContaDAO;
import br.ujr.scorecard.model.conta.ContaOrdenador;
import br.ujr.scorecard.model.observacao.Observacao;
import br.ujr.scorecard.model.observacao.ObservacaoDAO;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.orcamento.OrcamentoDAO;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.PassivoDAO;
import br.ujr.scorecard.model.passivo.PassivoOrdenador;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cartao.Cartao.Operadora;
import br.ujr.scorecard.model.passivo.cartao.CartaoDAO;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.cheque.ChequeDAO;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.model.transferencia.TransferenciaDAO;
import br.ujr.scorecard.util.Util;

/**
 * Facade para a aplicação Scorecard
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class ScorecardManagerImpl implements ScorecardManager
{
	protected ArrayList<ScorecardManagerListener> listeners = new ArrayList<ScorecardManagerListener>();
	
	private static Logger logger = Logger.getLogger(ScorecardManagerImpl.class);
	
	private int mavenTests;
	
	public ScorecardManagerImpl()
	{
	}
	
	// Manutenção Entidade Conta
	public Conta saveConta(Conta conta)
	{
		return this.getContaDAO().save(conta);
	}
	public void deleteConta(Conta conta)
	{
		Set<Passivo> passivos = this.getPassivoDAO().findByContaContabilId(conta.getId()); 
		if (  passivos != null && passivos.size() > 0  ) {
			String msg = "A Conta Contábil " + conta.getNivel() + "-" + conta.getDescricao() + " ainda está sendo usada por registro de Passivos. Não é possível apagá-la.";
			logger.warn(msg);
			throw new RuntimeException(msg);
		}
		List<Ativo> ativos = this.getAtivoDAO().findByContaContabil(conta); 
		if (  ativos != null && ativos.size() > 0  ) {
			String msg = "A Conta Contábil " + conta.getNivel() + "-" + conta.getDescricao() + " ainda está sendo usada por registro de Ativos. Não é possível apagá-la.";
			logger.warn(msg);
			throw new RuntimeException(msg);
		}
		this.getContaDAO().remove(conta);
	}
	public String getContaProximoNivel()
	{
		return this.getContaProximoNivel(null);
	}
	public String getContaProximoNivel(String crt)
	{
	    String nextNivel = null;
	    if ( crt == null )
	    {
	        crt = "%.0";
		    List<Conta> list = this.getContaDAO().findByNivel(crt);
		    Collections.sort(list,ContaOrdenador.Nivel);
		    if ( list.size() > 0 )
		    {
			    Conta conta = (Conta)list.get(list.size()-1);
			    int index = conta.getNivel().indexOf(".");
			    int next = Integer.parseInt(conta.getNivel().substring(0,index));
			    nextNivel = ++next + ".0";
		    }
	    }
	    // Último número for MAIOR QUE zero...
	    else
	    {
	    	boolean firstLevel = false;
	        if ( crt.substring(crt.length()-1).equals("0") )
	        {
	            crt = crt.substring(0,crt.length()-1) + "%";
	            firstLevel = true;
	        }
	        else
	        {
	            crt = crt + ".%";
	        }
		    List<Conta> list = this.getContaDAO().findByNivel(crt);
		    Collections.sort(list,ContaOrdenador.Nivel);
		    if ( list.size() > 0 )
		    {
		    	if ( firstLevel ) {
		    		/**
		    		 * Retirar os demais registros fora do nível que se deseja criar uma conta
		    		 */
		    		ArrayList<Conta> listFiltrada = new ArrayList();
		    		for (Iterator i = list.iterator(); i.hasNext();) {
						Conta conta = (Conta) i.next();
						int index = conta.getNivel().indexOf(".");
						if ( conta.getNivel().substring(++index).length() == 1 ) {
							listFiltrada.add(conta);
						}
					}
		    		list = listFiltrada;
		    	}
			    Conta conta = (Conta)list.get(list.size()-1);
			    int next    = Integer.parseInt(conta.getNivel().substring(conta.getNivel().length()-1,conta.getNivel().length()));
			    nextNivel   = conta.getNivel().substring(0, conta.getNivel().length()-1) + (++next);
		    }
		    else
		    {
		        nextNivel = crt.replaceFirst("\\%", "1");
		    }
	    }
	    return nextNivel;
	}
	public Conta getContaPorId(int id)
	{
		return this.getContaDAO().findById(id);
	}
	public List<Conta> getContasPorNivel(String nivel)
	{
		return this.getContaDAO().findByNivel(nivel);
	}
	public List<Conta> getContaPorDescricao(String descricao)
	{
		return this.getContaDAO().findByDescricao(descricao);
	}
	public List<Conta> listarContas(ContaOrdenador<Conta> contaComparator) {
		List<Conta> list = this.getContaDAO().list();
		Collections.sort(list, contaComparator);
		return list;
	}
	
	public void savePassivo(Passivo passivo)
	{
		this.getPassivoDAO().save(passivo);
		
		for(ScorecardManagerListener l : listeners) {
			l.actionPassivo(passivo);
		}
	}
	public void deletePassivo(Passivo passivo)
	{
		this.getPassivoDAO().remove(passivo);
	}
	public Passivo getPassivoPorId(int id)
	{
		return this.getPassivoDAO().findById(id);
	}
	public Set<Passivo> getPassivosPorReferencia(ContaCorrente contaCorrente, long referencia)
	{
		Set<Passivo> passivos = this.getPassivoDAO().findByReferencia(contaCorrente, referencia, referencia);
		return passivos;
	}
	public Set<Passivo> getPassivosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal)
	{
		Set<Passivo> passivos = this.getPassivoDAO().findByReferencia(contaCorrente, referenciaInicial, referenciaFinal);
		return passivos;
	}
	public Cheque getChequePorNumero(ContaCorrente contaCorrente, String numero)
	{
		return this.getChequeDAO().getChequePorNumero(numero);
	}
	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Cartao.Operadora enumOperadora, long referenciaInicial, long referenciaFinal)
    {
	    Set<Cartao> cartoes = this.getCartaoDAO().getCartaoPorOperadora(contaCorrente, enumOperadora, referenciaInicial, referenciaFinal);
	    /** 
		 * Filtrando: Apenas as Parcelas do Período desejado
		 */
		for(Cartao cartao : cartoes) {
			List<Parcela> parcelas = cartao.getParcelas(referenciaInicial, referenciaFinal);
			cartao.setParcelas(new HashSet<Parcela>(parcelas));
		}
		return cartoes;
    }
	
	public void saveAtivo(Ativo ativo)
	{
		this.getAtivoDAO().save(ativo);
		
		for(ScorecardManagerListener l : listeners) {
			l.actionAtivo(ativo);
		}
	}
	public void deleteAtivo(Ativo ativo)
	{
		this.getAtivoDAO().remove(ativo);
	}
	public Ativo getAtivoPorId(int id)
	{
		return this.getAtivoDAO().findById(id);
	}
	/**
	 * Consulta todos os ativos do mês e ano informado
	 * @param referencia mês e ano no formato YYYYMM
	 * @return Coleção de ativos
	 */
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, long referencia)
	{
		return this.getAtivoDAO().findByReferencia(contaCorrente, referencia,referencia);
	}
	/**
	 * Consulta todos os ativos do período informado
	 * @param referenciaInicial mês e ano no formato YYYYMM
	 * @param referenciaFinal mês e ano no formato YYYYMM
	 * @return Coleção de ativos do período
	 */
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal)
	{
		return this.getAtivoDAO().findByReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	}
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal)
	{
		return this.getAtivoDAO().findByReferencia(contaCorrente, clazz,referenciaInicial, referenciaFinal);
	}
	/**
	 * Retorna o saldo anterior referente ao mês e ano informado
	 * @param referencia mês e ano no formato YYYYMM
	 */
	public SaldoAnterior getSaldoAnterior(ContaCorrente contaCorrente, long referencia)
	{
		List<Ativo> saldoAnterior = this.getAtivoDAO().findByReferencia(contaCorrente, SaldoAnterior.class, referencia, referencia);
		if ( saldoAnterior != null && saldoAnterior.size() > 0 ) {
			return (SaldoAnterior) saldoAnterior.get(0);
		}
		return null;
	}
	/**
	 * Retorna a soma do Saldo Anterior referente a faixa informada de mês e ano
	 * @param referenciaInicial mês e ano no formato YYYYMM
	 * @param referenciaFinal mês e ano no formato YYYYMM
	 */
	public BigDecimal getSaldoAnteriorPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal)
	{
		List<Ativo> saldoAnterior = this.getAtivoDAO().findByReferencia(contaCorrente, SaldoAnterior.class, referenciaInicial, referenciaFinal);
		BigDecimal total = new BigDecimal(0);
		for (Ativo ativo : saldoAnterior) {
			total = total.add(ativo.getValor()); 
		}
		return total;
	}
	/**
	 * Retorna o salário referente ao mês e ano informado
	 * @param referencia mês e ano no formato YYYYMM
	 */
	public Salario getSalario(ContaCorrente contaCorrente, long referencia)
	{
		List<Ativo> salarios = this.getAtivoDAO().findByReferencia(contaCorrente, Salario.class, referencia, referencia);
		return (Salario) salarios.get(0);
	}
	
	/**
	 * Retorna o montante do salário referente ao período informado
	 * @param referenciaInicial mês e ano no formato YYYYMM
	 * @param referenciaFinal mês e ano no formato YYYYMM
	 */
	public BigDecimal getSalarioPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal)
	{
		List<Ativo> salarios = this.getAtivoDAO().findByReferencia(contaCorrente, Salario.class, referenciaInicial, referenciaFinal);
		BigDecimal total = new BigDecimal(0);
		for (Ativo ativo : salarios) {
			total = total.add(ativo.getValor());
		}
		return total;
	}
	 
	public static void main(String[] args) {
		/*ScorecardManager s = (ScorecardManager)Util.getBean("scorecardManager");
		ResumoPeriodo resumo = s.getResumoPeriodo(200710, 200710);*/
		
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
		return this.getResumoPeriodo(contaCorrente, referenciaInicial, referenciaFinal, considerarOrcamento);
	}
	
	public ResumoPeriodo getResumoPeriodo(long referenciaInicial, long referenciaFinal) {
		return this.getResumoPeriodo(null, referenciaInicial, referenciaFinal, true);
	}
	/**
     * Reuni as informações de movimento de um período encapsulando em objeto Resumo
     * @param referenciaInicial mês e ano no formato YYYYMM do início do período
     * @param referenciaFinal   mês e ano no formato YYYYMM do final  do período
     * @param considerarOrcamento retirar do saldo previsto o valor de orçamento previsto ainda não realizado (o restante) ?
     * @return objeto Resumo com informações do movimento do período desejado
     */
	public ResumoPeriodo getResumoPeriodo(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, boolean considerarOrcamento)
	{
	    /**
	     * Passivos
	     */
		BigDecimal valorEfetivado    = new BigDecimal(0);
	    BigDecimal cheque            = new BigDecimal(0);
	    BigDecimal visa              = new BigDecimal(0);
	    BigDecimal electron          = new BigDecimal(0);
	    BigDecimal mastercard        = new BigDecimal(0);
	    BigDecimal saque             = new BigDecimal(0);
	    BigDecimal debitoCC          = new BigDecimal(0);
	    BigDecimal passivosAteDia20  = new BigDecimal(0);
	    Set<Passivo> passivos        = this.getPassivosPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	    
	    for(Passivo passivo : passivos) {
	    	
	        List<Parcela> parcelas = passivo.getParcelas(referenciaInicial, referenciaFinal);
	        for(Parcela parcela : parcelas) {
	            BigDecimal valor = parcela.getValor();
	            
	            /**
	             * Para o Saldo Real
	             */
	            if (parcela.isEfetivado()) {
	            	valorEfetivado = valorEfetivado.add(valor);
	            }
	            
	            /**
	             * Para o Saldo Previsto até o dia 20
	             */
	            
	            boolean isBetweenDay01AndDay20 = Util.isDateBetweenDays(parcela.getDataVencimento(), 1, 20);
	            if ( isBetweenDay01AndDay20 ) {
	            	if ( !parcela.isEfetivado() )
	            		passivosAteDia20 = passivosAteDia20.add(valor);
	            }
	            
	            if ( passivo instanceof Cheque )
	            {
	                cheque = cheque.add(valor);
	            }
	            else
	            if ( passivo instanceof Saque )
	            {
	                 saque = saque.add(valor);
	            }
	            else
	            if ( passivo instanceof DebitoCC )
	            {
	                debitoCC = debitoCC.add(valor);
	            }
	            else
	            if ( passivo instanceof Cartao )
	            {
	                 Cartao cartao = (Cartao)passivo;
	                 switch (cartao.getEnumOperadora())
	                 {
	                    case VISA:
	                         visa = visa.add(valor);
	                         break;
	                    case VISA_ELECTRON:
	                         electron = electron.add(valor);
	                         break;
	                    case MASTERCARD:
	                         mastercard = mastercard.add(valor);
	                         break;
	                 }
	            }
	        }
	    }
	    
	    /**
	     * Ativos
	     */
	    BigDecimal investimento                      = new BigDecimal(0);
	    BigDecimal deposito                          = new BigDecimal(0);
	    BigDecimal saldoAnterior                     = new BigDecimal(0);
	    BigDecimal salario                           = this.getSalarioPeriodo(contaCorrente, referenciaInicial,referenciaFinal);
	    BigDecimal totalDepositoOrigemTranferencia   = new BigDecimal(0);
	    BigDecimal totalEstipendioOrigemTranferencia = new BigDecimal(0);
	    	    
	    List<Ativo> ativos = this.getAtivosPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	    for(Ativo ativo : ativos) {
	        BigDecimal valor = ativo.getValor();
	        
	        if ( ativo instanceof Investimento )
	        {
	            investimento = investimento.add(valor);
	        }
	        else
	        if ( ativo instanceof Deposito )
	        {
	            deposito = deposito.add(valor);
	            /**
		         * Caso o Resumo do Período calculado seje Geral (somatório de todas as contas correntes),
		         * os ativos originados de uma Transferência de uma para outra Conta Corrente não devem 
		         * ser somado duas vezes  
		         */
		        if ( ativo.isOrigemTransferencia() ) {
		        	totalDepositoOrigemTranferencia = totalDepositoOrigemTranferencia.add(valor);
	            }
	        }
	        else
	        if ( ativo instanceof SaldoAnterior ) 
	        {
	        	saldoAnterior = saldoAnterior.add(valor);
	        }
	        else
	        if ( ativo instanceof Salario ) 
	        {
	        	 /**
		         * Caso o Resumo do Período calculado seje Geral (somatório de todas as contas correntes),
		         * os ativos originados de uma Transferência de uma para outra Conta Corrente não devem 
		         * ser somado duas vezes  
		         */
		        if ( ativo.isOrigemTransferencia() ) {
		        	totalEstipendioOrigemTranferencia = totalEstipendioOrigemTranferencia.add(valor);
	            }
	        }
	    }
	    
	    /**
	     * Transferências
	     */
	    List<Transferencia> listTransferencia = this.getTransferenciasPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	    BigDecimal transferencias = new BigDecimal(0);
	    for (Iterator iteratorTransferencias = listTransferencia.iterator(); iteratorTransferencias.hasNext();) {
			Transferencia transf = (Transferencia) iteratorTransferencias.next();
			transferencias = transferencias.add(transf.getValor());
		}
	    
	    ResumoPeriodo resumo = new ResumoPeriodo(referenciaInicial,referenciaFinal);
	    resumo.setSaldoAnterior(saldoAnterior);
	    resumo.setCheques(cheque);
	    resumo.setVisa(visa);
	    resumo.setElectron(electron);
	    resumo.setMastercard(mastercard);
	    resumo.setSaques(saque);
	    resumo.setDebitosCC(debitoCC);
	    resumo.setInvestimentos(investimento);
	    resumo.setDepositos(deposito);
	    resumo.setSalario(salario);
	    resumo.setTransferencias(transferencias);
	    
	    /**
	     * Cálculo Saldo Previsto
	     */
	    BigDecimal negativo = new BigDecimal(0);
	    negativo = negativo.add(resumo.getCheques());
	    negativo = negativo.add(resumo.getElectron());
	    negativo = negativo.add(resumo.getMastercard());
	    negativo = negativo.add(resumo.getVisa());
	    negativo = negativo.add(resumo.getDebitosCC());
	    negativo = negativo.add(resumo.getSaques());
	    negativo = negativo.add(resumo.getTransferencias());
	    negativo = negativo.add(resumo.getInvestimentos());
	    BigDecimal valorOrcadoRestante = new BigDecimal(0);
	    if ( considerarOrcamento ) {
	    	Set<Orcamento> setOrcamentos = this.getOrcamentosPorReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	    	for (Iterator iteratorOrcamentos = setOrcamentos.iterator(); iteratorOrcamentos.hasNext();) {
	    		Orcamento orcamento = (Orcamento) iteratorOrcamentos.next();

	    		/**
	    		 * Valor Orçado MENOR do que o Realizado, orçamento estourou
	    		 * Neste caso, o valor negativo não deve ser somado como valor ainda ser gasto
	    		 * pois: negativo + negativo = positivo, terá o efeito contrário irá tirar este valor estourado do montante negativo
	    		 */
	    		if ( orcamento.getOrcado().compareTo(orcamento.getRealizado()) == -1 ) {
	    			// do nothing (proposital, inteligível)
	    		} else {
	    			valorOrcadoRestante = valorOrcadoRestante.add(orcamento.getOrcado().subtract(orcamento.getRealizado()));
	    		}
	    	}
	    }
	    negativo = negativo.add(valorOrcadoRestante);
	    BigDecimal positivo = new BigDecimal(0);
	    positivo = positivo.add(resumo.getDepositos());
	    positivo = positivo.add(resumo.getSaldoAnterior());
	    positivo = positivo.add(resumo.getSalario());
	    resumo.setSaldoPrevisto(positivo.subtract(negativo));
	    
	    /**
	     * Cálculo Saldo Real
	     */
	    negativo = new BigDecimal(0);
	    negativo = negativo.add(valorEfetivado);
	    negativo = negativo.add(resumo.getTransferencias());
	    negativo = negativo.add(resumo.getInvestimentos());
	    resumo.setSaldoReal(positivo.subtract(negativo));
	    
	    /**
	     * Cálculo Saldo Previsto Até o dia 20 do mês
	     */
	    passivosAteDia20 = passivosAteDia20.add(valorOrcadoRestante);
	    BigDecimal positivoSaldoPrevistoAteDia20 = new BigDecimal(0);
	    positivoSaldoPrevistoAteDia20 = resumo.getSaldoReal().subtract(passivosAteDia20);
	    resumo.setSaldoPrevistoAteDia20(positivoSaldoPrevistoAteDia20);
	    
	    /**
         * Caso o Resumo do Período calculado seje Geral
         * Retirar o que foi transferido de uma conta corrente para outra para que
         * não seje somado duas vezes 
         */
	    if ( contaCorrente == null ) {
	    	resumo.setDepositos(resumo.getDepositos().subtract(totalDepositoOrigemTranferencia));
	    	resumo.setSalario(resumo.getSalario().subtract(totalEstipendioOrigemTranferencia));
	    }
	    
	    return resumo;
	}
	
	public void saveObservacao(Observacao obs)
	{
		this.getObservacaoDAO().save(obs);
	}
	public List<Observacao> getObservacao(String descricao)
	{
	   return this.getObservacaoDAO().findByDescricao(descricao);
	}
	public Observacao getObservacao(int id) {
		return this.getObservacaoDAO().findById(id);
	}
	public void deleteObservacao(Observacao obs)
	{
		this.getObservacaoDAO().remove(obs);
	}
	
	/**
	 * Dependencies injecteds by Spring Framework 
	 */
	private ContaDAO contaDAO;
	public void setContaDAO(ContaDAO contaDAO) {
		this.contaDAO = contaDAO;
	}
	private ContaDAO getContaDAO() {
		return this.contaDAO;
	}
	
	private PassivoDAO passivoDAO;
	public void setPassivoDAO(PassivoDAO passivoDAO) {
		this.passivoDAO = passivoDAO;
	}
	private PassivoDAO getPassivoDAO() {
		return this.passivoDAO;
	}
	
	private ChequeDAO chequeDAO;
	public void setChequeDAO(ChequeDAO chequeDAO) {
		this.chequeDAO = chequeDAO;
	}
	private ChequeDAO getChequeDAO() {
		return this.chequeDAO;
	}
	
	private CartaoDAO cartaoDAO;
	public void setCartaoDAO(CartaoDAO cartaoDAO) {
		this.cartaoDAO = cartaoDAO;
	}
	private CartaoDAO getCartaoDAO() {
		return this.cartaoDAO;
	}
	
	private AtivoDAO ativoDAO;
	public AtivoDAO getAtivoDAO() {
		return ativoDAO;
	}
	public void setAtivoDAO(AtivoDAO ativoDAO) {
		this.ativoDAO = ativoDAO;
	}
	
	private ObservacaoDAO observacaoDAO;
	public ObservacaoDAO getObservacaoDAO() {
		return observacaoDAO;
	}
	public void setObservacaoDAO(ObservacaoDAO observacaoDAO) {
		this.observacaoDAO = observacaoDAO;
	}
	
	private ContaCorrenteDAO contaCorrenteDAO;
	public ContaCorrenteDAO getContaCorrenteDAO() {
		return contaCorrenteDAO;
	}
	public void setContaCorrenteDAO(ContaCorrenteDAO contaCorrenteDAO) {
		this.contaCorrenteDAO = contaCorrenteDAO;
	}
	
	private CartaoContratadoDAO cartaoContratadoDAO;
	public void setCartaoContratadoDAO(CartaoContratadoDAO cartaoContratadoDAO) {
		this.cartaoContratadoDAO = cartaoContratadoDAO;
	}
	public CartaoContratadoDAO getCartaoContratadoDAO() {
		return  this.cartaoContratadoDAO;
	}
	
	private BancoDAO bancoDAO;
	public BancoDAO getBancoDAO() {
		return bancoDAO;
	}
	public void setBancoDAO(BancoDAO bancoDAO) {
		this.bancoDAO = bancoDAO;
	}
	
	private OrcamentoDAO orcamentoDAO;
	public OrcamentoDAO getOrcamentoDAO() {
		return orcamentoDAO;
	}
	public void setOrcamentoDAO(OrcamentoDAO orcamentoDAO) {
		this.orcamentoDAO = orcamentoDAO;
	}
	
	private TransferenciaDAO transferenciaDAO;
	public TransferenciaDAO getTransferenciaDAO() {
		return transferenciaDAO;
	}
	public void setTransferenciaDAO(TransferenciaDAO transferenciaDAO) {
		this.transferenciaDAO = transferenciaDAO;
	}

	public void addScorecardManagerListener(ScorecardManagerListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeScorecardManagerListener(ScorecardManagerListener listener) {
		this.listeners.remove(listener);
	}

	public Set<Passivo> getEspecificoPassivoPorReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal) {
		Set<Passivo> passivos = this.passivoDAO.findSpecificByReferencia(contaCorrente,clazz, referenciaInicial, referenciaFinal);
		/** 
		 * Filtrando: Apenas as Parcelas do Período desejado
		 */
		for(Passivo passivo : passivos) {
			List<Parcela> parcelas = passivo.getParcelas(referenciaInicial, referenciaFinal);
			passivo.setParcelas(new HashSet(parcelas));
		}
		return passivos;
	}

	// Apagar (Temporário)
	public Session getHibernateSession() {
		HibernateDaoSupport daoHibernate = (HibernateDaoSupport) this.getAtivoDAO();
		return daoHibernate.getSessionFactory().openSession();		
	}

	public void ordenarPassivos(List<Passivo> passivos, PassivoOrdenador ordenador) {
		Collections.sort(passivos, ordenador);
	}
	
	public void ordenarParcelas(List<Parcela> parcelas, ParcelaOrdenador ordenador) {
		Collections.sort(parcelas,ordenador);
	}

	public void saveContaCorrente(ContaCorrente contaCorrente) {
		if (contaCorrente.getOrdem() == null) {
			contaCorrente.setOrdem(new Integer(9));
		}
		this.getContaCorrenteDAO().save(contaCorrente);
	}

	public void deleteContaCorrente(ContaCorrente contaCorrente) {
		this.getContaCorrenteDAO().remove(contaCorrente);
	}

	public List<ContaCorrente> getContaCorrentePorDescricao(String descricao) {
		return this.getContaCorrenteDAO().findByDescricao(descricao);
	}

	public ContaCorrente getContaCorrentePorId(int id) {
		return this.getContaCorrenteDAO().findById(id);
	}

	public List<ContaCorrente> listarContaCorrente() {
		return this.getContaCorrenteDAO().list();
	}

	public void deleteBanco(Banco Banco) {
		this.getBancoDAO().remove(Banco);
	}

	public Banco getBancoPorId(int id) {
		return this.getBancoDAO().findById(id);
	}

	public List<Banco> getBancoPorNome(String nome) {
		return this.getBancoDAO().findByNome(nome);
	}

	public List<Banco> listarBanco() {
		return this.getBancoDAO().list();
	}
	
	public List<CartaoContratado> listarCartoesContaCorrente(ContaCorrente contaCorrente) {
		return this.getCartaoContratadoDAO().findByContaCorrente(contaCorrente);
	}

	public void saveBanco(Banco banco) {
		this.getBancoDAO().save(banco);
	}

	public void deleteOrcamento(Orcamento orcamento) {
		this.getOrcamentoDAO().remove(orcamento);
	}

	public Set<Orcamento> getOrcamentoPorDescricao(String descricao) {
		return this.getOrcamentoDAO().findByDescricao(descricao);
	}

	public Orcamento getOrcamentoPorId(int id) {
		return this.getOrcamentoDAO().findById(id);
	}

	public Set<Orcamento> getOrcamentosPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.getOrcamentoDAO().findByReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	}

	public boolean saveOrcamento(Orcamento orcamento) {
		boolean result =  this.getOrcamentoDAO().save(orcamento);
		for(ScorecardManagerListener l : listeners) {
			l.actionOrcamento(orcamento);
		}
		return result;
	}

	public void deleteTransferencia(Transferencia transferencia) {
		this.getTransferenciaDAO().remove(transferencia);
		for(ScorecardManagerListener l : listeners) {
			l.actionTransferencia(transferencia);
		}
	}

	public Transferencia getTransferenciaPorId(int id) {
		return this.getTransferenciaDAO().findById(id);
	}

	public List<Transferencia> getTransferenciasPorReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		return this.getTransferenciaDAO().findByReferencia(contaCorrente, referenciaInicial, referenciaFinal);
	}

	public void saveTransferencia(Transferencia transferencia) {
		int ativoOrfao = transferencia.getAtivoOrfao();
		
		this.getTransferenciaDAO().save(transferencia);
		for(ScorecardManagerListener l : listeners) {
			l.actionTransferencia(transferencia);
		}
		
		Ativo ativo = this.getAtivoDAO().findById(ativoOrfao);
		if ( ativo != null ) {
			this.deleteAtivo(ativo);
			for(ScorecardManagerListener l : listeners) {
				l.actionAtivo(ativo);
			}
		}
	}
	
	public int getMavenTests() {
		return mavenTests;
	}

	public void setMavenTests(int mavenTests) {
		this.mavenTests = mavenTests;
	}

	public boolean isBancoRemovable(Banco banco) {
		return this.getBancoDAO().isBancoRemovable(banco);
	}

	public boolean isContaCorrenteRemovable(ContaCorrente contaCorrente) {
		return this.getContaCorrenteDAO().isRemovable(contaCorrente);
	}
	
	/**
     * Processa Saldos Anteriores do período inicial e final informado, opcionalmente os reprocessa if(force == true)
     * @param contaCorrente
     * @param refIni
     * @param refFim
     * @param force
     */
	public void consistirSaldosAnteriores(ContaCorrente contaCorrente, long refIni, long refFim, boolean force) {
		/** 
		 * Busca Conta Contábil que cobre o ativo Saldo Anterior
		 */
		Conta contaContabilSaldosAnteriories = null;
		List<Conta> contas = this.getContaPorDescricao("Saldos Anteriores");
		if ( contas != null && contas.size() > 0 ) {
			contaContabilSaldosAnteriories = contas.get(0);
		} else {
			String msg = "Não existe a Conta Contábil \"Saldo Anteriores\" cadastrada para cobrir a geração automática dos saldos anteriores.";
			logger.fatal(msg);
			throw new RuntimeException(msg);
		}
		
		/**
		 * Notifica Listeners inicio do processamento
		 */
		for(ScorecardManagerListener l : listeners) {
			l.actionConsistirSaldosAnteriores(new SaldoProcessadoEvent("*** Início Processamento: " + new SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime()),null));
			l.actionConsistirSaldosAnteriores(new SaldoProcessadoEvent("Conta Corrente: " + contaCorrente.getDescricao(),null));
		}
		
		/**
		 * Busca Saldos Anteriores já gravados neste período
		 */
		HashMap<Long,SaldoAnterior> saldosGravados = new HashMap<Long,SaldoAnterior>();
		List<Ativo> list = this.getAtivoDAO().findByReferencia(contaCorrente, SaldoAnterior.class, refIni, refFim);
		for(Ativo ativo : list) {
			saldosGravados.put(ativo.getReferencia(), (SaldoAnterior)ativo);
		}
		
		/**
		 * Processamento
		 */
		for(; refIni <= refFim; refIni = Util.computeReferencia(refIni, 1)) {
			for(ScorecardManagerListener l : listeners) {
				l.actionConsistirSaldosAnteriores(new SaldoProcessadoEvent(" Processando " + Util.formatReferencia(refIni),new Long(refIni)));
			}
			if ( !saldosGravados.containsKey(refIni) || force ) {
				/**
				 * Calcular e Salvar o Saldo Anterior com base na leitura do Saldo do Movimento Anterior
				 * Ex: Procurando Saldo Anterior de 10/2008, caso não calculado ainda, 
				 *     processar o movimento de 09/2008 para buscar o Saldo e gravar em 10/2008 como Saldo Anterior   
				 */
				BigDecimal valorSaldoAnteriorCalculado = this.calcularSaldo(contaCorrente, Util.computeReferencia(refIni, -1));
				SaldoAnterior saldoAnterior = new SaldoAnterior();
				if ( saldosGravados.containsKey(refIni) ) {
					saldoAnterior = saldosGravados.get(refIni);
				}
				saldoAnterior.setConta(contaContabilSaldosAnteriories);
				saldoAnterior.setContaCorrente(contaCorrente);
				saldoAnterior.setDataMovimento(Util.referenciaToDate(refIni));
				saldoAnterior.setHistorico("Saldo Anterior Calculado em " + Util.formatDate(Calendar.getInstance().getTime()));
				saldoAnterior.setReferencia(refIni);
				saldoAnterior.setValor(valorSaldoAnteriorCalculado);
				this.saveAtivo(saldoAnterior);
				String msg = "      Saldo Anterior \"R$ " 
							 + Util.formatCurrency(saldoAnterior.getValor()) + "\" referente a " + Util.formatReferencia(Util.computeReferencia(refIni, -1)) + "";
				for(ScorecardManagerListener l : listeners) {
					l.actionConsistirSaldosAnteriores(new SaldoProcessadoEvent(msg,new Long(refIni)));
				}
			}
		}
		for(ScorecardManagerListener l : listeners) {
			l.actionConsistirSaldosAnteriores(new SaldoProcessadoEvent("*** Fim Processamento: " + new SimpleDateFormat("hh:mm:ss").format(Calendar.getInstance().getTime()),null));
			l.actionConsistirSaldosAnteriores(new SaldoProcessadoEvent(SaldoProcessadoEvent.Event.FINALIZADO,""));
		}
	}
	
	/**
     * Gravar Saldo Anterior do seis meses passados, se estes não existerem gravados
     * Não refazer, apenas criar se não existirem ainda!
     */
	public void consistirSaldosAnteriores(ContaCorrente contaCorrente) {
		Calendar calendarCurrente = Calendar.getInstance();
		Calendar calendarBefore   = Calendar.getInstance();
		calendarBefore.add(Calendar.MONTH,-5);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		long refIni  = Long.parseLong(sdf.format(calendarBefore.getTime()));
		long refFim  = Long.parseLong(sdf.format(calendarCurrente.getTime()));
		
		this.consistirSaldosAnteriores(contaCorrente, refIni, refFim, false);
	}
	
	/**
	 * Calcula o Saldo do movimento de um determinado Mês/Ano
	 */
	public BigDecimal calcularSaldo(ContaCorrente contaCorrente, long referencia) {
		BigDecimal negativo = new BigDecimal(0,MathContext.DECIMAL32);
		BigDecimal positivo = new BigDecimal(0,MathContext.DECIMAL32);
		
	    /**
	     * Passivos
	     */
	    Set<Passivo> passivos = this.getPassivosPorReferencia(contaCorrente, referencia);
	    for(Passivo passivo : passivos) {
	        List<Parcela> parcelas = passivo.getParcelas(referencia, referencia);
	        for(Parcela parcela : parcelas) {
	            BigDecimal valor = parcela.getValor();
	            negativo = negativo.add(valor,MathContext.DECIMAL32);
	        }
	    }
	    /**
	     * Ativos
	     */
	    /*BigDecimal salario = this.getSalarioPeriodo(contaCorrente, referencia, referencia);
	    positivo = positivo.add(salario);*/
	    /**
	     * Ativos = Depósito + Saldo Anterior + Salário
	     */
	    List<Ativo> ativos = this.getAtivosPorReferencia(contaCorrente, referencia);
	    for(Ativo ativo : ativos) {
	    	if ( (ativo instanceof Deposito) || (ativo instanceof SaldoAnterior) || (ativo instanceof Salario) ) {
	    		BigDecimal valor = ativo.getValor();
	    		positivo = positivo.add(valor,MathContext.DECIMAL32);
			} else
	    	if ( ativo instanceof Investimento ) {
	    		/**
	    		 * Retirar o valor do Investimento como parte do Saldo restante
	    		 * Este não pertence mais a Conta Corrente, está salvo em outro lugar  
	    		 */
	    		BigDecimal valor = ativo.getValor();
	    		positivo = positivo.subtract(valor,MathContext.DECIMAL32);
	    	}
	    }
	    /**
	     * Transferências
	     */
	    List<Transferencia> listTransferencia = this.getTransferenciasPorReferencia(contaCorrente, referencia, referencia);
	    for (Iterator iteratorTransferencias = listTransferencia.iterator(); iteratorTransferencias.hasNext();) {
			Transferencia transf = (Transferencia) iteratorTransferencias.next();
			negativo = negativo.add(transf.getValor(),MathContext.DECIMAL32);
		}
	    
	    /**
	     * Cálculo do Saldo
	     */
	    BigDecimal resultado = positivo.subtract(negativo,MathContext.DECIMAL32);
	    return resultado;
	}

	public Set<Passivo> getPassivoPorHistorico(String historico) {
		return this.getPassivoDAO().findByHistorico(historico);
	}

	public Set<Passivo> getPassivosPorNiveisContaContabil(ContaCorrente contaCorrente, String[] niveis, boolean incluirDescendentes, long refIni, long refFim) {
		return this.getPassivoDAO().findByContaContabilNiveis(contaCorrente, niveis, incluirDescendentes, refIni, refFim);
	}
	
	public Set<Passivo> getPassivosAssociadosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta) {
		return this.getOrcamentoDAO().listPassivosOrcamento(contaCorrente, referenciaInicial, referenciaFinal, nivelConta);
	}

	public Set<Passivo> getPassivoPorValor(ContaCorrente cc, float valor) {
		return this.getPassivoDAO().findByValor(cc, valor);
	}

	public Set<Passivo> getPassivoPorValor(float valor) {
		return this.getPassivoPorValor(null, valor);
	}

	public Set<Cartao> getCartaoPorFiltro(long referenciaInicial, long referenciaFinal, Cartao cartao) {
		return this.getCartaoDAO().getCartaoPorFiltro(referenciaInicial, referenciaFinal, cartao);
	}
	
	public List<Ativo> getAtivosPorReferencia(ContaCorrente contaCorrente,Class clazz, Date referenciaInicial, Date referenciaFinal) {
		return this.getAtivosPorReferencia(contaCorrente, clazz, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}
	
	public List<Transferencia> getTransferenciasPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal) {
		return this.getTransferenciasPorReferencia(contaCorrente, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}
	
	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Operadora enumOperadora, Date referenciaInicial, Date referenciaFinal) {
		return this.getCartaoPorOperadora(contaCorrente, enumOperadora, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}
	
	public Set<Passivo> getEspecificoPassivoPorReferencia(ContaCorrente contaCorrente, Class clazz, java.util.Date referenciaInicial, java.util.Date referenciaFinal) {
		return this.getEspecificoPassivoPorReferencia(contaCorrente, clazz, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}
	
	public Set<Orcamento> getOrcamentosPorReferencia(ContaCorrente contaCorrente, Date referenciaInicial, Date referenciaFinal) {
		return this.getOrcamentosPorReferencia(contaCorrente, Util.extrairReferencia(referenciaInicial), Util.extrairReferencia(referenciaFinal));
	}
	
}
