package strategy.filter;

import io.Movie;
import io.User;
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

    @Override
    public List<User> filterUsers(final List<User> users, final String field) {
        return null;
    }


}
