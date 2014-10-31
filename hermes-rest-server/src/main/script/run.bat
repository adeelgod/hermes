"C:\Program Files\Java\jre1.8.0_25\bin\java.exe" -Dlogback.configurationFile=logback.xml -Dhermes.config=hermes.properties -Xmx512m -Xms512m -jar hermes-rest-server-1.0.0-SNAPSHOT-shaded.jar --port "8080" --jersey "/api/*" --cors "/api/*"
pause
