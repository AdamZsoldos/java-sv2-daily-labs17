package day01;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActorsMoviesServiceTest {

    ActorsMoviesService actorsMoviesService;

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

        ActorsRepository actorsRepository = new ActorsRepository(dataSource);
        MoviesRepository moviesRepository = new MoviesRepository(dataSource);
        ActorToMovieRepository actorToMovieRepository = new ActorToMovieRepository(dataSource);
        actorsMoviesService = new ActorsMoviesService(actorsRepository, moviesRepository, actorToMovieRepository);

        actorsMoviesService.addMovieWithActors("Titanic", LocalDate.parse("1997-12-11"), List.of("Leonardo DiCaprio", "Kate Winslet"));
        actorsMoviesService.addMovieWithActors("Great Gatsby", LocalDate.parse("2012-07-23"), List.of("Tobey Maguire", "Leonardo DiCaprio"));
        actorsMoviesService.addMovieWithActors("The Thing", LocalDate.parse("1981-04-03"), List.of());
    }

    @Test
    void testGetActorsByMovieTitle() {
        List<Actor> actors = actorsMoviesService.getActorsByMovieTitle("Titanic");
        assertEquals(2, actors.size());
        assertEquals("Leonardo DiCaprio", actors.get(0).getName());
        assertEquals("Kate Winslet", actors.get(1).getName());
        actors = actorsMoviesService.getActorsByMovieTitle("Great Gatsby");
        assertEquals(2, actors.size());
        assertEquals("Tobey Maguire", actors.get(0).getName());
        assertEquals("Leonardo DiCaprio", actors.get(1).getName());
        actors = actorsMoviesService.getActorsByMovieTitle("The Thing");
        assertEquals(0, actors.size());
    }

    @Test
    void testGetMoviesByActorName() {
        List<Movie> movies = actorsMoviesService.getMoviesByActorName("Leonardo DiCaprio");
        assertEquals(2, movies.size());
        assertEquals("Titanic", movies.get(0).getTitle());
        assertEquals(LocalDate.parse("1997-12-11"), movies.get(0).getReleaseDate());
        assertEquals("Great Gatsby", movies.get(1).getTitle());
        assertEquals(LocalDate.parse("2012-07-23"), movies.get(1).getReleaseDate());
        movies = actorsMoviesService.getMoviesByActorName("Kate Winslet");
        assertEquals(1, movies.size());
        assertEquals("Titanic", movies.get(0).getTitle());
        assertEquals(LocalDate.parse("1997-12-11"), movies.get(0).getReleaseDate());
    }
}
