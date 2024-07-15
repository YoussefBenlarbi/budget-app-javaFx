package com.example.budget.DAO;

import com.example.budget.Entity.User;

import java.sql.SQLException;
import java.util.List;

public interface IDAO<T> {
    T get(long id) throws SQLException;
    List<T> getAll() throws SQLException;
    void save(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t) throws SQLException;
}