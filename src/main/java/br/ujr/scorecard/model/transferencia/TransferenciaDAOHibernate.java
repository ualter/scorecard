package br.ujr.scorecard.model.transferencia;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import br.ujr.scorecard.model.cc.ContaCorrente;

public class TransferenciaDAOHibernate extends HibernateDaoSupport implements TransferenciaDAO {

	private static Logger logger = Logger.getLogger(TransferenciaDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public List<Transferencia> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Transferencia as Transferencia where upper(Transferencia.descricao) like ? ");
			List<Transferencia> list = this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public Transferencia findById(int id) {
		try {
			return (Transferencia)this.getHibernateTemplate().get(Transferencia.class, new Integer(id));
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public void remove(Transferencia Transferencia) {
		try {
			this.getHibernateTemplate().delete(Transferencia);
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	public Transferencia save(Transferencia Transferencia) {
		try {
			boolean isNew = Transferencia.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(Transferencia);
			} 
			else 
			{
				this.getHibernateTemplate().update(Transferencia);
			}
			return Transferencia;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Transferencia> findByReferencia(ContaCorrente contaCorrente, long referenciaInicial, long referenciaFinal) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Transferencia as Transferencia where Transferencia.referencia >= ? and Transferencia.referencia <= ? ");
			if ( contaCorrente != null ) {
				strQuery.append(" and Transferencia.contaCorrente.id = ? ");
			}
			
			Object[] params = null;
			if ( contaCorrente != null ) {
				params = new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId()};
			} else {
				params = new Object[]{referenciaInicial,referenciaFinal};
			}
			
			List<Transferencia> list = this.getHibernateTemplate().find(strQuery.toString(),params);
			return list;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	
}
