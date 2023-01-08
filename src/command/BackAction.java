package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import lombok.AllArgsConstructor;
import service.OutputService;

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
        switch (frontPage.getName()) {
            case "register", "login" -> {
                currentPage.setName(frontPage.getName());
                new OutputService().addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
            }
            case "homepage" -> currentPage.setName("homepage");
            default ->
                currentPage.changePage(jsonOutput, frontPage.getName(), inputData,
                        movieName);
        }
    }
}
