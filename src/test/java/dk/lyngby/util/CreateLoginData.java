package dk.lyngby.util;

import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import org.hibernate.Session;

public class CreateLoginData {
    public static void createLoginData(Session session) {

        User user = new User("usertest", "user123");
        User admin = new User("admintest", "admin123");
        User superuser = new User("superusertest", "superuser123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user.addRole(userRole);
        admin.addRole(adminRole);

        try (session; Session session1 = session) {
            session1.getTransaction().begin();
            session1.createNamedQuery("Role.deleteAllRows").executeUpdate();
            session1.createNamedQuery("User.deleteAllRows").executeUpdate();
            session1.persist(userRole);
            session1.persist(adminRole);
            session1.persist(user);
            session1.persist(admin);
            session1.getTransaction().commit();
        }
    }
}