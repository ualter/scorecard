package br.ujr.scorecard.model.passivo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import br.com.ujr.utils.TimeTracker;
import br.com.ujr.utils.TimeTracker.TimeTrack;
import br.ujr.scorecard.model.cc.ContaCorrente;

public class PassivoDAOHibernate extends HibernateDaoSupport implements PassivoDAO {

	private static Logger logger = Logger.getLogger(PassivoDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public Set<Passivo> findByHistorico(String historico) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Passivo as passivo where upper(passivo.historico) like ? ");
			List<Passivo> list = (List<Passivo>)this.getHibernateTemplate().find(strQuery.toString(),historico.toUpperCase());
			return new HashSet(list);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<Passivo> findByValor(ContaCorrente contaCorrente, float valor) {
		try {
			Set<Passivo> result = new HashSet<Passivo>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.valor = ?");
			if ( contaCorrente != null )
				strQuery.append(" and P.passivo.contaCorrente.id = ?");
			
			Object[] params = null;
			if ( contaCorrente != null ) {
				params = new Object[]{valor,contaCorrente.getId()};
			} else {
				params = new Object[]{valor};
			}
			List list = this.getHibernateTemplate().find(strQuery.toString(),params);
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				/**
				 * Atribuir o número total real de parcelas que o Passivo possui antes da filtragem das parcelas pelo período requisitado
				 */
				Passivo passivo = (Passivo)objects[0];
				passivo.setTotalParcelas(passivo.getParcelas().size());
				result.add((Passivo)objects[0]);
			}
			
			return result;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public Passivo findById(int id) {
		try {
			return (Passivo)this.getHibernateTemplate().get(Passivo.class, new Integer(id));
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@Transactional
	public void remove(Passivo passivo) {
		try {
			this.getHibernateTemplate().delete(passivo);
			this.getHibernateTemplate().flush();
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@Transactional
	public Passivo save(Passivo passivo) {
		try {
			boolean isNew = passivo.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(passivo);
				this.getHibernateTemplate().flush();
			} 
			else 
			{
				this.getHibernateTemplate().update(passivo);
				this.getHibernateTemplate().flush();
			}
			return passivo;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public Set<Passivo> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		try {
			Set<Passivo> result = new HashSet<Passivo>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.referencia >= ?0 and P.referencia <= ?1 ");
			if ( contaCorrente != null )
				strQuery.append(" and P.passivo.contaCorrente.id = ?2");
			
			Object[] params = null;
			if ( contaCorrente != null ) {
				params = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId()};
			} else {
				params = new Object[]{referenciaInicial,referenciaFinal};
			}
			List list = this.getHibernateTemplate().find(strQuery.toString(),params);
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				/**
				 * Atribuir o número total real de parcelas que o Passivo possui antes da filtragem das parcelas pelo período requisitado
				 */
				Passivo passivo = (Passivo)objects[0];
				passivo.setTotalParcelas(passivo.getParcelas().size());
				result.add((Passivo)objects[0]);
			}
			
			return result;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Set<Passivo> findSpecificByReferencia(ContaCorrente contaCorrente, Class clazz, long referenciaInicial, long referenciaFinal) {
		try {
			Set<Passivo> result = new HashSet<Passivo>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.referencia >= ?0 and P.referencia <= ?1 ");
			strQuery.append(" and P.passivo.contaCorrente.id = ?2 ");
			
			List list = this.getHibernateTemplate().find(strQuery.toString(),
					new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId()});
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				Passivo passivo = (Passivo)objects[0];
				/**
				 * Atribuir o número total real de parcelas que o Passivo possui antes da filtragem das parcelas pelo período requisitado
				 */
				passivo.setTotalParcelas(passivo.getParcelas().size());
				/**
				 * Filtrar os passivos apenas da Classe requerida
				 */
				if ( passivo.getClass() == clazz) {
					result.add(passivo);
				}
			}
			
			return result;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public Set<Passivo> findByContaContabilId(int contaId) {
		try {
			Set<Passivo> result = new HashSet<Passivo>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P from Passivo as P ");
			strQuery.append(" where P.conta.id = ?");
			
			List list = this.getHibernateTemplate().find(strQuery.toString(),
					new Object[]{contaId});
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Object[] objects = (Object[]) iterator.next();
				Passivo passivo = (Passivo)objects[0];
				/**
				 * Atribuir o número total real de parcelas que o Passivo possui
				 */
				passivo.setTotalParcelas(passivo.getParcelas().size());
				result.add(passivo);
			}
			
			return result;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public Set<Passivo> findByContaContabilNiveis(ContaCorrente contaCorrente, String[] niveis, boolean incluirDescendentes, long refIni, long refFim) {
		try {
			
			boolean todasAsContasCorrentes = contaCorrente != null ? false : true; 
			
			Set<Passivo> result = new HashSet<Passivo>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo from Parcela as P ");
			strQuery.append(" where ");
			
			StringBuffer queryBuffers = new StringBuffer();
			for (int i = 0; i < niveis.length; i++) {
				if ( queryBuffers.length() > 0 ) queryBuffers.append(" or ");
				//queryBuffers.append(" P.passivo.conta.nivel like ? ");
				// Busca o Nível exato selecionado e o seus descendentes
				queryBuffers.append(" (P.passivo.conta.nivel like ? or P.passivo.conta.nivel = '").append(niveis[i]).append("')");
			}
			
			strQuery.append("(").append(queryBuffers.toString()).append(")");
			strQuery.append(" and   P.referencia >= ? and P.referencia <= ? ");
			if ( !todasAsContasCorrentes )
				strQuery.append(" and   P.passivo.contaCorrente.id = ?");
			
			/**
			 * Retirar o Zero de Níveis como 1.0, 2.0, 
			 * tornando-os 1.%, 2.%, pois não existem parcelas para 1.0%
			 */
			if ( incluirDescendentes ) {
				for (int i = 0; i < niveis.length; i++) {
					if ( niveis[i].substring(niveis[i].length() - 1, niveis[i].length()).equals("0") ) {
						niveis[i] = niveis[i].substring(0,niveis[i].length() - 1); 
					}
				}
			}
			if ( incluirDescendentes ) {
				for (int i = 0; i < niveis.length; i++) {
					if ( niveis[i].substring(niveis[i].length() - 1).equals(".") ) {
						niveis[i] += "%";
					} else {
						niveis[i] += ".%";
					}
				}
			}
			
			Object[] parameters = new Object[niveis.length + ( !todasAsContasCorrentes ? 3 : 2 )];
			for (int i = 0; i < niveis.length; i++) {
				parameters[i] = niveis[i];
			}
			parameters[niveis.length    ] = refIni;
			parameters[niveis.length + 1] = refFim;
			if ( !todasAsContasCorrentes )
				parameters[niveis.length + 2] = contaCorrente.getId();
			
			List list = this.getHibernateTemplate().find(strQuery.toString(),parameters);
			
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Passivo passivo = (Passivo) iterator.next();
				/**
				 * Atribuir o número total real de parcelas que o Passivo possui
				 */
				passivo.setTotalParcelas(passivo.getParcelas().size());
				result.add(passivo);
			}
			
			return result;
			
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

}
