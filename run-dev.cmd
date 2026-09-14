@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot
call "%~dp0mvnw.cmd" -f "%~dp0pom.xml" spring-boot:run
