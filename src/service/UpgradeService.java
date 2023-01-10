package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Action;
import utils.Utils;

public final class UpgradeService {
    private final OutputService outputService;
    private final ObjectMapper objectMapper;
    public UpgradeService() {
        objectMapper = new ObjectMapper();
        outputService = new OutputService();
    }

    /**
     * @param jsonOutput to add json object
     * @param page to extract information
     */
    public void buyPremiumAccountOnPage(final ArrayNode jsonOutput,
                                         final Page page) {
        if (page.getName().equals("upgrades")) {
            var count = page.getCurrentUser().getTokensCount();
            if (count >= Utils.TOKEN_COST
                    && !page.getCurrentUser().getCredentials().getAccountType()
                    .equals("premium")) {
                page.getCurrentUser().getCredentials().setAccountType("premium");
                page.getCurrentUser().setTokensCount(count - Utils.TOKEN_COST);
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }

    /**
     * @param jsonOutput to add json object
     * @param page to extract information
     */
    public void buyTokensOnPage(final ArrayNode jsonOutput, final Action action,
                                 final Page page) {
        if (page.getName().equals("upgrades")) {
            var balance =
                    Integer.parseInt(page.getCurrentUser().getCredentials().getBalance());
            var count = Integer.parseInt(action.getCount());
            if (balance >= count) {
                page.getCurrentUser()
                        .setTokensCount(page.getCurrentUser().getTokensCount() + count);
                page.getCurrentUser().getCredentials()
                        .setBalance(String.valueOf(balance - count));
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
    }
}
