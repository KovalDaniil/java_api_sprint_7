package tests;

import model.Courier;

import java.util.Random;

// чтобы не плодить одинаковые курьеры в каждом тесте, собираем их тут
public class TestData {
    public static Courier courier() {
        return courier("Иван", "Иванов", "+79991234567");
    }
    public static Courier courier(String firstName, String lastName, String phone) {
        Courier courier = new Courier();
        // логин рандомный, чтобы тесты не мешали друг другу и не падали из-за дублей
        courier.setLogin("ninja" + new Random().nextInt(1_000_000));
        courier.setPassword("1234");
        courier.setFirstName(firstName);
        courier.setLastName(lastName);
        courier.setMiddleName("Иванович");
        courier.setPhone(phone);
        return courier;
    }
}
