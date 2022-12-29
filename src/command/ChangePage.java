package command;


import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Input;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class ChangePage implements ICommand {
    private Page currentPage;
    private ArrayNode jsonOutput;
    private Action action;
    private Input inputData;
    @Override
    public void executeCommand() {
        currentPage.changePage(jsonOutput, action, inputData);
    }
}
