package entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Created at 8.3
 */

public class History extends AbstractBaseEntity {

    /**
     * Holds the unique key of this history entity
     */
    private int id;

    /**
     * Holds the name of this history entity
     */
    private String actionName;

    /**
     * Holds the display information of this history entity
     */
    private String displayString;

    /**
     * Whether this history entity has been undone.
     */
    private boolean undone;

    /**
     * Holds data related to the history.
     */
    private final Map<String, Serializable> data = new HashMap<>();

    /**
     * Returns the unique id of the this entity.
     *
     * @return Returns the unique id
     */
    @Override
    public int getKey() {
        return id;
    }

    /**
     * Sets the unique id of this entity.
     *
     * @param id an integer representing the unique id of this entity.
     */
    @Override
    public void setKey(int id) {
        this.id = id;
    }

    /**
     * Whether this historical action has been undone.
     *
     * @return True iff this action was undone.
     */
    public boolean isUndone() {
        return this.undone;
    }

    /**
     * Sets the undone status of this historical action.
     *
     * @param undone A boolean representing the historical action.
     */
    public void setUndone(boolean undone) {
        this.undone = undone;
    }

    /**
     * Returns the data associated with this historical action.
     *
     * @return A serializable piece of data.
     */
    public Map<String, Serializable> getData() {
        return this.data;
    }

    /**
     * Returns a particular piece of data associated with this action by key
     *
     * @param key The unique key associated with this piece of data
     * @return The data
     */
    public Object getData(String key) {
        return this.data.get(key);
    }

    /**
     * Adds a piece of data to this action.
     *
     * @param key   The unique key associated with this piece of data
     * @param value The value of the piece of data
     */
    public void addData(String key, Serializable value) {
        this.data.put(key, value);
    }

    /**
     * Gets the name of this historical action.
     *
     * @return A string
     */
    public String getActionName() {
        return this.actionName;
    }

    /**
     * Sets the name of this historical action.
     *
     * @param actionName A string representing this action's name
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * Gets the display value of this historical action.
     *
     * @return A string representing the display name of this action.
     */
    public String getDisplayString() {
        return this.displayString;
    }


    /**
     * Sets the display value of this historical action.
     *
     * @return A string representing the display name of this action.
     */
    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

}
