package dk.lyngby.dao;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public abstract class Dao<T, D> {

        EntityManagerFactory emf;
        public Dao(EntityManagerFactory emf) {
            this.emf = emf;
        }
        public abstract List<T> getAll();
        public abstract T get(D id);
        public abstract void create(T t);
        public abstract void update(T t);
        public abstract void delete(D id);

}
