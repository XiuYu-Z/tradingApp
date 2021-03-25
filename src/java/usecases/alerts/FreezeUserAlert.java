package usecases.alerts;

import usecases.rules.RuleDoesNotExistException;
import usecases.rules.RuleValidator;
import usecases.rules.SystemRule;

import java.io.IOException;
import java.util.List;

/**
 * Represents an alert to admin when a user needs to be frozen according to related system rules
 */
public class FreezeUserAlert extends SystemAlert {

    private List<SystemRule> rules;

    private final RuleValidator checker;

    /**
     * To create an instance of FreezeUserAlert
     *
     * @param checker an instance of RuleValidator which checks whether a system is violated by a user
     */
    public FreezeUserAlert(RuleValidator checker) {
        this.checker = checker;
    }

    /**
     * To set the systems rules which are used to determine whether a user should be frozen
     *
     * @param rules which are the related system rules
     */
    public void setRules(List<SystemRule> rules) {
        this.rules = rules;
    }


    /**
     * To check if the user with the input id needs to be alerted to the admin
     *
     * @param userId of the user
     * @return true iff the user violates related at least one of the system rules and needs to be alerted to the admin
     * @throws IOException               if there is a IO error
     * @throws RuleDoesNotExistException if a system rule which is not defined in the system is intended to be checked.
     */
    @Override
    public boolean needAlert(int userId) throws IOException, RuleDoesNotExistException {
        for (SystemRule rule : rules) {
            if (checker.violate(rule, userId)) {
                return true;
            }
        }
        return false;
    }


    /**
     * To get the name of the alert
     *
     * @return the name of this alert
     */
    @Override
    public String getAlert() {
        return "FreezeUserAlert";
    }
}
