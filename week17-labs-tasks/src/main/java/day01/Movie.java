package day01;

import java.time.LocalDate;

public class Movie {

    private final long id;
    private final String title;
    private final LocalDate releaseDate;

    public Movie(long id, String title, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
