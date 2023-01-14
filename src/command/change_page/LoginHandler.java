package command.change_page;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Input;
import java.util.ArrayList;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class LoginHandler extends PageHandler {
    private Page currentPage;

    @Override
    public void handlePage(final ArrayNode jsonOutput, final String pageName, final Input input,
                           final String movieNameForDetails) {
        if ("login".equals(pageName)) {
            if (currentPage.getCurrentUser() == null && currentPage.getName().equals("homepage")) {
                // Perform login logic
                currentPage.populateCurrentPage(pageName, new ArrayList<>(), null, null);
            } else {
                currentPage.setName("homepage");
                currentPage.getOutputService().addErrorPOJOToArrayNode(jsonOutput,
                        new ObjectMapper());
            }
        } else {
            // Pass on to next handler
            super.getNextHandler().handlePage(jsonOutput, pageName, input, movieNameForDetails);
        }
    }
}
