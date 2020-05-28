---
title: "Testing"
weight: 1040400001
---
This chapter explains how QuantumMaid supports you in testing your application.
For a step-by-step example of testing a real application, please check out
our [basic tutorial](https://github.com/quantummaid/quantummaid-tutorials/blob/master/basic-tutorial/README.md).

## Dependencies
To test a QuantumMaid application, you need to add the following
dependency:

<!---[CodeSnippet](testdependency)-->
```xml
<dependency>
    <groupId>de.quantummaid.quantummaid.packagings</groupId>
    <artifactId>quantummaid-test-essentials</artifactId>
    <version>1.0.48</version>
    <scope>test</scope>
</dependency>
```

QuantumMaid tests are run with [JUnit 5](https://junit.org/junit5/).
To have Maven execute them properly, you need to add an up-to-date
version of the `maven-surefire-plugin` to the plugin section
of your project's `pom.xml` file:
<!---[CodeSnippet](surefire)-->
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M4</version>
</plugin>
```

## Writing QuantumMaid tests
To give your testsuite a way to instantiate your QuantumMaid application, implement the
`QuantumMaidProvider` interface:
<!---[CodeSnippet](testProvider)-->
```java
import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.quantummaid.QuantumMaid;
import de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidProvider;

public final class MyQuantumMaidProvider implements QuantumMaidProvider {

    @Override
    public QuantumMaid provide(final int port) {
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        return QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(port);
    }
}
```
**Note:** The class needs to have a constructor with no parameters. Otherwise, QuantumMaid will not to be able to instantiate it.

You can now annotate your test classes with the `@QuantumMaidTest` annotation and provide the `QuantumMaidProvider` implementation:
<!---[CodeSnippet](test)-->
```java
import de.quantummaid.quantummaid.integrations.junit5.QuantumMaidTest;
import org.junit.jupiter.api.Test;

@QuantumMaidTest(MyQuantumMaidProvider.class)
public final class MyTest {

    @Test
    public void myTest() {
        // implement test
    }
}
```

Alternatively, you can implement the `QuantumMaidProvider` interface in the test class itself and omit the parameter in the annotation:
<!---[CodeSnippet](inlinedTest)-->
```java
import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.quantummaid.QuantumMaid;
import de.quantummaid.quantummaid.integrations.junit5.QuantumMaidTest;
import de.quantummaid.quantummaid.integrations.testsupport.QuantumMaidProvider;
import org.junit.jupiter.api.Test;

@QuantumMaidTest
public final class MyInlinedTest implements QuantumMaidProvider {

    @Override
    public QuantumMaid provide(final int port) {
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        return QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(port);
    }

    @Test
    public void myTest() {
        // implement test
    }
}
```
If the test class implements `QuantumMaidProvider`, QuantumMaid will always use the test class to obtain your QuantumMaid instance.
This will overwrite any `QuantumMaidProvider` implementation that has been configured via the annotation parameter.

When running tests with the `@QuantumMaidTest` annotation, QuantumMaid will automatically create and deploy an application instance
for every test.
To write meaningful tests, your tests need to know how to interact with the currently deployed application.
If your test declares a parameter of the name `url` and the type `String`, the test will be executed
with that parameter set to the URL of the currently deployed application:   

<!---[CodeSnippet](testWithUrl)-->
```java
@Test
public void testWithUrl(final String url) {
    // implement test
}
```

Alternatively, you can have QuantumMaid provide the host and port of the current deployment: 

<!---[CodeSnippet](testWithHostAndPort)-->
```java
@Test
public void testWithHostAndPort(final String host, final int port) {
    // implement test
}
```

If you use the [REST Assured](http://rest-assured.io/) library, QuantumMaid will automatically initialize the
library for you:

<!---[CodeSnippet](testWithRestAssured)-->
```java
@Test
public void testWithRestAssured() {
    given()
            .when().get("/")
            .then()
            .statusCode(200)
            .body(is("Hello World!"));
}
```

**Warning:** The REST Assured library is supported because it is well known and
allows for very readable test definitions. Despite its widespread use, REST Assured
introduces the vulnerabilities 
<a href="https://nvd.nist.gov/vuln/detail/CVE-2016-6497" target="_blank">CVE-2016-6497</a>,
<a href="https://nvd.nist.gov/vuln/detail/CVE-2016-5394" target="_blank">CVE-2016-5394</a> and 
<a href="https://nvd.nist.gov/vuln/detail/CVE-2016-6798" target="_blank">CVE-2016-6798</a> to your project.
Please check for your project whether these vulnerabilities pose an actual threat.

## Parallel test execution
QuantumMaid tests can be run in parallel.
The only exception is the REST Assured integration because the configuration of REST Assured is global.