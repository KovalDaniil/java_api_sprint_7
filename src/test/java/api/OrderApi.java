package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

// запросы к заказам
public class OrderApi {
    public static Response createOrder(String token, Order order) {
        return given().baseUri(Config.BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }
    public static Response getOrders(String token) {
        return given().baseUri(Config.BASE_URI)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/orders");
    }
    // отменяем заказ по треку, статус меняется на CANCELLED
    public static Response cancelOrder(String token, String track) {
        return given().baseUri(Config.BASE_URI)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body("[\"" + track + "\"]")
                .when()
                .patch("/api/v1/orders");
    }
}
