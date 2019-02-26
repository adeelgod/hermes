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

    
### Camel integration
Camel is used for integration of bank statements and post stampts. Camel configuration work in a way that when spring 
active profile param is set to production then there camel configuration is expected to be provided in filesystem 
otherwise it will be read from the resources/META-INF/spring directory. Essentially the camel xml will be copied to 
distribution package on the assembly. This is done to have ability to provide custom changes to routes with no need 
to make changes in code.

There are two elements that needs to be added /modified in camel xml

1. Filter definition that tells camel what is file mask
    ```
    <bean id="csvFidor" class="org.apache.camel.component.file.AntPathMatcherGenericFileFilter">
        <property name="includes" value="*Fidorpay-Transaktionen.csv"/>
    </bean>
    ```

2. Route definition which tells what to do when a specific file is in inbox
    ```
    <route>
        <from uri="file://{{hermes.inbox.dir}}?move={{hermes.archive.dir}}&amp;filter=#csvFidor&amp;charset=UTF-8" />
        <setProperty propertyName="name"><constant>fidor</constant></setProperty>
        <setProperty propertyName="statement"><constant>INSERT INTO fidor_raw(`date`, `text`, `text2`, `value`) VALUES (:1, :2, :3, :4)</constant></setProperty>
        <setProperty propertyName="expectedColumns"><constant>4</constant></setProperty>
        <unmarshal>
            <csv delimiter=";"/>
        </unmarshal>
        <to uri="bean:bankStatementsProcessor"/>
    </route>
    ```
    `name` defines the unique name for a route\
    `statement` is insert statement with values enumerated as :1,:2,:3, ... (numbers refers to an order in the file)\
    `expectedColumns` is needed to make validation of row from the file
    
For exotic encoding (other that UTF-8) additional converter needs to be added
```
<route>
    <from uri="file://{{hermes.inbox.dir}}?move={{hermes.archive.dir}}&amp;filter=#csvHypovereinsbank&amp;charset=UTF-16LE" />
    <convertBodyTo type="java.lang.String" charset="UTF-16LE"/>
    <setProperty propertyName="name"><constant>hypovereinsbank</constant></setProperty>
    ...
</route
```