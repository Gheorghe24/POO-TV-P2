package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Credentials;
import io.Input;
import io.Movie;
import io.Notification;
import java.util.List;
import java.util.Stack;
import lombok.Getter;
import lombok.Setter;
import services.MovieService;
import services.OutputService;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterCountry;
import strategy.sort.ContextForSort;
import strategy.sort.SortLikes;

@Getter
@Setter
public final class Platform {
    private static Platform platform = null;
    private Input inputData;
    private ArrayNode output;
    private Page currentPage;
    private List<ICommand> commandList;
    private Stack<Page> pageStack;

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
                case "database" -> takeCommand(new DatabaseAction(output, action, inputData));
                default -> {
                }
            }
        });

        placeCommands();
        writeRecommendation();
    }

    /**
     * firstly verify if a user is logged in
     * then verify if is a premium user
     * verify if user has liked movies
     */
    private void writeRecommendation() {
        if (currentPage.getCurrentUser() != null
                && currentPage.getCurrentUser().getCredentials().getAccountType()
                .equals("premium")) {
            Notification notification;
            if (currentPage.getCurrentUser().getLikedMovies().isEmpty()) {
                notification = new Notification("No recommendation", "Recommendation");
            } else {
                List<Movie> recommendationList = new ContextForSort<>(new SortLikes())
                        .executeStrategy(new ContextForFilter<>(new FilterCountry())
                                        .executeMoviesStrategy(inputData.getMovies(),
                                                currentPage.getCurrentUser()
                                                        .getCredentials().getCountry()),
                                "decreasing");
                recommendationList =
                        recommendationList.stream().filter(movie -> new MovieService().
                                        getMoviesByName(movie.getName(),
                                                currentPage.getCurrentUser().getWatchedMovies())
                                        .isEmpty())
                                .toList();
                notification = new Notification(recommendationList.get(0).getName(),
                        "Recommendation");
            }
            currentPage.getCurrentUser().getNotifications().add(notification);
            new OutputService().addPOJOWithPopulatedOutput(output, currentPage,
                    new ObjectMapper(), null);
        }
    }

    /**
     * Preparing for New Test
     */
    public void prepareForNewEntry() {
        currentPage = Page.builder()
                .name("homepage")
                .build();
        Platform.getInstance().getPageStack().push(Page.builder()
                .name("homepage")
                .build());
    }
}
