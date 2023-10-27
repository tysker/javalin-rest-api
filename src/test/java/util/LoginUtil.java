package util;

import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManagerFactory;

import java.util.Set;

import static io.restassured.RestAssured.given;

public class LoginUtil {

    private static void persistTestUsers(EntityManagerFactory emf){

        User user = new User("user", "user123");
        User admin = new User("admin", "admin123");
        Role userRole = new Role(Role.RoleName.USER);
        Role adminRole = new Role(Role.RoleName.ADMIN);
        user.setRoleList(Set.of(userRole));
        admin.setRoleList(Set.of(adminRole));

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        }

    }

    private static Object login(String username, String password) {
        String json = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

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

    public static void createTestUsers(EntityManagerFactory emf){
        persistTestUsers(emf);
    }
    public static Object getAdminToken() {
        return login("admin", "admin123");
    }
    public static Object getUserToken() {
        return login("user", "user123");
    }
}
