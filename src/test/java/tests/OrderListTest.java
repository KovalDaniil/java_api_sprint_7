package tests;
import api.CourierApi;
import api.OrderApi;
import config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Random;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class OrderListTest {
    private RequestSpecification spec;
    private String adminToken;
    private Courier courier;
    private Long createdCourierId;
    private String createdTrack;
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

        courier.setFirstName("Олег");

        courier.setLastName("Олегов");

        courier.setMiddleName("Олегович");

        courier.setPhone("+79995554433");

        Response create = CourierApi.createCourier(spec, courier);

        createdCourierId = create.jsonPath().getLong("id");

        create.then().statusCode(201);

    }
    @Test

    void listContainsCreatedOrder() {

        String token = CourierApi.login(courier.getLogin(), courier.getPassword())

                .then().statusCode(200).extract().jsonPath().getString("token");

        Order order = new Order();

        order.setComment("проверка списка");

        order.setColors(Collections.emptyList());

        order.setDeliveryDate(LocalDate.now().plusDays(1).toString());

        order.setWeight(1);

        Response create = OrderApi.createOrder(token, order);

        createdTrack = create.jsonPath().getString("track");

        create.then().statusCode(201);

        Response list = OrderApi.getOrders(token);

        list.then()
                .statusCode(200)

                .body("orders", hasSize(greaterThan(0)));
        // проверяем что наш заказ действительно прилетел в списке

        int total = list.jsonPath().getInt("totalItems");

        assertTrue(total >= 1);
    }
        // без токена список заказов недоступен

    @Test
    void listWithoutTokenForbidden() {

        OrderApi.getOrders(null)

                .then()

                .statusCode(403)

                .body("message", containsString(Config.ERROR_FORBIDDEN));

    }
    @AfterEach
    void tearDown() {

        if (createdTrack != null) {

            OrderApi.cancelOrder(adminToken, createdTrack).then().statusCode(204);

        }

        if (createdCourierId != null) {

            CourierApi.deleteCourier(adminToken, createdCourierId).then().statusCode(200);

        }

    }
}
