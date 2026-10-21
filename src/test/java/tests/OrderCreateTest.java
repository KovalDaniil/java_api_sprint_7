package tests;

import api.CourierApi;
import api.OrderApi;
import io.restassured.response.Response;
import model.Courier;
import model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

// проверяем создание заказа с разными цветами
public class OrderCreateTest {
    private String adminToken;
    private Courier courier;
    private Long createdCourierId;
    // трек созданного заказа, им и будем отменять после теста
    private String createdTrack;
    @BeforeEach
    void setUp() {
        adminToken = CourierApi.getAdminToken();
        // для создания заказа нужен авторизованный пользователь, заводим своего курьера
        courier = TestData.courier("Сидор", "Сидоров", "+79991112233");
        Response create = CourierApi.createCourier(courier);
        create.then().statusCode(201);
        createdCourierId = create.jsonPath().getLong("id");
    }
    // один цвет, второй цвет, оба сразу и без цветов
    @ParameterizedTest
    @ValueSource(strings = {"BLACK", "GREY", "BLACK,GREY", "none"})
    void createOrderWithColors(String colorsParam) {
        List<String> colors;
        if (colorsParam.equals("none")) {
            colors = Collections.emptyList();
        } else if (colorsParam.contains(",")) {
            colors = Arrays.asList(colorsParam.split(","));
        } else {
            colors = Collections.singletonList(colorsParam);
        }

        String token = CourierApi.login(courier.getLogin(), courier.getPassword())
                .then().statusCode(200).extract().jsonPath().getString("token");

        Order order = new Order();
        order.setComment("проверка");
        order.setColors(colors);
        order.setDeliveryDate(LocalDate.now().plusDays(1).toString());
        order.setWeight(1);

        Response response = OrderApi.createOrder(token, order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());   // тело ответа содержит track — требование ТЗ

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