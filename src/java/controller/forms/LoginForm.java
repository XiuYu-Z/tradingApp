package controller.forms;

import javax.validation.constraints.NotBlank;

public class LoginForm {

    @NotBlank(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Please enter a password.")
    private String password;

    /**
     * Gets the email address.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
