DELIMITER //
DROP PROCEDURE IF EXISTS cartaoContratado_vs_cartaoPassivo;
CREATE PROCEDURE cartaoContratado_vs_cartaoPassivo
()
BEGIN

  DECLARE v_finished INTEGER DEFAULT 0;
  DECLARE v_passivo_id INTEGER;
  DECLARE v_operadora INTEGER;
  DECLARE v_conta_corrente_id INTEGER;
  DECLARE v_descricao varchar(150);
  DECLARE v_historico varchar(150);
  DECLARE v_cartao_contratado_id INTEGER;
  
  DECLARE cartaoPassivo_Cursor CURSOR FOR
   SELECT cartao.passivo_id, cartao.operadora, passivo.CONTA_CORRENTE_ID, cc.DESCRICAO, passivo.HISTORICO
   FROM scorecard_dev.cartao cartao
   INNER JOIN passivo passivo on cartao.PASSIVO_ID = passivo.id
   INNER JOIN conta_corrente cc on passivo.CONTA_CORRENTE_ID = cc.id; 
   
  DECLARE CONTINUE HANDLER 
    FOR NOT FOUND SET v_finished = 1;  
  
  DROP TABLE IF EXISTS cartaoContratadoVsCartaoPassivo;
  CREATE TEMPORARY TABLE cartaoContratadoVsCartaoPassivo
    (passivoId INTEGER, operadora INTEGER, conta_corrente_id INTEGER, descricao varchar(150), historico varchar(150));  

  OPEN cartaoPassivo_Cursor;
  read_loop:LOOP
  
	 FETCH cartaoPassivo_Cursor INTO v_passivo_id, v_operadora, v_conta_corrente_id, v_descricao, v_historico;
     IF v_finished = 1 THEN 
	    LEAVE read_loop;
     END IF;  
     
     SELECT id INTO v_cartao_contratado_id 
            from cartao_contratado cc where cc.CONTA_CORRENTE_ID = v_conta_corrente_id AND cc.OPERADORA = v_operadora;
            
     UPDATE cartao 
	 	    SET CARTAO_CONTRATADO_ID = v_cartao_contratado_id 
                WHERE passivo_id     = v_passivo_id;       
     
     INSERT INTO cartaoContratadoVsCartaoPassivo (passivoId, operadora, conta_corrente_id, descricao, historico) 
     VALUES (v_passivo_id, v_operadora, v_conta_corrente_id, v_descricao, v_historico);
            
  END LOOP;
  
  select * from cartaoContratadoVsCartaoPassivo;
 
  CLOSE cartaoPassivo_Cursor;
  DROP TABLE cartaoContratadoVsCartaoPassivo;
  
END //
DELIMITER ;