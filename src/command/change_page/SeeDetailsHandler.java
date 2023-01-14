package command.change_page;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Input;
import io.Movie;
import java.util.List;
import lombok.AllArgsConstructor;
import service.MovieService;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterName;

@AllArgsConstructor
public final class SeeDetailsHandler extends PageHandler {
    private Page currentPage;

    @Override
    public void handlePage(final ArrayNode jsonOutput, final String pageName, final Input input,
                           final String movieNameForDetails) {
        if ("see details".equals(pageName)) {
            if (currentPage.getCurrentUser() != null && currentPage.getName().equals("movies")) {
                List<Movie> moviesByName = new MovieService()
                        .getMoviesByName(movieNameForDetails, currentPage.getMoviesList());
                List<Movie> foundMovie = new ContextForFilter<>(new FilterName())
                        .executeMoviesStrategy(moviesByName, movieNameForDetails);
                if (foundMovie.isEmpty()) {
                    currentPage.getOutputService().addErrorPOJOToArrayNode(jsonOutput,
                            new ObjectMapper());
                } else {
                    currentPage.populateCurrentPage(pageName, foundMovie, foundMovie.get(0),
                            currentPage.getCurrentUser());
                    currentPage.getOutputService().addPOJOWithPopulatedOutput(jsonOutput,
                            currentPage,
                            new ObjectMapper(),
                            currentPage.getMoviesList());
                }
            } else {
                currentPage.getOutputService().addErrorPOJOToArrayNode(jsonOutput,
                        new ObjectMapper());
            }
        } else {
            // Pass on to next handler
            super.getNextHandler().handlePage(jsonOutput, pageName, input, movieNameForDetails);
        }
    }
}
