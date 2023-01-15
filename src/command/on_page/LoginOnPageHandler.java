package command.on_page;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Action;
import io.Credentials;
import io.Input;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class LoginOnPageHandler extends RequestHandler {
    private Page currentPage;

    @Override
    public void handleRequest(final ArrayNode jsonOutput, final Action action,
                              final Input inputData, final Credentials credentials) {
        if ("login".equals(action.getFeature())) {
            currentPage.getUserService().loginOnPage(jsonOutput, inputData, credentials,
                    new ObjectMapper(), currentPage);
        } else {
            next.handleRequest(jsonOutput, action, inputData, credentials);
        }
    }
}
