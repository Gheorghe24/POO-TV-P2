package io;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.Utils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class User {
    private Credentials credentials;
    @Builder.Default
    private Integer tokensCount = 0;
    @Builder.Default
    private Integer numFreePremiumMovies = Utils.FREE_PREMIUM_MOVIES;
    @Builder.Default
    private ArrayList<Movie> purchasedMovies = new ArrayList<>();
    @Builder.Default
    private ArrayList<Movie> watchedMovies = new ArrayList<>();
    @Builder.Default
    private ArrayList<Movie> likedMovies = new ArrayList<>();
    @Builder.Default
    private Queue<Notification> notifications = new LinkedList<>();
    @Builder.Default
    private ArrayList<Movie> ratedMovies = new ArrayList<>();
    @Builder.Default
    @JsonIgnore
    private ArrayList<String> subscribedGenres = new ArrayList<>();
}
