package usecases.rules;

import usecases.config.ListensForConfig;

import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Represent the rule that a user can have a maximum number of incomplete transactions before being alert to admin to be frozen
 */
public class MaxIncompleteTransactionRule extends SystemRule implements ListensForConfig {

    /**
     * Instance variables
     */
    private int maxIncompleteTransaction; // set by configManager, see updateConfig


    /**
     * To get the name of this rule
     *
     * @return the name of this rule
     */
    @Override
    public String getRule() {
        return "MaxIncompleteTransaction";
    }

    /**
     * To get the maximum number of incomplete transactions a user can have before being alert to admin
     *
     * @return the maximum number
     */
    @Override
    public int restriction() {
        return maxIncompleteTransaction;
    }

    /**
     * To update the threshold value, maximum number of incomplete transactions a user can have before being alert to admin
     *
     * @param config A map with the key value pairs of all the configurable options.
     */
    @Override
    public void updateConfig(Map<String, String> config) {
        this.maxIncompleteTransaction = parseInt(config.get("maxIncompleteTransactions"));
    }
}
