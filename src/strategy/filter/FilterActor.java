package strategy.filter;

import io.Movie;
import io.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterActor implements IFilterStrategy<ArrayList<String>> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final ArrayList<String> list) {
        return movies
                .stream()
                .filter(movie
                        -> movie.getActors().containsAll(list))
                        .collect(Collectors.toList());
    }

    @Override
    public List<User> filterUsers(final List<User> users, final ArrayList<String> field) {
        return null;
    }


}
