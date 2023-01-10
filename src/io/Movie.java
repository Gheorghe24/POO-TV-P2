package io;


import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class Movie {
    private String name;
    private String year;
    private Integer duration;
    @Builder.Default
    private ArrayList<String> genres = new ArrayList<>();
    @Builder.Default
    private ArrayList<String> actors = new ArrayList<>();
    @Builder.Default
    private ArrayList<String> countriesBanned = new ArrayList<>();
    @Builder.Default
    private Integer numLikes = 0;
    @Builder.Default
    private Double rating = 0.00;
    @Builder.Default
    private Integer numRatings = 0;

    public Movie(final Movie movie) {
        this.name = movie.getName();
        this.year = movie.getYear();
        this.duration = movie.getDuration();
        this.genres = movie.getGenres();
        this.actors = movie.getActors();
        this.countriesBanned = movie.getCountriesBanned();
        this.numLikes = movie.getNumLikes();
        this.rating = movie.getRating();
        this.numRatings = movie.getNumRatings();
    }
}
