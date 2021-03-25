package controller.forms;

public class UnfreezeUserForm {

    private int userId;

    /**
     * The user id we will unfreeze.
     *
     * @return The user id we will unfreeze.
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Sets the user id we will unfreeze.
     *
     * @param userId The user id we will unfreeze.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
