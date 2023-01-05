package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Action;
import io.Contains;
import io.Input;
import io.Movie;
import io.Sort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Setter;
import strategy.filter.ContextForFilter;
import strategy.filter.FilterActor;
import strategy.filter.FilterGenre;
import strategy.filter.FilterName;
import strategy.sort.ContextForSort;
import strategy.sort.SortComplex;
import strategy.sort.SortDuration;
import strategy.sort.SortRating;
import utils.OrderPair;
import utils.Utils;

/**
 * Service that resolves requests for movies
 */
@Setter
public final class MovieService {

    private OutputService outputService;

    public MovieService() {
        outputService = new OutputService();
    }

    /**
     * @param action
     * @param movie
     * @return
     */
    public String extractMovieName(final Action action, final Movie movie) {
        if (action.getMovie() != null) {
            return action.getMovie();
        } else {
            return movie.getName();
        }
    }

    /**
     * @param fieldFilter
     * @param testedList list to filter
     * @return final result
     */
    public List<Movie> getMoviesByName(final String fieldFilter, final List<Movie> testedList) {
        return new ContextForFilter<>(new FilterName())
                .executeStrategy(testedList,
                        fieldFilter);
    }

    /**
     * @param containsField
     * @param moviesList
     * @return
     */
    public List<Movie> filterInputMoviesByContains(final Contains containsField,
                                                   final List<Movie> moviesList) {
        List<Movie> filteredList = new ArrayList<>(moviesList);
        if (containsField != null) {
            if (containsField.getActors() != null) {
                filteredList = new ContextForFilter<>(new FilterActor())
                        .executeStrategy(moviesList, containsField.getActors());
            }
            if (containsField.getGenre() != null) {
                filteredList = new ContextForFilter<>(new FilterGenre())
                        .executeStrategy(filteredList, containsField.getGenre());
            }
        }
        return filteredList;
    }

    /**
     * @param sortField
     * @param moviesList
     * @return sort movies with different strategy
     * in this case with duration and rating I created a Pair object to contain
     * both sort order and duration order
     */
    public List<Movie> sortInputMovies(final Sort sortField, final List<Movie> moviesList) {
        List<Movie> sortedList = new ArrayList<>(moviesList);
        if (sortField != null) {
            if (sortField.getRating() != null && sortField.getDuration() != null) {
                OrderPair pair = new OrderPair(sortField.getRating(), sortField.getDuration());
                sortedList = new ContextForSort<>(new SortComplex())
                        .executeStrategy(moviesList, pair);
            } else if (sortField.getRating() != null) {
                sortedList = new ContextForSort<>(new SortRating())
                        .executeStrategy(moviesList, sortField.getRating());
            } else {
                sortedList = new ContextForSort<>(new SortDuration())
                        .executeStrategy(moviesList, sortField.getDuration());
            }
        }
        return sortedList;
    }

    /**
     * Updating movie sent as parameter in all 5 lists
     */
    public void updateMovieInAllObjects(final Movie movie, final Input input) {
        updateMovieInList(movie, input.getMovies());

        input.getUsers().forEach((user) -> {
            updateMovieInList(movie, user.getWatchedMovies());

            updateMovieInList(movie, user.getLikedMovies());

            updateMovieInList(movie, user.getPurchasedMovies());

            updateMovieInList(movie, user.getRatedMovies());
        });
    }

    private void updateMovieInList(final Movie movie, final ArrayList<Movie> user) {
        getMoviesByName(movie.getName(), user).forEach(movie1 ->
            user.set(user.indexOf(movie1),
                    new Movie(movie))
        );
    }

