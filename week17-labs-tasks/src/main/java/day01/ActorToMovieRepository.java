package day01;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActorToMovieRepository {

    private final DataSource dataSource;

    public ActorToMovieRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertActorAndMovieId(long actorId, long movieId) {
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement("insert into actor_to_movie(actor_id, movie_id) values (?, ?)")
        ) {
            stmt.setLong(1, actorId);
            stmt.setLong(2, movieId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert", e);
        }
    }
}
