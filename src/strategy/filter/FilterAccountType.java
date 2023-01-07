package strategy.filter;

import io.Movie;
import io.User;
import java.util.List;

public final class FilterAccountType implements IFilterStrategy<String> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final String field) {
        return null;
    }

    @Override
    public List<User> filterUsers(final List<User> users, final String field) {
        return users.stream().filter(user ->
                        user.getCredentials().getAccountType().equals(field))
                .toList();
    }
}
