package de.quantummaid.quantummaid.documentation;

//Showcase start testProvider
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
//Showcase end testProvider
