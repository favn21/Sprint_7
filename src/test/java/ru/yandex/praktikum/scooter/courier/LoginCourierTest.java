package ru.yandex.praktikum.scooter.courier;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.CourierClient;
import model.Courier;
import model.CourierLogin;
import util.TestData;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

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
        createResponse.then().statusCode(SC_CREATED);
        Allure.addAttachment("Создание тестового курьера", createResponse.asString());

        Response loginResponse = courierClient.login(new CourierLogin(testCourier.getLogin(), testCourier.getPassword()));
        loginResponse.then().statusCode(SC_OK);
        courierId = loginResponse.path("id");
        Allure.addAttachment("ID тестового курьера после входа", String.valueOf(courierId));
    }

    @Test
    @Story("Курьер может войти в систему")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Успешный логин курьера")
    @Description("Проверка, что курьер может авторизоваться с корректными данными и получает id.")
    public void courierCanLoginTest() {
        CourierLogin login = new CourierLogin(testCourier.getLogin(), testCourier.getPassword());

        Response response = courierClient.login(login);
        response.then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
        Allure.addAttachment("Ответ на логин курьера", response.asString());
    }

    @Test
    @Story("Невозможно войти без логина")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Логин без логина")
    @Description("Проверка, что при попытке входа без логина возвращается ошибка 400 с корректным сообщением.")
    public void cannotLoginWithoutLoginTest() {
        CourierLogin login = new CourierLogin(null, testCourier.getPassword());

        Response response = courierClient.login(login);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
        Allure.addAttachment("Ответ при логине без логина", response.asString());
    }

    // В тесте возвращается статус код 403 вместо 400
    @Test
    @Story("Невозможно войти без пароля")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Логин без пароля")
    @Description("Проверка, что при попытке входа без пароля возвращается ошибка 400 и сообщение о нехватке данных.")
    public void cannotLoginWithoutPasswordTest() {
        CourierLogin login = new CourierLogin(testCourier.getLogin(), null);

        Response response = courierClient.login(login);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));

        Allure.addAttachment("Ответ при логине без пароля", response.asString());
    }

    @Test
    @Story("Система вернёт ошибку при неверном пароле")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что при вводе некорректного пароля система возвращает ошибку 404 и сообщение о том, что учётная запись не найдена.")
    public void cannotLoginWithWrongPasswordTest() {
        CourierLogin login = new CourierLogin(testCourier.getLogin(), "wrongPass");

        Response response = courierClient.login(login);
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Учетная запись не найдена"));
        Allure.addAttachment("Ответ при неверном пароле", response.asString());
    }

    @Test
    @Story("Система вернёт ошибку при авторизации под несуществующим пользователем")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин с несуществующим пользователем")
    @Description("Проверка, что при попытке войти под несуществующим логином возвращается ошибка 404 и сообщение о том, что учётная запись не найдена.")
    public void cannotLoginNonExistingCourierTest() {
        CourierLogin login = new CourierLogin("nonExistingLogin", "1234");

        Response response = courierClient.login(login);
        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", containsString("Учетная запись не найдена"));
        Allure.addAttachment("Ответ при несуществующем пользователе", response.asString());
    }
    @After
    public void deleteCourierIfCreated() {
        if (courierId != null) {
            Response deleteResponse = courierClient.deleteCourier(courierId);
            deleteResponse.then().statusCode(SC_OK);
            Allure.addAttachment("Удаление тестового курьера", deleteResponse.asString());
        }
    }
}



