import api.UserAPI;
import entity.user.Identity;
import entity.user.User;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class TestUserAPI {
    private final User user = new User();
    private final UserAPI api = new UserAPI();
    private String token;

    @Before
    public void initializations() {
        user.setEmail("default@mail.ru");
        user.setPassword("12345");
        user.setName("Default");
    }

    @Test
    public void createUserTest() {
        ValidatableResponse response = api.register(user);
        int code = response.extract().statusCode();
        Assert.assertEquals(SC_OK, code);
        token = response.extract().path("accessToken").toString().split("Bearer ")[1];
    }

    @Test
    public void createSameUserTest() {
        ValidatableResponse response = api.register(user);
        token = response.extract().path("accessToken").toString().split("Bearer ")[1];
        response = api.register(user);
        int code = response.extract().statusCode();
        Assert.assertEquals(SC_FORBIDDEN, code);
    }

    @Test
    public void createUserWithEmptyFieldTest() {
        ValidatableResponse response = api.register(user);
        token = response.extract().path("accessToken").toString().split("Bearer ")[1];
        user.setName("");
        response = api.register(user);
        int code = response.extract().statusCode();
        Assert.assertEquals(SC_FORBIDDEN, code);
    }

    @Test
    public void loginTest() {
        api.register(user);
        ValidatableResponse response = api.login(new Identity(user));
        int code = response.extract().statusCode();
        Assert.assertEquals(SC_OK, code);
        token = response.extract().path("accessToken").toString().split("Bearer ")[1];
    }

    @Test
    public void loginWithInvalidMailTest() {
        ValidatableResponse responseCreate = api.register(user);
        token = responseCreate.extract().path("accessToken").toString().split("Bearer ")[1];
        Identity identity = new Identity(user);
        identity.setEmail("invalid");
        ValidatableResponse responseLogin = api.login(identity);
        int code = responseLogin.extract().statusCode();
        Assert.assertEquals(SC_UNAUTHORIZED, code);
    }

    @Test
    public void loginWithInvalidPasswordTest() {
        ValidatableResponse responseCreate = api.register(user);
        token = responseCreate.extract().path("accessToken").toString().split("Bearer ")[1];
        Identity identity = new Identity(user);
        identity.setPassword("12");
        ValidatableResponse responseLogin = api.login(identity);
        int code = responseLogin.extract().statusCode();
        Assert.assertEquals(SC_UNAUTHORIZED, code);
    }

    @Test
    public void updateDataUserWithAuthorizationTest() {
        ValidatableResponse responseCreate = api.register(user);
        token = responseCreate.extract().path("accessToken");
        user.setEmail("new" + user.getEmail());
        user.setName("new" + user.getName());

        ValidatableResponse responseUpdate = api.patchUserWithToken(user, token);
        int code = responseUpdate.extract().statusCode();
        Assert.assertEquals(SC_OK, code);
        String actualEmail = responseUpdate.extract().path("user.email");
        Assert.assertEquals(user.getEmail(), actualEmail);
        String actualName = responseUpdate.extract().path("user.name");
        Assert.assertEquals(user.getName(), actualName);
        token = token.split("Bearer ")[1];
    }

    @Test
    public void updateDataUserWithoutAuthorizationTest() {
        ValidatableResponse responseCreate = api.register(user);
        token = responseCreate.extract().path("accessToken").toString().split("Bearer ")[1];
        user.setEmail("new" + user.getEmail());
        user.setPassword("new" + user.getPassword());
        ValidatableResponse responseUpdate = api.patchUserWithoutToken(user);
        int code = responseUpdate.extract().statusCode();
        Assert.assertEquals(SC_UNAUTHORIZED, code);
    }


    @After
    public void clear() {
        ValidatableResponse response = api.deleteUser(token);
        Assert.assertEquals(SC_ACCEPTED, response.extract().statusCode());
    }
}