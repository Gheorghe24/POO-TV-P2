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
        stack.pop();
        Page frontPage = stack.pop();
        String movieName = frontPage.getCurrentMovie() != null ?
                frontPage.getCurrentMovie().getName() : null;
        if (frontPage.getName().equals("register")
                || frontPage.getName().equals("login")) {
            new OutputService().addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
        } else {
            currentPage.changePage(jsonOutput, frontPage.getName(), inputData,
                    movieName);
        }
    }
}
