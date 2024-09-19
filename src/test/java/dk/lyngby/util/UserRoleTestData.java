package dk.lyngby.util;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UserRoleTestData {

    static EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {
        User user = new User("usertest", "user123");
        User admin = new User("admintest", "admin123");
        User superuser = new User("superusertest", "superuser123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user.addRole(userRole);
        admin.addRole(adminRole);
        superuser.addRole(userRole);
        superuser.addRole(adminRole);

        try(EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows", Role.class).executeUpdate();
            em.createNamedQuery("User.deleteAllRows", User.class).executeUpdate();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(superuser);
            em.getTransaction().commit();
        };
    }
}
