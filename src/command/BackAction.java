package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import lombok.AllArgsConstructor;
import services.OutputService;

@AllArgsConstructor
public final class BackAction implements ICommand {
    private Page currentPage;
    private ArrayNode jsonOutput;
    private Input inputData;

    @Override
    public void executeCommand() {
        var stack = Platform.getInstance().getPageStack();
        if (stack.isEmpty()) {
            new OutputService().addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
            return;
        }
        if (stack.pop().getName().equals("homepage")) {
            currentPage.setName("homepage");
            return;
        }
        Page frontPage = stack.pop();
        String movieName = frontPage.getCurrentMovie() != null
                ? frontPage.getCurrentMovie().getName() : null;
        if (frontPage.getName().equals("register")
                || frontPage.getName().equals("login")) {
            currentPage.setName(frontPage.getName());
            new OutputService().addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
        } else if (frontPage.getName().equals("homepage")) {
            currentPage.setName("homepage");
        } else {
            currentPage.changePage(jsonOutput, frontPage.getName(), inputData,
                    movieName);
        }
    }
}
