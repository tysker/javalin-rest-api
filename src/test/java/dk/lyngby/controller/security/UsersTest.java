package dk.lyngby.controller.security;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.LoginUtil;

import static io.restassured.RestAssured.given;

public class UsersTest {
    private static Javalin app;
    private static final String BASE_URL = "http://localhost:7777/api/v1";
    private static Object adminToken;

    @BeforeAll
    public static void setUp() {
        // Setup test database
        EntityManagerFactory emfTest = HibernateConfig.getEntityManagerFactory(true);
        LoginUtil.createTestUsers(emfTest);

        // Start server
//        app = Javalin.create();
        ApplicationConfig.startServer(7777);

        adminToken = LoginUtil.getAdminToken();

    }

    @AfterAll
    static void afterAll() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    public void test() {

        System.out.println(adminToken);

        // given
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .when()
                .get(BASE_URL + "/test/")
                .then()
                .statusCode(200);
    }
}
