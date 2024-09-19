package dk.lyngby.util;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class PopulateTestData {

    static EntityManagerFactory EMF = HibernateConfig.getEntityManagerFactory();

    public static void main(String[] args) {

        EntityManager em = EMF.createEntityManager();
        User user = new User("usertest", "user123");
        User admin = new User("admintest", "admin123");
        User superuser = new User("superusertest", "superuser123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user.addRole(userRole);
        admin.addRole(adminRole);
        superuser.addRole(userRole);
        superuser.addRole(adminRole);

        try {
            em.getTransaction().begin();
            em.createNamedQuery("Role.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(superuser);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
