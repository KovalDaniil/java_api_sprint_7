package tests;

import courier.Courier;
import courier.CourierData;
import courier.ScooterSteps;
import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

// Тесты создания курьера
public class CourierCreateTest {

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
    @Description("Курьера можно создать, ответ возвращает код 201 и ok: true")
    public void createCourierTest() {
        Courier courier = new Courier(courierData.getLogin(), courierData.getPassword(), courierData.getFirstName());
        ScooterSteps.createCourier(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
        courierId = ScooterSteps.loginCourier(courierData.getLogin(), courierData.getPassword())
                .then().extract().path("id").toString();
    }

    @Test
    @Description("Нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourierTest() {
        Courier courier = new Courier(courierData.getLogin(), courierData.getPassword(), courierData.getFirstName());
        ScooterSteps.createCourier(courier).then().statusCode(201);
        courierId = ScooterSteps.loginCourier(courierData.getLogin(), courierData.getPassword())
                .then().extract().path("id").toString();
        ScooterSteps.createCourier(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Description("Без логина курьер не создаётся")
    public void createCourierWithoutLoginTest() {
        Courier courier = new Courier("", courierData.getPassword(), courierData.getFirstName());
        ScooterSteps.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Description("Без пароля курьер не создаётся")
    public void createCourierWithoutPasswordTest() {
        Courier courier = new Courier(courierData.getLogin(), "", courierData.getFirstName());
        ScooterSteps.createCourier(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Description("С логином, который уже есть, возвращается ошибка")
    public void createCourierWithExistingLoginTest() {
        Courier courier = new Courier(courierData.getLogin(), courierData.getPassword(), courierData.getFirstName());
        ScooterSteps.createCourier(courier).then().statusCode(201);
        courierId = ScooterSteps.loginCourier(courierData.getLogin(), courierData.getPassword())
                .then().extract().path("id").toString();
        // Второй курьер с тем же логином, но другими данными
        Courier secondCourier = new Courier(courierData.getLogin(), "otherPass", "Petr");
        ScooterSteps.createCourier(secondCourier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }
}