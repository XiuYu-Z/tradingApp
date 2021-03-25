package entities;

import persistence.relations.HasRelations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents an user account
 *
 * @version July 02, 2020
 */
public class User extends AbstractBaseEntity implements HasRelations {

    /**
     * User's id which is unique
     */
    private int userId;

    /**
     * User's name
     */
    private String name;

    /**
     * The password which can be used to login
     */
    private String password;

    /**
     * User's status, including normal, frozen, admin, requestUnfreeze, vacation, demo
     */
    private String status;


    /**
     * A credit calculated by the system based on the trading history of the user
     */
    private int credit;

    /**
     * The string representing the home city
     */
    private String homeCity;


    /**
     * Default constructor without input id
     * Create an user with input information, and the status is normal
     * Set the default id to 0, it will be changed later
     * This constructor will NOT generate wishlist and inventory
     *
     * @param name     the name of user
     * @param password the password of this user which used to login
     * @param status   the status of this user, the beginning status should be "admin" or "normal"
     */
    public User(String name, String password, String homeCity, String status) {
        this.userId = 0;
        this.name = name;
        this.password = password;
        this.status = status;
        this.homeCity = homeCity;
        this.credit = 0;
    }

    /**
     * The methods to get the name of this user
     *
     * @return the name of user
     */
    public String getName() {
        return name;
    }

    /**
     * The methods to get the password
     *
     * @return the password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * get the home city of the user
     *
     * @return the home city of the user
     */
    public String getHomeCity() {
        return homeCity;
    }

    /**
     * set the home city of the user
     *
     * @param homeCity the home city of the user
     */
    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    /**
     * The methods to get the status
     *
     * @return the status of this user
     */
    public String getStatus() {
        return status;
    }


    /**
     * The methods to set the new name to this user
     *
     * @param name the new name for this user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The methods to set the new password to this user
     *
     * @param password the new password for this user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * To set the method and return true only if the set succeed
     * The status can only switch to the status in status list
     *
     * @param status the new status for this User
     * @return true
     */
    public boolean setStatus(String status) {
        this.status = status;
        return true;
    }

    /**
     * get the credit of the user
     *
     * @return Returns the credit value
     */
    public int getCredit() {
        return credit;
    }

    /**
     * set the credit of the user
     *
     * @param credit the credit value
     */
    public void setCredit(int credit) {
        this.credit = credit;
    }


    /**
     * Checks if this user is an administrator.
     *
     * @return true iff the user is an administrator.
     */
    public boolean isAdmin() {
        return this.status.equals("admin");
    }


    /**
     * A method to get the key to this user, which is the id
     *
     * @return the id of this user
     */
    @Override
    public int getKey() {
        return this.userId;
    }

    /**
     * A method to change the id of this user
     *
     * @param id set new id to user
     */
    @Override
    public void setKey(int id) {
        this.userId = id;
    }


    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", credit='" + credit + '\'' +
                ", homeCity='" + homeCity + '\'' +
                '}';
    }

    /**
     * Maps relations for the user.
     *
     * @return Returns the relation
     */
    @Override
    public Map<String, List<Integer>> getDefinedRelations() {
        Map<String, List<Integer>> relationMap = new HashMap<>();
        return relationMap;
    }

}

