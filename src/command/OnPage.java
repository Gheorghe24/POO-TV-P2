package command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Credentials;
import io.Input;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class OnPage implements ICommand {
    private Page currentPage;
    private ArrayNode jsonOutput;
    private Action action;
    private Input inputData;
    private Credentials credentials;

    @Override
    public void executeCommand() {
        currentPage.onPage(jsonOutput, action,
                inputData, credentials);
    }
}
