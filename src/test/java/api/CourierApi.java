package api;
import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import static io.restassured.RestAssured.given;

// все запросы к курьерам собрали тут, чтобы в тестах не было длинных цепочек
public class CourierApi {
    public static Response createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/couriers");
    }
    public static Response createCourier(RequestSpecification spec, Courier courier) {
        return spec
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/couriers");
    }
    public static Response login(String login, String password) {
        return given()
                .contentType(ContentType.JSON)
                .auth().preemptive().basic(login, password)
                .when()
                .post("/api/v1/couriers/login");
    }
    public static Response getCouriers(String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/couriers");
    }
    public static Response deleteCourier(String token, long id) {
        return given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/api/v1/couriers/" + id);
    }
        // логинимся под админом и забираем токен. нужен почти в каждом тесте
    public static String getAdminToken() {
        Response response = given()
                .contentType(ContentType.JSON)
                .auth().preemptive().basic(Config.ADMIN_LOGIN, Config.ADMIN_PASSWORD)
                .when()
                .post("/api/v1/couriers/login");
        return response.jsonPath().getString("token");
    }
}
