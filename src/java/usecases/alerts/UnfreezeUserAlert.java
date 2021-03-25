package usecases.alerts;

import entities.User;
import persistence.PersistenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an alert to admin when a user has requested to be unfrozen
 */
public class UnfreezeUserAlert extends SystemAlert {

    private final PersistenceInterface gateway;

    /**
     * To create an instance of UnfreezeUserAlert
     *
     * @param gateway which gives access to the stored data
     */
    public UnfreezeUserAlert(PersistenceInterface gateway) {
        this.gateway = gateway;
    }


    /**
     * To check if the user with the input id needs to be alerted to the admin
     *
     * @param userId of the user
     * @return true iff the user has requested to be unfrozen
     * @throws IOException if there is a IO error
     */
    @Override
    public boolean needAlert(int userId) throws IOException {
        List<Integer> users = new ArrayList<>();
        users.add(userId);
        return gateway.get(users, User.class).get(0).getStatus().equals("requestUnfreeze");
    }


    /**
     * To get the name of the alert
     *
     * @return the name of this alert
     */
    @Override
    public String getAlert() {
        return "UnfreezeUserAlert";
    }
}
