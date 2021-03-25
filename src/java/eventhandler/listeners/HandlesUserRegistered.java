package eventhandler.listeners;

import eventhandler.events.UserRegisteredEvent;

public interface HandlesUserRegistered {

    /**
     * Handles the event fired
     *
     * @param event UserRegisteredEvent
     */
    void handle(UserRegisteredEvent event);

}
