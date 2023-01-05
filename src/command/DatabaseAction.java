package command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Input;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DatabaseAction implements ICommand {
    private Page currentPage;
    private ArrayNode jsonOutput;
    private Action action;
    private Input inputData;

    @Override
    public void executeCommand() {
        if (action.getFeature().equals("add")) {
            currentPage.addToDatabase(action.getAddedMovie(),
                    jsonOutput, inputData);
        } else {
            currentPage.deleteFromDatabase(action.getDeletedMovie(),
                    jsonOutput, inputData);
        }
    }
}
