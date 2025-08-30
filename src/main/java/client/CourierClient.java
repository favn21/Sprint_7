package client;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import model.Courier;
import model.CourierLogin;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CourierClient extends BaseClient {

    private static final String COURIER = "/api/v1/courier";
    private static final String COURIER_LOGIN = "/api/v1/courier/login";
    private final String baseUrl = "https://qa-scooter.praktikum-services.ru";


    public CourierClient() {
        spec().filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Step("Создать нового курьера")
    public Response create(Courier courier) {
        return spec()
                .body(courier)
                .when()
                .post(COURIER);
    }

    @Step("Войти в систему как курьер")
    public Response login(CourierLogin courierLogin) {
        return spec()
                .body(courierLogin)
                .when()
                .post(COURIER_LOGIN);
    }
    @Step("Войти в систему как курьер (сырой JSON)")
    public Response loginRaw(String loginJson) {
        return RestAssured.given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .basePath("/api/v1/courier/login")
                .header("Content-Type", "application/json")
                .body(loginJson)
                .post()
                .andReturn();
    }
    @Step("Удалить курьера")
    public Response deleteCourier(int courierId) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", courierId);

        return RestAssured
                .given()
                .header("Content-type", "application/json")
                .body(body)
                .delete(baseUrl + "/api/v1/courier/" + courierId);
    }
}


