package br.ujr.scorecard.model.passivo.cartao;

import br.ujr.scorecard.model.cartao.contratado.CartaoContratado;
import br.ujr.scorecard.model.passivo.Passivo;


/**
 * Cartao de Crédito
 * @author <a href="ualter@uol.com.br">Ualter Otoni Azambuja Junior</a>  
 */
public class Cartao extends Passivo
{
	public enum CartaoCatalogo 
	{
		VISA, VISA_ELECTRON, MASTERCARD, VISA_SIN;
	}
	
	//Todo: refactoring de operadora para um catalogo de cartoes
	private int cartaoCatalogo;
	private CartaoContratado cartaoContratado;
	
	public Cartao()
	{
		super();
	}
	
	public int getCartaoCatalogo()
	{
		return cartaoCatalogo;
	}
	public void setCartaoCatalogo(int i)
	{
		cartaoCatalogo = i;
	}
	public void setOperadora(CartaoCatalogo enumOperadora)
	{
		this.cartaoCatalogo = enumOperadora.ordinal();
	}
	public Cartao.CartaoCatalogo getEnumOperadora() {
		return CartaoCatalogo.values()[this.cartaoCatalogo];
	}
	
	public String getNomeOperadora()
	{
		CartaoCatalogo enumOperadora = CartaoCatalogo.values()[this.cartaoCatalogo];
		return enumOperadora.name();
	}

	public CartaoContratado getCartaoContratado() {
		return cartaoContratado;
	}

	public void setCartaoContratado(CartaoContratado cartaoContratado) {
		this.cartaoContratado = cartaoContratado;
	}
	
	
}
