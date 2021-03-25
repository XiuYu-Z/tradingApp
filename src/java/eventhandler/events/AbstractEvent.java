package eventhandler.events;

abstract public class AbstractEvent {

    protected String eventName;

    /**
     * Returns the event name
     *
     * @return Returns the event name
     */
    public String getName() {
        return this.eventName;
    }

}
