package utils;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site/api/";

    @Step("Создать пользователя")
    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(BASE_URL + "auth/register");
    }

    @Step("Удалить пользователя")
    public Response delete(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(BASE_URL + "auth/user");
    }

    @Step("Обновить данные пользователя")
    public Response update(User user, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken != null ? accessToken : "")
                .body(user)
                .when()
                .patch(BASE_URL + "auth/user");
    }

    @Step("Логин пользователя")
    public Response login(String email, String password) {
        return given()
                .header("Content-type", "application/json")
                .body("{\"email\":\""+email+"\",\"password\":\""+password+"\"}")
                .when()
                .post(BASE_URL + "auth/login");
    }

    @Step("Логин через User объект")
    public Response login(User user) {
        return login(user.getEmail(), user.getPassword());
    }
}
