# Configuring and running a QuantumMaid application
This chapter is explains how to configure and run a QuantumMaid application.

## Dependencies and compiler configuration
To run an application with the QuantumMaid framework, you need to add the following dependency to your project:
<!---[CodeSnippet](dependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.quantummaid.packagings</groupId>
    <artifactId>quantummaid-essentials</artifactId>
    <version>1.0.22</version>
</dependency>
```

QuantumMaid requires your application to be compiled with the `-parameters` compile option.
Doing so gives QuantumMaid [runtime access to parameter names](http://openjdk.java.net/jeps/118) and
enables it to map parameters automatically.
Take a look [here](https://www.logicbig.com/how-to/java-command/java-compile-with-method-parameter-names.html) to learn
how to configure this.
Also make sure that your IDE correctly adopted the `-parameters` option.
If you need to set it manually, look [here](https://www.jetbrains.com/help/idea/specifying-compilation-settings.html#configure_compiler_settings)
for IntelliJ IDEA and [here](https://stackoverflow.com/questions/9483315/where-do-you-configure-eclipse-java-compiler-javac-flags) for Eclipse.

## Configuring an application

You can configure a QuantumMaid application like this:
<!---[CodeSnippet](configuration)-->
```java
final HttpMaid httpMaid = HttpMaid.anHttpMaid()
        .get("/", (request, response) -> response.setBody("Hello World!"))
        .build();
final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
        .withHttpMaid(httpMaid)
        .withLocalHostEndpointOnPort(port);
```
Please refer to the [HttpMaid documentation](https://github.com/quantummaid/httpmaid/blob/master/README.md) for details on the HttpMaid configuration. 


## Running the application
There are two different options to run a configured QuantumMaid application.
If you want QuantumMaid to start the application and then block the current thread, you can run it like this: 
<!---[CodeSnippet](runSynchronously)-->
```java
quantumMaid.run();
```

If you want QuantumMaid to start the application and then return, you run it like this:

<!---[CodeSnippet](runAsynchronously)-->
```java
quantumMaid.runAsynchronously();
```


## Cleaning up resources
In order to clean up allocated resources like open network ports, you can run the `close()` method:

<!---[CodeSnippet](close)-->
```java
quantumMaid.close();
```

Because the `QuantumMaid` class implements the `AutoClosable` interface, you can alternatively use
a try-with-resources statement:

<!---[CodeSnippet](tryWithResources)-->
```java
try (QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
        .withHttpMaid(httpMaid)
        .withLocalHostEndpointOnPort(port)) {
    quantumMaid.runAsynchronously();
    // do something
}
```
