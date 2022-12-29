package services;

import io.Credentials;
import io.Input;
import io.User;
import lombok.NoArgsConstructor;

/**
 * Service that resolves login, register, verifications of user in "database"
 */
@NoArgsConstructor
public final class UserService {

    /**
     * @param inputData from Test
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
     * @param inputData from Test
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

}
