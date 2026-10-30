package tests;

import courier.ScooterSteps;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

// Тест получения списка заказов
public class OrderListTest {

    @Test
    @Description("В тело ответа возвращается список заказов")
    public void getOrderListTest() {
        // Создаём заказ, чтобы гарантировать, что список заказов не пустой
        String track = ScooterSteps.createOrder(ScooterSteps.newOrder(null))
                .then()
                .statusCode(201)
                .extract().path("track").toString();
        try {
            List<Object> orders = ScooterSteps.getOrderList()
                    .then()
                    .statusCode(200)
                    .extract().path("orders");
            assertFalse(orders.isEmpty(), "Список заказов пуст");
        } finally {
            ScooterSteps.cancelOrder(track);
        }
    }
}