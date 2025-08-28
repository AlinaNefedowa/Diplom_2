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

public class LoginTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    private User testUser;

    @BeforeEach
    void setUp() {
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
    @Description("Логин под существующим пользователем")
    void loginWithValidUser() {
        userClient.login(testUser)
                .then().statusCode(200).body("success", equalTo(true));
    }

    @Test
    @Description("Логин с неверным логином и паролем")
    void loginWithInvalidCredentials() {
        userClient.login("wrong" + testUser.getEmail(), "wrongPassword")
                .then().statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    private String randomEmail() {
        return "user" + UUID.randomUUID() + "@example.com";
    }
}
