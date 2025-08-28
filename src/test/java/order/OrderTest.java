package order;

import io.qameta.allure.Description;
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

public class OrderTest {

    private UserClient userClient = new UserClient();
    private OrderClient orderClient = new OrderClient();
    private String accessToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        // создаём пользователя для заказов
        testUser = new User(randomEmail(), "password123", "TestUser");
        Response response = userClient.create(testUser);
        accessToken = response.then().extract().path("accessToken");
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Создание заказа с авторизацией и ингредиентами")
    void createOrderWithAuthAndIngredients() {
        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6f")); // пример хеша ингредиента

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Создание заказа без авторизации")
    void createOrderWithoutAuth() {
        Order order = new Order(List.of("61c0c5a71d1f82001bdaaa6f"));

        orderClient.createOrder(order, null)
                .then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    void createOrderWithoutIngredients() {
        Order order = new Order(List.of());

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиента")
    void createOrderWithInvalidIngredientHash() {
        Order order = new Order(List.of("invalidHash"));

        orderClient.createOrder(order, accessToken)
                .then()
                .statusCode(500); // сервер может вернуть 500 на неправильный хеш
    }

    private String randomEmail() {
        return "user" + UUID.randomUUID() + "@example.com";
    }
}
