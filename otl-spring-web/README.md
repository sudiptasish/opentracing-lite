# Background

Spring web or web mvc is another way of building web application. If follows the model-view-controller design pattern. Behind the scene it follows the same container standard once laid by the www consortium. Only thing is it abstracts that information and provide a simplistic view of the mapping/bean in dispatcher config xml.

The advantage of spring webmvc is , it provides everything under the hood. E.g., the servlet to handle incoming request, client to send outboud request, etc.

In subsequent section we will see how we can instrument a spring web application to start tracing the flow.

# How to configure

otl-spring-web module contains the server side as well as client side instrumentation. As explained above, spring web is nothing but a containerized application, therefore it can leverage the same OTLFilter (module: otl-web) to intercept incoming request flow. 

For the client side, spring developer prefers to use RestTemplate or the recent WebClient. otl-spring-web provides the library to instrument both these types of clients to pass on the traceId information plus other contextual info to the caller service.

Again Going back to the same example of Employee service and Department service, imagine these two services are now spring web service and Employee service uses spring RestTemplate to communicte with Department service.

In order for the interceptor instrumentation to work, it assumes that you have defined the RestTemplate as a Bean in your project.

```
@Bean
public RestTemplate defaultRestTemplate() {
    return new RestTemplate(); 
}

```

A working example of Employee controller class. See how your RestTemplate is injected.

```
@RestController
@RequestMapping(path = "/ecp/api/v1/employees"
    , produces = MediaType.APPLICATION_JSON_VALUE
    , consumes = MediaType.APPLICATION_JSON_VALUE)
public class EmpRestController {
    
    private final Logger logger = LoggerFactory.getLogger(EmpRestController.class);
    
    private static final String DEPT_URL = "http://localhost:8082/ecp/api/v1/departments";
   
    @Autowired
    private EmployeeService service;
    
    @Autowired
    private RestTemplate template;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createEmployee(@RequestBody Employee emp) {
        service.insert(emp);
        
        if (logger.isInfoEnabled()) {
            logger.info("Employee {} created successfully !", emp);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            
        HttpEntity<Department> request = new HttpEntity<>(emp.getDept(), headers);
        ResponseEntity<String> response = template.exchange(DEPT_URL
            , HttpMethod.POST
            , request
            , String.class);
        return ResponseEntity.created(URI.create("/employees/"  + emp.getId())).build();
    }
}

```

The RestTemplate will be instrumented to have the necessary interceptor, which will propagate the span context to Department service.

Using the RestTemplate in the following manner will never activate the interceptor, therefore it is strongly discouraged. Even spring does not recommed it either. You create RestTemplate only once and inject it in relevant classes.

``
public void createEmployee() {
    RestTemplate rt = new RestTemplate();
    rt.exchange( .... )

``

## Configure pom file

```
<dependencies>
    ....
    <dependency>
        <groupId>com.sc.hm.otl</groupId>
        <artifactId>otl-spring-web</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    ....
</dependencies>
```

## Build

Build your application to produce the final war file.

## Add otl-agent

Mo
