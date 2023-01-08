package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import io.Movie;
import io.Notification;
import io.User;
import java.util.List;
import lombok.Setter;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterAccountType;
import strategy.filter.FilterGenre;

@Setter
public class DatabaseService {
    private OutputService outputService;
    private MovieService movieService;

    public DatabaseService() {
        outputService = new OutputService();
        movieService = new MovieService();
    }

    /**
     * @param movie      to add in Database
     * @param jsonOutput to write POJO
     * @param inputData, actual database
     * checked if movie is in database
     * added movie to database
     * notified users that have subscription on movie genres
     */
    public void addToDatabase(final Movie movie, final ArrayNode jsonOutput,
                              final Input inputData) {
        if (movieService.getMoviesByName(movie.getName(), inputData.getMovies()).isEmpty()) {
            inputData.getMovies().add(movie);
            sendNotificationsToAvailableUsers(inputData, movie, "ADD");
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
        }
    }

    /**
     * @param movieName  to remove from Database
     * @param jsonOutput to write POJO
     * @param inputData, actual database
     * checked if movie is in database
     * removed movie to database
     * notified users that have subscription on movie genres
     * returned tokens or freePremiumMovies to standard or premium users
     */
    public void deleteFromDatabase(final String movieName, final ArrayNode jsonOutput,
                                   final Input inputData) {
        Movie movieToDelete =
                !movieService.getMoviesByName(movieName, inputData.getMovies()).isEmpty()
                        ? movieService.getMoviesByName(movieName, inputData.getMovies()).get(0)
                        : null;

        if (movieToDelete != null) {
            inputData.getMovies().remove(movieToDelete);
            List<User> availableUsers = sendNotificationsToAvailableUsers(inputData, movieToDelete,
                    "DELETE");

            List<User> premiumUsers = new ContextForFilter<>(new FilterAccountType())
                    .executeUsersStrategy(availableUsers, "premium");
            List<User> standardUsers = new ContextForFilter<>(new FilterAccountType())
                    .executeUsersStrategy(availableUsers, "standard");

            premiumUsers.forEach(user ->
                    user.setNumFreePremiumMovies(user.getNumFreePremiumMovies() + 1));
            standardUsers.forEach(user ->
                    user.setTokensCount(user.getTokensCount() + 2));
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, new ObjectMapper());
        }
    }

    /**
     * @param movieToDelete from which to get CountriesBanned
     * @param users to filter
     * @return list of users that can have access to that specific movie
     */
    private static List<User> getAvailableUsers(final Movie movieToDelete, final List<User> users) {
        return users.stream().filter(user ->
                        !movieToDelete.getCountriesBanned()
                                .contains(user.getCredentials().getCountry()))
                .toList();
    }

    /**
     * @param inputData actual database
     * @param movie to add or delete
     * @param databaseAction "add" or "delete"
     * @return list of users that have been notified
     */
    private List<User> sendNotificationsToAvailableUsers(final Input inputData, final Movie movie,
                                                         final String databaseAction) {
        List<User> users = new ContextForFilter<>(new FilterGenre())
                .executeUsersStrategy(inputData.getUsers(), movie.getGenres());

        List<User> availableUsers = getAvailableUsers(movie, users);
        Notification notification = new Notification(movie.getName(), databaseAction);
        availableUsers.forEach(user -> user.getNotifications().add(notification));
        return availableUsers;
    }

}
