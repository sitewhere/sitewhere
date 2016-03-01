  @echo off
  echo.
  echo   SiteWhere Start Script for Windows v1.0
  echo.

  java -version >nul 2>&1
  if errorlevel 1 goto NOJAVA

  set "JAVA_OPTS=-Djava.net.preferIPv4Stack=true -noverify %JAVA_OPTS%"
  rem set "JPDA_OPTS=-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n"


  IF "%SITEWHERE_HOME%" == "" GOTO NOPATH
  set SITEWHERE_FOLDER="%SITEWHERE_HOME%";
  GOTO CHECKPATH

:NOPATH
  SET ROOT=%~dp0
  rem Resolve is fetching the parent directory
  CALL :RESOLVE "%ROOT%\.." PARENT_ROOT
  set SITEWHERE_FOLDER="%PARENT_ROOT%"
:CHECKPATH
  set SITEWHERE_FOLDER=%SITEWHERE_FOLDER:;=%
  rem Check if directory exists
  FOR %%i IN (%SITEWHERE_FOLDER%) DO IF EXIST %%~si\NUL  GOTO CHECKFILE
  echo ERROR! SiteWhere Home Folder not found or readable.
  GOTO EXIT

:CHECKFILE
  rem Check if file exists
  FOR %%i IN (%SITEWHERE_FOLDER%/lib/sitewhere.war) DO IF EXIST %%~si  GOTO START
  echo ERROR! SiteWhere WAR not found.
  GOTO EXIT

:START
      set "JAVA_OPTS=-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%SITEWHERE_FOLDER%/heap-dump.hprof %JAVA_OPTS%"

      echo -------------------------------------------------------------------------
      echo.
      echo   SITEWHERE_HOME: %SITEWHERE_FOLDER%
      echo.
      echo   JAVA_OPTS: %JAVA_OPTS%
      echo.
      echo -------------------------------------------------------------------------
      echo.
    
  set "JAVA_OPTS=-Dsitewhere.home=%SITEWHERE_FOLDER% -Dspring.config.location=%SITEWHERE_FOLDER%/conf/ %JAVA_OPTS%"
  
  java %JAVA_OPTS% %JPDA_OPTS% -jar %SITEWHERE_FOLDER%/lib/sitewhere.war
  GOTO EXIT

:NOJAVA
  echo You do not have the Java Runtime Environment installed, please install Java JRE from java.com/en/download and try again.

:EXIT
  pause

:EXIT_WO_PAUSE

GOTO :EOF

:RESOLVE
SET %2=%~f1
GOTO :EOF