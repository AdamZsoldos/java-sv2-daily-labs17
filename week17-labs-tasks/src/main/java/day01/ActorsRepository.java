package day01;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActorsRepository {

    private final DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long insertActor(Actor actor) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO actors (actor_name) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, actor.getName());
            statement.executeUpdate();
            return getGeneratedKeyFromStatement(statement);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert", e);
        }
    }

    private long getGeneratedKeyFromStatement(PreparedStatement statement) throws SQLException {
        try (ResultSet rs = statement.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new IllegalStateException("No key has been generated");
        }
    }

    public Optional<Actor> fetchActorByName(String name) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM actors WHERE actor_name = ?")
        ) {
            statement.setString(1, name);
            List<Actor> actors = getActorsFromStatement(statement);
            if (!actors.isEmpty()) return Optional.of(actors.get(0));
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }

    public List<Actor> findActorsByPrefix(String prefix) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM actors WHERE actor_name LIKE ?")
        ) {
            statement.setString(1, prefix + '%');
            return getActorsFromStatement(statement);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }

    private List<Actor> getActorsFromStatement(PreparedStatement statement) {
        List<Actor> result = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(new Actor(
                        resultSet.getLong("id"),
                        resultSet.getString("actor_name")
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
        return result;
    }
}
