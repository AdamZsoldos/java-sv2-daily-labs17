package day01;

import java.util.Arrays;
import java.util.List;

public class MoviesRatingsService {

    private final MoviesRepository moviesRepository;
    private final RatingsRepository ratingsRepository;

    public MoviesRatingsService(MoviesRepository moviesRepository, RatingsRepository ratingsRepository) {
        this.moviesRepository = moviesRepository;
        this.ratingsRepository = ratingsRepository;
    }

    public void addRatingsById(long movieId, Integer... ratings) {
        ratingsRepository.insertRatings(movieId, Arrays.asList(ratings));
    }

    public void addRatingsByTitle(String title, Integer... ratings) {
        addRatingsById(moviesRepository.idFromTitle(title), ratings);
    }

    public List<Integer> getRatingsById(long movieId) {
        return ratingsRepository.fetchRatingsByMovieId(movieId);
    }

    public List<Integer> getRatingsByTitle(String title) {
        return getRatingsById(moviesRepository.idFromTitle(title));
    }

    public double getAverageRatingById(long movieId) {
        List<Integer> ratings = getRatingsById(movieId);
        return ratings.stream()
                .mapToInt(i -> i)
                .average()
                .orElseThrow(() -> new IllegalStateException("No ratings"));
    }

    public double getAverageRatingByTitle(String title) {
        return getAverageRatingById(moviesRepository.idFromTitle(title));
    }
}
