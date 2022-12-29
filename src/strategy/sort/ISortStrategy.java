package strategy.sort;

import io.Movie;
import java.util.List;

public interface ISortStrategy<T> {
    /**
     * @param movies from input
     * @param order ascending(increasing) / desceding(decreasing)
     * @return sorted list
     */
    List<Movie> sortMovies(List<Movie> movies, T order);
}
