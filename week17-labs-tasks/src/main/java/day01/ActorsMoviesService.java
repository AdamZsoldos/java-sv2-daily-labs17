package day01;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ActorsMoviesService {

    private final ActorsRepository actorsRepository;
    private final MoviesRepository moviesRepository;
    private final ActorToMovieRepository actorToMovieRepository;

    public ActorsMoviesService(ActorsRepository actorsRepository, MoviesRepository moviesRepository, ActorToMovieRepository actorToMovieRepository) {
        this.actorsRepository = actorsRepository;
        this.moviesRepository = moviesRepository;
        this.actorToMovieRepository = actorToMovieRepository;
    }

    public void insertMovieWithActors(String title, LocalDate releaseDate, List<String> actorNames) {
        long movieId = moviesRepository.insertMovie(title, releaseDate);
        for (String actorName : actorNames) {
            long actorId = actorsRepository.fetchActorByName(actorName)
                    .map(Actor::getId)
                    .orElseGet(() -> actorsRepository.insertActor(actorName));
            actorToMovieRepository.insertActorAndMovieId(actorId, movieId);
        }
    }
}
