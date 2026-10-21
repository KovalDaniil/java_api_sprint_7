package tests;

import api.CourierApi;
import config.Config;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

// проверяем ручку логина курьера
public class CourierLoginTest {

    private RequestSpecification spec;
    private String adminToken;
    // тестового курьера заводим до теста и удаляем после
    private Courier courier;
    private Long createdCourierId;

    @BeforeEach
    void setUp() {
        spec = CourierApi.baseSpec();
        adminToken = CourierApi.getAdminToken();

        courier = TestData.courier("Пётр", "Петров", "+79998887766");
        Response create = CourierApi.createCourier(spec, courier);
        create.then().statusCode(201);
        createdCourierId = create.jsonPath().getLong("id");
    }

    @Test
    void loginSuccess() {
        Response response = CourierApi.login(courier.getLogin(), courier.getPassword());

        response.then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("id", notNullValue());

        // id в ответе должен быть тот же, что и при создании
        assertEquals(createdCourierId, response.jsonPath().getLong("id"));
    }

    // неправильно указали то логин, то пароль
    @ParameterizedTest
    @CsvSource({
            "wrongLogin, password",
            "qwe123456, wrongPassword"
    })
    void loginWithWrongCredentials(String login, String password) {
        CourierApi.login(login, password)
                .then()
                .statusCode(401)
                .body("message", containsString(Config.ERROR_BAD_CREDENTIALS));
    }

    @Test
    void loginNotExistingCourier() {
        CourierApi.login("nettakogo" + (int) (Math.random() * 1_000_000), "password")
                .then()
                .statusCode(401)
                .body("message", containsString(Config.ERROR_BAD_CREDENTIALS));
    }

    // без basic auth заголовка ручка не пустит
    @Test
    void loginWithoutAuthHeader() {
        given(spec)
                .contentType("application/json")
                .when()
                .post("/api/v1/couriers/login")
                .then()
                .statusCode(401);
    }

    @AfterEach
    void tearDown() {
        if (createdCourierId != null) {
            CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
        }
    }
}
