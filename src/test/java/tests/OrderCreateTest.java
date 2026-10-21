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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
public class OrderCreateTest {
    private RequestSpecification spec;
    private String adminToken;

    private Long createdCourierId;

    private String createdTrack; // трек созданного заказа, им и будем отменять

    @BeforeAll

    static void setBaseUri() {

        Config.BASE_URI = System.getProperty("base.uri", Config.BASE_URI);

    }

    @BeforeEach

    void setUp() {

        spec = new RequestSpecBuilder()

                .setBaseUri(Config.BASE_URI)

                .setContentType(ContentType.JSON)

                .addFilter(new AllureRestAssured())

                .build();

        adminToken = CourierApi.getAdminToken();

        // для создания заказа нужен авторизованный пользователь, заводим своего курьера

        Courier courier = new Courier();

        courier.setLogin("qwe" + new Random().nextInt(1000000));

        courier.setPassword("password");

        courier.setFirstName("Сидор");

        courier.setLastName("Сидоров");

        courier.setMiddleName("Сидорович");

        courier.setPhone("+79991112233");

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
    static Stream<Arguments> colorProvider() {
        return Stream.of(

                Arguments.of(Collections.singletonList("BLACK")),

                Arguments.of(Collections.singletonList("GREY")),

                Arguments.of(Arrays.asList("BLACK", "GREY"))

        );
    }
    // один цвет, второй цвет и оба сразу + случай без цветов ниже отдельным тестом
    @ParameterizedTest
    @MethodSource("colorProvider")
    void createOrderWithColors(List<String> colors) {

        String token = CourierApi.login("qwe", "password").jsonPath().getString("token");

        // токен берем того курьера которого создали в beforeeach

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
