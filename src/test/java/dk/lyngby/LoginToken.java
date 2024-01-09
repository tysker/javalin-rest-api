package dk.lyngby;

import static io.restassured.RestAssured.given;

public class LoginToken {

    private static Object login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);

        var token =  given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("http://localhost:7777/api/v1/auth/login")
                .then()
                .extract()
                .response()
                .body()
                .path("token");

        return "Bearer " + token;
    }

    public static Object getAdminToken() {
        return login("admintest", "admin123");
    }

    public static Object getUserToken() {
        return login("usertest", "user123");
    }
}
