package day01;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorsRepository {

    private final DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveActor(String name) {
        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO actors (actor_name) VALUES (?)")) {
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot update: " + name, e);
        }
    }

    public List<String> findActorsByPrefix(String prefix) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT actor_name FROM actors WHERE actor_name LIKE ?")) {
            statement.setString(1, prefix + '%');
            return getValues(statement, "actor_name");
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
    }

    private List<String> getValues(PreparedStatement statement, String fieldName) {
        List<String> result = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                result.add(resultSet.getString(fieldName));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error", e);
        }
        return result;
    }
}
