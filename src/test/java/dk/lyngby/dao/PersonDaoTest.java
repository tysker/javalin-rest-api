package dk.lyngby.dao;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.model.Person;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

class PersonDaoTest {

    private static EntityManagerFactory emf;
    private static PersonDao personDao;

    private Person p1;
    private Person p2;
    private Person p3;

    @BeforeEach
    public void init() {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Person").executeUpdate();
            p1 = new Person("Person 1", 1);
            p2 = new Person("Person 2", 2);
            p3 = new Person("Person 3", 3);
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.getTransaction().commit();
        }
    }

    @BeforeAll
    public static void setUp() {
        emf = HibernateConfig.getEntityManagerFactory(true);
        personDao = PersonDao.getInstance(emf);
    }

    @Test
    @DisplayName("Get all persons")
    void getAll() {
        // given
        int expected = 3;
        // when
        int actual = personDao.getAll().size();
        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get person by id")
    void get() {
        // given
        int id = p1.getId();
        // when
        Person actual = personDao.get(id);
        // then
        Assertions.assertEquals(p1, actual);

    }

    @Test
    @DisplayName("Create person")
    void create() {
        // given
        Person expected = new Person("Person 4", 4);
        // when
        personDao.create(expected);
        // then
        Assertions.assertTrue(expected.getId() > 0);
    }

    @Test
    @DisplayName("Update Person 2 to Person 100")
    void update() {
        // given
        Person expected = new Person("Person 100", 100);
        expected.setId(p2.getId());
        // when
        Person actual = personDao.update(expected, p2.getId());
        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Delete Person 3")
    void delete() {
        // given
        int id = p3.getId();
        // when
        personDao.delete(id);
        // then
        Assertions.assertNull(personDao.get(id));

    }
}