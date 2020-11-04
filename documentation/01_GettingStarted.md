# Getting Started
This chapter describes the most basic way of creating a running QuantumMaid instance.

## Dependencies and compiler configuration
To run an application with the QuantumMaid framework, you need to add the following dependency to your project:
<!---[CodeSnippet](dependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.quantummaid.packagings</groupId>
    <artifactId>quantummaid-essentials</artifactId>
    <version>1.0.73</version>
</dependency>
```

## Configuring an application

You can configure a QuantumMaid application like this:
<!---[CodeSnippet](configuration)-->
```java
final int port = 8080;
final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
        .get("/", (request, response) -> response.setBody("Hello World!"))
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
<a href="https://github.com/quantummaid/quantummaid-tutorials/blob/master/basic-tutorial/README.md" target="_blank">15 Minute Tutorial</a>.

The remainder of this documentation explains QuantumMaid in depth.
