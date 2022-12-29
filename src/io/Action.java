package io;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class Action {
    private String type;
    private String page;
    private String feature;
    private String count;
    private Integer rate;
    private Credentials credentials;
    private String movie;
    private String deletedMovie;
    private Movie addedMovie;
    private String subscribedGenre;
    private String startsWith;
    private Filter filters;
    private String objectType;
    private ArrayList<Movie> movies;
}
