package org.example.repository;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Game;
import org.example.model.Ticket;
import org.example.utils.JdbcUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameDBRepository implements GameRepository {

    private final JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public GameDBRepository(JdbcUtils jdbcUtils) {
        this.jdbcUtils = jdbcUtils;
    }

    @Override
    public Iterable<Game> findGamesForTickets(Iterable<Ticket> tickets) {
        return null;
    }

    @Override
    public Iterable<Game> findAll() {
        logger.traceEntry();
        String query = "SELECT * FROM Games";
        try (PreparedStatement statement = jdbcUtils.getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Integer, Game> entities = new HashMap<>();
            while (resultSet.next()) {
                Game entity = mapResultSetToEntity(resultSet);
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
    public Optional<Game> findById(Integer id) {
        logger.traceEntry();
        String query = "SELECT * FROM Games WHERE \"id\" = ?";
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
    public Game save(Game entity) {
        logger.traceEntry();
        String query = "INSERT INTO Games (team1, team2, team_1_score, team_2_score, competition, capacity, stage, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getTeam1());
            stmt.setString(2, entity.getTeam2());
            stmt.setInt(3, entity.getTeam1Score());
            stmt.setInt(4, entity.getTeam2Score());
            stmt.setString(5, entity.getCompetition());
            stmt.setInt(6, entity.getCapacity());
            stmt.setString(7, entity.getStage());
            stmt.setFloat(8, entity.getTicketPrice());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                logger.log(Level.WARN, "No rows affected");
                throw new RuntimeException("No rows affected");
            }
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
                logger.traceExit();
                return entity;
            }
            throw new SQLException("No ID obtained");

        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Game> deleteById(Integer id) {
        logger.traceEntry();
        String query = "DELETE FROM Games WHERE \"id\" = ?";
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
    public Game update(Game entity) {
        logger.traceEntry();
        String query = "UPDATE GAMES SET team1 = ?, team2 = ?, team_1_score = ?, team_2_score = ?, competition = ?, capacity = ?, stage = ?, price = ? WHERE id = ?";
        try (PreparedStatement stmt = jdbcUtils.getConnection().prepareStatement(query)) {
            stmt.setString(1, entity.getTeam1());
            stmt.setString(2, entity.getTeam2());
            stmt.setInt(3, entity.getTeam1Score());
            stmt.setInt(4, entity.getTeam2Score());
            stmt.setString(5, entity.getCompetition());
            stmt.setInt(6, entity.getCapacity());
            stmt.setString(7, entity.getStage());
            stmt.setFloat(8, entity.getTicketPrice());
            stmt.setInt(9, entity.getId());
            int rowsAffected = stmt.executeUpdate();
            logger.traceExit();
            return entity;
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
    private Game mapResultSetToEntity(ResultSet resultSet) {
        logger.traceEntry();
        try {
            int id = resultSet.getInt("id");
            String team1 = resultSet.getString("team1");
            String team2 = resultSet.getString("team2");
            int team1Score = resultSet.getInt("team_1_score");
            int team2Score = resultSet.getInt("team_2_score");
            String competition = resultSet.getString("competition");
            int capacity = resultSet.getInt("capacity");
            String stage = resultSet.getString("stage");
            Float ticketPrice = resultSet.getFloat("price");
            logger.traceExit();
            return new Game(id, team1, team2, team1Score, team2Score, competition, capacity, stage, ticketPrice);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }
}
