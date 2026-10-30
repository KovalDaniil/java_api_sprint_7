package config;

public class Config {
    public static final String SCOOTER_URL = "https://qa-scooter.education-services.ru";

    // Ручка создания, удаления и получения списка курьеров. В конец подставляется id курьера
    public static final String COURIER = "/api/v1/courier";

    // Ручка авторизации курьера
    public static final String COURIER_LOGIN = "/api/v1/courier/login";

    // Ручка создания заказа и получения списка заказов
    public static final String ORDERS = "/api/v1/orders";

    // Ручка отмены заказа. В конец подставляется track заказа
    public static final String CANCEL_ORDER = "/api/v1/orders/cancel?track=";
}