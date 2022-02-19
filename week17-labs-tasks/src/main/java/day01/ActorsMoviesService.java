package day01;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ActorsMoviesService {

    private final ActorsRepository actorsRepository;
    private final MoviesRepository moviesRepository;
    private final ActorToMovieRepository actorToMovieRepository;

    public ActorsMoviesService(ActorsRepository actorsRepository, MoviesRepository moviesRepository, ActorToMovieRepository actorToMovieRepository) {
        this.actorsRepository = actorsRepository;
        this.moviesRepository = moviesRepository;
        this.actorToMovieRepository = actorToMovieRepository;
    }

    public void addMovieWithActors(String title, LocalDate releaseDate, List<String> actorNames) {
        long movieId = moviesRepository.insertMovie(title, releaseDate);
        for (String actorName : actorNames) {
            long actorId = actorsRepository.fetchActorByName(actorName)
                    .map(Actor::getId)
                    .orElseGet(() -> actorsRepository.insertActor(actorName));
            actorToMovieRepository.insertActorAndMovieId(actorId, movieId);
        }
    }

    public List<Actor> getActorsByMovieTitle(String title) {
        Movie movie = moviesRepository.fetchMovieByTitle(title).orElseThrow(() -> new IllegalArgumentException("No result"));
        List<Long> actorIds = actorToMovieRepository.fetchActorIdsByMovieId(movie.getId());
        List<Actor> actors = new ArrayList<>();
        for (long actorId : actorIds) {
            actors.add(actorsRepository.fetchActorById(actorId).orElseThrow(() -> new IllegalStateException("Invalid actor ID")));
        }
        return actors;
    }

    public List<Movie> getMoviesByActorName(String name) {
        Actor actor = actorsRepository.fetchActorByName(name).orElseThrow(() -> new IllegalArgumentException("No result"));
        List<Long> movieIds = actorToMovieRepository.fetchMovieIdsByActorId(actor.getId());
        List<Movie> movies = new ArrayList<>();
        for (long movieId : movieIds) {
            movies.add(moviesRepository.fetchMovieById(movieId).orElseThrow(() -> new IllegalStateException("Invalid movie ID")));
        }
        return movies;
    }
}
