/*
 * Observacao.java created on 31/10/2004, 18:05:46
 */

package br.ujr.scorecard.model.observacao;

import br.ujr.scorecard.model.persistence.BusinessObject;

/**
 * Controla observações referentes a um movimento mensal
 * @author ualter.junior
 */
public class Observacao extends BusinessObject
{
    private long   referencia;
    private String descricao;

    public Observacao()
    {
        
    }
    public Observacao(long referencia, String descricao)
    {
        this.referencia = referencia;
        this.descricao  = descricao;
    }
    public String getDescricao()
    {
        return descricao;
    }
    public long getReferencia()
    {
        return referencia;
    }
    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }
    public void setReferencia(long referencia)
    {
        this.referencia = referencia;
    }
}
