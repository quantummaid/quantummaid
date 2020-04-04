package de.quantummaid.quantummaid.documentation;

//Showcase start inlinedTest
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
//Showcase end inlinedTest
