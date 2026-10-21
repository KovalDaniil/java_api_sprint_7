package tests;
import api.CourierApi;
import config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.Random;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class CourierLoginTest {
    private RequestSpecification spec;
    private String adminToken;
    private Courier courier; // тестовый курьера создаем до теста и удаляем после

    private Long createdCourierId;
    @BeforeEach
    void setUp() {

        spec = new RequestSpecBuilder()

                .setBaseUri(Config.BASE_URI)

                .setContentType(ContentType.JSON)

                .addFilter(new AllureRestAssured())

                .build();

        adminToken = CourierApi.getAdminToken();

        courier = new Courier();

        courier.setLogin("qwe" + new Random().nextInt(1000000));

        courier.setPassword("password");

        courier.setFirstName("Петр");

        courier.setLastName("Петров");

        courier.setMiddleName("Петрович");

        courier.setPhone("+79998887766");

        Response create = CourierApi.createCourier(spec, courier);

        create.then().statusCode(201);

        createdCourierId = create.jsonPath().getLong("id");

    }

    @Test

    void loginSuccess() {

        Response response = CourierApi.login(courier.getLogin(), courier.getPassword());
        response.then()

                .statusCode(200)

                .body("id", notNullValue());

        // id в ответе должен быть тот же что и при создании

        assertEquals(createdCourierId, response.jsonPath().getLong("id"));

    }
        // неправильно указали то логин то пароль
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

        CourierApi.login("necokolalavka" + new Random().nextInt(1000000), "password")

                .then()

                .statusCode(401)

                .body("message", containsString(Config.ERROR_BAD_CREDENTIALS));

    }
        // без basic auth заголовка вообще пустит на 401

    @Test

    void loginWithoutAuthHeader() {

        givenWithNoAuth()

                .then()

                .statusCode(401);

    }

    private Response givenWithNoAuth() {

        return io.restassured.RestAssured.given(spec)

                .contentType(ContentType.JSON)

                .when()

                .post("/api/v1/couriers/login");

    }

    @AfterEach

    void tearDown() {

        if (createdCourierId != null) {

            CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);

        }

    }

}
