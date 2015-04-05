package br.ujr.scorecard.model.observacao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ObservacaoDAOHibernate extends HibernateDaoSupport implements ObservacaoDAO {

	private static Log log = LogFactory.getLog(ObservacaoDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public List<Observacao> findByDescricao(String descricao) {
		try {
			StringBuffer strQuery = new StringBuffer(" from Observacao as obs where upper(obs.descricao) like ? ");
			List<Observacao> list = this.getHibernateTemplate().find(strQuery.toString(),descricao.toUpperCase());
			return list;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public Observacao findById(int id) {
		try {
			return (Observacao)this.getHibernateTemplate().get(Observacao.class, new Integer(id));
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public void remove(Observacao observacao) {
		try {
			this.getHibernateTemplate().delete(observacao);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

	public Observacao save(Observacao observacao) {
		try {
			boolean isNew = observacao.getId() > 0 ? false : true;
			if ( isNew ) 
			{
				this.getHibernateTemplate().save(observacao);
			} 
			else 
			{
				this.getHibernateTemplate().update(observacao);
			}
			return observacao;
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
