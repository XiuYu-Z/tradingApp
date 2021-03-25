package usecases.alerts;

import entities.Item;
import entities.User;
import persistence.PersistenceInterface;
import usecases.rules.RuleDoesNotExistException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A use case which deals with all the operations relating to various alerts for an admin
 */

public class AlertManager {
    /**
     * Classes dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * Instance variables
     */
    private final Map<String, SystemAlert> alertList = new HashMap<>();


    /**
     * To instantiate an AlertManager which provides alert in the given list
     *
     * @param gateway to access the file
     */
    public AlertManager(PersistenceInterface gateway) {
        this.gateway = gateway;
    }

    /**
     * Sets alerts
     *
     * @param alerts A list of alerts
     */
    public void setAlerts(List<SystemAlert> alerts) {
        for (SystemAlert alert : alerts) {
            alertList.put(alert.getAlert(), alert);
        }
    }


    /**
     * To get the id of users who request to be unfrozen
     *
     * @return a List of user ids
     * @throws IOException               IOException
     * @throws RuleDoesNotExistException An exception indicating the rule does not exist
     */
    public List<User> getUnfreezeRequests() throws IOException, RuleDoesNotExistException {
        SystemAlert unfreezeUserAlert = alertList.get("UnfreezeUserAlert");
        List<User> requests = new ArrayList<>();
        List<User> users = gateway.all(User.class);
        for (User user : users) {
            if (unfreezeUserAlert.needAlert(user.getKey())) {
                requests.add(user);
            }
        }

        return requests;
    }


    /**
     * To get the id of Items which are requested by a user to be added to inventory
     *
     * @return a List of item ids
     * @throws IOException               IOException
     * @throws RuleDoesNotExistException An exception indicating the rule does not exist
     */
    public List<Integer> getAddItemRequests() throws IOException, RuleDoesNotExistException {
        SystemAlert addInventoryAlert = alertList.get("AddInventoryAlert");
        List<Item> items = gateway.all(Item.class);

        List<Integer> requests = new ArrayList<>();
        for (Item item : items) {
            int itemId = item.getKey();
            if (addInventoryAlert.needAlert(itemId)) {
                requests.add(itemId);
            }
        }

        return requests;
    }


    /**
     * To get the id of users who needs to be frozen
     *
     * @return a List of user ids
     * @throws IOException               if there is a IO error.
     * @throws RuleDoesNotExistException if the rule does not exist
     */
    public List<User> getFreezeSuggestions() throws IOException, RuleDoesNotExistException {
        SystemAlert freezeUserAlert = alertList.get("FreezeUserAlert");
        List<User> users = gateway.all(User.class);

        List<User> suggestions = new ArrayList<>();
        for (User user : users) {
            if (freezeUserAlert.needAlert(user.getKey()) && !user.getStatus().equals("frozen")) {
                suggestions.add(user);
            }
        }
        return suggestions;
    }


}
