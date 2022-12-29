package strategy.filter;

import io.Movie;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterActor implements IFilterStrategy<ArrayList<String>> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final ArrayList<String> list) {
        return movies
                .stream()
                .filter(movie
                        -> list.stream()
                        .allMatch(elem ->
                                movie.getActors()
                                        .contains(elem)))
                        .collect(Collectors.toList());
    }
}
