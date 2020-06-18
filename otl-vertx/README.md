# Background

From the wiki: Eclipse Vert.x is a polyglot **event-driven** application framework that runs on the Java Virtual Machine.
It is baed on NIO Selector, which is a component which can examine one or more Java NIO Channels, and determine which channels are ready either for reading or writing. This way a single thread can manage multiple channels, and thus multiple network connections. In conventional container based architecture (web server/app server), where a pool of worker threads are maintained, only one thread can handle one request at a time, however, in vertx, because of it's unique design, one worker thread is capable of handling multiple client requests.

Because a single worker thread is capable of managing multiple client connections, therefore, we cannot leverage threadlocal for storing any contextual info. A slightly different strategy is adopted while integrating with opentracing.

Before we integrate vertx with opentracing-lite library, let us understand the different flow, where a span propagation is required.

1. Http Server - Whenever a new request is made to any vertx server, it has to be captured and a new span will be created. However, because we cannot store the newly created span in the threadlocal, therefore it is stored in a special context object. When the final response is sent to client, the appropriate handler will ensure to extract the span from the context object and complete the processing.
1. Event Bus - Multiple verticles can communicate with each other via eventbus. If the http server verticle is willing to cmmunicate with some other (worker) verticle, it must ensure to pass the span context to the worker.
1. Web client - If one vertx app is trying to communicate other vertx app over tcp/http, the calling service has to ensure the span context is propagated.

The opentracing-lite library for vertx (otl-vertx) addresses all the above scenarios. Although, the configuration is not automatic, developer has to write very little code for seemless integration.

# How to configure

## Pom file

```
....
<dependencies>
    <dependency>
        <groupId>io.opns.otl</groupId>
        <artifactId>otl-vertx</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <!--  Add the below, only if you want to monitor the span metrics -->
    <dependency>
        <groupId>io.opns.otl</groupId>
        <artifactId>otl-metrics</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
<dependencies>
```

## Http server

Attach the TracingVtxHandler and TracingVtxErrorHandler to the vertx router.

```
....
....

@Override
public void start() throws Exception {
    ....
    // Initialize the vertx HttpServer
    ....
        
    Router router = Router.router(getVertx());
        
    // Add the vertx router hook
    // -1 indicates that this should be the first middleware to be invoked,
    // thereby acting as a web filter.
    router.route().order(-1)
        .handler(new TracingVtxHandler())
        .failureHandler(new TracingVtxErrorHandler());
        
    ....
    // Add other middleware/handler
    ....
    
    // Start the server
    httpServer.listen(....);
}
```

## Eventbus

Add the eventbus interceptor. Here is the same example, but the eventbus interceptor is added.

```
@Override
public void start() throws Exception {
    ....
    // Initialize the vertx HttpServer
    ....
        
    Router router = Router.router(getVertx());
        
    // Add the vertx router hook
    // -1 indicates that this should be the first middleware to be invoked,
    // thereby acting as a web filter.
    router.route().order(-1)
        .handler(new TracingVtxHandler())
        .failureHandler(new TracingVtxErrorHandler());
    
    getVertx().eventBus().addOutboundInterceptor(new EventBusOutboundInterceptor());
        
    ....
    // Add other middleware/handler
    ....
    
    // Start the server
    httpServer.listen(....);
}

public void sendToEventbus(RoutingContext ctx, String destAddress, Object payload) {
    Span span = TracingVtxHandler.activeSpan(ctx);
    if (span != null) {
        try (Scope scope = tracer.activateSpan(span)) { 
            vertx.eventBus().request(destAddress
                    , payload
                    , new DeliveryOptions()
                    , <Reply_Handler>);

        }
    }
}

```

Note 1: The eventbus API invocation must happen within the **try-with-resources** block. Otherwise no context data will be propagated to the verticle/consumer on the other side. In case the consumer responds using ```Meesage.reply(...)```, the same outbound interceptor will be invoked.

Note 2: interceptor does not create a new span. Because an application which heavily relies on the event bus, it will be flooded with spans. Hence the span creation is disabled. It will only propagate the span context data from one verticle to another. The 2nd verticle may decide to start a new span to represent it's own work. If the event bus is distributed, then the span creation will be enabled. You can, however, control the behavior by tweaking the system property "-Dvertx.eventbus.span". It's default value is "false"

## Web Client

Vertx' web client is again asynchronous. Which means, the thread that initiates the request, may not handle the final response received from remote service.

Vertx has the WebClient library to make any cross service call. WebClient should be created once, and used multiple times. Here is how you can configure WebClient to trace the outbound request flow.

```
public WebClient getClient() {
    WebClient client = WebClient
           .create(vertx, new WebClientOptions()
           .setSsl(false)
           .setTrustAll(true)
           .setVerifyHost(false)
           .setMaxPoolSize(...)
           ....
           ....);
        
    ((WebClientInternal)client)
           .addInterceptor(new VertxWebClientInterceptor());
}

public void invoke(RoutingContext ctx, String port, String endpoint, String uri) {
    Client client = getClient();

    Span span = TraacingVtxHandler.currentSpan(ctx);
  
    try (Scope scope = tracer.activateSpan(span)) {
             
         // Send Request to next service.
         JsonObject json = new JsonObject();
         json.put("id", "...");
         json.put("name", "....");
 
         client.post(port, endpoint, uri)
             .sendJson(json, new ResponseHandler(ctx));
    }
}
```

Note that, the client invocation must happen within the **try-with-resources** block. Otherwise no context data will be propagated to called service.

## Start your application

Add the otl-agent and start your vertx server.

```
java -javaagent:/path/to/otl-agent.jar ......
```

