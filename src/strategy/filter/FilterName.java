package strategy.filter;

import io.Movie;
import java.util.List;

public final class FilterName implements IFilterStrategy<String> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final String name) {
        return  movies
                .stream()
                .filter(movie
                        -> movie.getName().startsWith(name))
                .toList();
    }
}
