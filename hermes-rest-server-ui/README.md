### Setting up hermes locally

First make sure all the modules are building with
`mvn clean install` in the hermes main

Common issues
1. Port is free on 3306 (there cannot be MySQL server running locally other wise there will be SSH tunnel exception on tests in `hermes-persistence` and on running)
2. There is `Java 8` and `mvn` installed 
3. There is `node` installed on the computer otherwise build will fail on `hermes-ui`
4. There are `bower` and `gulp` installed locally otherwise build will fail on `hermes-ui`
Output distribution zip is places in hermes-rest-server-ui in target. It contains all the files necessary to distribute package

### Development
#### Server (hermes-rest-server-ui)
In IntelliJ define new configuration based on Application template with following settings
 
__Main__ 
```
com.m11n.hermes.rest.server.core.Main
```

__VM options__ 
```
-Dspring.profiles.active="debug"
-Dlogback.configurationFile=file:<hermes repo root>/hermes-test/src/main/resources/logback.xml
-Dhermes.config=file:<hermes repo root>/hermes-test/src/main/resources/hermes.properties
-Dcommon.config=file:<hermes repo root>/hermes-test/src/main/resources/common.properties
-Xmx512m
-Xms512m
-Dhermes.inbox.dir=<path to hermes inbox>
-Dhermes.archive.dir=<path to hermes archive> 
```
Subsitutute `<hermes repo root>` with absolute path to directory where hermes repository was cloned.
Substiture `<path to hermes inbox>` and `<path to hermes archive>` with paths to file inbox and archive directories.

__Program arguments__ 
```
--port "8081" --jersey "/api/*" --cors "/api/*"
```

__Working directory__
```
/Users/bartosz/Development/lcarbshop/hermes
```

__Use classpath of__
```
hermes-rest-server-ui
```

__Before launch:__ make sure to have Build stage added.
    
### UI (hermes-web-ui)
Application is in `hermes-web-ui` undes `app`. Find `app.js` and modify it first, find following lines: 
```
}).constant('HermesApi', {
    baseUrl: ''
    // baseUrl: 'http://localhost:8081/'
}
```
and uncomment `http://localhost:8081/` and comment `baseUrl: ''`. 

This is needed as backend will be started on port `8081` and will not have UI added to jetty as UI will be run separately by *gulp*

Also make sure that you have
```
$sceDelegateProvider.resourceUrlWhitelist([
    // Allow same origin resource loads.
    'self',
    // Allow loading from our assets domain.  Notice the difference between * and **.
    'http://localhost:8081/**']);
```
to resolve CORS issues on local environment.
    
Now go to terminal, `cd` to `hermes-web-ui` and run `gulp` (if running for a first time run `npm i` in install dependencies)

    
    