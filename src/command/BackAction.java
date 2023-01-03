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
        Page frontPage = Platform.getInstance().getPageQueue().poll();
        if (frontPage != null) {
            if (frontPage.getName().equals("register")
                    || frontPage.getName().equals("login") || frontPage.getCurrentMovie() == null) {
                new OutputService().addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
            } else {
                currentPage.changePage(jsonOutput, frontPage.getName(), inputData,
                        frontPage.getCurrentMovie().getName());
            }
        }
    }
}
