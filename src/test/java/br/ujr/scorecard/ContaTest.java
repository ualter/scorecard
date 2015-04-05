package br.ujr.scorecard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import br.ujr.scorecard.model.ScorecardManager;
import br.ujr.scorecard.model.conta.Conta;
import br.ujr.scorecard.util.Util;

/**
 * 
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior </a>
 */
public class ContaTest extends TestCase
{

    protected ScorecardManager manager;

    protected void setUp() throws Exception
    {
    	this.manager = Util.getBean("scorecardManager");
    }

    public void testProximoNivel() 
    {
    	 // Consulta Próximo Nível
        Conta contaAlimentacao = new Conta("2.0","ALIMENTAÇÃO");
        contaAlimentacao.addContaFilho(new Conta("2.1","ALMOÇO"));
        manager.saveConta(contaAlimentacao);
        List list = manager.getContasPorNivel("2.0");
        Assert.assertTrue(list.size() == 1);
        Conta almoco = (Conta)((Conta)list.get(0)).getContasFilhos().iterator().next();
        Assert.assertTrue(almoco.getNivel().equals("2.1"));
        Assert.assertTrue(almoco.getDescricao().equals("ALMOÇO"));
        String proximoNivel = manager.getContaProximoNivel();
        Assert.assertTrue(proximoNivel.equals("3.0"));
        proximoNivel = manager.getContaProximoNivel("3.0");
        Assert.assertTrue(proximoNivel.equals("3.1"));
        contaAlimentacao.addContaFilho(new Conta(proximoNivel,"JANTAR"));
        manager.saveConta(contaAlimentacao);
        proximoNivel = manager.getContaProximoNivel("3.0");
        Assert.assertTrue(proximoNivel.equals("3.2"));
        proximoNivel = manager.getContaProximoNivel("3.1");
        Assert.assertTrue(proximoNivel.equals("3.1.1"));
        contaAlimentacao.addContaFilho(new Conta(proximoNivel,"JANTAR FORA"));
        manager.saveConta(contaAlimentacao);
        proximoNivel = manager.getContaProximoNivel("3.1");
        Assert.assertTrue(proximoNivel.equals("3.1.2"));
        
        // Deleção
        manager.deleteConta(contaAlimentacao);
        list = manager.getContaPorDescricao("ALIMENTAÇÃO");
        Assert.assertTrue(list.size() == 0);
    }
    
	public void testManterConta()
    {
        Conta contaTransporte;
        Conta contaCarro;
        Conta contaOficina;

        contaTransporte = new Conta();
        contaTransporte.setDescricao("TRANSPORTE TESTE");
        contaTransporte.setNivel("1.0");

        contaCarro = new Conta();
        contaCarro.setDescricao("CARRO TESTE");
        contaCarro.setNivel("1.1");

        contaOficina = new Conta();
        contaOficina.setDescricao("OFICINA TESTE");
        contaOficina.setNivel("1.2");

        contaTransporte.addContaFilho(contaCarro);
        contaTransporte.addContaFilho(contaOficina);

        manager.saveConta(contaTransporte);
        
        // Consulta por Id
        Assert.assertNotNull(manager.getContaPorId(contaTransporte.getId()));

        // Consulta por Nivel
        List list = manager.getContasPorNivel("1.2");
        Assert.assertTrue(list.size() == 1);

        // Consulta por Descrição
        list = manager.getContaPorDescricao("TRANSPORTE TESTE");
        Assert.assertTrue(list.size() >= 1);
        contaTransporte = (Conta) list.get(0);
        
        // Alteração Inclusão Conta
        contaOficina = new Conta();
        contaOficina.setDescricao("GASOLINA");
        contaOficina.setNivel("1.3");
        contaTransporte.addContaFilho(contaOficina);
        manager.saveConta(contaTransporte);
        contaTransporte = manager.getContaPorId(contaTransporte.getId());
        int total = contaTransporte.getContasFilhos().size();
        Assert.assertTrue(total == 3);
        
        // Alteração Exclusão Conta
        Conta contaRemover = null;
        for (Iterator i = contaTransporte.getContasFilhos().iterator(); i.hasNext();) {
			Conta c = (Conta) i.next();
			if ( c.getNivel().equals("1.3") ) {
				contaRemover = c;
				break;
			}
		}
        contaTransporte.removeContaFilho(contaRemover);
        manager.saveConta(contaTransporte);
        contaTransporte = manager.getContaPorId(contaTransporte.getId());
        total = contaTransporte.getContasFilhos().size();
        Assert.assertTrue(total == 2);

        // Deleção
        manager.deleteConta(contaTransporte);
        list = manager.getContaPorDescricao("TRANSPORTE TESTE");
        Assert.assertTrue(list.size() == 0);
    }
	
	public void testComparacao() {
		Conta conta = new Conta("1.0","TRANSPORTE");
		conta.setId(1);
        Assert.assertTrue(conta.equals(conta));
        Assert.assertTrue(!conta.equals(new Object()));
        conta.toString();
	}
	
	@SuppressWarnings("unchecked")
	public void testOrdenacao() {
		Conta transporte  = new Conta("1.0","TRANSPORTE");
		Conta alimentacao = new Conta("2.0","ALIMENTACAO");
		Conta lazer       = new Conta("3.0","LAZER");
		
		transporte.setId(1);
		alimentacao.setId(2);
		lazer.setId(3);
		
        List<Conta> order = new ArrayList<Conta>();
        order.add(transporte);
        order.add(alimentacao);
        order.add(lazer);
        Collections.sort(order);
	}

}