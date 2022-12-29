package out;

import io.Movie;
import io.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public final class Output {
    private String error;
    private List<Movie> currentMoviesList;
    private User currentUser;
}
