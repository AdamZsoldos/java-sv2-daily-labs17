package day01;

import sqlutil.SqlUtil;

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
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = SqlUtil.createParameterizedStatement(connection, Statement.RETURN_GENERATED_KEYS,
                        "insert into movies(title, release_date) values (?, ?)", title, releaseDate);
                ResultSet rs = SqlUtil.executeAndGetGeneratedKeys(stmt)
        ) {
            if (rs.next()) return rs.getLong(1);
            throw new IllegalStateException("No key has been generated");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert movie", e);
        }
    }

    public List<Movie> fetchMovies() {
        return fetchMoviesByParams("select * from movies");
    }

    public List<Movie> fetchMoviesByTitle(String title) {
        return fetchMoviesByParams("select * from movies where title = ?", title);
    }

    public Optional<Movie> fetchMovieById(long id) {
        return fetchMovieByParams("select * from movies where id = ?", id);
    }

    public Optional<Movie> fetchMovieByTitle(String title) {
        return fetchMovieByParams("select * from movies where title = ?", title);
    }

    public long idFromTitle(String title) {
        return fetchMovieByTitle(title)
                .orElseThrow(() -> new IllegalArgumentException("No result")).getId();
    }

    private Optional<Movie> fetchMovieByParams(String sql, Object... params) {
        try (ResultSet rs = fetchResultSet(sql, params)) {
            return rs.next() ? Optional.of(processResult(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch movie", e);
        }
    }

    private List<Movie> fetchMoviesByParams(String sql, Object... params) {
        try (ResultSet rs = fetchResultSet(sql, params)) {
            List<Movie> movies = new ArrayList<>();
            while (rs.next()) movies.add(processResult(rs));
            return movies;
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot fetch movies", e);
        }
    }

    private ResultSet fetchResultSet(String sql, Object... params) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = SqlUtil.createParameterizedStatement(connection, sql, params)
        ) {
            return stmt.executeQuery();
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
