package command.on_page;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Action;
import io.Credentials;
import io.Input;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public abstract class RequestHandler {
    protected RequestHandler next;

    /**
     * @param jsonOutput to add POJO
     * @param action for platform to handle
     * @param inputData database
     * @param credentials of user if needed
     */
    public abstract void handleRequest(ArrayNode jsonOutput, Action action,
                                       Input inputData, Credentials credentials);
}
