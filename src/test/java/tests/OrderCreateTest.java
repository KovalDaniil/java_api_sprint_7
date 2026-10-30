package tests;

import courier.ScooterSteps;
import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

// Тесты создания заказа с параметризацией по цвету
public class OrderCreateTest {

    private String track;

    // Отменяем созданный заказ после каждого теста, чтобы тесты были независимыми
    @AfterEach
    public void cancelOrder() {
        if (track != null) {
            ScooterSteps.cancelOrder(track);
            track = null;
        }
    }

    @ParameterizedTest(name = "цвет = {0}")
    @ValueSource(strings = {"BLACK", "GREY", "BLACK,GREY"})
    @Description("Заказ можно создать с одним цветом или с обоими цветами")
    public void createOrderWithColorTest(String colors) {
        List<String> color = Arrays.asList(colors.split(","));
        track = ScooterSteps.createOrder(ScooterSteps.newOrder(color))
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract().path("track").toString();
    }

    @Test
    @Description("Заказ можно создать без указания цвета")
    public void createOrderWithoutColorTest() {
        track = ScooterSteps.createOrder(ScooterSteps.newOrder(null))
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract().path("track").toString();
    }
}