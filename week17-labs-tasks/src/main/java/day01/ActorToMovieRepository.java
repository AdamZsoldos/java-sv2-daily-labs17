package day01;

import sqlutil.Param;
import sqlutil.SqlQuery;

import javax.sql.DataSource;
import java.sql.SQLException;

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
}
