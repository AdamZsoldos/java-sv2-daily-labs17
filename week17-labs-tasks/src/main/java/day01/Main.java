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
        flyway.migrate();

        ActorsRepository actorsRepository = new ActorsRepository(dataSource);
        //actorsRepository.saveActor("Jill Poe");
        System.out.println(actorsRepository.findActorsByPrefix("j"));
        System.out.println(actorsRepository.findActorsByPrefix("jo"));
        System.out.println(actorsRepository.findActorsByPrefix("o"));

        MoviesRepository moviesRepository = new MoviesRepository(dataSource);
        //moviesRepository.saveMovie("Titanic", LocalDate.of(1997, 12, 11));
        System.out.println(moviesRepository.fetchMovies());
    }
}
