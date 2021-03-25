package controller.forms;

import javax.validation.constraints.NotBlank;

public class UpdatePasswordForm {

    @NotBlank(message = "Password should not be empty")
    private String newPassword;

    /**
     * Returns the new password.
     *
     * @return The new password
     */
    public String getNewPassword() {
        return this.newPassword;
    }

    /**
     * Sets the new password.
     *
     * @param newPassword The new password
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
