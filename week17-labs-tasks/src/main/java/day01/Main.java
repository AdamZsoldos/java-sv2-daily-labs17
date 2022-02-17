package day01;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
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

        ActorsMoviesService actorsMoviesService = new ActorsMoviesService(actorsRepository,moviesRepository, actorToMovieRepository);
        actorsMoviesService.insertMovieWithActors("Titanic", LocalDate.parse("1997-12-11"), List.of("Leonardo DiCaprio", "Kate Winslet"));
        actorsMoviesService.insertMovieWithActors("Great Garsby", LocalDate.parse("2012-07-23"), List.of("Tobey Maguire", "Leonardo DiCaprio"));

        System.out.println(moviesRepository.fetchMovies());
        System.out.println(actorsRepository.fetchActorByName("Leonardo DiCaprio"));
        System.out.println(actorsRepository.fetchActorByName("Leonardo DiCapri"));
        System.out.println(actorsRepository.findActorsByPrefix("k"));
    }
}
