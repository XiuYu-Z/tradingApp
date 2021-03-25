package controller.forms;

import javax.validation.constraints.NotBlank;

public class UpdateHomeCityForm {

    @NotBlank(message = "Home City should not be empty!")
    private String newHomeCity;

    /**
     * Gets the new home city we are going to update to.
     *
     * @return The new home city we are going to update to.
     */
    public String getNewHomeCity() {
        return this.newHomeCity;
    }

    /**
     * Sets the new home city we are going to update to.
     *
     * @param newHomeCity the new home city we are going to update to.
     */
    public void setNewHomeCity(String newHomeCity) {
        this.newHomeCity = newHomeCity;
    }

}
