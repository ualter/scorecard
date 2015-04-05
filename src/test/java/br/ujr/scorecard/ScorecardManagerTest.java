package br.ujr.scorecard;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.ujr.scorecard.model.ResumoPeriodo;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.ativo.Ativo;
import br.ujr.scorecard.model.ativo.deposito.Deposito;
import br.ujr.scorecard.model.ativo.investimento.Investimento;
import br.ujr.scorecard.model.ativo.salario.Salario;
import br.ujr.scorecard.model.ativo.saldoanterior.SaldoAnterior;
import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.model.observacao.Observacao;
import br.ujr.scorecard.model.orcamento.Orcamento;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cartao.Cartao;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.debitocc.DebitoCC;
import br.ujr.scorecard.model.passivo.parcela.Parcela;
import br.ujr.scorecard.model.passivo.parcela.ParcelaOrdenador;
import br.ujr.scorecard.model.passivo.saque.Saque;
import br.ujr.scorecard.model.transferencia.Transferencia;
import br.ujr.scorecard.util.Util;

/**
 * 
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior </a>
 */
public class ScorecardManagerTest extends TestCase
{

    protected ScorecardManager manager;
    
    public ScorecardManagerTest() 
    {
    	
    }
    public ScorecardManagerTest(String test)
    {
    	super(test);
    }

    protected void setUp() throws Exception
    {
    	this.manager = (ScorecardManager)Util.getBean("scorecardManager");
    	if ( this.manager.getMavenTests() != 1) {
    		this.manager = null;
    		throw new RuntimeException("Arquivo de configuração do Spring não está preparado para o ambiente de testes.\n Apontar para o banco de dados de testes");
    	}
    }
    
    public void testResumoPeriodo()
    {
        // Contas
        Conta contaImpostos = new Conta("1.0","IMPOSTOS");
        Conta contaImpostosEmpresa = contaImpostos.addContaFilho(new Conta("1.1","IMPOSTOS EMPRESA"));
        
        Conta contaLazer = new Conta("2.0","LAZER");
        
        Conta contaTransporte = new Conta("3.0","TRANSPORTE");
        Conta contaPasseMetro = contaTransporte.addContaFilho(new Conta("3.1","PASSES METRÔ"));
        Conta contaAutomovel = contaTransporte.addContaFilho(new Conta("3.2","AUTOMOVEL"));
        Conta contaCombustivel = contaAutomovel.addContaFilho(new Conta("3.2.1","COMBUSTIVEL"));
        
        Conta contaAlimentacao = new Conta("4.0","ALIMENTAÇÃO");
        Conta contaAlmoco = contaAlimentacao.addContaFilho(new Conta("4.1","ALMOÇO"));
        
        Conta contaDespesasPessoais = new Conta("5.0","DESPESAS PESSOAIS");
        
        Conta contaMoradia = new Conta("6.0","MORADIA");
        Conta contaAluguel = contaMoradia.addContaFilho(new Conta("6.1","ALUGUEL"));
        Conta contaCondominio = contaMoradia.addContaFilho(new Conta("6.2","CONDOMINIO"));
        
        Conta contaReceita = new Conta("7.0","RECEITAS");
        Conta contaSalario = contaReceita.addContaFilho(new Conta("7.1","SALARIO"));
        Conta contaDeposito = contaReceita.addContaFilho(new Conta("7.2","DEPOSITO"));
        Conta contaSaldo = contaReceita.addContaFilho(new Conta("7.3","SALDO"));
        
        Conta contaInvestimento = new Conta("8.0","INVESTIMENTOS");
        Conta contaRendaFixa = contaInvestimento.addContaFilho(new Conta("8.1","RENDA FIXA"));
        Conta contaPoupanca = contaInvestimento.addContaFilho(new Conta("8.2","POUPANÇA"));

        this.manager.saveConta(contaImpostos);
        this.manager.saveConta(contaLazer);
        this.manager.saveConta(contaTransporte);
        this.manager.saveConta(contaAlimentacao);
        this.manager.saveConta(contaDespesasPessoais);
        this.manager.saveConta(contaMoradia);
        this.manager.saveConta(contaReceita);
        this.manager.saveConta(contaInvestimento);
        
        // Banco
        Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
        // ContaCorrente
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);

        // Cheques
        Cheque cheque;
        cheque = new Cheque();
        cheque.setContaCorrente(contaCorrente);
        cheque.setDataMovimento("15/10/2004");
        cheque.setHistorico("Imposto Vialle");
        cheque.setConta(contaImpostosEmpresa);
        Parcela parc = new Parcela();
        parc.setDataVencimento("15/10/2004");
        parc.setEfetivado(false);
        parc.setValor(285.50f);
        cheque.addParcela("850230", parc);
        this.manager.savePassivo(cheque);
        
        // Não deve Entrar - outro Mês/Ano Referência
        cheque = new Cheque();
        cheque.setContaCorrente(contaCorrente);
        cheque.setDataMovimento("15/11/2004");
        cheque.setHistorico("IPVA");
        cheque.setConta(contaImpostosEmpresa);
        parc = new Parcela();
        parc.setDataVencimento("15/11/2004");
        parc.setEfetivado(false);
        parc.setValor(800f);
        cheque.addParcela("00550", parc);
        this.manager.savePassivo(cheque);

