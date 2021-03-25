package usecases.users;

import entities.User;
import persistence.PersistenceInterface;
import usecases.rules.NoMoreBorrowThanLendRule;
import usecases.rules.RuleDoesNotExistException;
import usecases.rules.RuleValidator;
import usecases.rules.VacationRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Managers account's access to various functions in the system
 */
public class PermissionsManager implements ProvidesAccess {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final RuleValidator validator;


    /**
     * To create an instance of PermissionsManager
     *
     * @param gateway   PersistenceInterface which gives access to the stored data
     * @param validator RuleValidator which checks the system rules
     */
    public PermissionsManager(PersistenceInterface gateway, RuleValidator validator) {
        this.validator = validator;
        this.gateway = gateway;
    }


    /**
     * Checks if the user is an administrator.
     *
     * @return True iff the user is an administrator.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isAdmin(int userId) throws IOException {
        return checkStatus(userId, "admin");
    }


    /**
     * Checks if the user's account is frozen.
     *
     * @return True iff the user's account is frozen.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isFrozen(int userId) throws IOException {
        return checkStatus(userId, "frozen");
    }


    /**
     * Checks if the user is on vacation.
     *
     * @return True iff the user's account is marked vacation.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isVacation(int userId) throws IOException {
        return checkStatus(userId, "vacation");
    }


    /**
     * Checks if the user is an demo user.
     *
     * @return True iff the user's account is marked demo.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isDemo(int userId) throws IOException {
        return checkStatus(userId, "demo");
    }


    /**
     * Checks if the user's account is normal.
     *
     * @return True iff the user's account is normal.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isNormal(int userId) throws IOException {
        return checkStatus(userId, "normal");
    }

    /**
     * Checks if the user requested unfreeze.
     *
     * @return True iff the user's account is normal.
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean isRequestedUnfreeze(int userId) throws IOException {
        return checkStatus(userId, "requestUnfreeze");
    }


    /**
     * A method checking if the user can lend
     *
     * @return True iff the user can lend
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean canLend(int userId) throws IOException {
        return checkStatus(userId, "normal") | checkStatus(userId, "admin");
    }


    /**
     * A method checking if the user can borrow
     *
     * @return True iff the user can borrow
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean canBorrow(int userId) throws IOException {
        NoMoreBorrowThanLendRule rule = new NoMoreBorrowThanLendRule();

        if (!canLend(userId)) return false;
        List<User> users = getUsers(userId);
        if (users.size() != 0 && users.get(0).getCredit() >= 1200) {
            return canLend(userId);
        }

        try {
            if (validator.violate(rule, userId)) {
                return false;
            }
        } catch (RuleDoesNotExistException e) {
            return canLend(userId);
        }

        return canLend(userId);
    }


    /**
     * A method checking if the user can borrow
     *
     * @return True iff the user can borrow
     * @throws IOException if there is a IO error.
     */
    @Override
    public boolean canVacation(int userId) throws IOException {
        if (checkStatus(userId, "demo")) return true;
        if (!canLend(userId)) return false;
        VacationRule rule = new VacationRule();

        try {
            if (validator.violate(rule, userId)) {
                return false;
            }
        } catch (RuleDoesNotExistException e) {
            return canLend(userId);
        }

        return true;
    }

    /********************************************************************************************************
     *
     * Helper methods
     *
     *********************************************************************************************************/

    //This checks the status of a user
    private boolean checkStatus(int userId, String status) throws IOException {
        List<User> users = getUsers(userId);
        if (users.size() != 0) {
            return users.get(0).getStatus().equals(status);
        }
        return false;
    }

    private List<User> getUsers(int userId) throws IOException {
        List<Integer> ids = new ArrayList<>(1);
        ids.add(userId);
        return gateway.get(ids, User.class);
    }

}
