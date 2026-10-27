package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static org.hamcrest.Matchers.*;
import config.Config;
import model.Courier;
import model.Order;
import api.CourierApi;
import api.OrderApi;

class OrderListTest extends BaseTest {
    private String adminToken;
    private Long createdCourierId;
    private String createdTrack;
    private String userToken;

    @BeforeEach void setUp() {
        adminToken = CourierApi.getAdminToken();
        Courier courier = TestData.courier();
        createdCourierId = CourierApi.createCourier(courier).then().statusCode(201).extract().jsonPath().getLong("id");
        userToken = CourierApi.login(courier.getLogin(), courier.getPassword()).extract().jsonPath().getString("token");

        Order order = new Order();
        order.setComment("list test");
        order.setDeliveryDate(java.time.LocalDate.now().plusDays(1).toString());
        order.setWeight(1);
        createdTrack = OrderApi.createOrder(userToken, order).then().statusCode(201).extract().jsonPath().getString("track");
    }

    @Test @Tag("smoke") void listContainsCreatedOrder() {
        Response list = OrderApi.getOrders(userToken);
        list.then().statusCode(200).body("orders", hasSize(greaterThan(0)));
        list.then().body("orders.track", containsString(createdTrack));
    }

    @Test @Tag("negative") void listWithoutTokenForbidden() {
        OrderApi.getOrders(null).then().statusCode(403).body("message", containsString(Config.ERROR_FORBIDDEN));
    }

    @AfterEach void tearDown() {
        if (createdTrack != null) OrderApi.cancelOrder(adminToken, createdTrack).then().statusCode(204);
        if (createdCourierId != null) CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);
    }
}