        cheque = new Cheque();
        cheque.setContaCorrente(contaCorrente);
        cheque.setDataMovimento("15/10/2004");
        cheque.setHistorico("Roupa de Mergulho");
        cheque.setConta(contaLazer);
        parc = new Parcela();
        parc.setDataVencimento("15/10/2004");
        parc.setEfetivado(false);
        parc.setValor(234);
        cheque.addParcela("850230",parc);
        parc = new Parcela();
        parc.setDataVencimento("15/11/2004");
        parc.setEfetivado(false);
        parc.setValor(234);
        cheque.addParcela("850231",parc);
        parc = new Parcela();
        parc.setDataVencimento("15/12/2004");
        parc.setEfetivado(false);
        parc.setValor(234);
        cheque.addParcela("850232",parc);
        parc = new Parcela();
        parc.setDataVencimento("15/01/2005");
        parc.setEfetivado(false);
        parc.setValor(234);
        cheque.addParcela("850233",parc);
        this.manager.savePassivo(cheque);

        cheque = new Cheque();
        cheque.setContaCorrente(contaCorrente);
        cheque.setDataMovimento("15/10/2004");
        cheque.setHistorico("Mensalidade Acadêmia");
        cheque.setConta(contaLazer);
        parc = new Parcela();
        parc.setDataVencimento("15/10/2004");
        parc.setEfetivado(false);
        parc.setValor(142);
        cheque.addParcela("850235",parc);
        this.manager.savePassivo(cheque);

        // Cartões
        Cartao cartao = new Cartao();
        cartao.setContaCorrente(contaCorrente);
        cartao.setDataMovimento("02/10/2004");
        cartao.setHistorico("Livro Stephen King");
        cartao.setConta(contaLazer);
        cartao.setOperadora(Cartao.Operadora.VISA);
        parc = new Parcela();
        parc.setDataVencimento("13/10/2004");
        parc.setEfetivado(false);
        parc.setValor(33.68f);
        cartao.addParcela(parc);
        this.manager.savePassivo(cartao);

        cartao = new Cartao();
        cartao.setContaCorrente(contaCorrente);
        cartao.setDataMovimento("18/10/2004");
        cartao.setHistorico("Gasolina");
        cartao.setConta(contaCombustivel);
        cartao.setOperadora(Cartao.Operadora.MASTERCARD);
        parc = new Parcela();
        parc.setDataVencimento("28/10/2004");
        parc.setEfetivado(false);
        parc.setValor(65);
        cartao.addParcela(parc);
        this.manager.savePassivo(cartao);

        cartao = new Cartao();
        cartao.setContaCorrente(contaCorrente);
        cartao.setDataMovimento("21/10/2004");
        cartao.setHistorico("Almoço");
        cartao.setConta(contaAlmoco);
        cartao.setOperadora(Cartao.Operadora.VISA_ELECTRON);
        parc = new Parcela();
        parc.setDataVencimento("21/10/2004");
        parc.setEfetivado(false);
        parc.setValor(12);
        cartao.addParcela(parc);
        this.manager.savePassivo(cartao);

        // Saques
        Saque saque = new Saque();
        saque.setContaCorrente(contaCorrente);
        saque.setDataMovimento("07/10/2004");
        saque.setHistorico("Despesas Pessoais");
        saque.setConta(contaDespesasPessoais);
        parc = new Parcela("15/10/2004", 50f);
        saque.addParcela(parc);
        this.manager.savePassivo(saque);

        saque = new Saque();
        saque.setContaCorrente(contaCorrente);
        saque.setDataMovimento("17/10/2004");
        saque.setHistorico("Passes Metrô");
        saque.setConta(contaPasseMetro);
        parc = new Parcela("17/10/2004", 50f);
        saque.addParcela(parc);
        this.manager.savePassivo(saque);

        //Debitos
        DebitoCC debitoCC = new DebitoCC();
        debitoCC.setContaCorrente(contaCorrente);
        debitoCC.setDataMovimento("03/10/2004");
        debitoCC.setHistorico("Aluguel");
        debitoCC.setConta(contaAluguel);
        parc = new Parcela();
        parc.setDataVencimento("05/10/2004");
        parc.setEfetivado(false);
        parc.setValor(630);
        debitoCC.addParcela(parc);
        this.manager.savePassivo(debitoCC);

        debitoCC = new DebitoCC();
        debitoCC.setContaCorrente(contaCorrente);
        debitoCC.setDataMovimento("03/10/2004");
        debitoCC.setHistorico("Condomínio");
        debitoCC.setConta(contaCondominio);
        parc = new Parcela();
        parc.setDataVencimento("05/10/2004");
        parc.setEfetivado(false);
        parc.setValor(230);
        debitoCC.addParcela(parc);
        this.manager.savePassivo(debitoCC);

        debitoCC = new DebitoCC();
        debitoCC.setContaCorrente(contaCorrente);
        debitoCC.setDataMovimento("08/10/2004");
        debitoCC.setHistorico("Speedy");
        debitoCC.setConta(contaLazer);
        parc = new Parcela();
        parc.setDataVencimento("08/10/2004");
        parc.setEfetivado(false);
        parc.setValor(100);
        debitoCC.addParcela(parc);
        this.manager.savePassivo(debitoCC);

