package usecases.rules;

/**
 * Represent a general system rule
 */
public abstract class SystemRule {
    /**
     * To get the name of the rule
     *
     * @return the name of the rule
     */
    public abstract String getRule();

    /**
     * To get a threshold value set by this rule
     *
     * @return the threshold value
     */
    public abstract int restriction();

}
