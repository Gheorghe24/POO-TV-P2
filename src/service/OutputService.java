package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import command.Page;
import io.Movie;
import java.util.ArrayList;
import java.util.List;
import out.Output;

/**
 * Service that writes resolves requests from portal to Output
 */
public final class OutputService {
    /**
     * @param jsonOutput   jsonObject to write
     * @param currentPage  to extract data
     * @param objectMapper for writing to JsonObject
     * @param movies       to add
     */
    public void addPOJOWithPopulatedOutput(final ArrayNode jsonOutput,
                                           final Page currentPage,
                                           final ObjectMapper objectMapper,
                                           final List<Movie> movies) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .currentMoviesList(movies)
                .currentUser(currentPage.getCurrentUser())
                .build());
        jsonOutput.add(node);
    }

    /**
     * @param jsonOutput   jsonObject to write
     * @param objectMapper for writing to JsonObject
     */
    public void addErrorPOJOToArrayNode(final ArrayNode jsonOutput,
                                        final ObjectMapper objectMapper) {
        ObjectNode node = objectMapper.valueToTree(Output
                .builder()
                .error("Error")
                .currentMoviesList(new ArrayList<>())
                .build());
        jsonOutput.add(node);
    }
}
