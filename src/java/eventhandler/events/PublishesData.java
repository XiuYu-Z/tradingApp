package eventhandler.events;

/**
 * An event should implement this interface if it publishes data.
 */
public interface PublishesData extends Publishes {


    /**
     * Allows the event handler to give this event data.
     *
     * @param data
     */
    void passData(Object data);


}
