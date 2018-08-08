package br.ujr.scorecard.model.conta;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

public class ContaDAOHibernate extends HibernateDaoSupport implements ContaDAO {

	private static Logger log = Logger.getLogger(ContaDAOHibernate.class);
	
	public void remove(Conta conta) {
		try {
			this.getHibernateTemplate().delete(conta);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}

	}

	public Conta save(Conta conta) {
		/*
		 * Another way 
		 * Session session  = this.getSessionFactory().openSession(); 
		 * Transaction tx   = session.beginTransaction(); 
		 * Integer sequence = (Integer)session.save(investmentType); 
		 * tx.commit();
		 * session.close();
		 */
		try {
			boolean isNew = conta.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(conta);
			} 
			else 
			{
				this.getHibernateTemplate().update(conta);
			}
			return conta;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Conta> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Conta as conta where upper(conta.descricao) like ?0 ");
			List<Conta> list = (List<Conta>)this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List findByNivel(String nivel) {
		try {
			/**
			 * Using Query By Criteria
			 * Considered by many developers a more object-oriented approach
			 */
			DetachedCriteria criteria = DetachedCriteria.forClass(Conta.class);
			criteria.add(Expression.like("nivel",nivel));
			List<Conta> list = (List<Conta>)this.getHibernateTemplate().findByCriteria(criteria);
			return list;
			/**
			 * Using HQL - Hibernate Query Language 
			 * StringBuffer strQuery = new StringBuffer(" from Conta as conta where conta.nivel = ? ");
			 * List list = this.getHibernateTemplate().find(strQuery.toString(),nivel);
			 * return list;
			 */
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public Conta findById(int id) {
		try {
			return (Conta)this.getHibernateTemplate().get(Conta.class, new Integer(id));
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public List<Conta> list() {
		try {
			StringBuffer strQuery = new StringBuffer(" from Conta as conta");
			List<Conta> list = (List<Conta>)this.getHibernateTemplate().find(strQuery.toString());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
