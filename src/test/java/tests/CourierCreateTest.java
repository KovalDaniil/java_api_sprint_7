package tests;

import api.CourierApi;
import config.Config;
import io.restassured.response.Response;
import model.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

//создание курьера
public class CourierCreateTest {
    private String adminToken;
    // id созданного курьера, чтобы удалить его после теста
    private Long createdCourierId;
    @BeforeEach
    void setUp() {
        adminToken = CourierApi.getAdminToken();
    }
    @Test
    void createCourierSuccess() {
        Courier courier = TestData.courier();

        Response response = CourierApi.createCourier(courier);

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true))
                .body("id", notNullValue());

        createdCourierId = response.jsonPath().getLong("id");
    }
    @Test
    void createDuplicateCourier() {
        Courier courier = TestData.courier();

        Response first = CourierApi.createCourier(courier);
        first.then().statusCode(201);
        createdCourierId = first.jsonPath().getLong("id");
        // пробуем создать второго такого же курьера
        CourierApi.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", containsString("Courier with such login already exists"));
    }
    // без каждого обязательного поля создание падает с ошибкой
    // middleName сюда не берем, необязательный, без него курьер создается нормально
    @ParameterizedTest
    @ValueSource(strings = {"login", "password", "firstName", "lastName", "phone"})
    void createCourierWithoutRequiredField(String missingField) {
        Courier courier = TestData.courier();

        switch (missingField) {
            case "login":
                courier.setLogin(null);
                break;
            case "password":
                courier.setPassword(null);
                break;
            case "firstName":
                courier.setFirstName(null);
                break;
            case "lastName":
                courier.setLastName(null);
                break;
            case "phone":
                courier.setPhone(null);
                break;
        }

        Response response = CourierApi.createCourier(courier);

        // у этой ручки в ошибке всегда поле message
        response.then()
                .statusCode(400)
                .body("message", containsString(missingField));
    }
    @Test
    void createCourierWithExistingLogin() {
        Courier courier = TestData.courier();

        Response first = CourierApi.createCourier(courier);
        first.then().statusCode(201);
        createdCourierId = first.jsonPath().getLong("id");
        // второй курьер с другим именем, но таким же логином — тоже ошибка
        Courier second = TestData.courier("Пётр", "Петров", "+79997654321");
        second.setLogin(courier.getLogin());

        CourierApi.createCourier(second)
                .then()
                .statusCode(409)
                .body("message", containsString("Courier with such login already exists"));
    }
    @Test
    void createCourierWithoutBody() {
        given().baseUri(Config.BASE_URI)
                .contentType("application/json")
                .when()
                .post("/api/v1/couriers")
                .then()
                .statusCode(400)
                .body("message", containsString(Config.ERROR_EMPTY_BODY));
    }
    @AfterEach
    void tearDown() {
        if (createdCourierId != null) {
            CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
            createdCourierId = null;
        }
    }
}