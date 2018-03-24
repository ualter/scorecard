DELIMITER //
DROP PROCEDURE IF EXISTS CarregarReportOrcamento;
CREATE PROCEDURE CarregarReportOrcamento(IN referencia INT)
BEGIN
   DECLARE v_orcado DECIMAL(4);
   DECLARE v_orc_id INTEGER;
   DECLARE v_orc_descricao VARCHAR(100);
   DECLARE v_contaId INTEGER;
   DECLARE v_conta_descricao VARCHAR(100);
   DECLARE v_nivel VARCHAR(10);
   DECLARE fin INTEGER DEFAULT 0;
   DECLARE nivel_conta VARCHAR(10);
   
   DECLARE v_totalPassivos DECIMAL(18,4);
   
   DECLARE orcamento_cursor CURSOR FOR 
      select o.orcado, o.id, o.descricao, c.id, c.descricao, c.nivel from orcamento o, conta c where 
                o.CONTA_ID = c.ID and o.REFERENCIA = referencia and o.conta_corrente_id = 69;
      
   DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin=1;
   
   delete from `scorecard_prd`.`report_orcamento_passivos` where 1 = 1;
   delete from `scorecard_prd`.`report_orcamento` where 1 = 1;
   
   OPEN orcamento_cursor;
   get_orcamentos: LOOP
      FETCH orcamento_cursor INTO v_orcado, v_orc_id, v_orc_descricao, v_contaId, v_conta_descricao, v_nivel;
      IF fin = 1 THEN
         LEAVE get_orcamentos;
      END IF;

      IF right(v_nivel,1) = '0' THEN
         SET nivel_conta = concat(left(v_nivel,1),'%');
	  ELSE 
		 SET nivel_conta = concat(v_nivel,'.%');
      END IF;
      
      -- Grava a Lista de Passivos (Analitico)
      INSERT INTO `scorecard_prd`.`report_orcamento_passivos` (`historico`,`vencto`,`valor`,`referencia`,`conta`,`descricaoConta`,`orcamentoId`,`orcamentoDescricao`,`passivo_id`,`parcelaPassivo_id`) 
      select p.HISTORICO,parc.DATAVENCIMENTO,parc.VALOR,parc.REFERENCIA,c.nivel,c.descricao,v_orc_id, v_orc_descricao,p.id,parc.id from passivo p, parcelapassivo parc, conta c
		where p.ID = parc.PASSIVO_ID and p.CONTA_ID = c.ID 
		and parc.REFERENCIA = referencia
		and p.conta_corrente_id = 69
        and (parc.DEVOLVIDO <> 'T' or parc.DEVOLVIDO is null)
		and c.ID IN (select id from conta where nivel like nivel_conta or nivel = v_nivel);
        
      -- Get Total Valor Passivos para o Grupo de Passivos, 4%, 1.1%, 2.1.2%, etc. 
	  SET v_totalPassivos := 
       (select sum(parc.VALOR) from passivo p, parcelapassivo parc, conta c
		where p.ID = parc.PASSIVO_ID and p.CONTA_ID = c.ID 
		and parc.REFERENCIA = referencia
        and (parc.DEVOLVIDO <> 'T' or parc.DEVOLVIDO is null)
		and p.conta_corrente_id = 69
		and c.ID IN (select id from conta where nivel like nivel_conta or nivel = v_nivel));
        
       -- Grava total da Lista de Passivos (Sintetico)
       INSERT INTO `scorecard_prd`.`report_orcamento` (`referencia`, `orcamento_valor`, `orcamento_desc`, `passivo_valorTotal`, `nivel`) 
	   select referencia,v_orcado,v_orc_descricao,v_totalPassivos,v_nivel;
        
    END LOOP get_orcamentos;
	CLOSE orcamento_cursor;
    
    -- Grava a Lista de Passivos (Analitico) que NÃO está relacionado a nenhum orçamento
    INSERT INTO `scorecard_prd`.`report_orcamento_passivos` (`historico`,`vencto`,`valor`,`referencia`,`conta`,`descricaoConta`,`orcamentoId`,`orcamentoDescricao`,`passivo_id`,`parcelaPassivo_id`) 
    select p.HISTORICO,parc.DATAVENCIMENTO,parc.VALOR,parc.REFERENCIA,c.nivel,c.descricao,0,'Não Orçado',p.id,parc.id from passivo p, parcelapassivo parc, conta c
	  where p.ID = parc.PASSIVO_ID and p.CONTA_ID = c.ID 
	  and parc.REFERENCIA = referencia
      and (parc.DEVOLVIDO <> 'T' or parc.DEVOLVIDO is null)
	  and p.conta_corrente_id = 69
	  and p.id NOT IN (select passivo_Id from `scorecard_prd`.`report_orcamento_passivos` where referencia = referencia);
      
    -- Grava total da Lista de Passivos (Analitico) que NÃO está relacionado a nenhum orçamento
    SET v_totalPassivos := 
    (select sum(valor) from report_orcamento_passivos 
          where referencia = referencia
          and orcamentoId = 0);
    -- Grava total da Lista de Passivos (Sintetico)
	INSERT INTO `scorecard_prd`.`report_orcamento` (`referencia`, `orcamento_valor`, `orcamento_desc`, `passivo_valorTotal`, `nivel`) 
	   select referencia,'0','Não Orçado',v_totalPassivos,'0';      

    SELECT `report_orcamento`.`id`,
    `report_orcamento`.`referencia`,
    `report_orcamento`.`orcamento_valor`,
    `report_orcamento`.`orcamento_desc`,
    `report_orcamento`.`passivo_valorTotal`,
    `report_orcamento`.`nivel` FROM `scorecard_prd`.`report_orcamento`;
	
END //
DELIMITER ;
GRANT EXECUTE ON PROCEDURE scorecard_prd.CarregarReportOrcamento TO 'Scorecard'@'localhost';