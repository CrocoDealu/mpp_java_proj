package org.example.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Cashier;
import org.example.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CashierDBRepository implements CashierRepository {

    private final JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public CashierDBRepository(JdbcUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    @Override
    public Iterable<Cashier> findAll() {
        logger.traceEntry();
        String query = "SELECT * FROM Cashiers";
        try (PreparedStatement statement = jdbcUtils.getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, Cashier> entities = new HashMap<>();
            while (resultSet.next()) {
                Cashier entity = mapResultSetToEntity(resultSet);
                entities.put(entity.getId(), entity);
            }
            logger.traceExit();
            return entities.values();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cashier> findById(Integer id) {
        logger.traceEntry();
        String query = "SELECT * FROM Cashiers WHERE \"id\" = ?";
        try (PreparedStatement statement = jdbcUtils.getConnection().prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            logger.traceExit();
            return resultSet.next() ? Optional.of(mapResultSetToEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Cashier save(Cashier entity) {
        logger.traceEntry();
        String query = "INSERT INTO Cashiers (name, password, username) VALUES (?, ?, ?)";
        try(PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getPassword());
            stmt.setString(3, entity.getUsername());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.log(Level.WARN, "No rows affected");
                throw new RuntimeException("No rows affected");
            }
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                logger.traceExit();
                entity.setId(id);
                return entity;
            } else {
                throw new SQLException("No ID obtained");
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cashier> deleteById(Integer id) {
        logger.traceEntry();
        String query = "DELETE FROM Cashiers WHERE \"id\" = ?";
        try (PreparedStatement statement = jdbcUtils.getConnection().prepareStatement(query)) {
            statement.setObject(1, id);
            int rowsAffected = statement.executeUpdate();
            logger.traceExit();
            return rowsAffected > 0 ? findById(id) : Optional.empty();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Cashier update(Cashier entity) {
        logger.traceEntry();
        String query = "UPDATE Cashiers SET name = ?, password = ?, username = ? WHERE id = ?";
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(query)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getPassword());
            stmt.setString(3, entity.getUsername());
            stmt.setInt(4, entity.getId());
            int rowsAffected = stmt.executeUpdate();
            logger.traceExit();
            return entity;
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cashier> findByUsername(String username) {
        logger.traceEntry();
        String query = "SELECT * FROM Cashiers WHERE \"username\" = ?";
        try (PreparedStatement statement = jdbcUtils.getConnection().prepareStatement(query)) {
            statement.setObject(1, username);
            ResultSet resultSet = statement.executeQuery();
            logger.traceExit();
            return resultSet.next() ? Optional.of(mapResultSetToEntity(resultSet)) : Optional.empty();
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private Cashier mapResultSetToEntity(ResultSet resultSet) {
        logger.traceEntry();
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            logger.traceExit();
            return new Cashier(id, name, username, password);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
