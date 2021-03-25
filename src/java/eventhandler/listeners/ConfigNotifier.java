package eventhandler.listeners;

import eventhandler.events.AppConfigChangedEvent;
import usecases.config.ConfigManager;
import usecases.config.ListensForConfig;

import java.util.ArrayList;
import java.util.List;

public class ConfigNotifier implements HandlesAppConfigChanged {


    private final List<ListensForConfig> listensForConfig = new ArrayList<>();


    private final ConfigManager configManager;


    /**
     * Initializes this class.
     *
     * @param configManager The config manager dependency.
     */
    public ConfigNotifier(ConfigManager configManager) {
        this.configManager = configManager;
    }


    /**
     * Everyone who needs to be updated
     *
     * @param listener
     */
    public void addListener(ListensForConfig listener) {
        this.listensForConfig.add(listener);
    }


    /**
     * Every class in the application that implements ListensForConfig will receive a new version of the config params.
     *
     * @param event An event that takes place when the configuration of the system changes.
     */
    @Override
    public void handle(AppConfigChangedEvent event) {
        for (ListensForConfig listener : listensForConfig) {
            listener.updateConfig(configManager.all());
        }
    }


}
