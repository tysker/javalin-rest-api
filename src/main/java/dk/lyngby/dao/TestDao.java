package dk.lyngby.dao;

import dk.lyngby.model.Person;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TestDao extends CrudDao<Person, Integer> {

    public static TestDao instance;

    private TestDao(boolean isTest) {
        super(isTest);
    }

    public static TestDao getInstance(boolean isTest) {
        if (instance == null) {
            instance = new TestDao(isTest);
        }
        return instance;
    }

}
