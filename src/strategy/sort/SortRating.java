package strategy.sort;

import io.Movie;
import java.util.Comparator;
import java.util.List;

public final class SortRating implements ISortStrategy<String> {
    @Override
    public List<Movie> sortMovies(final List<Movie> movies, final String order) {
        if (order.equals("increasing")) {
            return movies.stream()
                    .sorted(Comparator.comparing(Movie::getRating)).toList();
        }
        return movies.stream()
                .sorted(Comparator.comparing(Movie::getRating).reversed()).toList();
    }
}
