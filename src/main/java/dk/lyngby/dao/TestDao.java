package dk.lyngby.dao;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class TestDao extends Dao<String, Integer> {


    public static TestDao instance;
    private TestDao(EntityManagerFactory emf) {
        super(emf);
    }

    public static TestDao getInstance(EntityManagerFactory emf) {
        if(instance == null) {
            instance = new TestDao(emf);
        }
        return instance;
    }

    @Override
    public List<String> getAll() {
        return null;
    }

    @Override
    public String get(Integer id) {
        return null;
    }

    @Override
    public void create(String s) {

    }

    @Override
    public void update(String s) {

    }

    @Override
    public void delete(Integer id) {

    }
}
