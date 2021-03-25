package usecases.rules;

import usecases.config.ListensForConfig;

import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Represent the rule that a user can have a maximum number of transactions
 */
public class MaxTransactionPerWeekRule extends SystemRule implements ListensForConfig {

    /**
     * Instance variables
     */
    private int maxTransactionAllowed; // load by configManager

    /**
     * To get the name of this rule
     *
     * @return the name of this rule
     */
    @Override
    public String getRule() {
        return "MaxTransactionPerWeek";
    }

    /**
     * To get the maximum number of transactions a user can have per week before being alert to admin
     *
     * @return the maximum number
     */
    @Override
    public int restriction() {
        return maxTransactionAllowed;
    }


    /**
     * To update the threshold value, maximum number of incomplete transactions a user can have before being alert to admin
     *
     * @param config A map with the key value pairs of all the configurable options.
     */
    @Override
    public void updateConfig(Map<String, String> config) {
        this.maxTransactionAllowed = parseInt(config.get("maxTransactionsPerWeek"));
    }


}
