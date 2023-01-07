package strategy.filter;

import io.Movie;
import io.User;
import java.util.List;

/**
 * Interface for strategy pattern
 */
public interface IFilterStrategy<T> {
    /**
     * @param movies from input
     * @param field for filter
     * @return filtered list
     */
    List<Movie> filterMovies(List<Movie> movies, T field);

    /**
     * @param users to filter
     * @param field for filter
     * @return filtered list
     */
    List<User> filterUsers(List<User> users, T field);
}
