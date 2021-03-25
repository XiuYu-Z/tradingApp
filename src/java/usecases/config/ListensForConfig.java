package usecases.config;

import java.util.Map;

/**
 * A class that wants to be notified about any changes to system-wide configurations should implement this class.
 */
public interface ListensForConfig {

    /**
     * Sets the configuration given.
     *
     * @param config A map with the key value pairs of all the configurable options.
     */
    void updateConfig(Map<String, String> config);

}
