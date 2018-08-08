
# Disable Itau
UPDATE `banco` SET `IS_ATIVO` = 'F' WHERE (`ID` = '60');

# Has Cheque Conta Corrente
ALTER TABLE `conta_corrente` 
ADD COLUMN `HAS_CHEQUE` VARCHAR(1) NULL DEFAULT 'T' AFTER `ORDEM`;

# Insert Banc Sabadell
INSERT INTO `banco` (`NOME`, `DIAVENCIMENTOVISA`, `DIAVENCIMENTOMASTERCARD`, `IS_ATIVO`) VALUES ('Banc Sabadell', '29', '29', 'T');

set @bancoSabadellId := 64;
# Insert Cuenta Corriente Banc Sabadell
insert INTO `conta_corrente` (`BANCO_ID`, `DESCRICAO`, `NUMERO`, `ORDEM`, `HAS_CHEQUE`) VALUES (@bancoSabadellId, 'Banc Sabadell (Euros)', '00810133430001678371', '3', 'T');

# Keys Banco (Execute one at a time)
select 64 INTO @keyBB;
select 66 INTO @keySantander;
select 67 INTO @keyItau;
select 69 INTO @keyDB;
select 70 INTO @keyBancSabadell;

select @keyBB;
select @keySantander;
select @keyItau;
select @keyDB;
select @keyBancSabadell;

# Create Table cartao_contratado
DROP TABLE IF EXISTS `cartao_contratado`;
CREATE TABLE `cartao_contratado` (
  `ID` int(10) NOT NULL AUTO_INCREMENT,
  `OPERADORA` int(10) NOT NULL,
  `NOME` varchar(50) NOT NULL,
  `CONTA_CORRENTE_ID` int(10) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA BB',          @keyBB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron BB', @keyBB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard BB',    @keyBB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Santander',          @keySantander);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Santander', @keySantander);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Santander',    @keySantander);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Itaú',          @keyItau);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Itaú', @keyItau);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Itaú',    @keyItau);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA DB',          @keyDB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron DB', @keyDB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard DB',    @keyDB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA BS',          @keyBancSabadell);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron BS', @keyBancSabadell);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard BS',    @keyBancSabadell);






UPDATE `conta_corrente` SET `HAS_CHEQUE` = 'F' WHERE (`ID` = '70');

# Change cartao table structure
ALTER TABLE `scorecard_dev`.`cartao` 
ADD COLUMN `CARTAO_CONTRATADO_ID` INT(10) NULL AFTER `CONTA_CORRENTE_ID`;

# Create Stored Procedure cartaoContratado_vs_cartaoPassivo and execute it...
call cartaoContratado_vs_cartaoPassivo();