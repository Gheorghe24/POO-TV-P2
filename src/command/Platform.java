package command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Credentials;
import io.Input;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;
import lombok.Setter;
import services.MovieService;
import services.OutputService;
import services.UserService;

@Getter
@Setter
public final class Platform {
    private static Platform platform = null;
    private Input inputData;
    private ArrayNode output;
    private Page currentPage;
    private List<ICommand> commandList;
    private Queue<Page> pageQueue;

    private Platform() {
    }

    /**
     * returned platform instance
     */
    public static Platform getInstance() {
        if (platform == null) {
            platform = new Platform();
        }

        return platform;
    }

    /**
     * @param command object
     *                method for Invoker Class
     */
    public void takeCommand(final ICommand command) {
        commandList.add(command);
    }

    /**
     * method for Invoker Class
     */
    public void placeCommands() {
        commandList.forEach(ICommand::executeCommand);
        commandList.clear();
    }

    /**
     * place Commands of each type
     */
    public void executeListOfActions() {

        inputData.getActions().forEach(action -> {
            switch (action.getType()) {
                case "change page" -> takeCommand(new ChangePage(currentPage, output,
                        action, inputData));
                case "on page" -> takeCommand(new OnPage(currentPage, output, action,
                        inputData, new Credentials(action.getCredentials())));
                case "back" -> takeCommand(new BackAction(currentPage, output, inputData));
                // asta e doar o incercare
                //TODO
                case "database" -> takeCommand(new DatabaseAction(currentPage, output,
                        action, inputData));
                default -> {
                }
            }
        });

        placeCommands();
    }

    /**
     * Preparing for New Test
     */
    public void prepareForNewEntry() {
        currentPage = Page.builder()
                .currentUser(null)
                .name("homepage")
                .moviesList(new ArrayList<>())
                .movieService(new MovieService())
                .outputService(new OutputService())
                .userService(new UserService())
                .build();
    }
}
