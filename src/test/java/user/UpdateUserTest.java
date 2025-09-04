package user;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.UserClient;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UpdateUserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    private User originalUser;

    @BeforeEach
    void setUp() {
        originalUser = new User(randomEmail(), "password123", "TestUser");
        Response response = userClient.create(originalUser);
        accessToken = response.then().extract().path("accessToken");
    }

    @AfterEach
    void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Изменение имени пользователя с авторизацией")
    void updateUserWithAuthorization() {
        User updatedUser = new User(originalUser.getEmail(), originalUser.getPassword(), "UpdatedName");
        userClient.update(updatedUser, accessToken)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo("UpdatedName"));
    }

    @Test
    @Description("Попытка изменить данные без авторизации")
    void updateUserWithoutAuthorization() {
        User updatedUser = new User(originalUser.getEmail(), originalUser.getPassword(), "UpdatedName");
        userClient.update(updatedUser, null)
                .then().statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @Description("Изменение email пользователя с авторизацией")
    void updateUserEmailWithAuthorization() {
        String newEmail = randomEmail();
        User updatedUser = new User(newEmail, originalUser.getPassword(), originalUser.getName());
        userClient.update(updatedUser, accessToken)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail.toLowerCase()));
    }

    @Test
    @Description("Изменение пароля пользователя с авторизацией")
    void updateUserPasswordWithAuthorization() {
        String newPassword = "newPassword123";
        User updatedUser = new User(originalUser.getEmail(), newPassword, originalUser.getName());
        userClient.update(updatedUser, accessToken)
                .then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", notNullValue());


        userClient.login(originalUser.getEmail(), newPassword)
                .then().statusCode(200)
                .body("success", equalTo(true));
    }

    private String randomEmail() {
        return "user" + UUID.randomUUID() + "@example.com";
    }
}
