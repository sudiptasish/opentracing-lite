# Background

Springboot is an extension of spring webmvc, and it is much more easy to use. It is primarily used to build REST services, which takes most of the work out of configuring spring-based applications. It reduces lots of development effort and one can quickly build and deploy production ready code.

Note that, behind the scene it uses Apache Tomcat.

# How to configure.

Instrumenting a springboot application for tracing is quite easy.

## Modify your pom file

Add the following dependecies:

```
<dependencies>
    <dependency>
        <groupId>com.sc.hm.otl</groupId>
        <artifactId>otl-spring-boot</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>

```

It will pull up all the necessary libraries.

Because springboot is also container based j2ee application, therefore it can easily leverage the OTLFilter to intercept any incoming request and use RestTemplate instrumentation (from otl-spring-web) to instrument the outbound request.

## Sample springboot application

Let's say the Employee micro service is now a spring boot application.

```
@SpringBootApplication
@Import({OTLSpringBootConfig.class})
public class EmpSpringBootAppMain {
    
    public static void main(String[] args) {
        SpringApplication.run(EmpSpringBootAppMain.class, args);
    }
}
```

One just need add the OTLSpringBootConfig in the Import section.

## Starting your web server
Modify your java run command, to add the javaagent:

```
java -javaagent:/path/to/otl-agent.jar classpath <> EmpSpringBootAppMain
```

Note that, here we do not start any web/application server and do the deployment, etc. The beauty of springboot is, whenever you start SpringApplication, it will start the embedded Tomcat server and automatically deploy your application.
