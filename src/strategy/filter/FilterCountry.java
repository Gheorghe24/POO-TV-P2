package strategy.filter;

import io.Movie;
import java.util.List;

public final class FilterCountry implements IFilterStrategy<String> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final String country) {
        return movies
                .stream()
                .filter(movie
                        -> !movie
                        .getCountriesBanned()
                        .contains(country))
                .toList();
    }
}
