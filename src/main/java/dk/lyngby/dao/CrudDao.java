package dk.lyngby.dao;

import dk.lyngby.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;


public abstract class CrudDao<T, U> {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getInstanceOfEntityManagerFactory(boolean isTest) {
        if (emf == null) {
            emf = HibernateConfig.getEntityManagerFactory(isTest);
        }
        return emf;
    }

    public CrudDao(boolean isTest) {
        emf = getInstanceOfEntityManagerFactory(isTest);
        System.out.println(emf);
    }

    //CREATE
    public void create(T t) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
        }
    }

    //READ
    public T read(Class<T> tClass, U id) {
        try (EntityManager em = emf.createEntityManager()) {
            //Entity is managed after being retrieved
            T entity = em.find(tClass, id);
            // entity is detached after the entity is returned
            return entity;
        }
    }


    //UPDATE
    public T update(T t) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            //The entity is managed after the merge
            T entity = em.merge(t);
            // entity is in transient state (after being retrieved)
            em.getTransaction().commit();
            //entity is detached after it is returned
            return entity;
        }
    }


    //DELETE
    public void delete(Class<T> tClass, U id) {
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // the entity is managed after it is found/retrieved
            T entity = read(tClass, id);
            if (entity != null) {
                em.remove(entity);
                System.out.println("The entity has been deleted");
            } else {
                System.out.println("The entity you are looking for does not exist");
            }
            em.getTransaction().commit();
        }
    }

    //GET-ALL
    private void getAll(EntityManager em, T t) {
    }
}
