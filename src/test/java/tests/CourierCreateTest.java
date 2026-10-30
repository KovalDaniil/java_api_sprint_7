package tests;

import static org.hamcrest.CoreMatchers.equalTo;

import courier.Courier;
import courier.CourierData;
import courier.CourierSteps;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import order.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//Класс тестов для проверки создания курьеров через API
public class CourierCreateTest extends Client {
    private final CourierData courierData = new CourierData(); // Объект для получения тестовых данных
    String id = null; // Переменная для хранения id курьера

    //Устанавливаю базовый URI для RestAssured
    @Before
    // Устанавливаю предварительно настроенную спецификацию запроса в RestAssured перед каждым запуском теста.
    public void setUp() {
        RestAssured.requestSpecification = requestSpec;
    }

    //Тест на создание курьера с валидно заполненными полями.
    @Test
    @Description("Создание курьера с валидно заполеннными полями")
    public void createCourier () {
        // Создаю объект курьера с валидными данными
        Courier courier = new Courier(courierData.getExistingLogin(), courierData.getExistingPassword(), courierData.getFirstName());
        // Отправляю запрос на создание курьер
        Response response = CourierSteps.createCourier(courier);
        // Получаю ID курьера после его создания
        id = CourierSteps.loginCourier(courier)
                .then().extract()
                .path("id").toString();
        // Проверяю статус код и тело ответа
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    //Тест на создание курьера без логина.
    @Test
    @Description("Создание курьера только с паролем и именем")
    public void createCourierWithoutLogin () {
        // Создаю объект курьера без логина
        Courier courier = new Courier("", courierData.getExistingPassword(), courierData.getFirstName());
        // Отправляю запрос на создание курьер
        Response response = CourierSteps.createCourier(courier);
        // Проверяю статус код и тело ответа
        response.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    //Тест на создание курьера без пароля.
    @Test
    @Description("Создание курьера только с логином и именем")
    public void createCourierWithoutPassword() {
        // Создаю объект курьера без пароля
        Courier courier = new Courier(courierData.getExistingLogin(), "", courierData.getFirstName());
        // Отправляю запрос на создание курьер
        Response response = CourierSteps.createCourier(courier);
        // Проверяю статус код и тело ответа
        response.then().assertThat().statusCode(400)
                .and()
                .assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    //Тест на создание двух одинаковых курьеров.
    @Test
    @Description("Создание курьера с валидными данными и повтор создания этого же курьера")
    public void createDoubleCouriers() {
        // Создаю объект курьера со всеми валидными данными
        Courier courier = new Courier(courierData.getExistingLogin(), courierData.getExistingPassword(), courierData.getFirstName());
        // Отправляю запрос на создание курьер
        CourierSteps.createCourier(courier);
        // Повторно отправляем запрос на создание того же курьера
        Response response = CourierSteps.createCourier(courier);
        // Получаю id курьера после его создания
        id = CourierSteps.loginCourier(courier).then().extract().path("id").toString();
        // Проверяю статус код и тело ответа
        response.then().assertThat().statusCode(409)
                .and()
                .assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    //Этот метод выполняется после каждого теста. Он удаляет курьера, если его id не равен null.
    @After
    public void deleteCourier() {
        if (id != null) {
            CourierSteps.deleteCourier(id);
        }
    }
}