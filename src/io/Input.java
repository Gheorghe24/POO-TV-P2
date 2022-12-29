package io;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"users", "movies", "actions"})
public final class Input {
    @JsonProperty("users")
    private ArrayList<User> users;
    @JsonProperty("movies")
    private ArrayList<Movie> movies;
    @JsonProperty("actions")
    private ArrayList<Action> actions;

    @JsonIgnoreProperties
    private static Input input = null;
    private Input() {
    }

    /**
     * @return Singleton Instance
     */
    public static Input getInstance() {
        if (input == null) {
            input = new Input();
        }

        return input;
    }

}
