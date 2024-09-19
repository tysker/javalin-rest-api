package dk.lyngby.util;

import dk.lyngby.model.Person;
import dk.lyngby.model.Role;
import dk.lyngby.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class TestData {
    public static int[] createPersonTestData(EntityManagerFactory emf) {

        Person p1 = new Person("Mike", "Durham", 25, "durham@mail.com");
        Person p2 = new Person("Steve", "Michell", 55, "michell@mail.com");
        Person p3 = new Person("Petra", "Schmidt", 41, "schmidt@mail.com");
        Person p4 = new Person("Anita", "Hansen", 18, "hansen@mail.com");

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            em.getTransaction().commit();
        }
        return new int[]{p1.getId(), p2.getId(), p3.getId(), p4.getId()};
    }

    public static void createUserTestData(EntityManagerFactory emf) {

        User user = new User("user", "user123");
        User admin = new User("admin", "admin123");
        User superuser = new User("superuser", "superuser123");

        Role userRole = new Role("user");
        Role adminRole = new Role("admin");

        user.addRole(userRole);
        admin.addRole(adminRole);
        superuser.addRole(userRole);
        superuser.addRole(adminRole);

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(superuser);
           em.getTransaction().commit();
        }
    }
}