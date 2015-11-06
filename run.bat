SET JDK_DIR=%JAVA_HOME%

@REM SET JDK_DIR to JDK installation directory
if "%JDK_DIR%" == "" SET JDK_DIR=C:\Program Files\Java\jdk1.6.0_45

@REM COMPILE CLASSES
"%JDK_DIR%\bin\javac" src\*.java

@REM Run TweetsCleaned
"%JDK_DIR%\bin\java" -cp src TweetsCleaned

@REM Run AverageDegree
"%JDK_DIR%\bin\java" -cp src AverageDegree

pause
