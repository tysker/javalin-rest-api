package dk.lyngby.dao;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public interface Dao<T, D> {
    public List<T> getAll();
    public T get(D id);
    public void create(T t);
    public T update(T t, D id);
    public void delete(D id);
}
