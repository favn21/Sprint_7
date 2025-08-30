package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected static final String BASE_URI = "https://qa-scooter.praktikum-services.ru";
    @Step("Сформировать базовую спецификацию запроса")
    protected RequestSpecification spec() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .log().all();
    }
}


