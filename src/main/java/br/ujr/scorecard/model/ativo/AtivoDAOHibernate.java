package br.ujr.scorecard.model.ativo;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.conta.Conta;

public class AtivoDAOHibernate extends HibernateDaoSupport implements AtivoDAO {

	private static Logger logger = Logger.getLogger(AtivoDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public List<Ativo> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Ativo as ativo where upper(ativo.descricao) like ?0 ");
			List<Ativo> list = (List<Ativo>)this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public Ativo findById(int id) {
		try {
			return (Ativo)this.getHibernateTemplate().get(Ativo.class, new Integer(id));
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public void remove(Ativo ativo) {
		try {
			this.getHibernateTemplate().delete(ativo);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	//@Transactional
	public Ativo save(Ativo ativo) {
		try {
			boolean isNew = ativo.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(ativo);
			} 
			else 
			{
				this.getHibernateTemplate().update(ativo);
			}
			return ativo;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Ativo> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Ativo as ativo where ativo.referencia >= ?0 and ativo.referencia <= ?1 ");
			if ( contaCorrente != null )
				strQuery.append(" and ativo.contaCorrente.id = ?2 ");
			
			Object[] params = null;
			if ( contaCorrente != null ) {
				params = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId()};
			} else {
				params = new Object[]{referenciaInicial,referenciaFinal};
			}
			
			List<Ativo> list = (List<Ativo>)this.getHibernateTemplate().find(strQuery.toString(),params);
			// Distinct - Retirando as ocorrencias dos ativos duplicados
			/*HashSet<Ativo> distinctCollection = new HashSet<Ativo>(list);
			return new ArrayList<Ativo>(distinctCollection);*/
			return list;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Ativo> findByReferencia(ContaCorrente contaCorrente, Class ativoClass, long referenciaInicial, long referenciaFinal) {
		try {
			String alias = ativoClass.getSimpleName().substring(0,1).toLowerCase() + ativoClass.getSimpleName().substring(1);
			StringBuffer strQuery = new StringBuffer(" from ");
			strQuery.append(ativoClass.getSimpleName()).append(" as ").append(alias);
			strQuery.append(" where ");
			strQuery.append(alias).append(".referencia >= ?0 and ").append(alias).append(".referencia <= ?1");
			if ( contaCorrente != null ) {
				strQuery.append(" and ");
				strQuery.append(alias).append(".contaCorrente.id = ?2 ");
			}
			
			Object[] params = null;
			if ( contaCorrente != null ) {
				params = new Object[]{referenciaInicial, referenciaFinal,contaCorrente.getId()};
			} else {
				params = new Object[]{referenciaInicial, referenciaFinal};
			}
			List<Ativo> list = (List<Ativo>)this.getHibernateTemplate().find(strQuery.toString(),params);
			
			// Distinct - Retirando as ocorrencias dos ativos duplicados
			/*HashSet<Ativo> distinctCollection = new HashSet<Ativo>(list);
			return new ArrayList<Ativo>(distinctCollection);*/
			return list;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public List<Ativo> findByContaContabil(Conta conta) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Ativo as ativo where ativo.conta.id = ?0");
			List<Ativo> list = (List<Ativo>)this.getHibernateTemplate().find(strQuery.toString(),new Object[]{conta.getId()});
			return list;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
}
