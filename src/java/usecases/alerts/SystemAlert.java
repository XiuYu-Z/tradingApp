package usecases.alerts;

import usecases.rules.RuleDoesNotExistException;

import java.io.IOException;

/**
 * The system for checking if the input id of User should alerted to the admin
 */
public abstract class SystemAlert {

    /**
     * To check if the entity with the input id needs to be alerted to the admin
     *
     * @param entityId of the entity
     * @return true iff the entity of the input id needs to be alerted to the admin
     * @throws IOException               if there is a IO error
     * @throws RuleDoesNotExistException if a system rule which is not defined in the system is intended to be checked.
     */
    public abstract boolean needAlert(int entityId) throws IOException, RuleDoesNotExistException;


    /**
     * To get the name of the alert
     *
     * @return the name of this alert
     */
    public abstract String getAlert();
}
