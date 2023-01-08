package strategy.sort;

import io.Movie;
import java.util.List;

public final class ContextForSort<T> {
    private final ISortStrategy<T> sortStrategy;

    public ContextForSort(final ISortStrategy<T> sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    /**
     * @param movies from input
     * @param order increasing / decreasing
     * @return sorted list
     */
    public List<Movie> executeStrategy(final List<Movie> movies, final T order) {
        return sortStrategy.sortMovies(movies, order);
    }
}
