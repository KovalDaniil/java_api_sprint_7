package tests;
import api.CourierApi;
import config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class CourierCreateTest {
    private RequestSpecification spec;
    private String adminToken;
    private Long createdCourierId; // запоминаем id, чтобы удалить курьера после теста
    @BeforeEach
    void setUp() {
        spec = new RequestSpecBuilder()
                .setBaseUri(Config.BASE_URI)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .logRequest(LogDetail.ALL)
                .logResponse(LogDetail.ALL)
                .build();
        adminToken = CourierApi.getAdminToken();
    }
        // собираем курьера со случайным логином, чтобы тесты не мешали
    private Courier buildCourier() {
        Courier courier = new Courier();
        courier.setLogin("qwe" + new Random().nextInt(1000000));
        courier.setPassword("password");
        courier.setFirstName("Иван");
        courier.setLastName("Иванов");
        courier.setMiddleName("Иванович");
        courier.setPhone("+79999999999");
        return courier;
    }
    @Test
    void createCourierSuccess() {

        Courier courier = buildCourier();

        Response response = CourierApi.createCourier(spec, courier);

        response.then()

                .statusCode(201)

                .body("ok", equalTo(true))

                .body("id", notNullValue());

        createdCourierId = response.jsonPath().getLong("id");

    }

    @Test

    void createDuplicateCourier() {

        Courier courier = buildCourier();

        CourierApi.createCourier(spec, courier).then().statusCode(201);

        Response second = CourierApi.createCourier(spec, courier);

        second.then()

                .statusCode(409)

                .body("message", containsString("Courier with such login already exists"));

        // достаем id первого курьера из списка и чистим за собой

        Response list = CourierApi.getCouriers(adminToken);

        createdCourierId = list.jsonPath().getLong("couriers.find{it.login == '" + courier.getLogin() + "'}.id");

    }

        // проверяем что без каждого обязательного поля создание падает с ошибкой

    @ParameterizedTest

    @ValueSource(strings = {"login", "password", "firstName", "lastName", "phone"})

    void createCourierWithoutRequiredField(String missingField) {

        Courier courier = buildCourier();

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

                .body("message", containsString(missingField));

    }

    @Test

    void createCourierWithoutBody() {

        given(spec)

                .contentType(ContentType.JSON)

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
