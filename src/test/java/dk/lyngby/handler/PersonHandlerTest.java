package dk.lyngby.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.config.HibernateConfig;
import dk.lyngby.model.Person;
import dk.lyngby.util.LoginToken;
import dk.lyngby.util.TestData;
import io.javalin.Javalin;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class   PersonHandlerTest {

    private static Javalin app;
    private static EntityManagerFactory emfTest;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Object adminToken;
    private static Object userToken;
    private static final String BASE_URL = "http://localhost:7777/api/v1";

    @BeforeEach
    void setUpEach() {
        // Setup test database for each test
        TestData.createPersonTestData(emfTest);
    }

    @BeforeAll
    static void setUpAll() {
        // Setup test database
        HibernateConfig.setTest(true);
        emfTest = HibernateConfig.getEntityManagerFactory();

        // Start server
        app = Javalin.create();
        ApplicationConfig.startServer(app, 7777);

        // Get tokens
        adminToken = LoginToken.getAdminToken();
        userToken = LoginToken.getUserToken();
        System.out.println(adminToken);
        System.out.println(userToken);
    }

    @AfterAll
    static void afterAll() {
        HibernateConfig.setTest(false);
        ApplicationConfig.stopServer(app);
    }

    @Test
    @DisplayName("Create person with admin token")
    void createPersonWithAdminToken() {

        // given
        Person person = new Person("John", "Doe", 25, "doe@mail.com");

        // when
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .body(GSON.toJson(person))
                .when()
                .post(BASE_URL + "/person")
                .then()
                .assertThat()
                .statusCode(201)
        // then
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("age", equalTo(25))
                .body("email", equalTo("doe@mail.com"));
    }

    @Test
    @DisplayName("Create person with a not authorized role and expect 401 Unauthorized")
    void createPersonWithNotAcceptedRole() {

        // given
        Person person = new Person("John", "Doe", 25, "doe@mail.com");

        // when
        given()
                .header("Authorization", userToken)
                .contentType("application/json")
                .body(GSON.toJson(person))
                .when()
                .post(BASE_URL + "/person")
                .then()
                .assertThat()
                .statusCode(401)
        // then
                .body("message", equalTo("You are not authorized to perform this action"));

    }

    @Test
    @DisplayName("Create person with invalid email and expect 400 Bad Request")
    void createPersonWithInvalidEmail() {

        // given
        Person person = new Person("John", "Doe", 25, "doemail.com");
        System.out.println(adminToken);
        System.out.println(GSON.toJson(person));
        // when
        given()
                .header("Authorization", adminToken)
                .contentType("application/json")
                .body(GSON.toJson(person))
                .when()
                .post(BASE_URL + "/person")
                .then()
                .assertThat()
        // then
                .statusCode(400);

    }

    @Test
    void getAllPersons() {

        // given
        int listSize = 4;

        // when
        given()
                .contentType("application/json")
                .when()
                .get(BASE_URL + "/person")
                .then()
                .assertThat()
                .statusCode(200)
        // then
                .body("size()", equalTo(listSize));
    }

//    @Test
//    void getPersonById() {
//
//        // given
//
//        // when
//
//        // then
//    }
//
//    @Test
//    void updatePersonById() {
//
//        // given
//
//        // when
//
//        // then
//    }
//
//    @Test
//    void deletePersonById() {
//
//            // given
//
//            // when
//
//            // then
//    }
}