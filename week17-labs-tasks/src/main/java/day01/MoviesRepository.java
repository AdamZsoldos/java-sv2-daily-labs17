package day01;

import sqlutil.Param;
import sqlutil.SqlQuery;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoviesRepository {

    private final DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long insertMovie(String title, LocalDate releaseDate) {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(),
                "insert into movies(title, release_date) values (?, ?)", Statement.RETURN_GENERATED_KEYS,
                Param.of(1, title), Param.of(2, releaseDate))) {
            return query.fetchKeyLong();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert movie", e);
        }
    }

    public List<Movie> fetchMovies() {
        return fetchMoviesByParams("select * from movies");
    }

    public List<Movie> fetchMoviesByTitle(String title) {
        return fetchMoviesByParams("select * from movies where title = ?", Param.of(1, title));
    }

    public Optional<Movie> fetchMovieById(long id) {
        return fetchMovieByParams("select * from movies where id = ?", Param.of(1, id));
    }

    public Optional<Movie> fetchMovieByTitle(String title) {
        return fetchMovieByParams("select * from movies where title = ?", Param.of(1, title));
    }

    public long idFromTitle(String title) {
        return fetchMovieByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("No result")).getId();
    }

    private Optional<Movie> fetchMovieByParams(String sql, Param... params) {
        try (ResultSet rs = fetchResultSet(sql, params)) {
            return rs.next() ? Optional.of(processResult(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch movie", e);
        }
    }

    private List<Movie> fetchMoviesByParams(String sql, Param... params) {
        try (ResultSet rs = fetchResultSet(sql, params)) {
            List<Movie> movies = new ArrayList<>();
            while (rs.next()) movies.add(processResult(rs));
            return movies;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch movies", e);
        }
    }

    private ResultSet fetchResultSet(String sql, Param... params) throws SQLException {
        try (SqlQuery query = new SqlQuery(dataSource.getConnection(), sql, params)) {
            return query.statement().executeQuery();
        }
    }

    private Movie processResult(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getDate("release_date").toLocalDate()
        );
    }
}
