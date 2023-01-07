package command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Contains;
import io.Credentials;
import io.Input;
import io.Movie;
import io.Notification;
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
    private List<Movie> moviesList;
    private Movie currentMovie;
    private MovieService movieService;
    private OutputService outputService;
    private UserService userService;

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
                    if (currentMovie == null) {
                        movies = new ContextForFilter<>(new FilterCountry())
                                .executeStrategy(input.getMovies(),
                                        currentUser.getCredentials().getCountry());
                    } else {
                        movies = new ContextForFilter<>(new FilterCountry())
                                .executeStrategy(this.moviesList,
                                        currentUser.getCredentials().getCountry());
                    }
                    List<Movie> foundMovie = new ContextForFilter<>(new FilterName())
                            .executeStrategy(movies, movieNameForDetails);
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
                    movieService.watchMovie(jsonOutput, action, objectMapper, inputData, this);
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
                    // mai trebuie sa verific daca nu e abonat deja
                    // daca currentMovie are genul asta
                    //
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
     * @param movie      to add in Database
     * @param jsonOutput to write POJO
     * @param inputData, actual database
     */
    public void addToDatabase(final Movie movie, final ArrayNode jsonOutput,
                              final Input inputData) {
        if (movieService.getMoviesByName(movie.getName(), inputData.getMovies()).isEmpty()) {
            inputData.getMovies().add(movie);
            List<User> users = inputData.getUsers().stream().filter(user ->
                    user.getSubscribedGenres().stream().anyMatch(movie.getGenres()::contains))
                    .toList();
            List<User> availableUsers = users.stream().filter(user ->
                    !movie.getCountriesBanned().contains(user.getCredentials().getCountry()))
                    .toList();
            Notification notification = new Notification(movie.getName(), "ADD");
            availableUsers.forEach(user -> user.getNotifications().add(notification));
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
        }
    }

    /**
     * @param movieName  to add in Database
     * @param jsonOutput to write POJO
     * @param inputData, actual database
     */
    public void deleteFromDatabase(final String movieName, final ArrayNode jsonOutput,
                                   final Input inputData) {
        Movie movieToDelete =
                !movieService.getMoviesByName(movieName, inputData.getMovies()).isEmpty()
                        ? movieService.getMoviesByName(movieName, inputData.getMovies()).get(0)
                        : null;
        //TODO Logic for delete action
        if (movieToDelete != null) {
            inputData.getMovies().remove(movieToDelete);
            //aici trebuie sa adaug in notifications la utilizatorii care au subscribe la genul
            // filmului
            // vor fi notificati cei carora nu le este interzis in tara
//            Notification notification = new Notification(movie.getName(), "ADD");
//            currentUser.getNotifications().add(notification);
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
        }
    }

    private void populateCurrentPage(final String pageName, final List<Movie> movies,
                                     final Movie movie, final User user) {
        this.setName(pageName);
        this.setMoviesList(movies);
        this.setCurrentMovie(movie);
        this.setCurrentUser(user);
        if (pageName.equals("homepage")) {
            Platform.getInstance().getPageStack().clear();
        }
        Platform.getInstance().getPageStack().push(Page
                .builder()
                .name(pageName)
                .moviesList(movies)
                .currentMovie(movie)
                .currentUser(user)
                .build());
    }

}
