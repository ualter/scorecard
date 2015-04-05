package br.ujr.scorecard.model.banco;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class BancoDAOHibernate extends HibernateDaoSupport implements BancoDAO {

	private static Logger log = Logger.getLogger(BancoDAOHibernate.class);
	
	public void remove(Banco Banco) {
		try {
			this.getHibernateTemplate().delete(Banco);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}

	}

	public Banco save(Banco banco) {
		/*
		 * Another way 
		 * Session session  = this.getSessionFactory().openSession(); 
		 * Transaction tx   = session.beginTransaction(); 
		 * Integer sequence = (Integer)session.save(investmentType); 
		 * tx.commit();
		 * session.close();
		 */
		try {
			boolean isNew = banco.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(banco);
			} 
			else 
			{
				this.getHibernateTemplate().update(banco);
			}
			return banco;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Banco> findByNome(String nome) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Banco as Banco where upper(Banco.nome) like ? ");
			List<Banco> list = this.getHibernateTemplate().find(strQuery.toString(),nome.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public Banco findById(int id) {
		try {
			return (Banco)this.getHibernateTemplate().get(Banco.class, new Integer(id));
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public List<Banco> list() {
		try {
			StringBuffer strQuery = new StringBuffer(" from Banco as Banco");
			List<Banco> list = this.getHibernateTemplate().find(strQuery.toString());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public boolean isBancoRemovable(Banco banco) {
		try {
			StringBuffer strQuery = new StringBuffer("select C from ContaCorrente As C where C.banco.id = " + banco.getId());
			if ( this.getHibernateTemplate().find(strQuery.toString()).size() > 0 ) {
				return false;
			}
			return true;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
