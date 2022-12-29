package strategy.filter;

import io.Movie;
import java.util.List;

public final class ContextForFilter<T> {
    private final IFilterStrategy<T> filterStrategy;
    public ContextForFilter(final IFilterStrategy<T> stringStrategy) {
        this.filterStrategy = stringStrategy;
    }

    /**
     * @param movies from input
     * @param fields for filtering list of movies
     * @return filtered list
     */
    public List<Movie> executeStrategy(final List<Movie> movies, final T fields) {
        return filterStrategy.filterMovies(movies, fields);
    }
}
