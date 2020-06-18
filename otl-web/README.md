# Background

otl-web module enables instrumentation of web api. By now you are aware of the semantic of a span / trace. Going back to the same example of Employee service and Department service, as we saw, it is the responsibility of the client facing service to create a traceId and pass it on to subsequent service. And all subsequent services in the request chain must use the same traceId. However, the individual services will create spans to represent their own work.

Next is, who will create this traceId and spanId? And moreover, how to intercept the incoming request ?

For any vanilla web application, Filter is the preferred choice of intercepting any incoming request. otl-web provides the tracing filter which can intercept the request and check the traceId (request) header. If no value is present for this special header, then it will create a new traceId and mark it as start of a new request flow. It will also create a spanId to represent it's own work and ensure the span is closed before returning the final response to client.

Note that, propagating the traceId or other headers to next service is not the job of OTL Filter. It is the responsibility of the http/web client that developer uses in his/her project. Will cover that in subsequent section.

# Configure OTL Web Filter

Configuring OTLFilter is easy. Note that, it is a conventional http servlet filter, therefore can be configured in web.xml.

## Include dependent libraries in your pom.xml

```
<dependencies>
    ....
    <dependency>
        <groupId>io.opns.otl</groupId>
        <artifactId>otl-web</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!--  Add the below, only if you want to monitor the span metrics -->
    <dependency>
        <groupId>io.opns.otl</groupId>
        <artifactId>otl-metrics</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ....
</dependencies>

```

There are two ways to configure the OTL Web Filter.

## Using web.xml

```
<web-app>
    .....
    <filter>
        <filter-name>OpenTracingFilter</filter-name>
        <filter-class>io.opns.otl.web.filter.OTLFilter</filter-class>
        <load-on-startup>1</load-on-startup>
        
        <init-param>
            <param-name>DECORATOR</param-name>
             <param-value>decorator.class.name</param-value>
         </init-param>
         <init-param>
             <param-name>SKIP_PATTERN</param-name>
             <param-value>uri.pattern|reg.exp</param-value>
         </init-param>
   </filter>
    .....
    .....
</web-app>

```

Re-package your **war** or **ear** file and deploy them in any container (web server/application server) and you will see the OTL Filter in action.

## Dynamic configuration

Some project may want to initialize the Filter dynamically. In which case you can add the listener OTLServletContextListener in the web.xml.

```
<web-app>
    .....
    <context-param>
        <param-name>DECORATOR</param-name>
        <param-value>decorator.class.name</param-value>
    </context-param>
    <context-param>
        <param-name>SKIP_PATTERN</param-name>
        <param-value>uri.pattern|reg.exp</param-value>
    </context-param>
    .....
    <listener>
        <listener-class>io.opns.otl.web.ctx.OTLServletContextListener</listener-class>
    </listener>
    .....
    .....
</web-app>

```

Re-package the war/ear file and deploy. The container will ensure this context listener is called during bootstrap, which in turn will take care of initializing the OTL Web Filter.

## Starting your web server
Modify your server startup script, to add the javaagent:

```
java -javaagent:/path/to/otl-agent.jar ......
```

Start the server.
