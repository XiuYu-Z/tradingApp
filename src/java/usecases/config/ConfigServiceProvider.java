package usecases.config;

import eventhandler.HandlesEvents;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class ConfigServiceProvider {

    private HandlesEvents eventHandler;

    /**
     * Creates an instance of this class.
     *
     * @param eventHandler An object that handles events
     */
    public ConfigServiceProvider(HandlesEvents eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * Loads the config initially on boot.
     *
     * @param event A Spring event which indicates the application context has been refreshed.
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        this.eventHandler.fire("AppConfigChangedEvent");
    }


}

