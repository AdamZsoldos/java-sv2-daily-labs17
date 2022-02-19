package day01;

import sqlutil.Param;
import sqlutil.SqlQuery;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorToMovieRepository {

    private final DataSource dataSource;

    public ActorToMovieRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertActorAndMovieId(long actorId, long movieId) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "insert into actor_to_movie(actor_id, movie_id) values (?, ?)",
                Param.of(1, actorId), Param.of(2, movieId))) {
            query.statement().executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert", e);
        }
    }

    public List<Long> fetchMovieIdsByActorId(long actorId) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "select movie_id from actor_to_movie where actor_id = ?", Param.of(1, actorId))) {
            query.fetch();
            List<Long> movieIds = new ArrayList<>();
            while (query.result().next()) {
                movieIds.add(query.result().getLong("movie_id"));
            }
            return movieIds;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }

    public List<Long> fetchActorIdsByMovieId(long movieId) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "select actor_id from actor_to_movie where movie_id = ?", Param.of(1, movieId))) {
            query.fetch();
            List<Long> actorIds = new ArrayList<>();
            while (query.result().next()) {
                actorIds.add(query.result().getLong("actor_id"));
            }
            return actorIds;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }
}
