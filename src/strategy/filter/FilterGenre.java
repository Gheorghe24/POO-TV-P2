package strategy.filter;

import io.Movie;
import io.User;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterGenre implements IFilterStrategy<ArrayList<String>> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final ArrayList<String> listGenres) {
        return movies
                .stream()
                .filter(movie
                        -> listGenres.stream()
                        .anyMatch(elem ->
                                movie.getGenres()
                                        .contains(elem)))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> filterUsers(final List<User> users, final ArrayList<String> listGenres) {
        return users.stream().filter(user ->
                        user.getSubscribedGenres().stream()
                                .anyMatch(listGenres::contains))
                .toList();
    }


}
