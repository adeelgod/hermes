#!/bin/sh
java -Dlogback.configurationFile=logback.xml -Dhermes.config=hermes.properties -Xmx1024m -Xms512m -jar hermes-rest-server.jar --port "8080" --jersey "/api/*" --cors "/api/*"
