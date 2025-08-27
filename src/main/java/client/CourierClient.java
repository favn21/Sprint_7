package client;

import io.qameta.allure.Step;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import model.Courier;
import model.CourierLogin;

import static io.restassured.RestAssured.given;

public class CourierClient extends BaseClient {

    private static final String COURIER = "/api/v1/courier";
    private static final String COURIER_LOGIN = "/api/v1/courier/login";

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
    public Response loginRaw(String jsonBody) {
        return given()
                .header("Content-type", "application/json")
                .body(jsonBody)
                .when()
                .post("/api/v1/courier/login");
    }
}

