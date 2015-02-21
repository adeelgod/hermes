SET HERMES_LOG_PREFIX=daniel
"C:\Program Files\Java\jre1.8.0_25\bin\java.exe" -Dspring.profiles.active="production" -Dlogback.configurationFile=logback.xml -Dhermes.config=hermes.properties -Dcommon.config=common.properties -Xmx1024m -Xms512m -jar hermes-rest-server-ui.jar --port "8080" --jersey "/api/*" --cors "/api/*"
pause
