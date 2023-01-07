package strategy.filter;

import io.Movie;
import io.User;
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
    public List<Movie> executeMoviesStrategy(final List<Movie> movies, final T fields) {
        return filterStrategy.filterMovies(movies, fields);
    }

    /**
     * @param users
     * @param fields
     * @return
     */
    public List<User> executeUsersStrategy(final List<User> users, final T fields) {
        return filterStrategy.filterUsers(users, fields);
    }
}
