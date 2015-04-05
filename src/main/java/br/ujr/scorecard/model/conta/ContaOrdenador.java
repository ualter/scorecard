package br.ujr.scorecard.model.conta;

import static br.ujr.scorecard.model.conta.ContaOrdenador.Order.DESCRICAO;
import static br.ujr.scorecard.model.conta.ContaOrdenador.Order.NIVEL;

import java.util.Comparator;

public class ContaOrdenador<T> implements Comparator<T>{

	public static ContaOrdenador<Conta> Nivel     = new ContaOrdenador<Conta>(Order.NIVEL);
	public static ContaOrdenador<Conta> Descricao = new ContaOrdenador<Conta>(Order.DESCRICAO);
	
	public enum Order {
		NIVEL, DESCRICAO;
	}
	private Order order;
	
	private ContaOrdenador(Order order){
		this.order = order;
	}
	
	public int compare(T objectOne, T objectTwo) {
		int result = -1;
		switch (this.order) {
			case NIVEL:
			{
				result = this.compareNivel((Conta)objectOne, (Conta)objectTwo);
				break;
			}
			case DESCRICAO:
			{
				result = this.compareDescricao((Conta)objectOne, (Conta)objectTwo);
				break;
			}
			default:
				break;
		}
		return result;
	}
	
	private int compareNivel(Conta contaOne, Conta contaTwo) {
		return ContaContabilNivelOrdenador.compare(contaOne.getNivel(), contaTwo.getNivel());
	}
	
	private int compareDescricao(Conta contaOne, Conta contaTwo) {
		return contaOne.getDescricao().compareTo(contaTwo.getDescricao());
	}

}
