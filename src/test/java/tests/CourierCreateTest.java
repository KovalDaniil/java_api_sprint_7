package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import config.Config;
import model.Courier;
import api.CourierApi;

@Tag("smoke")
class CourierCreateTest extends BaseTest {
    private String adminToken;
    private Long createdCourierId;

    @BeforeEach void setUp() { adminToken = CourierApi.getAdminToken(); }

    @Test void createCourierSuccess() {
        Response response = CourierApi.createCourier(TestData.courier());
        response.then().statusCode(201).body("ok", equalTo(true)).body("id", notNullValue());
        createdCourierId = response.jsonPath().getLong("id");
    }

    @Test @Tag("negative") void createDuplicateCourier() {
        Courier courier = TestData.courier();
        CourierApi.createCourier(courier).then().statusCode(201);
        CourierApi.createCourier(courier).then().statusCode(409).body("message", containsString(Config.ERROR_LOGIN_EXISTS));
    }

    @ParameterizedTest @ValueSource(strings = {"login","password","firstName","lastName","phone"})
    void createCourierWithoutRequiredField(String missingField) {
        Courier courier = TestData.courier();
        switch (missingField) { case "login": courier.setLogin(null); break; /* ... other cases */ }
        CourierApi.createCourier(courier).then().statusCode(400).body("message", containsString(missingField));
    }

    @Test @Tag("negative") void createCourierWithExistingLogin() {
        Courier first = TestData.courier();
        CourierApi.createCourier(first).then().statusCode(201);
        Courier second = TestData.courier();
        second.setLogin(first.getLogin());
        CourierApi.createCourier(second).then().statusCode(409).body("message", containsString(Config.ERROR_LOGIN_EXISTS));
    }

    @Test @Tag("negative") void createCourierWithoutBody() {
        given().baseUri(Config.BASE_URI).contentType("application/json")
                .when().post("/api/v1/couriers")
                .then().statusCode(400).body("message", containsString(Config.ERROR_EMPTY_BODY));
    }

    @AfterEach void tearDown() {
        if (createdCourierId != null) CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
    }
}