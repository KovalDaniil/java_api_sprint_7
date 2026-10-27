package config;

public class Config {
    public static String BASE_URI = System.getenv().getOrDefault("SCOOTER_BASE_URI", "https://qa-scooter.education-services.ru");
    //public static final String ADMIN_LOGIN = "ninja";
    //public static final String ADMIN_PASSWORD = "1234";
    public static final String ERROR_EMPTY_BODY = "Request body is empty.";
    public static final String ERROR_BAD_CREDENTIALS = "Wrong login or password";
    public static final String ERROR_FORBIDDEN = "Access is denied";
    public static final String ERROR_LOGIN_EXISTS = "Courier with such login already exists";
}