package command.change_page;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.Input;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class PageHandler {
    private PageHandler nextHandler;

    /**
     * @param jsonOutput to add POJO
     * @param pageName for change page actions
     * @param input Database
     * @param movieNameForDetails for some actions
     */
    public abstract void handlePage(ArrayNode jsonOutput, String pageName,
                                    Input input, String movieNameForDetails);
}
