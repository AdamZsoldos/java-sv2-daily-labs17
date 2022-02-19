package day01;

import sqlutil.Param;
import sqlutil.SqlQuery;

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

    public long insertActor(String name) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "INSERT INTO actors (actor_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS,
                Param.of(1, name))) {
            return query.fetchKeyLong();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert", e);
        }
    }

    public Optional<Actor> fetchActorById(long id) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "SELECT * FROM actors WHERE id = ?", Param.of(1, id))) {
            if (query.fetch().next()) {
                return Optional.of(processResult(query.result()));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }

    public Optional<Actor> fetchActorByName(String name) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "SELECT * FROM actors WHERE actor_name = ?", Param.of(1, name))) {
            if (query.fetch().next()) {
                return Optional.of(processResult(query.result()));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }

    public List<Actor> findActorsByPrefix(String prefix) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "SELECT * FROM actors WHERE actor_name LIKE ?", Param.of(1, prefix + "%"))) {
            query.fetch();
            List<Actor> actors = new ArrayList<>();
            while (query.result().next()) {
                actors.add(processResult(query.result()));
            }
            return actors;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }

    private Actor processResult(ResultSet rs) throws SQLException {
        return new Actor(
                rs.getLong("id"),
                rs.getString("actor_name")
        );
    }
}
