package ru.yandex.praktikum.scooter.order;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.scooter.BaseTest;
import client.OrderClient;
import model.Order;
import utilModel.TestData;

import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("Заказы")
@Feature("Список заказов")
public class GetOrdersListTest extends BaseTest {

    private final OrderClient orderClient = new OrderClient();
    private Integer trackToCancel;

    @Test
    @Story("В ответе приходит список заказов")
    @Severity(SeverityLevel.CRITICAL)
    public void ordersListIsReturned() {
        Order order = Order.builder()
                .firstName(TestData.randomFirstName())
                .lastName(TestData.randomLastName())
                .address(TestData.randomAddress())
                .metroStation("4")
                .phone(TestData.randomPhone())
                .rentTime(5)
                .deliveryDate(TestData.futureDate(2))
                .comment("Автотест: проверка списка заказов")
                .color(List.of("BLACK"))
                .build();

        Response createResp = orderClient.createOrder(order);
        trackToCancel = createResp.then()
                .statusCode(201)
                .extract().path("track");
        Allure.addAttachment("Ответ на создание заказа", createResp.asString());

        Response listResp = orderClient.getOrders();
        listResp.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
        Allure.addAttachment("Ответ при получении списка заказов", listResp.asString());
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
