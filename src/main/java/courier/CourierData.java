package courier;

import java.util.concurrent.ThreadLocalRandom;

public class CourierData {
    private final String login;
    private final String password;
    private final String firstName;

    public CourierData() {
        this.login = randomString(10);
        this.password = randomString(8);
        this.firstName = "Ivan";
    }

    // Генерирует случайную строку из строчных латинских букв заданной длины
    public static String randomString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) ThreadLocalRandom.current().nextInt('a', 'z' + 1));
        }
        return result.toString();
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }
}