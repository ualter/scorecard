package br.ujr.scorecard.model.orcamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

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
			List<Orcamento> list = (List<Orcamento>)this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
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

	@Transactional
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
		strQuery.append("  where O.contaAssociada.id = ?0 and O.referencia = ?1 and O.contaCorrente.id = ?2");
		List list = this.getHibernateTemplate().find(strQuery.toString(),
				new Object[]{orcamento.getContaAssociada().getId(),orcamento.getReferencia(), orcamento.getContaCorrente().getId()});
		if ( list.size() > 0 ) {
			return true;
		}
		return false;
	}
	
	public Set<Passivo> listPassivosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta) {
		return this.listPassivosOrcamento(contaCorrente, referenciaInicial, referenciaFinal, nivelConta, true);
	}
	
	public Set<Passivo> listPassivosOrcamento(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, String nivelConta, boolean includeChildren) {
		try {
			
			String nivelContaFilhas = nivelConta;
			String jpaQlComparator  = " = ";
			if ( includeChildren ) {
				jpaQlComparator  = " like ";
				nivelContaFilhas = nivelConta + ".%";
				if ( checkContaMestre(nivelConta) ) {
					nivelConta = prepareForContaMestre(nivelConta);
					nivelContaFilhas = nivelConta + "%";
					nivelConta = nivelConta + ".0";
				}
			}
			
			Set<Passivo> passivos = new HashSet<Passivo>();
			String[] params = new String[]{"dataIni","dataFim","contaCorrente","nivelConta", "nivelContaFilhas"};
			Object[] values = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId(),nivelConta, nivelContaFilhas};
			
			StringBuffer strQuery = new StringBuffer();
			strQuery.delete(0, strQuery.length());
			strQuery.append("  select P, P.passivo from Parcela as P ");
			strQuery.append("   where P.referencia >= :dataIni and P.referencia <= :dataFim");
			strQuery.append("   and P.passivo.contaCorrente.id = :contaCorrente");
			strQuery.append("   and (P.passivo.conta.nivel = :nivelConta OR P.passivo.conta.nivel ").append(jpaQlComparator).append(":nivelContaFilhas) ");
			values = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId(),nivelConta, nivelContaFilhas};
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
	 * Retorna os Orçamentos de um período.
	 * É calculado também o que foi realizado, a soma do que foi gasto, ou seja, todas as parcelas dos passivos que pertencem 
	 * a Conta Contábil associado ao Orçamento, ou que seja descendente na hierarquia de Contas Contábeis da Conta Contábil associada ao Orçamento. 
	 */
	public Set<Orcamento> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal, List<PassivosForaOrcamento> listaPassivosForaOrcamento) {
		try {
			Set<Orcamento> result = new HashSet<Orcamento>();
			/**
			 * Buscando Orçamentos referentes ao período requisitado
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
			 * Buscando Somatório das Parcelas(Realizado) por Nível de Contas Contábeis no período requisitado
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
			HashMap<String,Object[]> somaPorNivelContaNoPeriodo = new HashMap<String,Object[]>();
			for(Iterator iterator = listSomaParcelas.iterator(); iterator.hasNext();) {
				Object[]   row      = (Object[]) iterator.next();
				String     nivel  = (String)row[0];
				BigDecimal total    = (BigDecimal)row[1];
				if ( somaPorNivelContaNoPeriodo.containsKey(nivel) ) {
					total = total.add((BigDecimal)somaPorNivelContaNoPeriodo.get(nivel)[0]);
					somaPorNivelContaNoPeriodo.put(nivel, new Object[] {total,false});
				}
				somaPorNivelContaNoPeriodo.put(nivel, new Object[] {total,false});
			}
			
			/**
			 * Distribuindo a soma de realizados por nível para os Orçamentos que têm relacionamento com sua Conta Contábil
			 */
			for (Iterator iterator = listOrcamentos.iterator(); iterator.hasNext();) {
				Orcamento orcamento = (Orcamento)iterator.next();
				
				/**
				 * Nivel da Conta Contábil do Orçamento
				 */
				String nivelContaOrcamento = orcamento.getContaAssociada().getNivel();
				BigDecimal jahRealizado = null;
				
				nivelContaOrcamento = prepareForContaMestre(nivelContaOrcamento);
				
				/**
				 * Iteração sobre os totais realizados por nível de conta contábil procurando por contas contábeis de nível descendente da do orçamento
				 */
				for(String key : somaPorNivelContaNoPeriodo.keySet()) {
					/**
					 * Inicial do Nível do Total Realizado é igual a do Orçamento em questão ?  (Ex:  "4.1.2.1" começa com "4.1" ?) 
					 */
					if ( key.equals(nivelContaOrcamento) || key.startsWith(nivelContaOrcamento + ".") ) {
						/**
						 * Caso positivo faz parte do realizado deste Orçamento, soma-se ao total realizado do mesmo.
						 */
						if ( jahRealizado == null ) jahRealizado = new BigDecimal(0);
						jahRealizado = jahRealizado.add((BigDecimal)somaPorNivelContaNoPeriodo.get(key)[0]);
						somaPorNivelContaNoPeriodo.get(key)[1] = true;
					}
				}
				
				if ( jahRealizado == null ) {
					jahRealizado = new BigDecimal(0);
				}
				orcamento.setRealizado(orcamento.getRealizado().add(jahRealizado));
				result.add(orcamento);
			}
			
			// Calculate the part that is not tabulated at the Budget (Orçamento)
			if ( listaPassivosForaOrcamento != null ) {
				listaPassivosForaOrcamento.clear();
			}
			for( String key : somaPorNivelContaNoPeriodo.keySet()) {
				Object[] row        = somaPorNivelContaNoPeriodo.get(key);
				boolean  tabulated  = (Boolean)row[1];
				if ( !tabulated ) {
					if ( contaCorrente != null  ) {
						PassivosForaOrcamento passivosForaOrcamento = new PassivosForaOrcamento();
						passivosForaOrcamento.setContaContabil(key);
						passivosForaOrcamento.setContaCorrente(contaCorrente);
						passivosForaOrcamento.setTotal((BigDecimal)row[0]);
						Set<Passivo> passivosNotTabulated = this.listPassivosOrcamento(contaCorrente, referenciaInicial, referenciaFinal, key, false);
						passivosForaOrcamento.setPassivos(passivosNotTabulated);
						if ( listaPassivosForaOrcamento == null ) {
							listaPassivosForaOrcamento = new ArrayList<PassivosForaOrcamento>();
						}
						listaPassivosForaOrcamento.add(passivosForaOrcamento);
					}
				}
			}
			
			return result;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	/**
	 * Se nivel da conta contábil do orçamento for um nível de raiz, exemplo: "4.0", cortar o zero para buscar com as iniciais a partir do ponto, 
	 * assim... será buscado contas que iniciem com "4.", e não com "4.0". 
	 * Os alvos neste caso poderão ser "4.1", "4.2", "4.1.1", "4.3.2.1", etc.
	 * 
	 * Caso não seja raiz, seja por exemplo "4.2", nada é feito, busca-se filhas de "4.2", como "4.2.1", "4.2.1.2", etc.
	 * 
	 *   ...a condicional abaixo sobre o teste de tamanho (lenght <= 3) 
	 *      é por causa de contas filhas que terminam com zero como 5.10 ou 6.10, etc. e não são contas "mestres", como a 5.0 ou 6.0
	 */
	private boolean checkContaMestre(String nivelContaOrcamento) {
		return  ( nivelContaOrcamento.endsWith("0") && nivelContaOrcamento.length() <= 3); 
	}
	private String prepareForContaMestre(String nivelContaOrcamento) {
		
		if ( checkContaMestre(nivelContaOrcamento) ) {
			nivelContaOrcamento = nivelContaOrcamento.substring(0, 1);
		}
		return nivelContaOrcamento;
	}
}
