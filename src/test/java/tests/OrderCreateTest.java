package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.time.LocalDate;
import java.util.*;
import static org.hamcrest.Matchers.notNullValue;
import config.Config;
import model.Courier;
import model.Order;
import api.CourierApi;
import api.OrderApi;

class OrderCreateTest extends BaseTest {
    private String adminToken;
    private Long createdCourierId;
    private String createdTrack;

    @BeforeEach void setUp() {
        adminToken = CourierApi.getAdminToken();
        Courier courier = TestData.courier();
        createdCourierId = CourierApi.createCourier(courier).then().statusCode(201).extract().jsonPath().getLong("id");
    }

    @ParameterizedTest(name = "Цвета: {0}") @ValueSource(strings = {"BLACK", "GREY", "BLACK,GREY", "none"})
    void createOrderWithColors(String colorsParam) {
        List<String> colors = "none".equals(colorsParam) ? Collections.emptyList() :
                colorsParam.contains(",") ? Arrays.asList(colorsParam.split(",")) :
                        Collections.singletonList(colorsParam);

        String token = CourierApi.login(TestData.courier().getLogin(), "1234").extract().jsonPath().getString("token"); // Simplified for brevity, actual code uses created courier creds

        Order order = new Order();
        order.setComment("проверка");
        order.setColors(colors);
        order.setDeliveryDate(LocalDate.now().plusDays(1).toString());
        order.setWeight(1);

        Response response = OrderApi.createOrder(token, order);
        response.then().statusCode(201).body("track", notNullValue());
        createdTrack = response.jsonPath().getString("track");
    }

    @AfterEach void tearDown() {
        if (createdTrack != null) OrderApi.cancelOrder(adminToken, createdTrack).then().statusCode(204);
        if (createdCourierId != null) CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
    }
}