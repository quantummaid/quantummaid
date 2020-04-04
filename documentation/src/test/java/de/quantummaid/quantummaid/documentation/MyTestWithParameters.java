package de.quantummaid.quantummaid.documentation;

import de.quantummaid.quantummaid.integrations.junit5.QuantumMaidTest;
import org.junit.jupiter.api.Test;

@QuantumMaidTest(MyQuantumMaidProvider.class)
public final class MyTestWithParameters {

    //Showcase start testWithUrl
    @Test
    public void testWithUrl(final String url) {
        // implement test
    }
    //Showcase end testWithUrl

    //Showcase start testWithHostAndPort
    @Test
    public void testWithHostAndPort(final String host, final int port) {
        // implement test
    }
    //Showcase end testWithHostAndPort
}
