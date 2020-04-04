package de.quantummaid.quantummaid.documentation;

import de.quantummaid.quantummaid.integrations.junit5.QuantumMaidTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@QuantumMaidTest(MyQuantumMaidProvider.class)
public final class TestWithRestAssured {

    //Showcase start testWithRestAssured
    @Test
    public void testWithRestAssured() {
        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body(is("Hello World!"));
    }
    //Showcase end testWithRestAssured
}
