package client;

import io.qameta.allure.Step;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import model.Order;

public class OrderClient extends BaseClient {

    private static final String ORDERS = "/api/v1/orders";

    public OrderClient() {
        spec().filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return spec()
                .body(order)
                .when()
                .post(ORDERS);
    }

    @Step("Отменить заказ по track={track}")
    public Response cancelOrder(int track) {
        return spec()
                .when()
                .put(ORDERS + "/cancel?track=" + track);
    }

    @Step("Получить список заказов")
    public Response getOrders() {
        return spec()
                .when()
                .get(ORDERS);
    }
}



