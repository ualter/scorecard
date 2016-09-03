package br.ujr.scorecard.model.orcamento;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.model.passivo.cheque.Cheque;
import br.ujr.scorecard.model.passivo.parcela.Parcela;

public class OrcamentoDAOHibernate extends HibernateDaoSupport implements OrcamentoDAO {

	private static Log log = LogFactory.getLog(OrcamentoDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public Set<Orcamento> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Orcamento as orca where upper(orca.descricao) like ? ");
			List<Orcamento> list = this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return new HashSet(list);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public Orcamento findById(int id) {
		try {
			return (Orcamento)this.getHibernateTemplate().get(Orcamento.class, new Integer(id));
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public void remove(Orcamento orcamento) {
		try {
			this.getHibernateTemplate().delete(orcamento);
			this.getHibernateTemplate().flush();
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public boolean save(Orcamento orcamento) {
		try {
			boolean isNew = orcamento.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				if ( !this.checkOrcamentoContaExistente(orcamento) ) {
					this.getHibernateTemplate().save(orcamento);
				} else {
					return false;
				}
			} 
			else 
			{
				this.getHibernateTemplate().update(orcamento);
			}
			this.getHibernateTemplate().flush();
			return true;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	private boolean checkOrcamentoContaExistente(Orcamento orcamento) {
		StringBuffer strQuery = new StringBuffer();
		strQuery.append("  select O from Orcamento as O ");
		strQuery.append("  where O.contaAssociada.id = ? and O.referencia = ? and O.contaCorrente.id = ?");
		List list = this.getHibernateTemplate().find(strQuery.toString(),
				new Object[]{orcamento.getContaAssociada().getId(),orcamento.getReferencia(), orcamento.getContaCorrente().getId()});
		if ( list.size() > 0 ) {
			return true;
		}
		return false;
	}
	
	public Set<Passivo> listPassivosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta) {
		try {
			
			if ( nivelConta.endsWith("0") ) {
				nivelConta = nivelConta.substring(0, 2);
			}
			nivelConta += "%";
			
			Set<Passivo> passivos = new HashSet<Passivo>();
			String[] params = new String[]{"dataIni","dataFim","contaCorrente","nivelConta"};
			Object[] values = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId(),nivelConta};
			
			
			StringBuffer strQuery = new StringBuffer();
			strQuery.delete(0, strQuery.length());
			strQuery.append("  select P, P.passivo from Parcela as P ");
			strQuery.append("   where P.referencia >= :dataIni and P.referencia <= :dataFim");
			strQuery.append("   and P.passivo.contaCorrente.id = :contaCorrente");
			strQuery.append("   and P.passivo.conta.nivel like :nivelConta ");
			List listPassivos = this.getHibernateTemplate().findByNamedParam(strQuery.toString(), params, values);
			for(Iterator iterator = listPassivos.iterator(); iterator.hasNext();) {
				Object[]   row      = (Object[]) iterator.next();
				Parcela    parcela  = (Parcela)row[0];
				Passivo    passivo  = (Passivo)row[1];
				
				int totalParcelas   = passivo.getParcelas().size();
				
				passivo.cleanParcelas();
				if ( passivo instanceof Cheque ) {
					((Cheque)passivo).addParcela(parcela.getNumeroCheque(), parcela);
				} else {
					passivo.addParcela(parcela);
				}
				
				
				passivo.setTotalParcelas(totalParcelas);
				passivos.add(passivo);
			}
			
			return passivos;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Retorna os Or�amentos de um per�odo.
	 * � calculado tamb�m o que foi realizado, a soma do que foi gasto, ou seja, todas as parcelas dos passivos que pertencem 
	 * a Conta Cont�bil associado ao Or�amento, ou que seja descendente na hierarquia de Contas Cont�beis da Conta Cont�bil associada ao Or�amento. 
	 */
	public Set<Orcamento> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		try {
			Set<Orcamento> result = new HashSet<Orcamento>();
			/**
			 * Buscando Or�amentos referentes ao per�odo requisitado
			 */
			StringBuffer strQuery = new StringBuffer();
			strQuery.append("  select O from Orcamento as O ");
			strQuery.append("  where ");
			strQuery.append("  O.referencia >= :dataIni and O.referencia <= :dataFim ");
			if ( contaCorrente != null ) {
				strQuery.append("  and O.contaCorrente.id = :contaCorrente ");
			}
			String[] params = null;
			Object[] values = null;
			if ( contaCorrente != null ) {
				params = new String[]{"dataIni","dataFim","contaCorrente"};
				values = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId()};
			} else {
				params = new String[]{"dataIni","dataFim"};
				values = new Object[]{referenciaInicial,referenciaFinal};
			}
			List listOrcamentos = this.getHibernateTemplate().findByNamedParam(strQuery.toString(), params, values);
			
			/**
			 * Buscando Somat�rio das Parcelas(Realizado) por N�vel de Contas Cont�beis no per�odo requisitado
			 */
			strQuery.delete(0, strQuery.length());
			strQuery.append("  select P.passivo.conta.nivel, sum(P.valor) from Parcela as P ");
			strQuery.append("   where P.referencia >= :dataIni and P.referencia <= :dataFim");
			if ( contaCorrente != null ) {
				strQuery.append("   and P.passivo.contaCorrente.id = :contaCorrente");
			}
			strQuery.append("   group by P.passivo.conta.nivel");
			if ( contaCorrente != null ) {
				params = new String[]{"dataIni","dataFim","contaCorrente"};
				values = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId()};
			} else {
				params = new String[]{"dataIni","dataFim"};
				values = new Object[]{referenciaInicial,referenciaFinal};
			}
			List listSomaParcelas = this.getHibernateTemplate().findByNamedParam(strQuery.toString(), params, values);
			HashMap<String,BigDecimal> somaPorNivelContaNoPeriodo = new HashMap<String,BigDecimal>();
			for(Iterator iterator = listSomaParcelas.iterator(); iterator.hasNext();) {
				Object[]   row      = (Object[]) iterator.next();
				String     nivel  = (String)row[0];
				BigDecimal total    = (BigDecimal)row[1];
				
				if ( somaPorNivelContaNoPeriodo.containsKey(nivel) ) {
					total = total.add((BigDecimal)somaPorNivelContaNoPeriodo.get(nivel));
					somaPorNivelContaNoPeriodo.put(nivel, total);
				}
				somaPorNivelContaNoPeriodo.put(nivel, total);
			}
			
			/**
			 * Distribuindo a soma de realizados por n�vel para os Or�amentos que t�m relacionamento com sua Conta Cont�bil
			 */
			for (Iterator iterator = listOrcamentos.iterator(); iterator.hasNext();) {
				Orcamento orcamento = (Orcamento)iterator.next();
				
				/**
				 * Nivel da Conta Cont�bil do Or�amento
				 */
				String nivelContaOrcamento = orcamento.getContaAssociada().getNivel();
				
				BigDecimal jahRealizado = null;
				/**
				 * Se nivel da conta cont�bil do or�amento for um n�vel de raiz, exemplo: "4.0", cortar o zero para buscar com as iniciais a partir do ponto, 
				 * assim... ser� buscado contas que iniciem com "4.", e n�o com "4.0". 
				 * Os alvos neste caso poder�o ser "4.1", "4.2", "4.1.1", "4.3.2.1", etc.
				 * 
				 * Caso n�o seja raiz, seja por exemplo "4.2", nada � feito, busca-se filhas de "4.2", como "4.2.1", "4.2.1.2", etc.
				 * 
				 *   ...a condicional abaixo sobre o teste de tamanho (lenght <= 3) 
				 *      � por causa de contas filhas que terminam com zero como 5.10 ou 6.10, etc. e n�o s�o contas "mestres", como a 5.0 ou 6.0
				 */
				if ( nivelContaOrcamento.endsWith("0") && nivelContaOrcamento.length() <= 3) {
					nivelContaOrcamento = nivelContaOrcamento.substring(0, 2);
				}
				
				/**
				 * Itera��o sobre os totais realizados por n�vel de conta cont�bil procurando por contas cont�beis de n�vel descendente da do or�amento
				 */
				for(String key : somaPorNivelContaNoPeriodo.keySet()) {
					/**
					 * Inicial do N�vel do Total Realizado � igual a do Or�amento em quest�o ?  (Ex:  "4.1.2.1" come�a com "4.1" ?) 
					 */
					if ( key.startsWith(nivelContaOrcamento) ) {
						/**
						 * Caso positivo faz parte do realizado deste Or�amento, soma-se ao total realizado do mesmo.
						 */
						if ( jahRealizado == null ) jahRealizado = new BigDecimal(0);
						jahRealizado = jahRealizado.add((BigDecimal)somaPorNivelContaNoPeriodo.get(key));
					}
				}
				
				if ( jahRealizado == null ) {
					jahRealizado = new BigDecimal(0);
				}
				orcamento.setRealizado(orcamento.getRealizado().add(jahRealizado));
				result.add(orcamento);
			}
			return result;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
}
