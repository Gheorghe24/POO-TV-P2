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
import services.MovieService;
import services.OutputService;
import services.UserService;
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
    private String startUserAction;
    private List<Movie> moviesList;
    private Movie currentMovie;
    private MovieService movieService;
    private OutputService outputService;
    private UserService userService;

    /**
     * @param jsonOutput Output to add Json Objects
     * @param action from Input
     */
    public void changePage(final ArrayNode jsonOutput, final Action action, final Input input) {
        ObjectMapper objectMapper = new ObjectMapper();
        String pageName = action.getPage();
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
                                    .executeStrategy(input.getMovies(),
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
                    List<Movie> movies;
                    if (currentMovie != null) {
                    movies = new ContextForFilter<>(new FilterCountry())
                            .executeStrategy(input.getMovies(),
                                    currentUser.getCredentials().getCountry());
                    } else {
                        movies = new ContextForFilter<>(new FilterCountry())
                                .executeStrategy(this.moviesList,
                                        currentUser.getCredentials().getCountry());
                    }
                    List<Movie> foundMovie = new ContextForFilter<>(new FilterName())
                            .executeStrategy(movies,
                                    action.getMovie());
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
                    populateCurrentPage(pageName, null, null, currentUser);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
                break;

            default:
        }

    }

    /**
     * @param jsonOutput Output to add Json Objects
     * @param action from Input
     * @param inputData Database/Input class from Test File
     * @param credentials from Input for register operation
     */
    public void onPage(final ArrayNode jsonOutput, final Action action,
                       final Input inputData, final Credentials credentials) {
        ObjectMapper objectMapper = new ObjectMapper();
        String feature = action.getFeature();
        switch (feature) {
            case "login" -> {
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

            case "register" -> {
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

            case "search" -> {
                if (this.getName().equals("movies")) {
                    this.moviesList = new ContextForFilter<>(new FilterCountry())
                            .executeStrategy(inputData.getMovies(),
                                    currentUser.getCredentials().getCountry());
                    this.moviesList = new ContextForFilter<>(new FilterName())
                            .executeStrategy(this.moviesList,
                                    action.getStartsWith());
                    outputService.addPOJOWithPopulatedOutput(jsonOutput, this, objectMapper,
                            this.moviesList);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            }

            case "filter" -> {
                if (this.getName().equals("movies")) {
                    this.moviesList = new ContextForFilter<>(new FilterCountry())
                            .executeStrategy(inputData.getMovies(),
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

            case "buy tokens" -> {
                if (this.getName().equals("upgrades")) {
                    var balance =
                            Integer.parseInt(this.getCurrentUser().getCredentials().getBalance());
                    var count = Integer.parseInt(action.getCount());
                    if (balance >= count) {
                        currentUser.setTokensCount(count);
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

            default -> {
            }
        }
    }

    private void populateCurrentPage(final String pageName, final List<Movie> movies,
                                     final Movie movie, final User user) {
        this.setName(pageName);
        this.setMoviesList(movies);
        this.setCurrentMovie(movie);
        this.setCurrentUser(user);
    }

}
