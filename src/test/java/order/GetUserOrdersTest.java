package order;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.OrderClient;
import utils.UserClient;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class GetUserOrdersTest {

    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
    private String accessToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = createUser();
        accessToken = getUserAccessToken(testUser);
        createOrderForUser(accessToken, List.of("61c0c5a71d1f82001bdaaa6f"));
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Получение заказов авторизованного пользователя")
    void getOrdersAuthorized() {
        getUserOrders(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Попытка получения заказов без авторизации")
    void getOrdersUnauthorized() {
        getUserOrders(null)
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    // ----------------- Step методы для Allure -----------------

    @Step("Создаём пользователя")
    private User createUser() {
        User user = new User(randomEmail(), "password123", "TestUser");
        userClient.create(user);
        return user;
    }

    @Step("Получаем access token пользователя")
    private String getUserAccessToken(User user) {
        Response response = userClient.login(user);
        return response.then().extract().path("accessToken");
    }

    @Step("Создаём заказ для пользователя")
    private void createOrderForUser(String token, List<String> ingredientHashes) {
        Order order = new Order(ingredientHashes);
        orderClient.createOrder(order, token)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Получаем заказы пользователя")
    private Response getUserOrders(String token) {
        return orderClient.getUserOrders(token);
    }

    private String randomEmail() {
        return "user" + UUID.randomUUID() + "@example.com";
    }
}
