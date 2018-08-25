# Table Cartao Operadora
CREATE TABLE `cartao_tipo` (
  `ID` INT NOT NULL,
  `NOME` VARCHAR(80) NULL,
  PRIMARY KEY (`ID`));
  
INSERT INTO `cartao_tipo` (`ID`, `NOME`) VALUES ('0', 'VISA Credito');
INSERT INTO `cartao_tipo` (`ID`, `NOME`) VALUES ('1', 'VISA Electron');
INSERT INTO `cartao_tipo` (`ID`, `NOME`) VALUES ('2', 'Mastercard Credito');

# Insert Banc Sabadell
INSERT INTO `banco` (`NOME`, `DIAVENCIMENTOVISA`, `DIAVENCIMENTOMASTERCARD`, `IS_ATIVO`) VALUES ('Banc Sabadell', '29', '29', 'T');

select ban.ID as 'Banco_ID' INTO @bancoIdSabadell from banco ban where ban.NOME = 'Banc Sabadell';
select @bancoIdSabadell;

# Insert Conta Corriente Sabadell
INSERT INTO `conta_corrente` (`BANCO_ID`, `DESCRICAO`, `NUMERO`, `ORDEM`) VALUES (@bancoIdSabadell, 'Banc Sabadell', '000', '3');


# Keys Conta Corrente Bancos (Execute one at a time)
select cc.ID as 'CC_ID', ban.ID as 'Banco_ID' INTO @contaCorrenteIdBB, @bancoIdBB 
from conta_corrente cc inner join banco ban on ban.id = cc.banco_id where ban.NOME = 'Banco do Brasil' and cc.NUMERO = '14.868-7';

select cc.ID as 'CC_ID', ban.ID as 'Banco_ID' INTO @contaCorrenteIdSantander, @bancoIdSantander 
from conta_corrente cc inner join banco ban on ban.id = cc.banco_id where ban.NOME = 'Santander';

select cc.ID as 'CC_ID', ban.ID as 'Banco_ID' INTO @contaCorrenteIdItau, @bancoIdItau
from conta_corrente cc inner join banco ban on ban.id = cc.banco_id where ban.NOME = 'Itaú';

select cc.ID as 'CC_ID', ban.ID as 'Banco_ID' INTO @contaCorrenteIdDeutsche, @bancoIdDeutsche 
from conta_corrente cc inner join banco ban on ban.id = cc.banco_id where ban.NOME = 'Deutsche Bank';

select cc.ID as 'CC_ID', ban.ID as 'Banco_ID' INTO @contaCorrenteIdSabadell, @bancoIdSabadell 
from conta_corrente cc inner join banco ban on ban.id = cc.banco_id where ban.NOME = 'Banc Sabadell';

select cc.ID as 'CC_ID', ban.ID as 'Banco_ID' INTO @contaCorrenteIdCitiBank, @bancoIdCitiBank 
from conta_corrente cc inner join banco ban on ban.id = cc.banco_id where ban.NOME = 'Citibank';

select @bancoIdBB, @contaCorrenteIdBB;
select @bancoIdItau, @contaCorrenteIdItau;
select @bancoIdSantander, @contaCorrenteIdSantander;
select @bancoIdDeutsche, @contaCorrenteIdDeutsche;
select @bancoIdSabadell, @contaCorrenteIdSabadell;
select @bancoIdCitiBank, @contaCorrenteIdCitiBank;

# Disable Itau
UPDATE `banco` SET `IS_ATIVO` = 'F' WHERE (`ID` = @bancoIdItau );

# Has Cheque Conta Corrente
ALTER TABLE `conta_corrente` 
ADD COLUMN `HAS_CHEQUE` VARCHAR(1) NULL DEFAULT 'T' AFTER `ORDEM`;

# Update Cuenta Corriente Banc Sabadell
UPDATE `conta_corrente` SET `HAS_CHEQUE` = 'F' WHERE (`ID` = @bancoIdSabadell);

# Create Table cartao_contratado
DROP TABLE IF EXISTS `cartao_contratado`;
CREATE TABLE `cartao_contratado` (
  `ID` int(10) NOT NULL AUTO_INCREMENT,
  `OPERADORA` int(10) NOT NULL,
  `NOME` varchar(50) NOT NULL,
  `CONTA_CORRENTE_ID` int(10) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA BB',          @contaCorrenteIdBB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron BB', @contaCorrenteIdBB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard BB',    @contaCorrenteIdBB);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Santander',          @contaCorrenteIdSantander);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Santander', @contaCorrenteIdSantander);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Santander',    @contaCorrenteIdSantander);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Itaú',          @contaCorrenteIdItau);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Itaú', @contaCorrenteIdItau);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Itaú',    @contaCorrenteIdItau);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA DB',          @contaCorrenteIdDeutsche);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron DB', @contaCorrenteIdDeutsche);
-- INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard DB',    @contaCorrenteIdDeutsche);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA BS',          @contaCorrenteIdSabadell);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Debit', @contaCorrenteIdSabadell);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA Citibank',          @contaCorrenteIdCitiBank);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('1', 'VISA Electron Citibank', @contaCorrenteIdCitiBank);
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('2', 'Mastercard Citibank',    @contaCorrenteIdCitiBank);

# Check It
SELECT cc.*, ccr.DESCRICAO, ban.NOME
FROM cartao_contratado cc
INNER JOIN conta_corrente ccr ON ccr.id = cc.conta_corrente_id
INNER JOIN banco ban ON ban.id = ccr.banco_id;

# Change cartao table structure
ALTER TABLE `cartao` 
ADD COLUMN `CARTAO_CONTRATADO_ID` INT(10) NULL AFTER `OPERADORA`;

# Create Stored Procedure cartaoContratado_vs_cartaoPassivo and execute it...
call cartaoContratado_vs_cartaoPassivo();

# Check it
SELECT c.*, p.historico, cc.DESCRICAO, cc.NUMERO FROM cartao c INNER JOIN passivo p on p.id = c.passivo_id
inner join conta_corrente cc on p.conta_corrente_id = cc.id;

# Check it All the Cartoes has the CartaoContratadoId set (must not exist NULLs)
SELECT c.*,p.conta_corrente_id FROM cartao c 
INNER JOIN PASSIVO P ON c.passivo_id = p.id
where c.cartao_contratado_id IS NULL;
-- UPDATE cartao set CARTAO_CONTRATADO_ID = NULL; (IN CASE has to launch again the Stored Procedure)


# Logo CartaoContratado
ALTER TABLE `cartao_contratado` 
ADD COLUMN `LOGO` VARCHAR(80) NULL AFTER `CONTA_CORRENTE_ID`;

# Add Cartao VISA SIN de Banc Sabadell
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'VISA SIN', @contaCorrenteIdSabadell);

UPDATE `cartao_contratado` SET `LOGO` = 'Visa.jpg' WHERE (`NOME` LIKE '%VISA%');

UPDATE `cartao_contratado` SET `LOGO` = 'Mastercard.jpg' WHERE (`NOME` LIKE '%Mastercard%');

# Disable Cheques Banc Sabadell
UPDATE `conta_corrente` SET `HAS_CHEQUE` = 'F' WHERE (`ID` = @contaCorrenteIdSabadell);

# Update Table Structure Cartao Contratado Info Debito ou Credito
ALTER TABLE `cartao_contratado` 
ADD COLUMN `DEBITO_CREDITO` VARCHAR(1) NULL DEFAULT 'C' AFTER `LOGO`;

# Tarjeta Visa SIN Banc Sabadell
INSERT INTO cartao_contratado (`OPERADORA`, `NOME`, `CONTA_CORRENTE_ID`) VALUES ('0', 'Visa SIN', @contaCorrenteIdSabadell);
UPDATE `cartao_contratado` SET `LOGO` = 'Visa.jpg' WHERE (`NOME` LIKE '%VISA%');
UPDATE `cartao_contratado` SET `LOGO` = 'VisaElectron.jpg' WHERE (`NOME` LIKE '%VISA Electron%');

# Update Info Cartao Contratado
UPDATE `cartao_contratado` SET `DEBITO_CREDITO` = 'D' WHERE (`NOME` LIKE '%Electron%');
UPDATE `cartao_contratado` SET `DEBITO_CREDITO` = 'D' WHERE (`NOME` LIKE '%Mastercard Debit%');


# Check IDs
UPDATE `cartao_contratado` SET `LOGO` = 'Mastercard_Debit_BS.png' WHERE (`ID` = '14');
UPDATE `cartao_contratado` SET `LOGO` = 'Visa_Credit_BS.png' WHERE (`ID` = '13');
UPDATE `cartao_contratado` SET `LOGO` = 'Visa_SIN_BS.png' WHERE (`ID` = '18');
UPDATE `cartao_contratado` SET `LOGO` = 'Visa_Credit_DB.png' WHERE (`ID` = '10');
UPDATE `cartao_contratado` SET `LOGO` = 'Visa_Debit_DB.png' WHERE (`ID` = '11');
UPDATE `cartao_contratado` SET `LOGO` = 'Mastercard_Credit_Santander.png' WHERE (`ID` = '6');
UPDATE `cartao_contratado` SET `LOGO` = 'Visa_Credit_Santander.png' WHERE (`ID` = '4');
UPDATE `cartao_contratado` SET `LOGO` = 'Visa_Debit_Santander.png' WHERE (`ID` = '5');








