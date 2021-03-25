package usecases.users;

import entities.User;
import eventhandler.HandlesEvents;
import persistence.PersistenceInterface;
import usecases.users.exceptions.DuplicatedUserNameException;

import java.io.IOException;
import java.util.List;


/**
 * A use case which is checks user accounts and makes changes to it.
 */
public class Authenticator {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final HandlesEvents eventsHandler;


    /**
     * To create an instance of Authenticator which checks user accounts and makes changes
     *
     * @param gateway       A persistence class used to persist data
     * @param eventsHandler A concrete instance that implements event handler interface
     */
    public Authenticator(PersistenceInterface gateway, HandlesEvents eventsHandler) {
        this.gateway = gateway;
        this.eventsHandler = eventsHandler;
    }


    /**
     * Checks login credentials, return true if and only if the credentials are valid
     *
     * @param username Username of the user
     * @param password Password of the user
     * @return The user id of the logged in user
     * @throws IOException An IOException
     */
    public int authenticate(String username, String password) throws IOException {
        int userId = checkCredentials(username, password);
        this.eventsHandler.fire("UserLoggedInEvent", userId);
        return userId;
    }


    /**
     * Create a new user account and save it to the file.
     *
     * @param username Username of the user
     * @param password Password of the user
     * @param isAdmin  Whether this user should be an admin.
     * @return The unique id of the user registered
     * @throws IOException                 IOException
     * @throws DuplicatedUserNameException Exception thrown if there already exists a user with the current username
     */
    public int register(String username, String password, String homeCity, boolean isAdmin, boolean isDemo)
            throws IOException, DuplicatedUserNameException {
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (user.getName().equals(username)) {
                throw new DuplicatedUserNameException();
            }
        }
        String status = "normal";
        if (isAdmin) status = "admin";
        if (isDemo) status = "demo";
        User newUser = new User(username, password, homeCity, status);
        User user = gateway.create(newUser, User.class);
        this.eventsHandler.fire("UserRegisteredEvent", user.getKey());
        return user.getKey();
    }


    /**
     * Create a new normal user account and save it to the file
     *
     * @param username Username of the user
     * @param password Password of the user
     * @return The unique id of the user registered
     * @throws IOException                 An IOException
     * @throws DuplicatedUserNameException Exception thrown if there already exists a user with the current username
     */
    public int register(String username, String password, String homeCity)
            throws IOException, DuplicatedUserNameException {
        List<User> users = gateway.all(User.class);
        if (users.size() == 0) {
            return this.register(username, password, homeCity, true, false);
        }
        return this.register(username, password, homeCity, false, false);
    }


    /**
     * Check if the new email is a duplicate
     *
     * @param username Username of the user
     * @return True if the input email has a duplicate, false if not
     */
    public boolean emailDuplicate(String username) throws IOException {
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (user.getName().equals(username)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Give the user's home city given userId
     *
     * @param userId The unique id of the user
     * @return A string representing the user's home city
     * @throws IOException An IOException
     */
    public String getUserHomeCity(int userId) throws IOException {
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (user.getKey() == userId) {
                return user.getHomeCity();
            }
        }
        return null;
    }


    /********************************************************************************************************
     *
     * Helper methods
     *
     *********************************************************************************************************/

    // Private method used as helper method, which returns true iff the credentials is valid
    private int checkCredentials(String username, String password) throws IOException {
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (user.getName().equals(username) && user.getPassword().equals(password)) {
                return user.getKey();
            }
        }
        return -1;
    }

}


