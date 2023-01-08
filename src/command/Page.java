package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Contains;
import io.Credentials;
import io.Input;
import io.Movie;
import io.Sort;
import io.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import service.DatabaseService;
import service.MovieService;
import service.OutputService;
import service.UserService;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterCountry;
import strategy.filter.FilterName;
import utils.Utils;

@Getter
@Setter
@Builder
public final class Page {
    private String name;
    private User currentUser;
    @Builder.Default
    private List<Movie> moviesList = new ArrayList<>();
    private Movie currentMovie;
    @Builder.Default
    private MovieService movieService = new MovieService();
    @Builder.Default
    private OutputService outputService = new OutputService();
    @Builder.Default
    private UserService userService = new UserService();
    @Builder.Default
    private DatabaseService databaseService = new DatabaseService();

    /**
     * @param jsonOutput Output to add Json Objects
     */
    public void changePage(final ArrayNode jsonOutput, final String pageName, final Input input,
                           final String movieNameForDetails) {
        ObjectMapper objectMapper = new ObjectMapper();
        switch (pageName) {
            case "register":
            case "login":
                if (this.getCurrentUser() == null && this.getName().equals("homepage")) {
                    populateCurrentPage(pageName, new ArrayList<>(), null, null);
                } else {
                    this.setName("homepage");
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "logout":
                if (this.getCurrentUser() != null) {
                    populateCurrentPage("homepage", new ArrayList<>(), null, null);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "movies":
                if (this.getCurrentUser() != null) {
                    populateCurrentPage(pageName,
                            new ContextForFilter<>(new FilterCountry())
                                    .executeMoviesStrategy(input.getMovies(),
                                            currentUser.getCredentials().getCountry()),
                            null,
                            currentUser);
                    outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                            this.moviesList);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "see details":
                if (this.getCurrentUser() != null && this.getName().equals("movies")) {
                    List<Movie> moviesByName = new MovieService()
                            .getMoviesByName(movieNameForDetails, this.moviesList);
                    List<Movie> foundMovie = new ContextForFilter<>(new FilterName())
                            .executeMoviesStrategy(moviesByName, movieNameForDetails);
                    if (foundMovie.isEmpty()) {
                        outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    } else {
                        populateCurrentPage(pageName, foundMovie, foundMovie.get(0), currentUser);
                        outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                                this.moviesList);
                    }
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            case "upgrades":
                if (this.getCurrentUser() != null) {
                    populateCurrentPage(pageName, new ArrayList<>(), null, currentUser);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            default:
        }

    }

    /**
     * @param jsonOutput  Output to add Json Objects
     * @param action      from Input
     * @param inputData   Database/Input class from Test File
     * @param credentials from Input for register operation
     */
    public void onPage(final ArrayNode jsonOutput, final Action action,
                       final Input inputData, final Credentials credentials) {
        ObjectMapper objectMapper = new ObjectMapper();
        String feature = action.getFeature();
        switch (feature) {
            case "login" -> loginOnPage(jsonOutput, inputData, credentials, objectMapper);

            case "register" -> registerOnPage(jsonOutput, inputData, credentials, objectMapper);

            case "search" -> searchOnPage(jsonOutput, action, inputData, objectMapper);

            case "filter" -> filterOnPage(jsonOutput, action, inputData, objectMapper);

            case "buy tokens" -> {
                if (this.getName().equals("upgrades")) {
                    var balance =
                            Integer.parseInt(this.getCurrentUser().getCredentials().getBalance());
                    var count = Integer.parseInt(action.getCount());
                    if (balance >= count) {
                        currentUser.setTokensCount(currentUser.getTokensCount() + count);
                        currentUser.getCredentials().setBalance(String.valueOf(balance - count));
                    } else {
                        outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "buy premium account" -> {
                if (this.getName().equals("upgrades")) {
                    var count = currentUser.getTokensCount();
                    if (count >= Utils.TOKEN_COST
                            && !currentUser.getCredentials().getAccountType().equals(
                            "premium")) {
                        currentUser.getCredentials().setAccountType("premium");
                        currentUser.setTokensCount(count - Utils.TOKEN_COST);
                    } else {
                        outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                    }
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "purchase" -> {
                if (this.getName().equals("upgrades") || this.getName().equals("see details")) {
                    movieService.purchaseMovie(jsonOutput, action, objectMapper, this);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "watch" -> {
                if (this.getName().equals("see details")) {
                    movieService.watchMovie(jsonOutput, action, objectMapper, this);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "like" -> {
                if (this.getName().equals("see details")) {
                    movieService.likeMovie(jsonOutput, action, objectMapper, inputData, this);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "rate" -> {
                if (this.getName().equals("see details")) {
                    movieService.rateMovie(jsonOutput, action, objectMapper,
                            inputData, this);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "subscribe" -> {
                if (this.getName().equals("see details")
                        && !currentUser.getSubscribedGenres().contains(action.getSubscribedGenre())
                        && currentMovie.getGenres().contains(action.getSubscribedGenre())) {
                    currentUser.getSubscribedGenres().add(action.getSubscribedGenre());
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
                }
            }

            default -> {
            }
        }
    }

    /**
     * @param jsonOutput   Output to add Json Objects
     * @param action       from Input
     * @param inputData    Database/Input class from Test File
     * @param objectMapper for json
     */
    private void filterOnPage(final ArrayNode jsonOutput, final Action action,
                              final Input inputData, final ObjectMapper objectMapper) {
        if (this.getName().equals("movies")) {
            this.moviesList = new ContextForFilter<>(new FilterCountry())
                    .executeMoviesStrategy(inputData.getMovies(),
                            currentUser.getCredentials().getCountry());
            Sort sortField = action.getFilters().getSort();
            this.moviesList = movieService.sortInputMovies(sortField, this.moviesList);
            Contains containsField = action.getFilters().getContains();
            this.moviesList = movieService.filterInputMoviesByContains(containsField,
                    moviesList);
            outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                    this.moviesList);
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    /**
     * @param jsonOutput   Output to add Json Objects
     * @param action       from Input
     * @param inputData    Database/Input class from Test File
     * @param objectMapper for json
     */
    private void searchOnPage(final ArrayNode jsonOutput, final Action action,
                              final Input inputData, final ObjectMapper objectMapper) {
        if (this.getName().equals("movies")) {
            this.moviesList = new ContextForFilter<>(new FilterCountry())
                    .executeMoviesStrategy(inputData.getMovies(),
                            currentUser.getCredentials().getCountry());
            this.moviesList = new ContextForFilter<>(new FilterName())
                    .executeMoviesStrategy(this.moviesList,
                            action.getStartsWith());
            outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                    this.moviesList);
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    /**
     * @param jsonOutput   Output to add Json Objects
     * @param credentials  from Input
     * @param inputData    Database/Input class from Test File
     * @param objectMapper for json
     */
    private void registerOnPage(final ArrayNode jsonOutput, final Input inputData,
                                final Credentials credentials,
                                final ObjectMapper objectMapper) {
        if (this.getCurrentUser() == null && this.getName().equals(
                "register")) {
            var registeredNewUser = userService.registerNewUser(inputData, credentials);
            if (registeredNewUser != null) {
                this.setCurrentUser(registeredNewUser);
                outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                        this.moviesList);
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                this.setName("homepage");
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            this.setName("homepage");
        }
    }

    /**
     * @param jsonOutput   Output to add Json Objects
     * @param credentials  from Input
     * @param inputData    Database/Input class from Test File
     * @param objectMapper for json
     */
    private void loginOnPage(final ArrayNode jsonOutput, final Input inputData,
                             final Credentials credentials,
                             final ObjectMapper objectMapper) {
        if (this.getCurrentUser() == null && this.getName().equals("login")) {
            User userFound = userService.checkForUserInData(inputData, credentials);

            if (userFound != null) {
                this.setCurrentUser(userFound);
                outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                        this.moviesList);
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
        this.setName("homepage");
    }

    /**
     * @param pageName to save in stack
     * @param movies available on page
     * @param movie current movie
     * @param user current user
     * every time this method is executed, the page is pushed to pagesStack
     */
    private void populateCurrentPage(final String pageName, final List<Movie> movies,
                                     final Movie movie, final User user) {
        this.setName(pageName);
        this.setMoviesList(movies);
        this.setCurrentMovie(movie);
        this.setCurrentUser(user);
        if (pageName.equals("homepage")) {
            Platform.getInstance().getPageStack().clear();
        }
        pushPageToStack(pageName, movies, movie, user);
    }

    /**
     * @param pageName to save in stack
     * @param movies available on page
     * @param movie current movie
     * @param user current user
     */
    private static void pushPageToStack(final String pageName, final List<Movie> movies,
                                        final Movie movie, final User user) {
        Platform.getInstance().getPageStack().push(Page
                .builder()
                .name(pageName)
                .moviesList(movies)
                .currentMovie(movie)
                .currentUser(user)
                .build());
    }

}
