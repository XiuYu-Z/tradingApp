package usecases.config;

import entities.Config;
import eventhandler.HandlesEvents;
import persistence.PersistenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and edits system-wide configuration.
 */
public class ConfigManager {

    /**
     * Class dependencies.
     */
    private final PersistenceInterface gateway;
    private final HandlesEvents eventHandler;

    /**
     * Create sensible defaults for various parameters of the program.
     *
     * @param gateway      An object that handles persistence
     * @param eventHandler An object that handles events
     */
    public ConfigManager(PersistenceInterface gateway, HandlesEvents eventHandler) {
        this.gateway = gateway;
        this.eventHandler = eventHandler;
        this.config.put("maxMeetingEdits", "3");
        this.config.put("maxIncompleteTransactions", "3");
        this.config.put("maxTransactionsPerWeek", "3");

        //We override any saved defaults
        this.loadConfig();

    }

    /**
     * Holds all the current configuration of the application.
     */
    private Map<String, String> config = new HashMap<>();


    /**
     * Retrieves a particular configuration by the unique key
     *
     * @param key The unique key for the configuration
     * @return The value of the configuration
     */
    public String get(String key) {
        return this.config.get(key);
    }

    /**
     * Returns a map of all available configurations of this program.
     *
     * @return A map of all available configurations of this program.
     */
    public Map<String, String> all() {
        return this.config;
    }


    /**
     * Edits the available configuration.
     *
     * @param key   A unique string representing the configuration
     * @param value The value of the configuration.
     * @return True iff the configuration succeeded.
     * @throws IOException An IOException
     */
    public boolean edit(String key, Integer value) throws IOException {
        this.config.put(key, value.toString());

        List<Config> configArr = new ArrayList<>();

        //Save it to persistence
        for (Map.Entry<String, String> config : this.config.entrySet()) {
            Config configObj = new Config();
            if (config.getKey().equals(key)) {
                configObj.setConfigName(key).setConfigValue(value.toString());
                configArr.add(configObj);
            } else {
                configObj.setConfigName(config.getKey()).setConfigValue(config.getValue());
                configArr.add(configObj);
            }
        }

        gateway.remove(Config.class);
        gateway.create(configArr, Config.class);

        //Notify the app that config has changed, so that every class can update its parameters.
        this.eventHandler.fire("AppConfigChangedEvent");

        return true;
    }


/********************************************************************************************************
 *
 * Helper methods
 *
 *********************************************************************************************************/

    /**
     * Load an available config
     */
    private void loadConfig() {
        try {
            //If there are multiple values per key, then we use the latest one.
            List<Config> configPair = gateway.all(Config.class);
            for (Config c : configPair) {
                this.config.put(c.getConfigName(), c.getConfigValue());
            }
        } catch (IOException e) {
            System.out.println("Config did not load successfully. Using defaults.");
        }
    }


}
