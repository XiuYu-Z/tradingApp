package eventhandler.events;

/**
 * An event should implement this interface if it can fire.
 */
public interface Publishes {

    /**
     * Notifies listeners to handle this event.
     */
    void fire();


}

