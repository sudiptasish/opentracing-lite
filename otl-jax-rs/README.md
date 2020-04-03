# Background

Jax-rs is a specification for building REST application. These are standardized today and has support from all web/application servers.

# How to configure

Because any Jax-rs implementation is a server side containerized application, it can leverage the same OTLFilter (module otl-web) to intercept any incoming request and instrument them. 

## Include dependent libraries in your pom.xml

```
<dependencies>
    ....
    <dependency>
        <groupId>com.sc.hm.otl</groupId>
        <artifactId>otl-jax-rs</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!--  Add the below, only if you want to monitor the span metrics -->
    <dependency>
        <groupId>com.sc.hm.otl</groupId>
        <artifactId>otl-metrics</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ....
</dependencies>

```

## Configure the OTL Web Filter.

Using web.xml

```
<web-app>
    .....
    <filter>
        <filter-name>OpenTracingFilter</filter-name>
        <filter-class>com.sc.hm.otl.web.filter.OTLFilter</filter-class>
        <load-on-startup>1</load-on-startup>
        
        <init-param>
            <param-name>DECORATOR</param-name>
             <param-value>fully_qualified_class_name_of_custom_decorator</param-value>
         </init-param>
         <init-param>
             <param-name>SKIP_PATTERN</param-name>
             <param-value>pattern_regular_exp</param-value>
         </init-param>
   </filter>
    .....
    .....
</web-app>

```

Re-package your **war** or **ear** file and deploy them in any container (web server/application server) and you will see the OTL Filter in action.

## Configure Jax-Rs container request/response Filter

If you are using jersey/resteasy or any other as jax-rs implementation, then you can specify the container request filter and response filter in your web.xml.

```
<web-app>
    .....
    <servlet>  
        <servlet-name>REST_Servlet</servlet-name>  
        <servlet-class>Jax.Rs.Servlet.Name</servlet-class>  
        <init-param>
            <param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
            <param-value>com.sc.hm.otl.jaxrs.filter.OTLRequestFilter</param-value>
        </init-param>
        <init-param>
            <param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
            <param-value>com.sc.hm.otl.jaxrs.filter.OTLResponseFilter</param-value>
        </init-param>
        <init-param>  
            <param-name>jersey.config.server.provider.packages</param-name>  
            <param-value>com.sc.hm.otl.jaxrs.filter,package.name.for.other.rest.classes</param-value>  
        </init-param>  
        <init-param>  
            <param-name>com.sun.jersey.api.json.POJOMappingFeature</param-name>  
            <param-value>true</param-value>  
        </init-param>      
        <load-on-startup>1</load-on-startup>  
    </servlet> 
    .....
    .....
</web-app>

```

## Configure Client

Jax-rs provides the spec for container request/response client for intercepting and instrumenting any outbound request. You have to manually add them in the jax-rs Client class.

```
public Client restClient() {
    Client client = ClientBuilder
        .newBuilder()
        .register(new OTLClientRequestFilter())
        .register(new OTLClientResponseFilter())
        .build();
}
```
or

```
public Client restClient() {
    ClientConfig config = new ClientConfig();
    config.register(OTLClientRequestFilter.class);
    config.register(OTLClientResponseFilter.class);
    return ClientBuilder.newClient(config);
}
```

Re-package the war/ear file and deploy. The Jersey container will ensure these two request filters will be configured.

## Starting your web server
Modify your server startup script, to add the javaagent:

```
java -javaagent:/path/to/otl-agent.jar ......
```

Start the server.
