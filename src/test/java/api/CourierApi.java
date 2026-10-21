package api;

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Courier;

import static io.restassured.RestAssured.given;

// все запросы к курьерам
public class CourierApi {
    public static Response createCourier(Courier courier) {
        return given().baseUri(Config.BASE_URI)
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }
    public static Response login(String login, String password) {
        return given().baseUri(Config.BASE_URI)
                .contentType(ContentType.JSON)
                .auth().preemptive().basic(login, password)
                .when()
                .post("/api/v1/courier/login");
    }
    public static Response getCouriersOrders(String token, long id) {
        return given().baseUri(Config.BASE_URI)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/courier/" + id + "/ordersCount");
    }
    public static Response deleteCourier(String token, long id) {
        return given().baseUri(Config.BASE_URI)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/courier/" + id);
    }

    public static String getAdminToken() {
        Response response = login(Config.ADMIN_LOGIN, Config.ADMIN_PASSWORD);
        return response.jsonPath().getString("token");
    }
}