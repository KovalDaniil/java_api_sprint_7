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
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// проверяем ручку создания курьера
public class CourierCreateTest {

    private RequestSpecification spec;
    private String adminToken;
    // id созданного курьера, чтобы удалить его после теста
    private Long createdCourierId;

    @BeforeEach
    void setUp() {
        spec = CourierApi.baseSpec();
        adminToken = CourierApi.getAdminToken();
    }

    @Test
    void createCourierSuccess() {
        Courier courier = TestData.courier();

        Response response = CourierApi.createCourier(spec, courier);

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true))
                .body("id", notNullValue());

        createdCourierId = response.jsonPath().getLong("id");
    }

    @Test
    void createDuplicateCourier() {
        Courier courier = TestData.courier();

        Response first = CourierApi.createCourier(spec, courier);
        first.then().statusCode(201);
        createdCourierId = first.jsonPath().getLong("id");

        // пробуем создать второго такого же курьера
        CourierApi.createCourier(spec, courier)
                .then()
                .statusCode(409)
                .body("message", containsString("Courier with such login already exists"));
    }

    // без каждого обязательного поля создание падает с ошибкой
    // middleName сюда не берем — он необязательный, без него курьер создается нормально
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

        Response response = CourierApi.createCourier(spec, courier);

        response.then()
                .statusCode(400)
                .body("errorMessage", containsString(missingField));
    }

    @Test
    void createCourierWithExistingLogin() {
        Courier courier = TestData.courier();

        Response first = CourierApi.createCourier(spec, courier);
        first.then().statusCode(201);
        createdCourierId = first.jsonPath().getLong("id");

        // второй курьер с другим именем, но таким же логином — тоже ошибка
        Courier second = TestData.courier("Пётр", "Петров", "+79997654321");
        second.setLogin(courier.getLogin());

        CourierApi.createCourier(spec, second)
                .then()
                .statusCode(409)
                .body("message", containsString("Courier with such login already exists"));
    }

    @Test
    void createCourierWithoutBody() {
        CourierApi.createCourier(spec, null)
                .then()
                .statusCode(400)
                .body("errorMessage", containsString(Config.ERROR_EMPTY_BODY));
    }

    @AfterEach
    void tearDown() {
        if (createdCourierId != null) {
            CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
            createdCourierId = null;
        }
    }
}
