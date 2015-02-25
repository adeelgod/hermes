#!/bin/sh
java -Dspring.profiles.active="production" -Dlogback.configurationFile=logback.xml -Dhermes.config=file:./hermes.properties -Dcommon.config=file:./common.properties -Xmx1024m -Xms512m -jar hermes-rest-server-crm.jar --port "8080" --jersey "/api/*" --cors "/api/*"
