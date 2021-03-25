package controller.forms;

import java.util.Map;

public class ConfigForm {

    private Map<String, String> config;

    /**
     * Get configurations
     *
     * @return configurations
     */
    public Map<String, String> getConfigurations() {
        return this.config;
    }

    /**
     * Set configuration
     *
     * @param config configuration
     */
    public void setConfigurations(Map<String, String> config) {
        this.config = config;
    }

}
