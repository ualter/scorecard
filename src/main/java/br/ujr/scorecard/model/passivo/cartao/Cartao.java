package br.ujr.scorecard.model.passivo.cartao;

import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.passivo.Passivo;


/**
 * Cartao de Crédito
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Cartao extends Passivo
{
	public enum Operadora 
	{
		VISA, VISA_ELECTRON, MASTERCARD;
	}
	
	private int operadora;
	private CartaoContratado cartaoContratado;
	
	public Cartao()
	{
		super();
	}
	
	public int getOperadora()
	{
		return operadora;
	}
	public void setOperadora(int i)
	{
		operadora = i;
	}
	public void setOperadora(Operadora enumOperadora)
	{
		this.operadora = enumOperadora.ordinal();
	}
	public Cartao.Operadora getEnumOperadora() {
		return Operadora.values()[this.operadora];
	}
	
	public String getNomeOperadora()
	{
		Operadora enumOperadora = Operadora.values()[this.operadora];
		return enumOperadora.name();
	}

	public CartaoContratado getCartaoContratado() {
		return cartaoContratado;
	}

	public void setCartaoContratado(CartaoContratado cartaoContratado) {
		this.cartaoContratado = cartaoContratado;
	}
	
	
}
