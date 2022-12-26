package api;

import entity.order.Order;
import io.restassured.response.ValidatableResponse;
import util.API;

import static io.restassured.RestAssured.given;

public class OrderAPI extends API {
    public static final String PATH_ORDERS_ALL = "api/orders/all";
    private static final String PATH_ORDERS = "/api/orders";
    private static final String PATH_INGREDIENTS = "/api/ingredients";

    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec())
                .log().all()
                .get(PATH_INGREDIENTS)
                .then()
                .log().all();
    }

    public ValidatableResponse getOrdersWithoutToken() {
        return given()
                .spec(getSpec())
                .log().all()
                .get(PATH_ORDERS)
                .then()
                .log().all();
    }

    public ValidatableResponse getOrdersWithToken(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .log().all()
                .get(PATH_ORDERS)
                .then()
                .log().all();
    }


    public ValidatableResponse createOrderWithoutToken(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .log().all()
                .post(PATH_ORDERS)
                .then()
                .log().all();
    }

    public ValidatableResponse createOrderWithToken(Order order, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .log().all()
                .post(PATH_ORDERS)
                .then()
                .log().all();
    }


}
