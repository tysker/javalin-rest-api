package dk.lyngby.dao;

import dk.lyngby.model.Person;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class PersonDao implements Dao<Person, Integer> {

    private static PersonDao instance;

    private static EntityManagerFactory emf;

    public static PersonDao getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonDao();
        }
        return instance;
    }

    @Override
    public List<Person> getAll() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
        }
    }

    @Override
    public Person get(Integer id) {
        try (var em = emf.createEntityManager()) {
            return em.find(Person.class, id);
        }
    }

    @Override
    public void create(Person p) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        }
    }

    @Override
    public Person update(Person p, Integer id) {
        Person merge;
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Person person = em.find(Person.class, id);
            person.setName(p.getName());
            person.setAge(p.getAge());
            merge = em.merge(person);
            em.getTransaction().commit();
        }
        return merge;
    }

    @Override
    public void delete(Integer id) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Person person = em.find(Person.class, id);
            em.remove(person);
            em.getTransaction().commit();
        }
    }
}
