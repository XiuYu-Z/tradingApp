package usecases.rules;

public class VacationRule extends SystemRule {

    /**
     * Instance variable describing the restriction threshold
     */
    private int restriction;

    /**
     * Instantiates this class
     */
    public VacationRule() {
        restriction = 0;
    }

    /**
     * Returns the name of the rule
     *
     * @return the name of this rule
     */
    @Override
    public String getRule() {
        return "VacationRule";
    }

    /**
     * Returns the restriction threshold
     *
     * @return the restriction threshold
     */
    @Override
    public int restriction() {
        return restriction;
    }
}
