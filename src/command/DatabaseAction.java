package command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Input;
import lombok.AllArgsConstructor;
import service.DatabaseService;

@AllArgsConstructor
public final class DatabaseAction implements ICommand {
    private ArrayNode jsonOutput;
    private Action action;
    private Input inputData;

    @Override
    public void executeCommand() {
        if (action.getFeature().equals("add")) {
            new DatabaseService().addToDatabase(action.getAddedMovie(),
                    jsonOutput, inputData);
        } else {
            new DatabaseService().deleteFromDatabase(action.getDeletedMovie(),
                    jsonOutput, inputData);
        }
    }
}
