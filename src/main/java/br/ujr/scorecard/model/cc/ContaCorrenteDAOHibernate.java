package br.ujr.scorecard.model.cc;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import br.ujr.scorecard.model.banco.Banco;

public class ContaCorrenteDAOHibernate extends HibernateDaoSupport implements ContaCorrenteDAO {

	private static Logger log = Logger.getLogger(ContaCorrenteDAOHibernate.class);
	
	public void remove(ContaCorrente contaCorrente) {
		try {
			this.getHibernateTemplate().delete(contaCorrente);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}

	}

	public ContaCorrente save(ContaCorrente contaCorrente) {
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
	public List<ContaCorrente> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from ContaCorrente as cc where upper(cc.descricao) like ? ");
			List<ContaCorrente> list = (List<ContaCorrente>)this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public ContaCorrente findById(int id) {
		try {
			return (ContaCorrente)this.getHibernateTemplate().get(ContaCorrente.class, new Integer(id));
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public List<ContaCorrente> list() {
		try {
			StringBuffer strQuery = new StringBuffer(" from ContaCorrente as cc");
			List<ContaCorrente> list = (List<ContaCorrente>)this.getHibernateTemplate().find(strQuery.toString());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}
	
	public boolean isRemovable(ContaCorrente contaCorrente) {
		try {
			StringBuffer strQuery = new StringBuffer("select P from Passivo As P where P.contaCorrente.id = " + contaCorrente.getId());
			if ( this.getHibernateTemplate().find(strQuery.toString()).size() > 0 ) {
				return false;
			}
			strQuery = new StringBuffer("select A from Ativo As A where A.contaCorrente.id = " + contaCorrente.getId());
			if ( this.getHibernateTemplate().find(strQuery.toString()).size() > 0 ) {
				return false;
			}
			return true;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	@Override
	public List<ContaCorrente> findByBanco(Banco banco) {
		try {
			StringBuffer strQuery = new StringBuffer(" from ContaCorrente as cc where cc.banco.id = ?0 ");
			List<ContaCorrente> list = (List<ContaCorrente>)this.getHibernateTemplate().find(strQuery.toString(),banco.getId());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
