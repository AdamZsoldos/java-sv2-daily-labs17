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
        RatingsRepository ratingsRepository = new RatingsRepository(dataSource);

        ActorsMoviesService actorsMoviesService = new ActorsMoviesService(actorsRepository,moviesRepository, actorToMovieRepository);
        actorsMoviesService.addMovieWithActors("Titanic", LocalDate.parse("1997-12-11"), List.of("Leonardo DiCaprio", "Kate Winslet"));
        actorsMoviesService.addMovieWithActors("Great Gatsby", LocalDate.parse("2012-07-23"), List.of("Tobey Maguire", "Leonardo DiCaprio"));

        MoviesRatingsService moviesRatingsService = new MoviesRatingsService(moviesRepository, ratingsRepository);
        moviesRatingsService.addRatingsByTitle("Titanic", 5, 2);
        try {
            moviesRatingsService.addRatingsByTitle("Great Gatsby", 1, 4, 6);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        moviesRatingsService.addRatingsByTitle("Great Gatsby", 1, 4);
        System.out.println(moviesRatingsService.getRatingsByTitle("Titanic"));
        System.out.println(moviesRatingsService.getAverageRatingById(1));
        System.out.println(moviesRatingsService.getRatingsById(2));
        System.out.println(moviesRatingsService.getAverageRatingByTitle("Great Gatsby"));

        System.out.println(moviesRepository.fetchMovies());
        System.out.println(actorsRepository.fetchActorByName("Leonardo DiCaprio"));
        System.out.println(actorsRepository.fetchActorByName("Leonardo DiCapri"));
        System.out.println(actorsRepository.findActorsByPrefix("k"));

        System.out.println(actorsMoviesService.getActorsByMovieTitle("Titanic"));
        System.out.println(actorsMoviesService.getMoviesByActorName("Leonardo DiCaprio"));
    }
}
