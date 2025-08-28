package user;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import utils.UserClient;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @AfterEach
    void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Успешное создание уникального пользователя")
    void createUniqueUser() {
        User user = new User(randomEmail(), "password123", "TestUser");
        Response response = userClient.create(user);
        accessToken = response.then().extract().path("accessToken");
        response.then().statusCode(200).body("success", equalTo(true));
    }

    @Test
    @Description("Создание пользователя, который уже зарегистрирован")
    void createExistingUser() {
        User user = new User(randomEmail(), "password123", "TestUser");
        Response response = userClient.create(user);
        accessToken = response.then().extract().path("accessToken");

        userClient.create(user)
                .then().statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @Description("Создание пользователя без одного из обязательных полей")
    void createUserWithoutPassword() {
        User user = new User(randomEmail(), "", "TestUser");
        userClient.create(user)
                .then().statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    private String randomEmail() {
        return "user" + UUID.randomUUID() + "@example.com";
    }
}
