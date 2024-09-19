package dk.lyngby.daos;

import dk.lyngby.config.HibernateConfig;
import dk.lyngby.exceptions.ApiException;
import dk.lyngby.model.Person;
import dk.lyngby.util.TestData;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonDAOTest {

    private static PersonDAO personDAO;
    private static EntityManagerFactory emfTest;

    private int[] IDS;
    @BeforeEach
    void setUp() {
        IDS = TestData.createPersonTestData(emfTest);
    }

    @BeforeAll
    static void setUpAll() {
        HibernateConfig.setTest(true);
        emfTest = HibernateConfig.getEntityManagerFactory();
        personDAO = PersonDAO.getInstance(emfTest);
    }

    @AfterAll
    static void tearDownAll() {
        HibernateConfig.setTest(false);
    }

    @Test
    @DisplayName("Create person")
    void create() throws ApiException {

        // given
        Person expected = new Person("John", "Doe", 25, "doe@mail.com");

        // when
        Person actual = personDAO.create(expected);

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Read all persons")
    void readAll() throws ApiException {

        // given
        int expected = 4;

        // when
        List<Person> actual = personDAO.readAll();

        // then
        assertEquals(expected, actual.size());
    }

    @Test
    @DisplayName("Read person by id")
    void read() throws ApiException {

        // given
        String expected = "durham@mail.com";

        // when
        Person actual = personDAO.read(IDS[0]);

        // then
        assertEquals(expected, actual.getEmail());
    }

    @Test
    @DisplayName("Update person")
    void update() throws ApiException {

        // given
        String expected = "hansen@mail.com";

        // when
        Person actual = personDAO.update(IDS[3], new Person("Anita", "Hansen", 18, expected));

        // then
        assertEquals(expected, actual.getEmail());
    }

    @Test
    @DisplayName("Delete person")
    void delete() throws ApiException {

        // given
        int expected = 3;

        // when
        personDAO.delete(IDS[0]);
        List<Person> actual = personDAO.readAll();

        // then
        assertEquals(expected, actual.size());
    }

    @Test
    @DisplayName("Validate id")
    void validateID() {

        // given
        int id = IDS[0];

        // when
        boolean actual = personDAO.validatePrimaryKey(id);

        // then
        assertTrue(actual);
    }

    @Test
    @DisplayName("Validate id - invalid")
    void validateIDInvalid() {

        // given
        int id = 0;

        // when
        boolean actual = personDAO.validatePrimaryKey(id);

        // then
        assertFalse(actual);
    }
}