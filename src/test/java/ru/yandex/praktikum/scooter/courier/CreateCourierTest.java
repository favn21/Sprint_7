package ru.yandex.praktikum.scooter.courier;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.scooter.BaseTest;
import client.CourierClient;
import model.Courier;
import model.CourierLogin;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

@Epic("Курьеры")
@Feature("Создание курьера")
public class CreateCourierTest extends BaseTest {

    private final CourierClient courierClient = new CourierClient();
    private Integer courierId;
    private Courier courierForLogin;

    @Test
    @Story("Курьер успешно создается")
    @DisplayName("Успешное создание нового курьера")
    @Description("Проверка, что курьер может быть создан с валидными данными. " +
            "После создания выполняется логин и проверяется, что возвращается id.")
    public void courierCanBeCreatedTest() {
        courierForLogin = new Courier("ninja" + System.currentTimeMillis(), "1234", "saske");

        Response createResponse = courierClient.create(courierForLogin);
        createResponse.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
        Allure.addAttachment("Ответ на создание курьера", createResponse.asString());
    }

    @Test
    @Story("Нельзя создать двух одинаковых курьеров")
    @DisplayName("Попытка создать дубликата курьера")
    @Description("Проверка, что при повторном создании курьера с теми же данными возвращается ошибка 409 " +
            "и сообщение о том, что логин уже используется.")
    public void cannotCreateDuplicateCourierTest() {
        courierForLogin = new Courier("dup" + System.currentTimeMillis(), "1234", "saske");

        Response firstCreateResponse = courierClient.create(courierForLogin);
        firstCreateResponse.then().statusCode(SC_CREATED);
        Allure.addAttachment("Первый ответ создания курьера", firstCreateResponse.asString());

        Response duplicateResponse = courierClient.create(courierForLogin);
        duplicateResponse.then()
                .statusCode(SC_CONFLICT)
                .body("message", containsString("Этот логин уже используется"));
        Allure.addAttachment("Ответ при попытке создать дубликат", duplicateResponse.asString());
    }

    @Test
    @Story("Нельзя создать курьера без обязательных полей")
    @DisplayName("Создание курьера без обязательных полей")
    @Description("Проверка, что если при создании курьера не указать пароль, то возвращается ошибка 400 " +
            "и сообщение 'Недостаточно данных для создания учетной записи'.")
    public void cannotCreateWithoutRequiredFieldsTest() {
        courierForLogin = new Courier("test" + System.currentTimeMillis(), null, "saske");

        Response response = courierClient.create(courierForLogin);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        Allure.addAttachment("Ответ при создании курьера без обязательных полей", response.asString());

        courierForLogin = null;
    }

    @Test
    @Story("Нельзя создать курьера без логина")
    @DisplayName("Создание курьера без логина")
    @Description("Проверка, что если при создании курьера не указать логин, то возвращается ошибка 400 " +
            "и сообщение 'Недостаточно данных для создания учетной записи'.")
    public void cannotCreateWithoutLoginTest() {
        courierForLogin = new Courier(null, "1234", "saske");

        Response response = courierClient.create(courierForLogin);
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        Allure.addAttachment("Ответ при создании курьера без логина", response.asString());

        courierForLogin = null;
    }

    @After
    public void cleanupCourier() {
        if (courierId != null) {
            Response deleteResponse = courierClient.deleteCourier(courierId);
            deleteResponse.then().statusCode(SC_OK);
            Allure.addAttachment("Удаление тестового курьера", deleteResponse.asString());
        } else if (courierForLogin != null && courierForLogin.getPassword() != null && courierForLogin.getLogin() != null) {
            Response loginResponse = courierClient.login(
                    new CourierLogin(courierForLogin.getLogin(), courierForLogin.getPassword())
            );
            if (loginResponse.statusCode() == SC_OK) {
                courierId = loginResponse.path("id");
                Response deleteResponse = courierClient.deleteCourier(courierId);
                deleteResponse.then().statusCode(SC_OK);
                Allure.addAttachment("Удаление тестового курьера после логина", deleteResponse.asString());
            }
        }
    }
}






