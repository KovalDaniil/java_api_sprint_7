package config;

// тут лежат адрес стенда и данные для входа под админом
public class Config {

    public static String BASE_URI = "https://qa-scooter.education-services.ru/";

    public static final String ADMIN_LOGIN = "admin";

    public static final String ADMIN_PASSWORD = "password";

    // сообщения об ошибках, которые возвращает апи. вынес сюда, чтобы не копипастить в тестах
    public static final String ERROR_EMPTY_BODY = "Request body is empty.";

    public static final String ERROR_BAD_CREDENTIALS = "Wrong login or password";

    public static final String ERROR_FORBIDDEN = "Access is denied";
}
