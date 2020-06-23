
# Running QuantumMaid

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
        .get("/", (request, response) -> response.setBody("Hello World!"))
        .withLocalHostEndpointOnPort(port)) {
    quantumMaid.runAsynchronously();
    // do something
}
```

## Using more advanced features of QuantumMaid
In the last chapter, a lambda function was used to handle the `/` route.
In case more complex features of HttpMaid and MapMaid are used, 
QuantumMaid requires your application to be compiled with the `-parameters` compile option.
Doing so gives QuantumMaid 
<a href="http://openjdk.java.net/jeps/118" target="_blank">runtime access to parameter names</a> and
enables it to map parameters automatically.
Take a look
 <a href="https://www.logicbig.com/how-to/java-command/java-compile-with-method-parameter-names.html" target="_blank">here</a> to learn
how to configure this.
Also make sure that your IDE correctly adopted the `-parameters` option.
If you need to set it manually, look 
<a href="https://www.jetbrains.com/help/idea/specifying-compilation-settings.html#configure_compiler_settings" target="_blank">here</a>
for IntelliJ IDEA and
 <a href="https://stackoverflow.com/questions/9483315/where-do-you-configure-eclipse-java-compiler-javac-flags" target="_blank">here</a> for Eclipse.