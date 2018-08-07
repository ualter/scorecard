# Create Table cartao_contratado
CREATE TABLE `cartao_contratado` (
  `ID` int(10) NOT NULL AUTO_INCREMENT,
  `OPERADORA` int(10) NOT NULL,
  `NOME` varchar(50) NOT NULL,
  `CONTA_CORRENTE_ID` int(10) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA BB', '64');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron BB', '64');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard BB', '64');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Santander', '66');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Santander', '66');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Santander', '66');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Itaú', '67');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Itaú', '67');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Itaú', '67');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA DB', '69');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron DB', '69');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard DB', '69');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA BS', '70');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron BS', '70');
INSERT INTO `scorecard_dev`.`cartao_contratado` (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard BS', '70');

# Has Cheque Conta Corrente
ALTER TABLE `scorecard_dev`.`conta_corrente` 
ADD COLUMN `HAS_CHEQUE` VARCHAR(1) NULL DEFAULT 'T' AFTER `ORDEM`;

# Disable Itau
UPDATE `scorecard_dev`.`banco` SET `IS_ATIVO` = 'F' WHERE (`ID` = '60');

# Insert Banc Sabadell
INSERT INTO `scorecard_dev`.`banco` (`NOME`, `DIAVENCIMENTOVISA`, `DIAVENCIMENTOMASTERCARD`) VALUES ('Banc Sabadell', '29', '29', 'T');

# Insert Cuenta Corriente Banc Sabadell
DECLARE keyBanc INT;
SET @keyBanc = (select id from banco where Nome = 'Banc Sabadell');
INSERT INTO `scorecard_dev`.`conta_corrente` (`BANCO_ID`, `DESCRICAO`, `NUMERO`, `ORDEM`) VALUES (@keyBanc, 'Banc Sabadell (Euros)', '00810133430001678371', '3');

UPDATE `scorecard_dev`.`conta_corrente` SET `HAS_CHEQUE` = 'F' WHERE (`ID` = '70');

# Change cartao table structure
ALTER TABLE `scorecard_dev`.`cartao` 
ADD COLUMN `CARTAO_CONTRATADO_ID` INT(10) NULL AFTER `CONTA_CORRENTE_ID`;

# Create Stored Procedure cartaoContratado_vs_cartaoPassivo and execute it...
call cartaoContratado_vs_cartaoPassivo();