package dk.lyngby.daos;

import dk.lyngby.exceptions.ApiException;
import dk.lyngby.model.Person;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PersonDAO implements IDAO<Person, Integer> {

    private static PersonDAO instance;
    private static EntityManagerFactory emf;

    public static PersonDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonDAO();
        }
        return instance;
    }

    @Override
    public Person read(Integer id) throws ApiException {
        try(var em = emf.createEntityManager()) {
            var person = em.find(Person.class, id);
            if (person == null) {
                throw new ApiException(404, "Person with id " + id + " not found");
            }
            return person;
        }
    }

    @Override
    public List<Person> readAll() {
        try(var em = emf.createEntityManager()) {
            var query = em.createQuery("SELECT p FROM Person p", Person.class);
            return query.getResultList();
        }
    }

    @Override
    public Person create(Person person) throws ApiException {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            return person;
        }
    }

    @Override
    public Person update(Integer id, Person person) throws ApiException {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var p = em.find(Person.class, id);
            p.setAge(person.getAge());
            p.setEmail(person.getEmail());
            p.setFirstName(person.getFirstName());
            p.setLastName(person.getLastName());
            em.getTransaction().commit();
            return p;
        }
    }

    @Override
    public void delete(Integer id) throws ApiException {
        try(var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            var person = em.find(Person.class, id);
            em.remove(person);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer number) {
        try(var em = emf.createEntityManager()) {
            var person = em.find(Person.class, number);
            return person != null;
        }
    }

}
