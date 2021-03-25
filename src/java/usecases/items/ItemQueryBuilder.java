package usecases.items;

import entities.Item;
import usecases.query.AbstractQueryBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Builds an item query. This query builder allows the user to chain filters together easily.
 * Example usage:
 * query().onlyApproved().onlyOwnedBy(5).heldByOwner().getNames();
 * This will return the names of items that have been approved, owned by user id 5 and held by user id 5.
 */
public class ItemQueryBuilder extends AbstractQueryBuilder {


    /**
     * Initializes this class by placing all known filters into existing categories.
     *
     * @param fetcher ItemFetcher
     */
    public ItemQueryBuilder(ItemFetcher fetcher) {
        this.fetcher = fetcher;
        this.booleanFilters.put("onlyDeleted", null);
        this.booleanFilters.put("notDeleted", null);
        this.booleanFilters.put("heldByOwner", null);
        this.booleanFilters.put("onlyApproved", null);
        this.booleanFilters.put("exceptApproved", null);
        this.booleanFilters.put("ownedByUnfrozenUser", null);
        this.booleanFilters.put("ownedByUnVacationUser", null);
        this.booleanFilters.put("forSell", null);
        this.booleanFilters.put("unreserved", null);

        this.integerFilters.put("findById", null);
        this.integerFilters.put("exceptOwnedBy", null);
        this.integerFilters.put("onlyOwnedBy", null);
        this.integerFilters.put("exceptHeldBy", null);
        this.integerFilters.put("onlyHeldBy", null);
        this.integerFilters.put("inWishlistOf", null);
        this.integerFilters.put("notInWishlistOf", null);
        this.integerFilters.put("sellBy", null);
        this.integerFilters.put("findByHomeCity", null);

        this.listFilters.put("fetchByTag", null);

    }

    private final ItemFetcher fetcher;

    private final Map<String, Boolean> booleanFilters = new HashMap<String, Boolean>();

    private final Map<String, Integer> integerFilters = new HashMap<String, Integer>();

    private final Map<String, List<String>> listFilters = new HashMap<>();

    /**
     * Holds whether this ItemQuery has been fetched or not.
     */
    private boolean fetched = false;


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
    public Map<String, Boolean> getBooleanFilters() {
        return this.booleanFilters;
    }

    /**
     * Returns the set of available integer filters.
     *
     * @return The set of available integer filters.
     */
    public Map<String, Integer> getIntegerFilters() {
        return this.integerFilters;
    }

    /**
     * Returns the set of available list filters
     *
     * @return The set of available list filters
     */
    public Map<String, List<String>> getListFilters() {
        return this.listFilters;
    }

    /**
     * Checks if this query has been fetched already. Each query can only be fetched once.
     *
     * @return True iff this query has been fetched already.
     */
    public boolean hasBeenFetched() {
        return this.fetched;
    }

    /**
     * Sets this query's fetched status.
     *
     * @param fetched Whether this query has been fetched.
     */
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }


/********************************************************************************************************
 *
 * All the String Filters Available
 *
 *********************************************************************************************************/

    /**
     * Checks that an item is tagged with all the tags
     *
     * @param tags A list of tags
     * @return An instance of this class.
     */
    public ItemQueryBuilder isTaggedWithAll(List<String> tags) {
        this.listFilters.put("isTaggedWithAll", tags);
        return this;
    }


/********************************************************************************************************
 *
 * All the Integer Filters Available
 *
 *********************************************************************************************************/

    /**
     * Allows the client to filter based on the home city of a user.
     *
     * @param userId The user id
     * @return An instance of this class.
     */
    public ItemQueryBuilder findByHomeCity(int userId) {
        this.integerFilters.put("findByHomeCity", userId);
        return this;
    }

    /**
     * Allows the client to filter the items by the item id.
     *
     * @param itemId A unique id of the item.
     * @return An instance of this class.
     */
    public ItemQueryBuilder findById(int itemId) {
        this.integerFilters.put("findById", itemId);
        return this;
    }

    /**
     * Allows the client exclude items owned by a particular user.
     *
     * @param userId A unique id of the user.
     * @return An instance of this class.
     */
    public ItemQueryBuilder exceptOwnedBy(int userId) {
        this.integerFilters.put("exceptOwnedBy", userId);
        return this;
    }

    /**
     * Allows the client to only include items owned by a particular user.
     *
     * @param userId A unique id of the user.
     * @return An instance of this class.
     */
    public ItemQueryBuilder onlyOwnedBy(int userId) {
        this.integerFilters.put("onlyOwnedBy", userId);
        return this;
    }

    /**
     * Allows the client to exclude items currently held by a particular user.
     * An item's owner and current holder can be different.
     *
     * @param userId A unique id of the user.
     * @return An instance of this class.
     */
    public ItemQueryBuilder exceptHeldBy(int userId) {
        this.integerFilters.put("exceptHeldBy", userId);
        return this;
    }

    /**
     * Allows the client to include items that are only currently held by a particular user.
     * An item's owner and current holder can be different.
     *
     * @param userId A unique id of the user.
     * @return An instance of this class.
     */
    public ItemQueryBuilder onlyHeldBy(int userId) {
        this.integerFilters.put("onlyHeldBy", userId);
        return this;
    }

    /**
     * Allows the client to include items that are only in the wishlist of a particular user.
     *
     * @param userId A unique id of the user.
     * @return An instance of this class.
     */
    public ItemQueryBuilder inWishlistOf(int userId) {
        this.integerFilters.put("inWishlistOf", userId);
        return this;
    }

    /**
     * Allows the client to exclude items that are in the wishlist of a particular user.
     *
     * @param userId A unique id of the user.
     * @return An instance of this class.
     */
    public ItemQueryBuilder notInWishlistOf(int userId) {
        this.integerFilters.put("notInWishlistOf", userId);
        return this;
    }

    /**
     * Gets items sold by this user.
     *
     * @param userId A unique id of the user
     * @return An instance of this class.
     */
    public ItemQueryBuilder sellBy(int userId) {
        this.integerFilters.put("sellBy", userId);
        return this;
    }


