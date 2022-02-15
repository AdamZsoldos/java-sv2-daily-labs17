package day01;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {

    private final DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveMovie(String title, LocalDate releaseDate) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("insert into movies(title, release_date) values (?, ?)")
        ) {
            statement.setString(1, title);
            statement.setDate(2, Date.valueOf(releaseDate));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect", e);
        }
    }

    public List<Movie> fetchMovies() {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from movies")
        ) {
            return processResultSet(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect", e);
        }
    }

    private List<Movie> processResultSet(ResultSet resultSet) throws SQLException {
        List<Movie> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(new Movie(
                    resultSet.getLong("id"),
                    resultSet.getString("title"),
                    resultSet.getDate("release_date").toLocalDate()
            ));
        }
        return result;
    }
}
