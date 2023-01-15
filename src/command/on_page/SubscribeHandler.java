package command.on_page;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Action;
import io.Credentials;
import io.Input;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class SubscribeHandler extends RequestHandler {
    private final Page currentPage;

    @Override
    public void handleRequest(final ArrayNode jsonOutput, final Action action,
                              final Input inputData, final Credentials credentials) {
        if ("subscribe".equals(action.getFeature())) {
            if (currentPage.getName().equals("see details")
                    && !currentPage.getCurrentUser().getSubscribedGenres()
                    .contains(action.getSubscribedGenre())
                    && currentPage.getCurrentMovie().getGenres()
                    .contains(action.getSubscribedGenre())) {
                currentPage.getCurrentUser().getSubscribedGenres().add(action.getSubscribedGenre());
            } else {
                currentPage.getOutputService().addErrorPOJOToArrayNode(jsonOutput,
                        new ObjectMapper());
            }
        } else {
            next.handleRequest(jsonOutput, action, inputData, credentials);
        }
    }
}
