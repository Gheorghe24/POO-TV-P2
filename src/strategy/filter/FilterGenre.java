package strategy.filter;

import io.Movie;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterGenre implements IFilterStrategy<ArrayList<String>> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final ArrayList<String> listGenres) {
        return movies
                .stream()
                .filter(movie
                        -> listGenres.stream()
                        .anyMatch(elem ->
                                movie.getGenres()
                                        .contains(elem)))
                .collect(Collectors.toList());
    }
}
