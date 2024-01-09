package dk.lyngby.daos;

import dk.lyngby.exceptions.ApiException;

import java.util.List;

public interface IDAO<T, D> {
    T read(D d) throws ApiException;
    List<T> readAll();
    T create(T t) throws ApiException;
    T update(D d, T t) throws ApiException;
    void delete(D d) throws ApiException;
    boolean validatePrimaryKey(D d);
}
