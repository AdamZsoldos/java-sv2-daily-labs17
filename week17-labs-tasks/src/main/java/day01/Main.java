package day01;

import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;

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
        actorsRepository.insertActor(new Actor(1, "Jill Poe"));
        actorsRepository.insertActor(new Actor(2, "John Doe"));
        actorsRepository.insertActor(new Actor(3, "Jane Roe"));
        System.out.println(actorsRepository.findActorsByPrefix("j"));
        System.out.println(actorsRepository.findActorsByPrefix("ji"));
        System.out.println(actorsRepository.findActorsByPrefix("o"));
        System.out.println(actorsRepository.fetchActorByName("Jane Roe"));
        System.out.println(actorsRepository.fetchActorByName("Jane Ro"));

        MoviesRepository moviesRepository = new MoviesRepository(dataSource);
        moviesRepository.insertMovie("Titanic", LocalDate.of(1997, 12, 11));
        moviesRepository.insertMovie("The Thing", LocalDate.of(1982, 5, 5));
        System.out.println(moviesRepository.fetchMovies());
    }
}
