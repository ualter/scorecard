@ECHO OFF
ECHO.
ECHO. MySQL Dump
ECHO.
cls

if "%~1"=="" goto no_parameters

SET dump_cmd="c:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"

set datestr=%date:~6,4%%date:~3,2%%date:~0,2%

set hour=%time:~0,2%
if "%hour:~0,1%" == " " set hour=0%hour:~1,1%
set min=%time:~3,2%
if "%min:~0,1%" == " " set min=0%min:~1,1%
set secs=%time:~6,2%
if "%secs:~0,1%" == " " set secs=0%secs:~1,1%

set fileName=scorecard_prd_%datestr%_%hour%%min%%secs%.sql


%dump_cmd% --opt -u Scorecard -p%1 scorecard_prd > %fileName%
echo. 
echo. Dump done!
echo.      %fileName%
echo.
goto end

:no_parameters
ECHO. 
ECHO. Syntax...
ECHO.      mysql_dump  "password"
ECHO.
ECHO.

:end