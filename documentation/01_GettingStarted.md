# Configuring and running a QuantumMaid application
This chapter is explains how to configure and run a QuantumMaid application.

## Dependencies and compiler configuration
To run an application with the QuantumMaid framework, you need to add the following dependency to your project:
<!---[CodeSnippet](dependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.quantummaid.packagings</groupId>
    <artifactId>quantummaid-essentials</artifactId>
    <version>1.0.29</version>
</dependency>
```

## Configuring an application

You can configure a QuantumMaid application like this:
<!---[CodeSnippet](configuration)-->
```java
final int port = 8080;
final HttpMaid httpMaid = HttpMaid.anHttpMaid()
        .get("/", (request, response) -> response.setBody("Hello World!"))
        .build();
final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
        .withHttpMaid(httpMaid)
        .withLocalHostEndpointOnPort(port);
quantumMaid.runAsynchronously();
```
Start the application and navigate to to the url to see the response. 
```
$ curl http://localhost:8080/helloworld
"hello world"
```

## Next Steps

To learn more about handling requests, check out the 
[15 Minute Tutorial](https://github.com/quantummaid/quantummaid-tutorials/blob/master/basic-tutorial/README.md).

The remainder of this documentation to explains QuantumMaid in depth.