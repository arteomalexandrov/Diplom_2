import api.OrderAPI;
import api.UserAPI;
import entity.order.Order;
import entity.user.User;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;

public class TestOrderAPI {
    UserAPI userAPI = new UserAPI();
    OrderAPI orderAPI = new OrderAPI();
    Order order;
    User user;

    @Before
    public void initializations() {
        user = new User();
        user.setEmail("default@mail.ru");
        user.setPassword("12345");
        user.setName("Default");
        order = new Order();
    }

    @Test
    public void createOrderWithAuthorizationAndWithIngredientsTest() {
        ValidatableResponse response = orderAPI.getIngredients();
        List<String> list = response.extract().path("data._id");
        order.getIngredients().add(list.get(1));
        order.getIngredients().add(list.get(2));
        order.getIngredients().add(list.get(2));

        response = userAPI.register(user);
        String token = response.extract().path("accessToken");

        response = orderAPI.createOrderWithToken(order, token);

        int code = response.extract().statusCode();

        Assert.assertEquals(SC_OK, code);

        token = token.split("Bearer ")[1];
        userAPI.deleteUser(token);


        boolean isCreated = response.extract().path("success");
        String orderNumber = response.extract().path("order.number").toString();
        String orderId = response.extract().path("order._id");

        Assert.assertTrue(isCreated);
        Assert.assertNotNull(orderNumber);
        Assert.assertNotNull(orderId);
    }

    @Test
    public void createOrderWithAuthorizationAndWithoutIngredientsTest() {
        ValidatableResponse response = userAPI.register(user);
        String token = response.extract().path("accessToken");

        response = orderAPI.createOrderWithToken(order, token);

        int code = response.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, code);

        token = token.split("Bearer ")[1];
        userAPI.deleteUser(token);

    }

    @Test
    public void createOrderWithoutAuthorizationAndWithIngredientsTest() {
        ValidatableResponse response = orderAPI.getIngredients();
        List<String> list = response.extract().path("data._id");
        order.getIngredients().add(list.get(1));
        order.getIngredients().add(list.get(2));
        order.getIngredients().add(list.get(2));

        response = orderAPI.createOrderWithoutToken(order);

        int code = response.extract().statusCode();

        Assert.assertEquals(SC_OK, code);

        boolean isCreated = response.extract().path("success");
        String orderNumber = response.extract().path("order.number").toString();

        Assert.assertTrue(isCreated);
        Assert.assertNotNull(orderNumber);
    }


    @Test
    public void createOrderWithoutAuthorizationAndWithoutIngredientsTest() {
        ValidatableResponse response = orderAPI.createOrderWithoutToken(order);

        int code = response.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, code);
    }

    @Test
    public void createOrderWithInvalidIngredientsTest() {
        order.getIngredients().add("Invalid");
        ValidatableResponse response = orderAPI.createOrderWithoutToken(order);

        int code = response.extract().statusCode();

        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, code);
    }


    @Test
    public void getOrdersWithAuthorizationTest() {
        ValidatableResponse response = userAPI.register(user);
        String token = response.extract().path("accessToken");
        response = orderAPI.getOrdersWithToken(token);

        int code = response.extract().statusCode();

        token = token.split("Bearer ")[1];
        userAPI.deleteUser(token);

        Assert.assertEquals(SC_OK, code);
    }

    @Test
    public void getOrdersWithoutAuthorizationTest() {
        ValidatableResponse response = orderAPI.getOrdersWithoutToken();

        int code = response.extract().statusCode();

        Assert.assertEquals(SC_UNAUTHORIZED, code);
    }
}