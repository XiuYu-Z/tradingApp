package usecases.rules;


import entities.Transaction;
import usecases.trade.TransactionFetcher;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;


/**
 * Checks whether a user violates one of the rules
 */
public class RuleValidator {

    /**
     * Class dependencies
     */
    private final TransactionFetcher transactionFetcher;

    /**
     * Constructor to create an instance of RuleValidator
     */
    public RuleValidator(TransactionFetcher transactionFetcher) {
        this.transactionFetcher = transactionFetcher;
    }

    /**
     * Check whether the input system rule is violate by the input user
     *
     * @param rule   which is one of the system rule needs to be checked
     * @param userId if the id of the user who needs to be checked
     * @return true if and only if the input user violates the input rule
     * @throws IOException               if there is an IO error
     * @throws RuleDoesNotExistException if the input system rule does not exist
     */
    public boolean violate(SystemRule rule, int userId) throws IOException, RuleDoesNotExistException {

        switch (rule.getRule()) {
            case "NoMoreBorrowThanLend":
                return moreBorrowThanLend(userId, rule.restriction());

            case "MaxTransactionPerWeek":
                return tooManyTransactionsPerWeek(userId, rule.restriction());

            case "MaxIncompleteTransaction":
                return tooManyIncompleteTransactions(userId, rule.restriction());

            case "VacationRule":
                return hasOnGoingTransaction(userId, rule.restriction());
        }
        throw new RuleDoesNotExistException();
    }


    /********************************************************************************************************
     *
     * Helper methods
     *
     *********************************************************************************************************/

    //Return true iff the user conducts too many transaction per week
    private boolean tooManyTransactionsPerWeek(int userId, int maxTransactionAllow) throws IOException {
        return transactionFetcher.query().after(LocalDate.now().with(DayOfWeek.MONDAY))
                .involvesUser(userId).isExpected()
                .getTransactions().size() > maxTransactionAllow;
    }

    /**
     * condition for Too many incomplete transactions
     *
     * @param userId                        id of the user
     * @param maxIncompleteTransactionAllow the value of maximum of incomplete transaction that is allowed
     * @return true iff the user has too many incomplete transactions
     * @throws IOException
     */
    private boolean tooManyIncompleteTransactions(int userId, int maxIncompleteTransactionAllow) throws IOException {
        return transactionFetcher.query().involvesUser(userId).isIncomplete()
                .getTransactions().size() > maxIncompleteTransactionAllow;
    }


    /**
     * condition to check in the user borrowed more than lend
     *
     * @param userId
     * @param threshold
     * @return true iff the user borrows more items than lends
     * @throws IOException
     */
    private boolean moreBorrowThanLend(int userId, int threshold) throws IOException {
        List<Transaction> borrows = transactionFetcher.query().involvesUserAsBorrower(userId).getTransactions();
        List<Transaction> lends = transactionFetcher.query().involvesUserAsLender(userId).getTransactions();

        return borrows.size() - lends.size() > threshold;

    }

    private boolean hasOnGoingTransaction(int userId, int threshold) throws IOException {
        List<Transaction> onGoings = transactionFetcher.query().involvesUser(userId).isOpen().getTransactions();
        return onGoings.size() != threshold;
    }

}
