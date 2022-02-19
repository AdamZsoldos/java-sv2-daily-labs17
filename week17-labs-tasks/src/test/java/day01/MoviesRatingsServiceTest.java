package day01;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoviesRatingsServiceTest {

    MoviesRatingsService moviesRatingsService;

    @BeforeEach
    void setUp() {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        try {
            dataSource.setUrl("jdbc:mariadb://localhost:3306/movies-actors?useUnicode=true");
            dataSource.setUserName("root");
            dataSource.setPassword("root");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot reach database", e);
        }

        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();

        MoviesRepository moviesRepository = new MoviesRepository(dataSource);
        RatingsRepository ratingsRepository = new RatingsRepository(dataSource);
        moviesRatingsService = new MoviesRatingsService(moviesRepository, ratingsRepository);

        moviesRepository.insertMovie("Titanic", LocalDate.parse("1997-12-11"));
        moviesRatingsService.addRatingsByTitle("Titanic", 3, 4);
        moviesRepository.insertMovie("Great Gatsby", LocalDate.parse("2012-07-23"));
        moviesRatingsService.addRatingsById(2, 1, 5, 3);
        moviesRepository.insertMovie("The Thing", LocalDate.parse("1981-04-03"));
    }

    @Test
    void testAddRatingsByInvalidTitle() {
        assertThrows(IllegalArgumentException.class, () -> moviesRatingsService.addRatingsByTitle("Titanic_", 3, 4));
    }

    @Test
    void testGetRatingsById() {
        assertEquals(List.of(3, 4), moviesRatingsService.getRatingsById(1));
        assertEquals(List.of(1, 5, 3), moviesRatingsService.getRatingsById(2));
        assertEquals(List.of(), moviesRatingsService.getRatingsById(3));
    }

    @Test
    void testGetRatingsByTitle() {
        assertEquals(List.of(3, 4), moviesRatingsService.getRatingsByTitle("Titanic"));
        assertEquals(List.of(1, 5, 3), moviesRatingsService.getRatingsByTitle("Great Gatsby"));
        assertEquals(List.of(), moviesRatingsService.getRatingsByTitle("The Thing"));
    }

    @Test
    void testGetAverageRatingById() {
        assertEquals(3.5, moviesRatingsService.getAverageRatingById(1));
        assertEquals(3, moviesRatingsService.getAverageRatingById(2));
        assertThrows(IllegalStateException.class, () -> moviesRatingsService.getAverageRatingById(3));
    }

    @Test
    void testGetAverageRatingByTitle() {
        assertEquals(3.5, moviesRatingsService.getAverageRatingByTitle("Titanic"));
        assertEquals(3, moviesRatingsService.getAverageRatingByTitle("Great Gatsby"));
        assertThrows(IllegalStateException.class, () -> moviesRatingsService.getAverageRatingByTitle("The Thing"));
    }
}
