package config;

public class Config {

    //URL
    public static final String SCOOTER_URL = "https://qa-scooter.education-services.ru/";

    // апи для работы с курьерами
    public static final String COURIER = "api/v1/courier/";

    // апи для авторизации курьера
    public static final String LOGIN = "api/v1/courier/login/";

    // апи для работы с заказами
    public static final String ORDER = "api/v1/orders";

    // апи для отмены заказа
    public static final String CANCEL_ORDER = "/api/v1/orders/cancel?track=";
}
