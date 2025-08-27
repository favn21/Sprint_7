package ru.yandex.praktikum.scooter.courier;

import io.qameta.allure.*;
import org.junit.Test;
import ru.yandex.praktikum.scooter.BaseTest;
import client.CourierClient;
import model.Courier;
import model.CourierLogin;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

@Epic("Курьеры")
@Feature("Создание курьера")
public class CreateCourierTest extends BaseTest {

    private final CourierClient courierClient = new CourierClient();
    private Integer courierId;

    @Test
    @Story("Курьер успешно создается")
    public void courierCanBeCreated() {
        Courier courier = new Courier("ninja" + System.currentTimeMillis(), "1234", "saske");

        Response createResponse = courierClient.create(courier);
        createResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));
        Allure.addAttachment("Ответ на создание курьера", createResponse.asString());

        Response loginResponse = courierClient.login(new CourierLogin(courier.getLogin(), courier.getPassword()));
        loginResponse.then().statusCode(200);
        courierId = loginResponse.path("id");
        Allure.addAttachment("ID курьера после входа", String.valueOf(courierId));
    }

    @Test
    @Story("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        Courier courier = new Courier("dup" + System.currentTimeMillis(), "1234", "saske");

        Response firstCreateResponse = courierClient.create(courier);
        firstCreateResponse.then().statusCode(201);
        Allure.addAttachment("Первый ответ создания курьера", firstCreateResponse.asString());

        Response duplicateResponse = courierClient.create(courier);
        duplicateResponse.then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
        Allure.addAttachment("Ответ при попытке создать дубликат", duplicateResponse.asString());

        Response loginResponse = courierClient.login(new CourierLogin(courier.getLogin(), courier.getPassword()));
        loginResponse.then().statusCode(200);
        courierId = loginResponse.path("id");
        Allure.addAttachment("ID курьера после входа", String.valueOf(courierId));
    }

    @Test
    @Story("Нельзя создать курьера без обязательных полей")
    public void cannotCreateWithoutRequiredFields() {
        Courier courier = new Courier("test" + System.currentTimeMillis(), null, "saske");

        Response response = courierClient.create(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        Allure.addAttachment("Ответ при создании курьера без обязательных полей", response.asString());
    }
}


