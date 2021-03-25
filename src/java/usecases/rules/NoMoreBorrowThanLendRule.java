package usecases.rules;

/**
 * Represent the rule that a user cannot borrow more items than lend
 */
public class NoMoreBorrowThanLendRule extends SystemRule {

    /**
     * Instance variable
     */
    private int restriction;

    /**
     * To create an instance of NoMoreBorrowThanLendRule
     */
    public NoMoreBorrowThanLendRule() {
        restriction = 0;
    }

    /**
     * To get the name of this rule
     *
     * @return the name of this rule
     */
    @Override
    public String getRule() {
        return "NoMoreBorrowThanLend";
    }

    /**
     * To get the number of borrowing is allowed to exceed the number of lending
     *
     * @return the number
     */
    @Override
    public int restriction() {
        return restriction;
    }

}
