# Background

otl-agent is a light-weight agent used to initialize the Opentracing LiTe framework. If this agent is absent, then the framework will never get initialized and you are likely to miss all the traces.

Always add the following parameter while starting your application. Or if you are using any web server to deploy your application, modify the server startup script to include the agent path.

```
java -javaagent:/path/to/otl-agent.jar .....

```
