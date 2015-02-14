#!/bin/sh
java -Dspring.profiles.active="production" -Dlogback.configurationFile=logback.xml -Dhermes.config=hermes.properties -Xmx1024m -Xms512m -jar hermes-rest-server-ui.jar --port "8080" --jersey "/api/*" --cors "/api/*"
