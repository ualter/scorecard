package br.ujr.scorecard.model.passivo.cheque;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ChequeDAOHibernate extends HibernateDaoSupport implements ChequeDAO {

	private static Logger log = Logger.getLogger(ChequeDAOHibernate.class);
	
	public Cheque getChequePorNumero(String numero) {
		try {
			StringBuffer strQuery = new StringBuffer(" select cheque from Cheque as cheque join cheque.parcelas as parcela where parcela.numeroCheque = :numeroParcela");
			List list = this.getHibernateTemplate().findByNamedParam(strQuery.toString(), "numeroParcela", numero);
			return (Cheque) list.get(0);
		} catch (DataAccessException e) {
			log.error(e.getMessage(),e);
			throw e;
		}
	}

}
