@ECHO OFF
ECHO.
ECHO. Syntax mysql_dump yyyyMMdd_hhmm.sql
ECHO.
mysqldump -u Scorecard -p scorecard_prd > scorecard_prd_%1.sql