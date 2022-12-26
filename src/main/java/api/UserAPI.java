package api;

import entity.user.Identity;
import entity.user.User;
import io.restassured.response.ValidatableResponse;
import util.API;

import static io.restassured.RestAssured.given;

public class UserAPI extends API {
    private static final String PATH_REGISTER = "/api/auth/register";
    private static final String PATH_LOGIN = "/api/auth/login";
    private static final String PATH_USER = "/api/auth/user";

    public ValidatableResponse register(User user) {
        return given()
                .spec(getSpec())
                .log().all()
                .body(user)
                .post(PATH_REGISTER)
                .then()
                .log().all();
    }

    public ValidatableResponse login(Identity identity) {
        return given()
                .spec(getSpec())
                .log().all()
                .body(identity)
                .post(PATH_LOGIN)
                .then()
                .log().all();
    }

    public ValidatableResponse patchUserWithoutToken(User user) {
        return given()
                .spec(getSpec())
                .log().all()
                .body(user)
                .patch(PATH_USER)
                .then()
                .log().all();
    }

    public ValidatableResponse patchUserWithToken(User user, String accessToken) {
        return given()
                .spec(getSpec())
                .log().all()
                .header("Authorization", accessToken)
                .body(user)
                .patch(PATH_USER)
                .then()
                .log().all();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpec())
                .auth().oauth2(accessToken)
                .log().all()
                .delete(PATH_USER)
                .then();
    }
}
