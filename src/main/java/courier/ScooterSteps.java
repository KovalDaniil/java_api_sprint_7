package courier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import order.Order;

import java.time.LocalDate;
import java.util.List;

import static config.Config.*;

public class ScooterSteps {

    // Gson с отключенным HTML-экранированием, чтобы кириллица в запросах не ломалась
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static RequestSpecification baseSpec() {
        return RestAssured.given()
                .baseUri(SCOOTER_URL)
                .contentType("application/json");
    }

    @Step("Создание курьера")
    public static Response createCourier(Courier courier) {
        return baseSpec()
                .body(GSON.toJson(courier))
                .when()
                .post(COURIER);
    }

    @Step("Авторизация курьера")
    public static Response loginCourier(String login, String password) {
        String body = GSON.toJson(new Courier(login, password, null));
        return baseSpec()
                .body(body)
                .when()
                .post(COURIER_LOGIN);
    }

    @Step("Удаление курьера")
    public static void deleteCourier(String courierId) {
        baseSpec()
                .when()
                .delete(COURIER + "/" + courierId);
    }

    @Step("Создание заказа")
    public static Response createOrder(Order order) {
        return baseSpec()
                .header("skipAuth", "true")
                .body(GSON.toJson(order))
                .when()
                .post(ORDERS);
    }

    @Step("Получение списка заказов")
    public static Response getOrderList() {
        return baseSpec()
                .header("skipAuth", "true")
                .param("limit", 100)
                .param("page", 1)
                .when()
                .get(ORDERS);
    }

    @Step("Отмена заказа")
    public static void cancelOrder(String track) {
        baseSpec()
                .header("skipAuth", "true")
                .when()
                .put(CANCEL_ORDER + track);
    }

    // Создаёт заказ с дефолтными данными. color может быть null — тогда цвет не указывается
    @Step("Создание тестового заказа")
    public static Order newOrder(List<String> color) {
        return new Order("Иван", "Петров", "Москва, ул. Тверская, д. 1", "Черкизовская",
                "+79998886677", 6, LocalDate.now().plusDays(1).toString(), "Оставить у двери", color);
    }
}