        // Investimento
        Investimento in = new Investimento();
        in.setContaCorrente(contaCorrente);
        in.setDataMovimento("05/10/2004");
        in.setHistorico("Economia");
        in.setConta(contaRendaFixa);
        in.setValor(500);
        this.manager.saveAtivo(in);
        
        in = new Investimento();
        in.setContaCorrente(contaCorrente);
        in.setDataMovimento("05/10/2004");
        in.setHistorico("Economia Poupança");
        in.setConta(contaPoupanca);
        in.setValor(250);
        this.manager.saveAtivo(in);
        
        Deposito dp = new Deposito();
        dp.setContaCorrente(contaCorrente);
        dp.setDataMovimento("05/10/2004");
        dp.setHistorico("Reembolso Maratona");
        dp.setConta(contaDeposito);
        dp.setValor(35);
        this.manager.saveAtivo(dp);
        dp = new Deposito();
        dp.setContaCorrente(contaCorrente);
        dp.setDataMovimento("25/10/2004");
        dp.setHistorico("Reembolso");
        dp.setConta(contaDeposito);
        dp.setValor(10);
        this.manager.saveAtivo(dp);
        
        Salario sal = new Salario();
        sal.setContaCorrente(contaCorrente);
        sal.setDataMovimento("05/10/2004");
        sal.setHistorico("Sal");
        sal.setConta(contaSalario);
        sal.setValor(5000);
        this.manager.saveAtivo(sal);
        
        SaldoAnterior sa = new SaldoAnterior();
        sa.setContaCorrente(contaCorrente);
        sa.setDataMovimento("05/10/2004");
        sa.setHistorico("Saldo Anterior");
        sa.setConta(contaSaldo);
        sa.setValor(200);
        
        this.manager.saveAtivo(sa);
        
        ResumoPeriodo resumo = this.manager.getResumoPeriodo(contaCorrente, 200410,200410, true);
        Assert.assertTrue(661.50f == resumo.getCheques().floatValue());
        Assert.assertTrue(33.68f == resumo.getVisa().floatValue());
        Assert.assertTrue(65 == resumo.getMastercard().floatValue());
        Assert.assertTrue(12 == resumo.getElectron().floatValue());
        Assert.assertTrue(110.68f == (resumo.getVisa().floatValue() + resumo.getElectron().floatValue() + resumo.getMastercard().floatValue()));
        Assert.assertTrue(100 == resumo.getSaques().floatValue());
        Assert.assertTrue(960 == resumo.getDebitosCC().floatValue());
        Assert.assertTrue(750 == resumo.getInvestimentos().floatValue());
        Assert.assertTrue(45 == resumo.getDepositos().floatValue());
        Assert.assertTrue(5000 == resumo.getSalario().floatValue());
        Assert.assertTrue(200 == resumo.getSaldoAnterior().floatValue());
        
        this.limparBase();
        
