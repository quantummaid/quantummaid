package de.quantummaid.quantummaid.documentation;

import de.quantummaid.httpmaid.HttpMaid;
import de.quantummaid.httpmaid.client.HttpMaidClient;
import de.quantummaid.quantummaid.QuantumMaid;
import org.junit.jupiter.api.Test;

import java.net.SocketException;

import static de.quantummaid.httpmaid.client.HttpClientRequest.aGetRequestToThePath;
import static de.quantummaid.httpmaid.client.HttpMaidClient.aHttpMaidClientForTheHost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public final class ConfigurationTest {
    private static final int PORT = 8080;

    @Test
    public void configuration() {
        //Showcase start configuration
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(PORT);
        //Showcase end configuration
        //Showcase start runAsynchronously
        quantumMaid.runAsynchronously();
        //Showcase end runAsynchronously

        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(PORT)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("Hello World!"));

        //Showcase start close
        quantumMaid.close();
        //Showcase end close

        boolean success = false;
        try {
            client.issue(aGetRequestToThePath("/").mappedToString());
            success = true;
        } catch (final RuntimeException e) {
            assertThat(e.getCause(), instanceOf(SocketException.class));
        }
        assertThat(success, is(false));
    }

    @Test
    public void runSynchronously() {
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(PORT);

        final Thread thread = new Thread(() -> {
            //Showcase start runSynchronously
            quantumMaid.run();
            //Showcase end runSynchronously
        });
        thread.start();

        final HttpMaidClient client = aHttpMaidClientForTheHost("localhost")
                .withThePort(PORT)
                .viaHttp()
                .build();
        final String response = client.issue(aGetRequestToThePath("/").mappedToString());
        assertThat(response, is("Hello World!"));
        thread.interrupt();
    }

    @Test
    public void tryWithResources() {
        final HttpMaid httpMaid = HttpMaid.anHttpMaid()
                .get("/", (request, response) -> response.setBody("Hello World!"))
                .build();
        //Showcase start tryWithResources
        try (final QuantumMaid quantumMaid = QuantumMaid.quantumMaid()
                .withHttpMaid(httpMaid)
                .withLocalHostEndpointOnPort(PORT)) {
            quantumMaid.runAsynchronously();
            // do something
        }
        //Showcase end tryWithResources
    }
}
