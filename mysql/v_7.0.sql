# Disable Itau
UPDATE `scorecard_dev`.`banco` SET `IS_ATIVO` = 'F' WHERE (`ID` = '60');

# Insert Banc Sabadell
INSERT INTO `scorecard_dev`.`banco` (`NOME`, `DIAVENCIMENTOVISA`, `DIAVENCIMENTOMASTERCARD`) VALUES ('Banc Sabadell', '29', '29', 'T');

# Insert Cuenta Corriente
declare keyBanc INT;
SET @keyBanc = (select id from banco where Nome = 'Banc Sabadell');
INSERT INTO `scorecard_dev`.`conta_corrente` (`BANCO_ID`, `DESCRICAO`, `NUMERO`, `ORDEM`) VALUES (@keyBanc, 'Banc Sabadell (Euros)', '00810133430001678371', '3');
