# DeploymentManager

##OpenAPI
Der DeploymentManager bietet eine OpenAPI-UI an. diese ist erreichbar unter
~~~
http://localhost:8080/deployment-manager/api/openapi-ui
~~~
Damit OpenAPI im Wildfly aktiviert wird, muss das module im Wildfly aktiviert werden. 
Siehe **.build/openapi.cli**
##Datasource
~~~
<datasource jndi-name="java:jboss/datasources/deployment-manager" pool-name="deployment-manager" enabled="true" use-java-context="true" statistics-enabled="${wildfly.datasources.statistics-enabled:${wildfly.statistics-enabled:false}}">
    <connection-url>jdbc:postgresql://localhost:5433/postgres</connection-url>
    <driver>postgresql</driver>
    <security>
        <user-name>docker</user-name>
        <password>docker</password>
    </security>
</datasource>

<drivers>
    <driver name="postgresql" module="org.postgresql">
        <driver-class>org.postgresql.Driver</driver-class>
    </driver>
</drivers>
~~~

### Neues Tag erfragen ohne zu Speichern
Damit man beim starten der Pipeline eine Buildnummer bekommt, kann über nachfolgenden Rest-Endpoint die nächste Buildnummer (TAG) abgefragt werden
~~~
GET /deployment-manager/api/projects/{ident}/images?version={aktuelleVersion}

Response = 1.0.0-1
~~~


### Neues Tag abfragen
~~~
stage('Push image to registry') {
    when {
        expression { return params.PUSH }
    }
    environment {
        DEPLOYMENT_MANAGER ="http://etw-docker-03.bvaetw.de:8088/deployment-manager/api"
        DEPLOYMENT_MANAGER_URL = "${env.DEPLOYMENT_MANAGER}/projects/${env.DEPLOYMENT_MANAGER_PROJECT}/images"
        DEPLOYMENT_MANAGER_PROJECT = "manager"
        IMAGE = "http://etw-docker-03.bvaetw.de/manager/manager"
        VERSION = sh( script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                    returnStdout: true)
        COMMIT = sh( script: "git show -s --format=short",
                    returnStdout: true)        

        TAG =  sh ( script: "curl --header \"Content-Type: application/json\" \\\n" +
                "  --request POST \\\n" +
                "  --data '{\n" +
                "  \"user\": \"${env.USER}\",\n" +
                "  \"image\": \"${env.image}\",\n" +
                "  \"commit\": \"${env.COMMIT}\",\n" +
                "  \"version\":  \"${env.VERSION}\"\n" +
                "}' \\\n" +
                "  ${env.DEPLOYMENT_MANAGER_URL}",
                returnStdout: true )
    }
    steps {
       // Baut das docker Image
       echo "PUSH"
       }
    }
}

~~~

### Flyway config 
Datei im Root des Projekts flyway.properties
~~~
flyway.user=docker
flyway.password=docker
flyway.url=jdbc:postgresql://localhost:5433/postgres
~~~