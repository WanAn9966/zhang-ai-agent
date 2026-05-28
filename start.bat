@echo off
chcp 65001 >nul
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8
echo Starting YuAiAgentApplication with UTF-8 encoding...
mvnw.cmd spring-boot:run
