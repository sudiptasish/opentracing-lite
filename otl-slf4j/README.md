# OTL-SLF4J

If your application is using some SLF4J compliant logging framework, you need this library. Note that no other configuration change is required. You can continue to use your existing appender and message format.

This module contains the necessary adapter to forward the logging call to underlying logging framework. The integration is seemless.

# How to configure

## Add entry in pom file

```
<dependencies>
    ....
    <dependency>
        <groupId>com.sc.hm.otl</groupId>
        <artifactId>otl-slf4j</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ....
</dependencies>
```

Note that, all the client libraries already include this module, therefore you may not add it again.

## Modify logger xml configuration

The idea of is to segregate the regular log data from the span data. The existing appender and pattern will continue work, however, you have to add a new logger specifically for span data which will dump it in a separate file.

```
<?xml version="1.0"?>
<configuration debug="true">

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd_HH:mm:ss.SSS} [%thread] [%X{trc}] [%X{spn}] [%X{snm}] [%X{pspn}] [%X{bgi}] %-5relative %-5level %logger{36} - %msg %n</pattern>
        </encoder>
    </appender>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>span.log</file>
        <append>true</append>
        <encoder>
            <pattern>%msg %n</pattern>
        </encoder>
    </appender>
    
    <logger name="otl.span.log" level="INFO" additivity="false">
        <appender-ref ref="FILE" />
    </logger>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

Note that the special logger "otl.span.log", which uses FILE appender to write the messages to span.log. At the end, this file will have only the span data. And regular log data can be sent to other file (here redirected to CONSOLE).

Apart from the regular pattern, 5 new attributes were added:

* trc : Will print the traceId, if present
* spn : Will print the span, if present
* snm : Span operation name
* pspn : Will print the parent spanId, if present
* bgi : Will print the baggage items, if present
