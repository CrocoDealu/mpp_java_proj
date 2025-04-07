package org.example.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dto.ClientFilterDTO;
import org.example.model.Cashier;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class TicketDBRepository implements TicketRepository {

    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();
    private GameRepository gameRepository;
    private CashierRepository cashierRepository;
    public TicketDBRepository(JdbcUtils jdbcUtils, GameRepository gameRepository, CashierRepository cashierRepository) {
        this.jdbcUtils = jdbcUtils;
        this.gameRepository = gameRepository;
        this.cashierRepository = cashierRepository;
    }

    private Ticket mapResultSetToEntity(ResultSet resultSet) {
        logger.traceEntry();
        try {
            int id = resultSet.getInt("id");
            String customerName = resultSet.getString("customer_name");
            String customerAddress = resultSet.getString("customer_address");
            int noOfSeats = resultSet.getInt("no_of_seats");
            int game_id = resultSet.getInt("game_id");
            int cashier_id = resultSet.getInt("cashier_id");
            Game game = gameRepository.findById(game_id).orElse(null);
            Cashier cashier = cashierRepository.findById(cashier_id).orElse(null);
            logger.traceExit();
            return new Ticket(id, game, customerName, customerAddress, cashier, noOfSeats);
        } catch (Exception e) {
            logger.error(e);
            System.out.println("Error mapping to ticket " + e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Ticket> findById(Integer id) {
        logger.traceEntry();
        String query = "SELECT * FROM Tickets WHERE \"id\" = ?";
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
    public Iterable<Ticket> findAll() {
        logger.traceEntry();
        String query = "SELECT * FROM Tickets";
        try (PreparedStatement statement = jdbcUtils.getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, Ticket> entities = new HashMap<>();
            while (resultSet.next()) {
                Ticket entity = mapResultSetToEntity(resultSet);
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
    public Ticket save(Ticket entity) {
        logger.traceEntry();
        String query = "INSERT INTO Tickets (game_id, customer_name, customer_address, cashier_id, no_of_seats) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getGame().getId());
            stmt.setString(2, entity.getCustomerName());
            stmt.setString(3, entity.getCustomerAddress());
            stmt.setInt(4, entity.getSeller().getId());
            stmt.setInt(5, entity.getNoOfSeats());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.log(Level.WARN, "No rows were affected");
                throw new RuntimeException("Failed to insert ticket");
            }
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                logger.traceExit();
                entity.setId(generatedId);
                return entity;
            } else {
                throw new SQLException("Failed to insert ticket");
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Ticket update(Ticket entity) {
        logger.traceEntry();
        String sql = "UPDATE Tickets SET game_id = ?, customer_name = ?, customer_address = ?, cashier_id = ?, no_of_seats = ?";
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entity.getGame().getId());
            stmt.setString(2, entity.getCustomerName());
            stmt.setString(3, entity.getCustomerAddress());
            stmt.setInt(4, entity.getSeller().getId());
            stmt.setInt(5, entity.getNoOfSeats());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.log(Level.WARN, "No rows were affected");
                throw new SQLException("Failed to insert ticket");
            }

            return entity;
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Ticket> deleteById(Integer id) {
        logger.traceEntry();
        String query = "DELETE FROM Tickets WHERE \"id\" = ?";
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
    public Iterable<Ticket> getTicketsSoldForGame(Game game) {
        String sql = "SELECT * FROM Tickets WHERE \"game_id\" = ?";
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, game.getId());
            ResultSet resultSet = stmt.executeQuery();
            Map<Integer, Ticket> entities = new HashMap<>();
            while (resultSet.next()) {
                Ticket entity = mapResultSetToEntity(resultSet);
                entities.put(entity.getId(), entity);
            }
            logger.traceExit();
            return entities.values();
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Ticket> getTicketsForClient(ClientFilterDTO filter) {
        logger.traceEntry();
        String sql = "SELECT * FROM Tickets";
        sql = processFilter(sql, filter);
        logger.log(Level.INFO, "Processed filter");
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            Map<Integer, Ticket> entities = new HashMap<>();
            while (resultSet.next()) {
                Ticket entity = mapResultSetToEntity(resultSet);
                entities.put(entity.getId(), entity);
            }
            logger.traceExit();
            return entities.values();
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    private String processFilter(String sql, ClientFilterDTO filter) {
        StringBuilder modified = new StringBuilder(sql);
        if (filter != null && (filter.getName() != null || filter.getAddress() != null)) {
            modified.append(" WHERE ");
            List<String> params = new ArrayList<>();
            List<String> values = new ArrayList<>();
            if (filter.getAddress() != null) {
                params.add("customer_address = ");
                values.add(filter.getAddress());
            }
            if (filter.getName() != null) {
                params.add("customer_name = ");
                values.add(filter.getName());
            }

            for (int i = 0; i < params.size(); i++) {
                if (i != params.size() - 1) {
                    modified.append(params.get(i)).append("'").append(values.get(i)).append("'").append(" AND ");
                } else {
                    modified.append(params.get(i)).append("'").append(values.get(i)).append("'");
                }
            }
        }
        return modified.toString();
    }
}
