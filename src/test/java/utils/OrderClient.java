package utils;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";

    @Step("Создать заказ")
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken != null ? accessToken : "")
                .body(order)
                .when()
                .post(BASE_URL + "orders");
    }

    @Step("Получить заказы пользователя")
    public Response getUserOrders(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken != null ? accessToken : "")
                .when()
                .get(BASE_URL + "orders");
    }
}