        /*
        this.manager.executeQuery(new StringBuffer("delete Passivo"));
        this.manager.executeQuery(new StringBuffer("delete Conta"));*/
   }
	private void limparBase() {
		Session session = null;
        Transaction tx = null;
        try {
        	session = this.manager.getHibernateSession();
        	tx = session.beginTransaction();
        	List<Ativo> listAtivo = session.createQuery("from Ativo").list();
        	for (Ativo ativo : listAtivo) {
				session.delete(ativo);
			}
        	List<Passivo> listPassivo = session.createQuery("from Passivo").list();
        	for (Passivo passivo : listPassivo) {
				session.delete(passivo);
			}
        	List<Orcamento> listOrcamento = session.createQuery("from Orcamento").list();
        	for (Orcamento orc : listOrcamento) {
				session.delete(orc);
			}
        	List<Conta> listConta = session.createQuery("from Conta").list();
        	for (Conta conta : listConta) {
				session.delete(conta);
			}
        	List<ContaCorrente> listContaCorrente = session.createQuery("from ContaCorrente").list();
        	for (ContaCorrente cc : listContaCorrente) {
				session.delete(cc);
			}
        	List<Banco> listBanco = session.createQuery("from Banco").list();
        	for (Banco bc : listBanco) {
				session.delete(bc);
			}
        	tx.commit();
        } finally {
        	session.close();
        }
	}
    
    
    public void testManterSaque()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	// ContaCorrente
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
        // Criando Conta
        Conta cDespPessoais = new Conta("1.0","DESPESAS PESSOAIS");
        Conta cTransporte   = new Conta("1.1","TRANSPORTE");
        Conta cTrem         = new Conta("1.1.1","TREM");
        Conta cMetro        = new Conta("1.1.2","METRO");
        Conta cOnibus       = new Conta("1.1.3","ONIBUS");
        cTransporte.addContaFilho(cTrem);
        cTransporte.addContaFilho(cMetro);
        cTransporte.addContaFilho(cOnibus);
        cDespPessoais.addContaFilho(cTransporte);
        this.manager.saveConta(cDespPessoais);

        // Manutenção Saque
        Saque saque = new Saque();
        saque.setContaCorrente(contaCorrente);
        saque.setDataMovimento(Util.today());
        saque.setHistorico("Passe de Trem");
        saque.setConta(cTrem);
        Parcela parc = new Parcela("15/09/2004", 250f);
        saque.addParcela(parc);
        this.manager.savePassivo(saque);

        saque = (Saque) this.manager.getPassivoPorId(saque.getId());
        Assert.assertTrue(saque.getParcelas().size() == 1);
        saque.getParcela().setEfetivado(true);
        this.manager.savePassivo(saque);

        saque = (Saque) this.manager.getPassivoPorId(saque.getId());
        Assert.assertTrue(saque.getParcelas().size() == 1);
        Assert.assertTrue(saque.getParcela().isEfetivado());

        // Removendo Entidades
        this.manager.deletePassivo(saque);
        this.manager.deleteConta(cDespPessoais);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
        Assert.assertNull(this.manager.getPassivoPorId(saque.getId()));
        
    }
    
    public void testManterDebitoCC()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	// ContaCorrente
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
    	
        // Criando Conta
        Conta cMoradia = new Conta("2.0","MORADIA");
        Conta cAluguel = cMoradia.addContaFilho(new Conta("2.1","ALUGUEL"));
        this.manager.saveConta(cMoradia);

        // Manutenção Cartão
        DebitoCC debitoCC = new DebitoCC();
        debitoCC.setContaCorrente(contaCorrente);
        debitoCC.setDataMovimento(Util.today());
        debitoCC.setHistorico("Aluguel mes de julho");
        debitoCC.setConta(cAluguel);
        Parcela parc = new Parcela();
        parc.setDataVencimento(Util.today());
        parc.setEfetivado(false);
        parc.setValor(285.50f);
        debitoCC.addParcela(parc);
        this.manager.savePassivo(debitoCC);

        debitoCC = (DebitoCC) this.manager.getPassivoPorId(debitoCC.getId());
        Assert.assertTrue(debitoCC.getParcelas().size() == 1);
        debitoCC.getParcela().setEfetivado(true);
        this.manager.savePassivo(debitoCC);

        debitoCC = (DebitoCC) this.manager.getPassivoPorId(debitoCC.getId());
        Assert.assertTrue(debitoCC.getParcelas().size() == 1);
        Assert.assertTrue(debitoCC.getParcela().isEfetivado());

        this.manager.deletePassivo(debitoCC);
        this.manager.deleteConta(cMoradia);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
        Assert.assertNull(this.manager.getPassivoPorId(debitoCC.getId()));
    }
    
    public void testManterOrcamento()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	// ContaCorrente
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
        // Criando Conta
        Conta cAlimentacao = new Conta("1.0","ALIMENTAÇÃO");
        Conta cAlmocos     = cAlimentacao.addContaFilho(new Conta("1.1","ALMOÇO"));
        this.manager.saveConta(cAlimentacao);
    	
        Orcamento orcamento = new Orcamento();
        orcamento.setDescricao("Almoço");
        orcamento.setContaCorrente(contaCorrente);
        orcamento.setContaAssociada(cAlmocos);
        orcamento.setReferencia("200801");
        orcamento.setOrcado(new BigDecimal(265F));
        
        this.manager.saveOrcamento(orcamento);
        
        Orcamento orcamentoRead = this.manager.getOrcamentoPorId(orcamento.getId());
        Assert.assertEquals(orcamento.getDescricao(), orcamentoRead.getDescricao());
        Assert.assertEquals(orcamentoRead.getOrcado().floatValue(), 265F);
        
        this.manager.deleteOrcamento(orcamentoRead);
        this.manager.deleteConta(cAlimentacao);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
        Assert.assertNull(this.manager.getOrcamentoPorId(orcamentoRead.getId()));
    }
    
    public void testOrcadoRealizado() {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	// ContaCorrente
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
        // Criando Conta
        Conta cAlmoco  = new Conta("1.0","ALMOÇO");
        Conta cMercado = new Conta("2.0","MERCADO");
        this.manager.saveConta(cAlmoco);
        this.manager.saveConta(cMercado);
    	
        Orcamento orcamento = new Orcamento();
        orcamento.setDescricao("Almoço");
        orcamento.setContaCorrente(contaCorrente);
        orcamento.setContaAssociada(cAlmoco);
        orcamento.setReferencia("200801");
        orcamento.setOrcado(new BigDecimal(265F));
        this.manager.saveOrcamento(orcamento);
        
        orcamento = new Orcamento();
        orcamento.setDescricao("Mercado");
        orcamento.setContaCorrente(contaCorrente);
        orcamento.setContaAssociada(cMercado);
        orcamento.setReferencia("200801");
        orcamento.setOrcado(new BigDecimal(800F));
        this.manager.saveOrcamento(orcamento);
        
        DebitoCC debitoAlmoco = new DebitoCC();
        debitoAlmoco.setContaCorrente(contaCorrente);
        debitoAlmoco.setDataMovimento(Util.parseDate("10/01/2008"));
        debitoAlmoco.setHistorico("Almoço");
        debitoAlmoco.setConta(cAlmoco);
        Parcela parc = new Parcela();
        parc.setDataVencimento("10/01/2008");
        parc.setEfetivado(false);
        parc.setValor(14.50f);
        debitoAlmoco.addParcela(parc);
        this.manager.savePassivo(debitoAlmoco);
        
        debitoAlmoco = new DebitoCC();
        debitoAlmoco.setContaCorrente(contaCorrente);
        debitoAlmoco.setDataMovimento(Util.parseDate("10/01/2008"));
        debitoAlmoco.setHistorico("Almoço");
        debitoAlmoco.setConta(cAlmoco);
        parc = new Parcela();
        parc.setDataVencimento("10/01/2008");
        parc.setEfetivado(false);
        parc.setValor(32.54f);
        debitoAlmoco.addParcela(parc);
        this.manager.savePassivo(debitoAlmoco);
        
        debitoAlmoco = new DebitoCC();
        debitoAlmoco.setContaCorrente(contaCorrente);
        debitoAlmoco.setDataMovimento(Util.parseDate("11/02/2008"));
        debitoAlmoco.setHistorico("Almoço");
        debitoAlmoco.setConta(cAlmoco);
        parc = new Parcela();
        parc.setDataVencimento("11/02/2008");
        parc.setEfetivado(false);
        parc.setValor(10.0f);
        debitoAlmoco.addParcela(parc);
        this.manager.savePassivo(debitoAlmoco);
        
        Cartao visa = new Cartao();
        visa.setContaCorrente(contaCorrente);
        visa.setDataMovimento(Util.parseDate("05/01/2008"));
        visa.setHistorico("Lodetti");
        visa.setConta(cMercado);
        visa.setOperadora(Cartao.Operadora.VISA_ELECTRON);
        parc = new Parcela();
        parc.setDataVencimento("05/01/2008");
        parc.setEfetivado(true);
        parc.setValor(89.53f);
        visa.addParcela(parc);
        this.manager.savePassivo(visa);
        
        visa = new Cartao();
        visa.setContaCorrente(contaCorrente);
        visa.setDataMovimento(Util.parseDate("08/01/2008"));
        visa.setHistorico("Carrefour");
        visa.setConta(cMercado);
        visa.setOperadora(Cartao.Operadora.VISA);
        parc = new Parcela();
        parc.setDataVencimento("08/01/2008");
        parc.setEfetivado(true);
        parc.setValor(350.75f);
        visa.addParcela(parc);
        this.manager.savePassivo(visa);
        
        visa = new Cartao();
        visa.setContaCorrente(contaCorrente);
        visa.setDataMovimento(Util.parseDate("08/02/2008"));
        visa.setHistorico("Extra");
        visa.setConta(cMercado);
        visa.setOperadora(Cartao.Operadora.VISA);
        parc = new Parcela();
        parc.setDataVencimento("08/02/2008");
        parc.setEfetivado(true);
        parc.setValor(340.00f);
        visa.addParcela(parc);
        this.manager.savePassivo(visa);
        
    	// ContaCorrente contaCorrente = this.manager.getContaCorrentePorId(13);
        // Almoço  01/2008 =  47,04
        // Mercado 01/2008 = 440,28 
        // Almoço  02/2008 =  10,00
        // Mercado 02/2008 = 340,00
        Set<Orcamento> orcamentos = this.manager.getOrcamentosPorReferencia(contaCorrente, 200801, 200801);
        for(Orcamento o : orcamentos) {
        	if ( o.getContaAssociada().getDescricao().equals("ALMOÇO") ) {
        		Assert.assertEquals(o.getRealizado().doubleValue(), 47.04d);
        	} else
    		if ( o.getContaAssociada().getDescricao().equals("MERCADO") ) {
    			Assert.assertEquals(o.getRealizado().doubleValue(), 440.28d);
    		}
        }
        
        Set<Orcamento> orcamentos2 = this.manager.getOrcamentosPorReferencia(contaCorrente, 200801, 200802);
        for(Orcamento o : orcamentos2) {
        	if ( o.getContaAssociada().getDescricao().equals("ALMOÇO") ) {
        		Assert.assertEquals(o.getRealizado().doubleValue(), 57.04d);
        	} else
    		if ( o.getContaAssociada().getDescricao().equals("MERCADO") ) {
    			Assert.assertEquals(o.getRealizado().doubleValue(), 780.28d);
    		}
        }
        
        this.limparBase();
    }
    
    public void testManterCheque()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	// Conta Corrente 
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
        // Criando Conta
    	Conta cImpostos = new Conta("3.0","IMPOSTO");
        Conta cImpostoPj = cImpostos.addContaFilho(new Conta("3.1","IRPF TRIMESTRAL"));
        this.manager.saveConta(cImpostos);

        // Manutenção Cheque
        Cheque cheque = new Cheque();
        cheque.setContaCorrente(contaCorrente);
        cheque.setDataMovimento(Util.today());
        cheque.setHistorico("Impostos Vialle");
        cheque.setConta(cImpostoPj);

        Parcela parc = new Parcela();
        parc.setDataVencimento(Util.today());
        parc.setEfetivado(false);
        parc.setValor(285.50f);
        cheque.addParcela("850230",parc);

        parc = new Parcela();
        parc.setDataVencimento("30/08/2004");
        parc.setEfetivado(false);
        parc.setValor(1500.00f);
        cheque.addParcela("850231",parc);

        this.manager.savePassivo(cheque);
        this.manager.savePassivo(cheque);
        this.manager.savePassivo(cheque);
        this.manager.savePassivo(cheque);
        
        cheque = (Cheque)this.manager.getPassivoPorId(cheque.getId());
        Assert.assertTrue(cheque.getParcelas().size() == 2);

        cheque = this.manager.getChequePorNumero(contaCorrente,"850230");
        Parcela parcela = cheque.getParcela("850230");
        Assert.assertTrue(parcela.getValor().floatValue() == 285.50f);
        parcela.setDataVencimento("15/09/2004");
        this.manager.savePassivo(cheque);

        cheque = this.manager.getChequePorNumero(contaCorrente,"850230");
        parcela = cheque.getParcela("850230");
        GregorianCalendar expectedVencimento = new GregorianCalendar(2004,8,15,00,00,00);
        GregorianCalendar savedVencimento = new GregorianCalendar();
        savedVencimento.setTime(parcela.getDataVencimento());
        savedVencimento.set(Calendar.HOUR, 00);
        savedVencimento.set(Calendar.MINUTE, 00);
        savedVencimento.set(Calendar.SECOND, 00);	
        
        Assert.assertTrue(expectedVencimento.getTime().compareTo(savedVencimento.getTime()) == 0);

        this.manager.deletePassivo(cheque);
        this.manager.deleteConta(cImpostos);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
    }
    
    public void testManterCartao()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	// Conta Corrente
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
        // Criando Conta
        Conta cLazer  = new Conta("1.0","LAZER");
        Conta cCinema = cLazer.addContaFilho(new Conta("1.1","Cinema"));
        this.manager.saveConta(cLazer);

        // Manutenção Cartão
        Cartao mastercard = new Cartao();
        mastercard.setContaCorrente(contaCorrente);
        mastercard.setDataMovimento(Util.today());
        mastercard.setHistorico("Cinemark");
        mastercard.setConta(cCinema);
        mastercard.setOperadora(Cartao.Operadora.MASTERCARD);
        Parcela parc = new Parcela();
        parc.setDataVencimento("28/08/2006");
        parc.setEfetivado(true);
        parc.setValor(70f);
        mastercard.addParcela(parc);
        parc = new Parcela();
        parc.setDataVencimento("28/09/2006");
        parc.setEfetivado(false);
        parc.setValor(70f);
        mastercard.addParcela(parc);
        this.manager.savePassivo(mastercard);
        
        Cartao visa = new Cartao();
        visa.setContaCorrente(contaCorrente);
        visa.setDataMovimento(Util.today());
        visa.setHistorico("Viagem Praia");
        visa.setConta(cCinema);
        visa.setOperadora(Cartao.Operadora.VISA);
        parc = new Parcela();
        parc.setDataVencimento("12/09/2006");
        parc.setEfetivado(true);
        parc.setValor(1500f);
        visa.addParcela(parc);
        this.manager.savePassivo(visa);

        mastercard = (Cartao) this.manager.getPassivoPorId(mastercard.getId());
        Assert.assertTrue(mastercard.getNomeOperadora().equalsIgnoreCase("MASTERCARD"));
        Assert.assertTrue(mastercard.getParcela().getValor().floatValue() == 70f);
        Assert.assertTrue(mastercard.getParcelas().size() == 2);
        Set<Parcela> setParcelas = mastercard.getParcelas(ParcelaOrdenador.DATA_VENCIMENTO);
        GregorianCalendar dataVenc = new GregorianCalendar(2006,7,28,00,00,00);
        int index = 0;
        for(Parcela p : setParcelas)
        {
        	if (index == 0) {
				Assert.assertTrue(p.getDataVencimento().compareTo(dataVenc.getTime()) == 0);
			}
        	index++;
        }
        
        
        visa = (Cartao) this.manager.getPassivoPorId(visa.getId());
        Assert.assertTrue(visa.getNomeOperadora().equalsIgnoreCase("VISA"));
        Assert.assertTrue(visa.getParcela().getValor().floatValue() == 1500f);
        
        Set<Cartao> set = this.manager.getCartaoPorOperadora(contaCorrente, Cartao.Operadora.MASTERCARD, 200609, 200609);
        Assert.assertTrue(set.size() == 1);
        
        this.manager.deletePassivo(mastercard);
        this.manager.deletePassivo(visa);
        this.manager.deleteConta(cLazer);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
    }
    
    public void testManterSalario()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
   	    // ContaCorrente
    	ContaCorrente contaCorrente = new ContaCorrente();
    	contaCorrente.setBanco(banco);
        contaCorrente.setDescricao("Teste");
        this.manager.saveContaCorrente(contaCorrente);
        
    	Conta cProventos  = new Conta("2.0","PROVENTOS");
        Conta cSalario = cProventos.addContaFilho(new Conta("2.1","Salario"));
        this.manager.saveConta(cProventos);
        
        Salario salario = new Salario();
        salario.setContaCorrente(contaCorrente);
        salario.setDataMovimento(Util.today());
        salario.setHistorico("Trabalho");
        salario.setReferencia(200408);
        salario.setValor(10000f);
        salario.setConta(cSalario);
        this.manager.saveAtivo(salario);

        salario = (Salario) this.manager.getAtivoPorId(salario.getId());

        salario.setValor(10500f);
        this.manager.saveAtivo(salario);

        salario = (Salario) this.manager.getAtivoPorId(salario.getId());
        Assert.assertTrue(salario.getValor().floatValue() == 10500f);

        this.manager.deleteAtivo(salario);
        this.manager.deleteConta(cProventos);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
    }
    
    public void testManterInvestimento()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
        Conta cProventos  = new Conta("2.0","PROVENTOS");
        Conta cInvestimento = cProventos.addContaFilho(new Conta("2.1","RENDA FIXA"));
        this.manager.saveConta(cProventos);

        // Manutenção Investimento
        Investimento investimento = new Investimento();
        investimento.setContaCorrente(contaCorrente);
        investimento.setDataMovimento(Util.today());
        investimento.setReferencia(200408);
        investimento.setValor(250);
        investimento.setConta(cInvestimento);

        this.manager.saveAtivo(investimento);

        investimento = (Investimento) this.manager.getAtivoPorId(investimento.getId());
        Assert.assertTrue(investimento.getValor().floatValue() == 250);
        
        this.manager.deleteAtivo(investimento);
        this.manager.deleteConta(cProventos);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
        Assert.assertTrue(this.manager.getAtivoPorId(investimento.getId()) == null);
    }
    
    public void testManterDeposito()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
    	Conta cProventos  = new Conta("2.0","PROVENTOS");
        Conta cInvestimento = cProventos.addContaFilho(new Conta("2.1","DEPOSITO"));
        this.manager.saveConta(cProventos);
        
        Deposito deposito = new Deposito();
        deposito.setContaCorrente(contaCorrente);
        deposito.setDataMovimento(Util.today());
        deposito.setHistorico("Reembolso");
        deposito.setReferencia(200408);
        deposito.setValor(100f);
        deposito.setConta(cInvestimento);
        this.manager.saveAtivo(deposito);

        deposito = (Deposito) this.manager.getAtivoPorId(deposito.getId());
        Assert.assertTrue(deposito.getValor().floatValue() == 100f);
        deposito.setValor(20f);
        this.manager.saveAtivo(deposito);

        deposito = (Deposito) this.manager.getAtivoPorId(deposito.getId());
        Assert.assertTrue(deposito.getValor().floatValue() == 20f);
        Assert.assertTrue(deposito.getConta().getDescricao().equals("DEPOSITO"));
        
        this.manager.deleteAtivo(deposito);
        this.manager.deleteConta(cProventos);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
        
        Assert.assertTrue(this.manager.getAtivoPorId(deposito.getId()) == null);
    }
    
    public void testManterSaldoAnterior()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        this.manager.saveContaCorrente(contaCorrente);
        
    	Conta cProventos  = new Conta("2.0","PROVENTOS");
        Conta cSaldoAnterior = cProventos.addContaFilho(new Conta("2.1","SALDO ANTERIOR"));
        this.manager.saveConta(cProventos);
        
        // Manutenção SaldoAnterior
        SaldoAnterior saldoAnterior = new SaldoAnterior();
        saldoAnterior.setContaCorrente(contaCorrente);
        saldoAnterior.setDataMovimento(Util.today());
        saldoAnterior.setReferencia(200408);
        saldoAnterior.setValor(148f);
        saldoAnterior.setConta(cSaldoAnterior);
        this.manager.saveAtivo(saldoAnterior);

        saldoAnterior = (SaldoAnterior) this.manager.getAtivoPorId(saldoAnterior.getId());
        Assert.assertTrue(saldoAnterior.getValor().floatValue() == 148f);
        saldoAnterior.setValor(152f);
        this.manager.saveAtivo(saldoAnterior);

        saldoAnterior = (SaldoAnterior) this.manager.getAtivoPorId(saldoAnterior.getId());
        Assert.assertTrue(saldoAnterior.getValor().floatValue() == 152f);
        Assert.assertTrue(saldoAnterior.getConta().getDescricao().equals("SALDO ANTERIOR"));

        this.manager.deleteAtivo(saldoAnterior);
        this.manager.deleteConta(cProventos);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteBanco(banco);
        Assert.assertTrue(this.manager.getAtivoPorId(saldoAnterior.getId()) == null);
    }
    
    public void testObservacao()
    {
        Observacao obs = new Observacao(200410,"Curso de Mergulho");
        this.manager.saveObservacao(obs);
        
        List<Observacao> list = this.manager.getObservacao("Curso de Mergulho");
        obs = list.get(0);
        Assert.assertTrue(obs.getDescricao().equals("Curso de Mergulho"));
        obs.setDescricao("TT");
        this.manager.saveObservacao(obs);
        
        obs = this.manager.getObservacao(obs.getId());
        Assert.assertTrue(obs.getDescricao().equals("TT"));
        
        this.manager.deleteObservacao(obs);
    }

    public void testManterContaCorrente() {
    	Banco banco = new Banco();
    	banco.setNome("Banco do Brasil");
    	this.manager.saveBanco(banco);
    	
    	ContaCorrente contaCorrente = new ContaCorrente();
    	contaCorrente.setDescricao("Test - Banco do Brasil");
    	contaCorrente.setBanco(banco);
    	this.manager.saveContaCorrente(contaCorrente);
    	
   	 	ContaCorrente cc = this.manager.getContaCorrentePorId(contaCorrente.getId());
    	Assert.assertEquals(cc.getDescricao(), "Test - Banco do Brasil");
    	
    	/*this.manager.deleteContaCorrente(cc);
    	this.manager.deleteBanco(banco);
    	List list = this.manager.listarContaCorrente();
    	Assert.assertEquals(0, list.size());*/
    }
    
    public void testManterBanco() {
    	Banco banco = new Banco();
    	banco.setNome("Banco do Brasil");
    	this.manager.saveBanco(banco);
    	
    	Banco banco2 = this.manager.getBancoPorId(banco.getId());
    	Assert.assertEquals(banco.getNome(), banco2.getNome());
    	
    	this.manager.deleteBanco(banco2);
    }
    
    public void cadastrarContasCorrentes() {
    	this.manager = (ScorecardManager)Util.getBean("scorecardManager");
    	List<Banco> bancos = this.manager.getBancoPorNome("Banco do Brasil");
    	Banco       banco;
    	if ( bancos.size() < 1) {
    		banco = new Banco();
    		banco.setNome("Banco do Brasil");
        	this.manager.saveBanco(banco);
    	} else {
    		banco = bancos.get(0);
    	}
    	
    	ContaCorrente ccUalter = new ContaCorrente();
    	ccUalter.setBanco(banco);
    	ccUalter.setNumero("14.868-7");
    	ccUalter.setDescricao("Ualter Jr.");
    	this.manager.saveContaCorrente(ccUalter);
    	
    	ContaCorrente ccUJR = new ContaCorrente();
    	ccUJR.setBanco(banco);
    	ccUalter.setNumero("48.000-2");
    	ccUJR.setDescricao("U.JR. Consultoria");
    	this.manager.saveContaCorrente(ccUJR);
    }
    
    public void testManterTransferencia()
    {
    	// Banco
    	Banco banco = new Banco();
        banco.setNome("Banco do Brasil");
        this.manager.saveBanco(banco);
        
    	ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setDescricao("Teste");
        contaCorrente.setBanco(banco);
        contaCorrente.setNumero("148687");
        this.manager.saveContaCorrente(contaCorrente);
        
    	Conta cProventos  = new Conta("1.0","TRANSF");
        Conta cInvestimento = cProventos.addContaFilho(new Conta("1.1","REPASSE"));
        this.manager.saveConta(cProventos);
        
        Transferencia transferencia = new Transferencia();
        transferencia.setContaCorrente(contaCorrente);
        transferencia.setDataMovimento(Util.today());
        transferencia.setHistorico("Testes");
        transferencia.setReferencia(200408);
        transferencia.setValor(100f);
        transferencia.setConta(cInvestimento);
        
        /**
         * Preparando Ativo Alvo que receberá a Transferência
         */
        // Conta Corrente Destino
        ContaCorrente contaCorrenteDestino = new ContaCorrente();
        contaCorrenteDestino.setDescricao("Ualter");
        contaCorrenteDestino.setBanco(banco);
        contaCorrenteDestino.setNumero("480002");
        this.manager.saveContaCorrente(contaCorrenteDestino);
        // Conta Contábil Destino 
        Conta cSalario  = new Conta("2.0","SALARIO");
        this.manager.saveConta(cSalario);
        // Ativo
        transferencia.setAtivoTransferido(contaCorrenteDestino, cSalario, Deposito.class,"Historico");
        this.manager.saveTransferencia(transferencia);

        transferencia = (Transferencia) this.manager.getTransferenciaPorId(transferencia.getId());
        Assert.assertTrue(transferencia.getValor().floatValue() == 100f);
        transferencia.setValor(20f);
        this.manager.saveTransferencia(transferencia);

        transferencia = (Transferencia) this.manager.getTransferenciaPorId(transferencia.getId());
        Assert.assertTrue(transferencia.getValor().floatValue() == 20f);
        
        transferencia = (Transferencia) this.manager.getTransferenciaPorId(transferencia.getId());
        transferencia.setValor(398f);
        this.manager.saveTransferencia(transferencia);
        
        transferencia = (Transferencia) this.manager.getTransferenciaPorId(transferencia.getId());
        Assert.assertTrue(transferencia.getAtivoTransferido().getValor().doubleValue() == 398f);
        
        this.manager.deleteTransferencia(transferencia);
        this.manager.deleteConta(cProventos);
        this.manager.deleteConta(cSalario);
        this.manager.deleteContaCorrente(contaCorrente);
        this.manager.deleteContaCorrente(contaCorrenteDestino);
        this.manager.deleteBanco(banco);
        
        Assert.assertTrue(this.manager.getAtivoPorId(transferencia.getId()) == null);
    }
    
    public static void main(String ... args) {
    	//junit.textui.TestRunner.run(new ScorecardManagerTest("testManterCheque"));
    	//junit.textui.TestRunner.run(new ScorecardManagerTest("testResumoPeriodo"));
    	//junit.textui.TestRunner.run(new ScorecardManagerTest("testOrcadoRealizado"));
    	//junit.textui.TestRunner.run(new ScorecardManagerTest("testManterTransferencia"));
    	//new ScorecardManagerTest().cadastrarContasCorrentes();
    	//junit.textui.TestRunner.run(new ScorecardManagerTest("testGravarSaldoAnterior"));
    	
    	ScorecardManager mm = (ScorecardManager)Util.getBean("scorecardManager");
    	ContaCorrente cc = mm.getContaCorrentePorId(57);
    	mm.consistirSaldosAnteriores(cc);
    	
	}
}