package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import command.Page;
import io.Credentials;
import io.Input;
import io.User;

/**
 * Service that resolves login, register, verifications of user in "database"
 */
public final class UserService {

    private final OutputService outputService;

    public UserService() {
        outputService = new OutputService();
    }

    /**
     * @param inputData   from Test
     * @param credentials for register action
     * @return valid user or null
     */
    public User checkForUserInData(final Input inputData, final Credentials credentials) {
        return inputData
                .getUsers()
                .stream()
                .filter(
                        user1 -> user1
                                .getCredentials()
                                .equals(credentials))
                .findAny()
                .orElse(null);
    }

    /**
     * @param inputData   from Test
     * @param credentials for register action
     * @return registered user or null
     */
    public User registerNewUser(final Input inputData, final Credentials credentials) {
        if (checkForUserInData(inputData, credentials) == null) {
            var user = User
                    .builder()
                    .credentials(new Credentials(credentials))
                    .build();
            inputData.getUsers().add(user);
            return user;
        }
        return null;
    }

    /**
     * @param jsonOutput   Output to add Json Objects
     * @param credentials  from Input
     * @param inputData    Database/Input class from Test File
     * @param objectMapper for json
     */
    public void loginOnPage(final ArrayNode jsonOutput, final Input inputData,
                            final Credentials credentials,
                            final ObjectMapper objectMapper,
                            final Page page) {
        if (page.getCurrentUser() == null && page.getName().equals("login")) {
            User userFound = checkForUserInData(inputData, credentials);

            if (userFound != null) {
                page.setCurrentUser(userFound);
                outputService.addPOJOWithPopulatedOutput(jsonOutput, page, objectMapper,
                        page.getMoviesList());
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
        }
        page.setName("homepage");
    }

    /**
     * @param jsonOutput   Output to add Json Objects
     * @param credentials  from Input
     * @param inputData    Database/Input class from Test File
     * @param objectMapper for json
     */
    public void registerOnPage(final ArrayNode jsonOutput, final Input inputData,
                               final Credentials credentials,
                               final ObjectMapper objectMapper, final Page page) {
        if (page.getCurrentUser() == null && page.getName().equals(
                "register")) {
            var registeredNewUser = registerNewUser(inputData, credentials);
            if (registeredNewUser != null) {
                page.setCurrentUser(registeredNewUser);
                outputService.addPOJOWithPopulatedOutput(jsonOutput, page, objectMapper,
                        page.getMoviesList());
            } else {
                outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
                page.setName("homepage");
            }
        } else {
            outputService.addErrorPOJOToArrayNode(jsonOutput, objectMapper);
            page.setName("homepage");
        }
    }

}
