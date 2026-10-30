package tests;

import courier.Courier;
import courier.CourierData;
import courier.ScooterSteps;
import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// Тесты авторизации курьера
public class CourierLoginTest {

    private final CourierData courierData = new CourierData();
    private String courierId;

    // Удаляем курьера после каждого теста, чтобы тесты были независимыми
    @AfterEach
    public void deleteCourier() {
        if (courierId != null) {
            ScooterSteps.deleteCourier(courierId);
            courierId = null;
        }
    }

    @Test
    @Description("Курьер может авторизоваться, успешный запрос возвращает id")
    public void loginCourierTest() {
        Courier courier = new Courier(courierData.getLogin(), courierData.getPassword(), courierData.getFirstName());
        ScooterSteps.createCourier(courier).then().statusCode(201);
        courierId = ScooterSteps.loginCourier(courierData.getLogin(), courierData.getPassword())
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id").toString();
    }

    @Test
    @Description("Без логина авторизация не проходит")
    public void loginCourierWithoutLoginTest() {
        ScooterSteps.loginCourier("", courierData.getPassword())
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Без пароля авторизация не проходит")
    public void loginCourierWithoutPasswordTest() {
        ScooterSteps.loginCourier(courierData.getLogin(), "")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Description("Под несуществующим логином авторизация не проходит")
    public void loginCourierWithWrongLoginTest() {
        ScooterSteps.loginCourier(courierData.getLogin() + "x", courierData.getPassword())
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("С неправильным паролем авторизация не проходит")
    public void loginCourierWithWrongPasswordTest() {
        Courier courier = new Courier(courierData.getLogin(), courierData.getPassword(), courierData.getFirstName());
        ScooterSteps.createCourier(courier).then().statusCode(201);
        courierId = ScooterSteps.loginCourier(courierData.getLogin(), courierData.getPassword())
                .then().extract().path("id").toString();
        ScooterSteps.loginCourier(courierData.getLogin(), courierData.getPassword() + "x")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}