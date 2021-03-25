package usecases.users;

import java.io.IOException;

public interface ProvidesAccess {

    /**
     * Checks if the user is an administrator.
     *
     * @return True iff the user is an administrator.
     * @throws IOException if there is a IO error.
     */
    boolean isAdmin(int userId) throws IOException;

    /**
     * Checks if the user's account is frozen.
     *
     * @return True iff the user's account is frozen.
     * @throws IOException if there is a IO error.
     */
    boolean isFrozen(int userId) throws IOException;

    /**
     * Checks if the user is on vacation.
     *
     * @return True iff the user's account is marked vacation.
     * @throws IOException if there is a IO error.
     */
    boolean isVacation(int userId) throws IOException;

    /**
     * Checks if the user is an demo user.
     *
     * @return True iff the user's account is marked demo.
     * @throws IOException if there is a IO error.
     */
    boolean isDemo(int userId) throws IOException;

    /**
     * Checks if the user's account is normal.
     *
     * @return True iff the user's account is normal.
     * @throws IOException if there is a IO error.
     */
    boolean isNormal(int userId) throws IOException;

    /**
     * Checks if the user requested unfreeze.
     *
     * @return True iff the user's account is normal.
     * @throws IOException if there is a IO error.
     */
    boolean isRequestedUnfreeze(int userId) throws IOException;

    /**
     * A method checking if the user can lend
     *
     * @return True iff the user lend
     * @throws IOException if there is a IO error.
     */
    boolean canLend(int userId) throws IOException;

    /**
     * A method checking if the user can borrow
     *
     * @return True iff the user borrow
     * @throws IOException if there is a IO error.
     */
    boolean canBorrow(int userId) throws IOException;

    /**
     * A method checking if the user can set account to vacation
     *
     * @return True iff the user can set account to vacation
     * @throws IOException if there is a IO error.
     */
    boolean canVacation(int userId) throws IOException;

}
