package com.example.budget.DAO;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public abstract class GenericDAO<T> implements IDAO<T> {
    protected Connection connection;
    protected String tableName;

    public GenericDAO(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }
    protected abstract T mapResultSetToObject(ResultSet rs) throws SQLException;
    protected abstract void setPreparedStatementForSave(PreparedStatement ps, T t) throws SQLException;
    protected abstract void setPreparedStatementForUpdate(PreparedStatement ps, T t) throws SQLException;

    @Override
    public T get(long id) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToObject(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<T> getAll() throws SQLException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToObject(rs));
            }
        }
        return list;
    }

    @Override
    public void save(T t) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (...) VALUES (...)";  // Customize this SQL
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setPreparedStatementForSave(ps, t);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(T t) throws SQLException {
        String sql = "UPDATE " + tableName + " SET ... WHERE id = ?";  // Customize this SQL
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setPreparedStatementForUpdate(ps, t);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(T t) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, getId(t));  // Implement getId method in your entity class
            ps.executeUpdate();
        }
    }

    protected abstract long getId(T t);
}