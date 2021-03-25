package controller.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RegisterForm {

    @NotBlank(message = "Please enter an email address.")
    @Email
    private String email;

    @NotBlank(message = "Please enter a password.")
    private String password;

    @NotBlank
    private String homeCity;

    /**
     * Gets the user's email
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     *
     * @param email the user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return the user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password the user's password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's home city.
     *
     * @return the user's home city.
     */
    public String getHomeCity() {
        return homeCity;
    }

    /**
     * Sets the user's home city.
     *
     * @param homeCity the user's home city.
     */
    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }


}
