package tests;

import java.util.Random;
import model.Courier;

public class TestData {
    public static Courier courier() {
        return courier("Иван", "Иванов", "+79991234567");
    }

    public static Courier courier(String firstName, String lastName, String phone) {
        Courier courier = new Courier();
        courier.setLogin("ninja" + new Random().nextInt(1_000_000));
        courier.setPassword("1234");
        courier.setFirstName(firstName);
        courier.setLastName(lastName);
        courier.setMiddleName("Иванович");
        courier.setPhone(phone);
        return courier;
    }
}