/********************************************************************************************************
 *
 * All the Boolean Filters Available
 *
 *********************************************************************************************************/

    /**
     * Allows the client to include only items that are held by the owner.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder heldByOwner() {
        this.booleanFilters.put("heldByOwner", true);
        return this;
    }


    /**
     * Allows the client to include only items that have been approved by the administrator.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder onlyApproved() {
        this.booleanFilters.put("onlyApproved", true);
        return this;
    }


    /**
     * Allows the client to exclude items that have been approved by the administrator.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder exceptApproved() {
        this.booleanFilters.put("exceptApproved", true);
        return this;
    }

    /**
     * Allows the client to include only items owned by users that are not frozen.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder ownedByUnfrozenUser() {
        this.booleanFilters.put("ownedByUnfrozenUser", true);
        return this;
    }

    /**
     * Allows the client to include only items owned by users that are not in vacation.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder ownedByUnVacationUser() {
        this.booleanFilters.put("ownedByUnVacationUser", true);
        return this;
    }

    /**
     * Allows the client to include only items that have been soft deleted.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder onlyDeleted() {
        this.booleanFilters.put("onlyDeleted", true);
        return this;
    }

    /**
     * Allows the client to include items that have been soft deleted.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder notDeleted() {
        this.booleanFilters.put("notDeleted", true);
        return this;
    }

    /**
     * Allows the client to include only items that are for sale.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder forSell() {
        this.booleanFilters.put("forSell", true);
        return this;
    }

    /**
     * Allows the client to include only items that are unreserved.
     *
     * @return An instance of this class.
     */
    public ItemQueryBuilder unreserved() {
        this.booleanFilters.put("unreserved", true);
        return this;
    }

/********************************************************************************************************
 *
 * All the ways we can retrieve the results
 *
 *********************************************************************************************************/

    /**
     * Retrieves a list of item names satisfying the query conditions.
     *
     * @return A list of item names satisfying the query conditions.
     * @throws IOException IOException
     */
    public List<String> getNames() throws IOException {
        return this.fetcher.fetchNames(this);
    }


    /**
     * Retrieves a list of item ids satisfying the query conditions.
     *
     * @return A list of item ids satisfying the query conditions.
     * @throws IOException IOException
     */
    public List<Integer> getIds() throws IOException {
        return this.fetcher.fetchIds(this);
    }

    /**
     * Retrieves a list of Item objects satisfying the query conditions.
     *
     * @return A list of Item objects satisfying the query conditions.
     * @throws IOException IOException
     */
    public List<Item> getObjects() throws IOException {
        return this.fetcher.fetchObjects(this);
    }


    /**
     * Retrieves the name of the first item satisfying the query conditions.
     *
     * @return An item name.
     * @throws IOException IOException
     */
    public String getName() throws IOException {
        List<String> result = this.fetcher.fetchNames(this);
        if (!result.isEmpty()) return result.get(0);
        return null;
    }


    /**
     * Retrieves the id of the first item satisfying the query conditions.
     *
     * @return An item id
     * @throws IOException IOException
     */
    public Integer getId() throws IOException {
        List<Integer> result = this.fetcher.fetchIds(this);
        if (!result.isEmpty()) return result.get(0);
        return null;
    }


    /**
     * Retrieves the first Item object satisfying the query conditions.
     *
     * @return An Item object.
     * @throws IOException IOException
     */
    public Item getObject() throws IOException {
        List<Item> result = this.fetcher.fetchObjects(this);
        if (!result.isEmpty()) return result.get(0);
        return null;
    }


}
