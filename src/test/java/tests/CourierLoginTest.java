package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import config.Config;
import model.Courier;
import api.CourierApi;

class CourierLoginTest extends BaseTest {
    private String adminToken;
    private Courier courier;
    private Long createdCourierId;

    @BeforeEach void setUp() {
        adminToken = CourierApi.getAdminToken();
        courier = TestData.courier("Пётр", "Петров", "+79998887766");
        createdCourierId = CourierApi.createCourier(courier).then().statusCode(201).extract().jsonPath().getLong("id");
    }

    @Test @Tag("smoke") void loginSuccess() {
        Response response = CourierApi.login(courier.getLogin(), courier.getPassword());
        response.then().statusCode(200).body("token", notNullValue()).body("id", notNullValue());
        assertEquals(createdCourierId, response.jsonPath().getLong("id"));
    }

    @ParameterizedTest @CsvSource({"wrongLogin, password", "qwe123456, wrongPassword"})
    void loginWithWrongCredentials(String login, String password) {
        CourierApi.login(login, password).then().statusCode(401).body("message", containsString(Config.ERROR_BAD_CREDENTIALS));
    }

    @Test void loginNotExistingCourier() {
        CourierApi.login("nettakogo" + System.currentTimeMillis(), "password").then().statusCode(401).body("message", containsString(Config.ERROR_BAD_CREDENTIALS));
    }

    @Test void loginWithoutAuthHeader() {
        given().baseUri(Config.BASE_URI).contentType("application/json")
                .when().post("/api/v1/couriers/login").then().statusCode(401);
    }

    @AfterEach void tearDown() { CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200); }
}