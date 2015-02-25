"C:\Program Files\Java\jre1.8.0_25\bin\java.exe" -Dspring.profiles.active="production" -Dlogback.configurationFile=logback.xml -Dhermes.config=file:./hermes.properties -Dcommon.config=file:./common.properties -Xmx1024m -Xms512m -jar hermes-rest-server-crm.jar --port "8080" --jersey "/api/*" --cors "/api/*"
pause
