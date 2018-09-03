package br.ujr.scorecard.model.passivo.cartao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.Passivo;

public class CartaoDAOHibernate extends HibernateDaoSupport implements CartaoDAO  {

	private static Logger logger = Logger.getLogger(CartaoDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Cartao.CartaoCatalogo enumOperadora, long referenciaInicial, long referenciaFinal) {
		try {
			Set<Cartao> result = new HashSet<Cartao>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.referencia >= ?0 and P.referencia <= ?1 ");
			strQuery.append(" and P.passivo.contaCorrente.id = ?2 ");
			strQuery.append(" and P.passivo.cartaoCatalogo = ?3 ");
			
			List list = this.getHibernateTemplate().find(strQuery.toString(),
					new Object[]{referenciaInicial,referenciaFinal,contaCorrente.getId(),enumOperadora.ordinal()});
			
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
				if ( passivo.getClass() == Cartao.class) {
					result.add((Cartao)passivo);
				}
			}
			
			return result;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	
	public Set<Cartao> getCartaoPorFiltro(long referenciaInicial, long referenciaFinal, Cartao cartao, Date dataMovimento) {
		try {
			
			List<Object> listParams = new ArrayList<Object>();
			listParams.add(referenciaInicial);
			listParams.add(referenciaFinal);
			listParams.add(cartao.getContaCorrente().getId());
			
			Set<Cartao> result = new HashSet<Cartao>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.referencia >= ?0 and P.referencia <= ?1 ");
			strQuery.append(" and P.passivo.contaCorrente.id = ?2 ");
			/*
			 * Cartao Contratado
			 */
			if ( cartao.getCartaoContratado() != null ) {
				strQuery.append(" and P.passivo.cartaoContratado.id = ?3 ");
				listParams.add(cartao.getCartaoContratado().getId());
			}
			/* 
			 * Valor
			 */
			if ( cartao.getParcela() != null ) {
				strQuery.append(" and P.valor = ?4 ");
				listParams.add( cartao.getParcela().getValor() );
			}
			/* 
			 * Data Movimento
			 */
			if ( dataMovimento != null ) {
				strQuery.append(" and P.passivo.dataMovimento = ?5 ");
				listParams.add( dataMovimento );
			}
			
			Object[] params = listParams.toArray();
			List list = this.getHibernateTemplate().find(strQuery.toString(),params);
			
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
				if ( passivo.getClass() == Cartao.class) {
					result.add((Cartao)passivo);
				}
			}
			
			return result;
		} catch (DataAccessException e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
	
	

}
