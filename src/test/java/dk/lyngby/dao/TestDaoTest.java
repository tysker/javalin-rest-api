package dk.lyngby.dao;

import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // https://www.baeldung.com/junit-testinstance-annotation
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestDaoTest {

    private int count = 0;

    @BeforeAll
    public static void setUp() {
        System.out.println("Before all");
    }

    @Test
    @Order(1)
    void getAll() {
        count++;
        System.out.println(count);
        System.out.println("1 ============");
    }

    @Test
    @Order(2)
    void get() {
        count++;
        System.out.println(count);
        System.out.println("2 ============");
    }

    @Test
    @Order(3)
    void create() {
        count++;
        System.out.println(count);
        System.out.println("3 ============");
    }

    @Test
    @Order(4)
    void update() {
        count++;
        System.out.println(count);
        System.out.println("4 ============");
    }

    @Test
    @Order(5)
    void delete() {
        count++;
        System.out.println(count);
        System.out.println("5 ============");
    }
}