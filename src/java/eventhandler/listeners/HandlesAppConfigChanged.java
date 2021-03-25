package eventhandler.listeners;

import eventhandler.events.AppConfigChangedEvent;

public interface HandlesAppConfigChanged {

    /**
     * Handles the event fired
     *
     * @param event AppConfigChangedEvent
     */
    void handle(AppConfigChangedEvent event);

}
