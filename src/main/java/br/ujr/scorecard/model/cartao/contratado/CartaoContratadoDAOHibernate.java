package br.ujr.scorecard.model.cartao.contratado;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import br.ujr.scorecard.model.banco.Banco;
import br.ujr.scorecard.model.cc.ContaCorrente;

public class CartaoContratadoDAOHibernate extends HibernateDaoSupport implements CartaoContratadoDAO {

	private static Logger log = Logger.getLogger(CartaoContratadoDAOHibernate.class);
	
	public void remove(CartaoContratado contaCorrente) {
		try {
			this.getHibernateTemplate().delete(contaCorrente);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}

	}

	public CartaoContratado save(CartaoContratado contaCorrente) {
		/*
		 * Another way 
		 * Session session  = this.getSessionFactory().openSession(); 
		 * Transaction tx   = session.beginTransaction(); 
		 * Integer sequence = (Integer)session.save(investmentType); 
		 * tx.commit();
		 * session.close();
		 */
		try {
			boolean isNew = contaCorrente.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(contaCorrente);
			} 
			else 
			{
				this.getHibernateTemplate().update(contaCorrente);
			}
			return contaCorrente;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CartaoContratado> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from CartaoContratado as cc where upper(cc.descricao) like ? ");
			List<CartaoContratado> list = this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public CartaoContratado findById(int id) {
		try {
			return (CartaoContratado)this.getHibernateTemplate().get(CartaoContratado.class, new Integer(id));
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public List<CartaoContratado> list() {
		try {
			StringBuffer strQuery = new StringBuffer(" from ContaCorrente as cc");
			List<CartaoContratado> list = this.getHibernateTemplate().find(strQuery.toString());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	@Override
	public List<CartaoContratado> findByContaCorrente(ContaCorrente contaCorrente) {
		try {
			StringBuffer strQuery = new StringBuffer(" from CartaoContratado as cc where cc.contaCorrente.id = ?");
			List<CartaoContratado> list = this.getHibernateTemplate().find(strQuery.toString(),contaCorrente.getId());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
