package eventhandler.events;

import eventhandler.listeners.ConfigNotifier;
import eventhandler.listeners.HandlesAppConfigChanged;

import java.util.ArrayList;
import java.util.List;


/**
 * An event that takes place when the configuration of the system changes.
 */
public class AppConfigChangedEvent extends AbstractEvent implements Publishes {

    /**
     * Holds all the listeners of this event.
     */
    private final List<HandlesAppConfigChanged> listeners = new ArrayList<>();

    /**
     * Initializes this class.
     *
     * @param appConfigListener ConfigNotifier
     */
    public AppConfigChangedEvent(ConfigNotifier appConfigListener) {
        this.eventName = "AppConfigChangedEvent";
        listeners.add(appConfigListener);
    }


    /**
     * Notifies users to handle this event.
     */
    @Override
    public void fire() {
        for (HandlesAppConfigChanged listener : this.listeners) {
            listener.handle(this);
        }
    }


}
