package usecases.query;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class AbstractQueryBuilder {

    /**
     * Holds boolean filters
     */
    protected final Map<String, Boolean> booleanFilters = new HashMap<String, Boolean>();

    /**
     * Holds integer filters
     */
    protected final Map<String, Integer> integerFilters = new HashMap<String, Integer>();

    /**
     * Holds list filters
     */
    protected final Map<String, List<String>> listFilters = new HashMap<String, List<String>>();

    /**
     * Holds date filters
     */
    protected final Map<String, LocalDate> dateFilters = new HashMap<String, LocalDate>();

    /**
     * Holds whether this ItemQuery has been fetched or not.
     */
    protected boolean fetched = false;

/********************************************************************************************************
 *
 * Getter and setter methods.
 *
 *********************************************************************************************************/

    /**
     * Returns the set of available boolean filters.
     *
     * @return The set of available boolean filters.
     */
    protected Map<String, Boolean> getBooleanFilters() {
        return this.booleanFilters;
    }

    /**
     * Returns the set of available integer filters.
     *
     * @return The set of available integer filters.
     */
    protected Map<String, Integer> getIntegerFilters() {
        return this.integerFilters;
    }

    /**
     * Returns the set of available list filters.
     *
     * @return The set of available list filters.
     */
    protected Map<String, List<String>> getListFilters() {
        return this.listFilters;
    }

    /**
     * Checks if this query has been fetched already. Each query can only be fetched once.
     *
     * @return True iff this query has been fetched already.
     */
    protected boolean hasBeenFetched() {
        return this.fetched;
    }

    /**
     * Sets this query's fetched status.
     *
     * @param fetched Whether this query has been fetched.
     */
    protected void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

}
