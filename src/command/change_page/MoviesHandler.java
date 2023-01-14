package command.change_page;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Input;
import lombok.AllArgsConstructor;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterCountry;

@AllArgsConstructor
public final class MoviesHandler extends PageHandler {
    private Page currentPage;

    @Override
    public void handlePage(final ArrayNode jsonOutput, final String pageName, final Input input,
                           final String movieNameForDetails) {
        if ("movies".equals(pageName)) {
            if (currentPage.getCurrentUser() != null) {
                currentPage.populateCurrentPage(pageName,
                        new ContextForFilter<>(new FilterCountry())
                                .executeMoviesStrategy(input.getMovies(),
                                        currentPage.getCurrentUser().getCredentials().getCountry()),
                        null,
                        currentPage.getCurrentUser());
                currentPage.getOutputService().addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                        new ObjectMapper(),
                        currentPage.getMoviesList());
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