    /**
     * checked failed cases and wrote added error to JSON
     * checked if the Movie exists in watched movies
     * Calculated average rating and added Movie to Rated
     * Updated Ratings in every
     */
    public void rateMovie(final ArrayNode jsonOutput, final Action action,
                          final ObjectMapper objectMapper,
                          final Input input,
                          final Page currentPage) {
        if (currentPage.getCurrentUser().getWatchedMovies().isEmpty()
                || action.getRate() < 1
                || action.getRate() > Utils.MAXIMUM_RATE) {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            return;
        }

        if (!getMoviesByName(extractMovieName(action, currentPage.getCurrentMovie()),
                currentPage.getCurrentUser().getWatchedMovies()).isEmpty()) {
            Movie movie = getMoviesByName(extractMovieName(action,
                            currentPage.getCurrentMovie()),
                    currentPage.getCurrentUser().getWatchedMovies()).get(0);

            int counterOfRatings = movie.getNumRatings();
            if (getMoviesByName(movie.getName(),
                    currentPage.getCurrentUser().getRatedMovies()).isEmpty()) {
                movie.setRating((movie.getRating() * counterOfRatings + action.getRate())
                        / (counterOfRatings + 1));
                movie.setNumRatings(movie.getNumRatings() + 1);
                currentPage.getCurrentUser().getRatedMovies().add(movie);
            }
            updateMovieInAllObjects(movie, input);


            outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                    objectMapper, new ArrayList<>(Collections.singleton(
                            new Movie(movie))));
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    /**
     * get available movies to purchase
     * checked if current user has right to purchase movie
     * here we have 2 posibilities to purchase movie
     * user with premium account has advantages
     * if user respects the rules, we add the Movie to purchased list
     */
    public void purchaseMovie(final ArrayNode jsonOutput, final Action action,
                              final ObjectMapper objectMapper, final Page currentPage) {
        List<Movie> availableMovies;
        if (action.getMovie() != null) {
            availableMovies = getMoviesByName(action.getMovie(), currentPage.getMoviesList());
        } else {
            availableMovies = getMoviesByName(currentPage.getCurrentMovie().getName(),
                    currentPage.getMoviesList());
        }

        if (!availableMovies.isEmpty()) {
            var firstAvailableMovie = availableMovies.get(0);
            if (getMoviesByName(firstAvailableMovie.getName(),
                    currentPage.getCurrentUser().getPurchasedMovies()).isEmpty()) {
                if (currentPage.getCurrentUser().getCredentials().getAccountType().equals("premium")
                        && currentPage.getCurrentUser().getNumFreePremiumMovies() > 0) {
                    currentPage.getCurrentUser().setNumFreePremiumMovies(
                            currentPage.getCurrentUser().getNumFreePremiumMovies() - 1);
                    currentPage.getCurrentUser().getPurchasedMovies().add(
                            new Movie(firstAvailableMovie));
                    outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                            objectMapper, availableMovies);
                } else if (currentPage.getCurrentUser().getTokensCount() >= 2) {
                    currentPage.getCurrentUser()
                            .setTokensCount(currentPage.getCurrentUser().getTokensCount() - 2);
                    currentPage.getCurrentUser().getPurchasedMovies().add(
                            new Movie(firstAvailableMovie));
                    outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                            objectMapper, availableMovies);
                } else {
                    outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                }
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    /**
     * we cannot watch a movie if we dind't buy it
     * if user respects the rules, we add the Movie to watched list
     */
    public void watchMovie(final ArrayNode jsonOutput, final Action action,
                            final ObjectMapper objectMapper, final Page currentPage) {
        if (currentPage.getCurrentUser().getPurchasedMovies().isEmpty()) {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        } else {
            String movieName = extractMovieName(action, currentPage.getCurrentMovie());
            List<Movie> availableFromPurchasedMovies = getMoviesByName(movieName,
                    currentPage.getCurrentUser().getPurchasedMovies());
            List<Movie> notFoundInWatchedMovies = getMoviesByName(movieName,
                    currentPage.getCurrentUser().getWatchedMovies());

            if (availableFromPurchasedMovies.isEmpty()) {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            } else if (notFoundInWatchedMovies.isEmpty()) {
                currentPage.setCurrentMovie(availableFromPurchasedMovies.get(0));
                currentPage.getCurrentUser().getWatchedMovies().add(
                        new Movie(currentPage.getCurrentMovie()));

                outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                        objectMapper, new ArrayList<>(Collections.singleton(
                                new Movie(currentPage.getCurrentMovie()))));
            }
        }
    }

    /**
     * verify that first the movie was watched
     * increment number of likes for every list containing him
     * call the method to update lists
     * if user respects the rules, we add the Movie to liked list
     */
    public void likeMovie(final ArrayNode jsonOutput, final Action action,
                           final ObjectMapper objectMapper, final Input inputData,
                          final Page currentPage) {
        if (currentPage.getCurrentUser().getWatchedMovies().isEmpty()) {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            return;
        }
        if (!getMoviesByName(action.getMovie(),
                currentPage.getCurrentUser().getWatchedMovies()).isEmpty()) {
            Movie movie =
                    getMoviesByName(extractMovieName(action,
                                    currentPage.getCurrentMovie()),
                            currentPage.getCurrentUser().getWatchedMovies()).get(0);
            movie.setNumLikes(movie.getNumLikes() + 1);
            currentPage.setCurrentMovie(new Movie(movie));
            updateMovieInAllObjects(movie, inputData);
            currentPage.getCurrentUser().getLikedMovies().add(movie);
            outputService.addPOJOWithPopulatedOutput(jsonOutput, currentPage,
                    objectMapper, new ArrayList<>(Collections.singleton(
                            new Movie(movie))));
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }
}
