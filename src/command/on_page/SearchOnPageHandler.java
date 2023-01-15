package command.on_page;

import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Action;
import io.Credentials;
import io.Input;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class SearchOnPageHandler extends RequestHandler {
    private final Page currentPage;

    @Override
    public void handleRequest(final ArrayNode jsonOutput, final Action action,
                              final Input inputData, final Credentials credentials) {
        if ("search".equals(action.getFeature())) {
            currentPage.getMovieService().searchOnPage(jsonOutput, action, inputData, currentPage);
        } else {
            next.handleRequest(jsonOutput, action, inputData, credentials);
        }
    }
}
