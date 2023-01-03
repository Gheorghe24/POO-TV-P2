package strategy.filter;

import io.Movie;
import java.util.ArrayList;
import java.util.List;

public final class FilterName implements IFilterStrategy<String> {
    @Override
    public List<Movie> filterMovies(final List<Movie> movies, final String name) {
        if (name == null) {
            System.out.println("name is null for some reason");
            return new ArrayList<>();
        }
        return  movies
                .stream()
                .filter(movie
                        -> movie.getName().startsWith(name))
                .toList();
    }
}
