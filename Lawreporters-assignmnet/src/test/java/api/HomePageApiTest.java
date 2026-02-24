package api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;
import utils.ConfigManager;

public class HomePageApiTest {

    @Test(description = "Validate The Law Reporters home page API response")
    public void validateHomePageResponse() {
        given()
                .baseUri(ConfigManager.get("api.base.url", "https://thelawreporters.com/"))
        .when()
                .get("/")
        .then()
                .statusCode(anyOf(equalTo(200), equalTo(301), equalTo(302)))
                .body(containsStringIgnoringCase("lawreporters"));
    }
}
