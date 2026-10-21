package tests;

import api.CourierApi;
import api.OrderApi;
import config.Config;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;

// проверяем создание заказа с разными цветами
public class OrderCreateTest {

    private RequestSpecification spec;
    private String adminToken;
    private Courier courier;
    private Long createdCourierId;
    // трек созданного заказа, им и будем отменять после теста
    private String createdTrack;

    @BeforeAll
    static void setBaseUri() {
        Config.BASE_URI = System.getProperty("base.uri", Config.BASE_URI);
    }

    @BeforeEach
    void setUp() {
        spec = CourierApi.baseSpec();
        adminToken = CourierApi.getAdminToken();

        // для создания заказа нужен авторизованный пользователь, заводим своего курьера
        courier = TestData.courier("Сидор", "Сидоров", "+79991112233");
        Response create = CourierApi.createCourier(spec, courier);
        create.then().statusCode(201);
        createdCourierId = create.jsonPath().getLong("id");
    }

    private Order buildOrder(List<String> colors) {
        Order order = new Order();
        order.setComment("проверка");
        order.setColors(colors);
        order.setDeliveryDate(LocalDate.now().plusDays(1).toString());
        order.setWeight(1);
        return order;
    }

    static Stream<List<String>> colorProvider() {
        return Stream.of(
                Collections.singletonList("BLACK"),
                Collections.singletonList("GREY"),
                Arrays.asList("BLACK", "GREY")
        );
    }

    // один цвет, второй цвет и оба сразу через параметризацию
    @ParameterizedTest
    @MethodSource("colorProvider")
    void createOrderWithColors(List<String> colors) {
        String token = CourierApi.login(courier.getLogin(), courier.getPassword())
                .then().statusCode(200).extract().jsonPath().getString("token");

        Response response = OrderApi.createOrder(token, buildOrder(colors));

        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        createdTrack = response.jsonPath().getString("track");
    }

    // заказ можно создать вообще без цветов
    @Test
    void createOrderWithoutColors() {
        String token = CourierApi.login(courier.getLogin(), courier.getPassword())
                .then().statusCode(200).extract().jsonPath().getString("token");

        Response response = OrderApi.createOrder(token, buildOrder(Collections.emptyList()));

        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        createdTrack = response.jsonPath().getString("track");
    }

    @AfterEach
    void tearDown() {
        if (createdTrack != null) {
            OrderApi.cancelOrder(adminToken, createdTrack).then().statusCode(204);
            createdTrack = null;
        }
        if (createdCourierId != null) {
            CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
        }
    }
}
