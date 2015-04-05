package br.ujr.scorecard.model.passivo.cartao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import br.ujr.scorecard.model.cc.ContaCorrente;
import br.ujr.scorecard.model.passivo.Passivo;
import br.ujr.scorecard.util.Util;

public class CartaoDAOHibernate extends HibernateDaoSupport implements CartaoDAO  {

	private static Logger logger = Logger.getLogger(CartaoDAOHibernate.class);
	
	@SuppressWarnings("unchecked")
	public Set<Cartao> getCartaoPorOperadora(ContaCorrente contaCorrente, Cartao.Operadora enumOperadora, long referenciaInicial, long referenciaFinal) {
		try {
			Set<Cartao> result = new HashSet<Cartao>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.referencia >= ? and P.referencia <= ? ");
			strQuery.append(" and P.passivo.contaCorrente.id = ? ");
			strQuery.append(" and P.passivo.operadora = ? ");
			
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
	
	public Set<Cartao> getCartaoPorFiltro(long referenciaInicial, long referenciaFinal, Cartao cartao) {
		try {
			
			List<Object> listParams = new ArrayList<Object>();
			listParams.add(referenciaInicial);
			listParams.add(referenciaFinal);
			listParams.add(cartao.getContaCorrente());
			
			Set<Cartao> result = new HashSet<Cartao>();
			StringBuffer strQuery = new StringBuffer();
			strQuery.append(" select P.passivo, P from Parcela as P ");
			strQuery.append(" where P.referencia >= ? and P.referencia <= ? ");
			strQuery.append(" and P.passivo.contaCorrente.id = ? ");
			/*
			 * Operadora
			 */
			if ( cartao.getEnumOperadora() != null ) {
				strQuery.append(" and P.passivo.operadora = ? ");
				listParams.add(cartao.getEnumOperadora().ordinal());
			}
			/* 
			 * Valor
			 */
			if ( cartao.getParcela() != null ) {
				strQuery.append(" and P.valor = ? ");
				String vlr = Util.formatCurrency(cartao.getParcela().getValor(),true); 
				listParams.add( Util.cleanNumber(vlr) );
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
