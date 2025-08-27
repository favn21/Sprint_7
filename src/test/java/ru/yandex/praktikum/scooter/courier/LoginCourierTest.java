package ru.yandex.praktikum.scooter.courier;

import io.qameta.allure.*;
import org.junit.Before;
import org.junit.Test;
import client.CourierClient;
import model.Courier;
import model.CourierLogin;
import utilModel.TestData;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

@Epic("Курьеры")
@Feature("Логин курьера")
public class LoginCourierTest {

    private final CourierClient courierClient = new CourierClient();
    private Integer courierId;
    private Courier testCourier;

    @Before
    public void setUp() {
        testCourier = new Courier(
                TestData.randomLogin(),
                "1234",
                TestData.randomFirstName()
        );

        Response createResponse = courierClient.create(testCourier);
        createResponse.then().statusCode(201);
        Allure.addAttachment("Создание тестового курьера", createResponse.asString());

        Response loginResponse = courierClient.login(new CourierLogin(testCourier.getLogin(), testCourier.getPassword()));
        loginResponse.then().statusCode(200);
        courierId = loginResponse.path("id");
        Allure.addAttachment("ID тестового курьера после входа", String.valueOf(courierId));
    }

    @Test
    @Story("Курьер может войти в систему")
    @Severity(SeverityLevel.CRITICAL)
    public void courierCanLogin() {
        CourierLogin login = new CourierLogin(testCourier.getLogin(), testCourier.getPassword());

        Response response = courierClient.login(login);
        response.then()
                .statusCode(200)
                .body("id", notNullValue());
        Allure.addAttachment("Ответ на логин курьера", response.asString());
    }

    @Test
    @Story("Невозможно войти без логина")
    @Severity(SeverityLevel.NORMAL)
    public void cannotLoginWithoutLogin() {
        CourierLogin login = new CourierLogin(null, testCourier.getPassword());

        Response response = courierClient.login(login);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
        Allure.addAttachment("Ответ при логине без логина", response.asString());
    }
    // В тесте возвращается статус код 403 вместо 400
    @Test
    @Story("Невозможно войти без пароля")
    @Severity(SeverityLevel.NORMAL)
    public void cannotLoginWithoutPassword() {
        String loginWithoutPassword = "{ \"login\": \"" + testCourier.getLogin() + "\" }";

        Response response = courierClient.loginRaw(loginWithoutPassword);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
        Allure.addAttachment("Ответ при логине без пароля", response.asString());
    }

    @Test
    @Story("Система вернёт ошибку при неверном пароле")
    @Severity(SeverityLevel.CRITICAL)
    public void cannotLoginWithWrongPassword() {
        CourierLogin login = new CourierLogin(testCourier.getLogin(), "wrongPass");

        Response response = courierClient.login(login);
        response.then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
        Allure.addAttachment("Ответ при неверном пароле", response.asString());
    }

    @Test
    @Story("Система вернёт ошибку при авторизации под несуществующим пользователем")
    @Severity(SeverityLevel.CRITICAL)
    public void cannotLoginNonExistingCourier() {
        CourierLogin login = new CourierLogin("nonExistingLogin", "1234");

        Response response = courierClient.login(login);
        response.then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
        Allure.addAttachment("Ответ при несуществующем пользователе", response.asString());
    }
}

