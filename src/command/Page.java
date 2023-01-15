package command;

import com.fasterxml.jackson.databind.node.ArrayNode;
import command.change_page.LoginHandler;
import command.change_page.LogoutHandler;
import command.change_page.MoviesHandler;
import command.change_page.PageHandler;
import command.change_page.RegisterHandler;
import command.change_page.SeeDetailsHandler;
import command.change_page.UpgradesHandler;
import command.on_page.BuyPremiumAccountHandler;
import command.on_page.BuyTokensOnPageHandler;
import command.on_page.FilterOnPageHandler;
import command.on_page.LikeHandler;
import command.on_page.LoginOnPageHandler;
import command.on_page.PurchaseHandler;
import command.on_page.RateHandler;
import command.on_page.RegisterOnPageHandler;
import command.on_page.RequestHandler;
import command.on_page.SearchOnPageHandler;
import command.on_page.SubscribeHandler;
import command.on_page.WatchHandler;
import io.Action;
import io.Credentials;
import io.Input;
import io.Movie;
import io.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import service.DatabaseService;
import service.MovieService;
import service.OutputService;
import service.UpgradeService;
import service.UserService;

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
    @Builder.Default
    private UpgradeService upgradeService = new UpgradeService();

    /**
     * @param pageName to save in stack
     * @param movies   available on page
     * @param movie    current movie
     * @param user     current user
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

    /**
     * @param jsonOutput Output to add Json Objects
     *                   Registered each type of request from user
     *                   Linked the handlers together
     *                   Start handling the page from the first handler
     */
    public void changePage(final ArrayNode jsonOutput, final String pageName,
                           final Input input, final String movieNameForDetails) {
        PageHandler registerHandler = new RegisterHandler(this);
        PageHandler loginHandler = new LoginHandler(this);
        PageHandler logoutHandler = new LogoutHandler(this);
        PageHandler moviesHandler = new MoviesHandler(this);
        PageHandler seeDetailsHandler = new SeeDetailsHandler(this);
        PageHandler upgradesHandler = new UpgradesHandler(this);

        registerHandler.setNextHandler(loginHandler);
        loginHandler.setNextHandler(logoutHandler);
        logoutHandler.setNextHandler(moviesHandler);
        moviesHandler.setNextHandler(seeDetailsHandler);
        seeDetailsHandler.setNextHandler(upgradesHandler);

        registerHandler.handlePage(jsonOutput, pageName, input, movieNameForDetails);
    }

    /**
     * @param jsonOutput to add POJO
     * @param action from user to handle
     * @param inputData database
     * @param credentials of user
     *                    Registered each type of request from user
     *                    Linked the handlers together
     *                    Start handling the page from the first handler
     *
     */
    public void onPageV2(final ArrayNode jsonOutput, final Action action,
                         final Input inputData, final Credentials credentials) {

        RequestHandler register = new RegisterOnPageHandler(this);
        RequestHandler login = new LoginOnPageHandler(this);
        RequestHandler search = new SearchOnPageHandler(this);
        RequestHandler filter = new FilterOnPageHandler(this);
        RequestHandler buyTokens = new BuyTokensOnPageHandler(this);
        RequestHandler buyPremium = new BuyPremiumAccountHandler(this);
        RequestHandler purchase = new PurchaseHandler(this);
        RequestHandler watch = new WatchHandler(this);
        RequestHandler like = new LikeHandler(this);
        RequestHandler rate = new RateHandler(this);
        RequestHandler subscribe = new SubscribeHandler(this);

        register.setNext(login);
        login.setNext(search);
        search.setNext(filter);
        filter.setNext(buyTokens);
        buyTokens.setNext(buyPremium);
        buyPremium.setNext(purchase);
        purchase.setNext(watch);
        watch.setNext(like);
        like.setNext(rate);
        rate.setNext(subscribe);

        register.handleRequest(jsonOutput, action, inputData, credentials);

    }

    /**
     * @param pageName to save in stack
     * @param movies   available on page
     * @param movie    current movie
     * @param user     current user
     *                 every time this method is executed, the page is pushed to pagesStack
     */
    public void populateCurrentPage(final String pageName, final List<Movie> movies,
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

}
