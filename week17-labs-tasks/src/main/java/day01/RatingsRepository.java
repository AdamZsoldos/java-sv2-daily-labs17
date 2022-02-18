package day01;

import sqlutil.SqlQuery;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingsRepository {

    private final DataSource dataSource;

    public RatingsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRatings(long movieId, List<Integer> ratings) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection())) {
            query.connection().setAutoCommit(false);
            query.setStatement(query.connection().prepareStatement(
                    "insert into ratings(movie_id, rating) values (?, ?)"));
            query.statement().setLong(1, movieId);
            for (int rating : ratings) {
                if (rating < 1 || rating > 5) {
                    query.connection().rollback();
                    throw new IllegalArgumentException("Invalid rating: " + rating);
                }
                query.statement().setInt(2, rating);
                query.statement().executeUpdate();
            }
            query.connection().commit();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert", e);
        }
    }

    public List<Integer> fetchRatingsByMovieId(long movieId) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection())) {
            query.setStatement(query.connection().prepareStatement(
                    "select rating from ratings where movie_id = ?"));
            query.statement().setLong(1, movieId);
            query.setResult(query.statement().executeQuery());
            List<Integer> ratings = new ArrayList<>();
            while (query.result().next()) {
                ratings.add(query.result().getInt("rating"));
            }
            return ratings;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch", e);
        }
    }
}
