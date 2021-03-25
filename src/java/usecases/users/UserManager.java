package usecases.users;

import entities.User;
import persistence.PersistenceInterface;
import persistence.exceptions.EntryDoesNotExistException;
import persistence.exceptions.PersistenceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a use case for user, which deals with all the changes made to a user upon request.
 */
public class UserManager {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * To create an instance of UserManager
     *
     * @param gateway to access the stored data
     */
    public UserManager(PersistenceInterface gateway) {
        this.gateway = gateway;
    }

    /**
     * Returns a list of user objects.
     *
     * @param userIds A list of user ids
     * @return Returns a list of user objects.
     * @throws IOException An IOException
     */
    public List<User> get(List<Integer> userIds) throws IOException {
        return gateway.get(userIds, User.class);
    }

    /**
     * Returns a list of all user objects.
     *
     * @return Returns a list of all user objects.
     * @throws IOException An IOException
     */
    public List<User> all() throws IOException {
        return gateway.all(User.class);
    }


    /**
     * Returns a map of all users by id.
     *
     * @return Returns a map of all user objects with the user Id as the key.
     * @throws IOException An IOException
     */
    public Map<Integer, User> allById() throws IOException {
        List<User> users = this.all();
        Map<Integer, User> map = new HashMap<>();
        for (User user : users) {
            map.put(user.getKey(), user);
        }
        return map;
    }


    /**
     * Promotes a user to administrator status.
     *
     * @param userId a unique id representing a user
     * @return True if and only if the update succeeded.
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean promote(int userId) throws IOException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("admin");
        return gateway.update(users, User.class);
    }


    /**
     * Allows a normal user to set status to vacation.
     *
     * @param userId a unique id representing a user
     * @return True if and only if the update succeeded.
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean setVacation(int userId) throws IOException {
        List<User> users = getUserList(userId);
        if (users.get(0).getStatus().equals("normal")) {
            users.get(0).setStatus("vacation");
            return gateway.update(users, User.class);
        }
        return false;
    }

    /**
     * Allows a user in vacation to change back to normal status.
     *
     * @param userId a unique id representing a user
     * @return True if and only if the update succeeded.
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean unVacation(int userId) throws IOException {
        List<User> users = getUserList(userId);
        if (users.get(0).getStatus().equals("vacation")) {
            users.get(0).setStatus("normal");
            return gateway.update(users, User.class);
        }
        return false;
    }


    /**
     * Change the password of a user.
     *
     * @param newPassword the new password which the user wants to change to
     * @param userId      the user id of the intended user
     * @return true if and only if the password is successfully changed
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean changePassword(String newPassword, int userId) throws IOException {
        List<User> users = getUserList(userId);
        try {
            users.get(0).setPassword(newPassword);
            return gateway.update(users, User.class);
        } catch (IndexOutOfBoundsException e) {
            throw new EntryDoesNotExistException();
        }
    }


    /**
     * Changes the home city of a user.
     *
     * @param newHomeCity The string of indicating the new home city
     * @param userId      the userId whose home city we are changing
     * @return true if the home city update was successful
     * @throws IOException An IOException.
     */
    public boolean changeHomeCity(String newHomeCity, int userId) throws IOException {
        List<User> users = getUserList(userId);
        try {
            users.get(0).setHomeCity(newHomeCity);
            return gateway.update(users, User.class);
        } catch (IndexOutOfBoundsException e) {
            throw new EntryDoesNotExistException();
        }
    }


    /**
     * Freezes a User's account.
     *
     * @param userId the user id of the intended user
     * @return true if and only if the user has been successfully frozen
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean freezeUser(int userId) throws IOException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("frozen");
        return gateway.update(users, User.class);
    }


    /**
     * Unfreeze a User's account.
     *
     * @param userId the user id of the intended user
     * @return true if and only if the user is successfully unfrozen
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean unFreezeUser(int userId) throws IOException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("normal");
        return gateway.update(users, User.class);
    }


    /**
     * Make a user's account a demo account.
     *
     * @param userId the user id of the intended user
     * @return true if and only if the account is made into a demo account.
     * @throws IOException IOException
     */
    public boolean setAccountToDemo(int userId) throws IOException {
        User user = gateway.get(userId, User.class);
        user.setStatus("demo");
        return gateway.update(user, User.class);
    }


    /**
     * Sets a user's status to "request unfreeze" indicating that the user has requested his or her account to be unfrozen.
     *
     * @param userId the user id of the intended user
     * @return true if and only if the user's request is logged
     * @throws IOException          IOException
     * @throws PersistenceException An exception that is thrown during persisting to database.
     */
    public boolean requestUnfreeze(int userId) throws IOException {
        List<User> users = getUserList(userId);
        users.get(0).setStatus("requestUnfreeze");
        return gateway.update(users, User.class);
    }


    /**
     * Returns the users and their credits.
     *
     * @return A list of users and credits
     * @throws IOException An IOException
     */
    public List<List<Integer>> userHighCredit() throws IOException {
        Map<Integer, Integer> creditCount = new HashMap<>();
        for (User user : this.all()) {
            creditCount.put(user.getKey(), user.getCredit());
        }
        return sort(creditCount);
    }

    /********************************************************************************************************
     *
     * Helper methods
     *
     *********************************************************************************************************/

    private List<List<Integer>> sort(Map<Integer, Integer> count) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        List<Integer> keys = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            int position = 0;
            for (int frequency : frequencies) {
                if (entry.getValue() < frequency) position++;
            }
            frequencies.add(position, entry.getValue());
            keys.add(position, entry.getKey());
        }

        result.add(keys);
        result.add(frequencies);
        return result;

    }

    //Private method used as a helper method
    private List<User> getUserList(int userId) throws IOException {
        List<Integer> intendedUsers = new ArrayList<>();
        intendedUsers.add(userId);
        return gateway.get(intendedUsers, User.class);
    }


}
