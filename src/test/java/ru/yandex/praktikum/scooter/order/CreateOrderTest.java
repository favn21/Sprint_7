package ru.yandex.praktikum.scooter.order;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import client.OrderClient;
import model.Order;
import utilModel.TestData;

import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("Заказы")
@Feature("Создание заказа")
@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final OrderClient orderClient = new OrderClient();
    private Integer trackToCancel;

    private final List<String> colors;

    public CreateOrderTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Object[][] getColorsData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {List.of()}
        };
    }

    @Test
    @Story("Создание заказа с разными вариантами цветов")
    @Severity(SeverityLevel.CRITICAL)
    public void createOrderWithColors() {
        Order order = Order.builder()
                .firstName(TestData.randomFirstName())
                .lastName(TestData.randomLastName())
                .address(TestData.randomAddress())
                .metroStation("4")
                .phone(TestData.randomPhone())
                .rentTime(3)
                .deliveryDate(TestData.futureDate(3))
                .comment("Тестовый заказ")
                .color(colors)
                .build();

        Response resp = orderClient.createOrder(order);

        trackToCancel = resp.then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract().path("track");
        Allure.addAttachment("Ответ на создание заказа", resp.asString());
    }

    @After
    public void tearDown() {
        if (trackToCancel != null) {
            Response cancelResp = orderClient.cancelOrder(trackToCancel);
            cancelResp.then().statusCode(200);
            Allure.addAttachment("Ответ на отмену заказа", cancelResp.asString());
        }
    }
}



