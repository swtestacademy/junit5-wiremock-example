
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class WireMockJUnit5Test {

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup () {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
        setupStub();
    }

    @AfterEach
    public void teardown () {
        wireMockServer.stop();
    }

    public void setupStub() {
        wireMockServer.stubFor(get(urlEqualTo("/an/endpoint"))
                .willReturn(aResponse().withHeader("Content-Type", "text/plain")
                        .withStatus(200)
                        .withBodyFile("json/glossary.json")));
    }

    @Test
    public void testStatusCodePositive() {
        given().
                when().
                get("http://localhost:8090/an/endpoint").
                then().
                assertThat().statusCode(200);
    }

    @Test
    public void testStatusCodeNegative() {
        given().
                when().
                get("http://localhost:8090/another/endpoint").
                then().
                assertThat().statusCode(404);
    }

    @Test
    public void testResponseContents() {
        Response response =  given().when().get("http://localhost:8090/an/endpoint");
        String title = response.jsonPath().get("glossary.title");
        System.out.println(title);
        Assert.assertEquals("example glossary", title);
    }
}
