package usecases.items;

import entities.Item;
import entities.Tag;
import entities.User;
import entities.WishList;
import persistence.PersistenceInterface;
import persistence.relations.MapsRelations;
import usecases.items.exceptions.ItemNotFoundException;
import usecases.query.AbstractFetcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Executes a ItemQuery and returns the results in the specified format of the query.
 * Can execute a number of pre-defined filters, such as whether the item is approve or not etc.
 * Can return a list of ids, a list of item names, or a list of Item objects.
 */
public class ItemFetcher extends AbstractFetcher {

    /**
     * Class dependencies
     */
    PersistenceInterface gateway;
    MapsRelations mapsRelation;

    /**
     * Initializes this class.
     *
     * @param gateway PersistenceInterface
     */
    public ItemFetcher(PersistenceInterface gateway, MapsRelations mapsRelation) {
        this.gateway = gateway;
        this.mapsRelation = mapsRelation;
    }

    /**
     * Stores the result of the current fetch.
     */
    private List<Item> allItems = new ArrayList<Item>();
    private List<Item> currentList = new ArrayList<Item>();
    private List<WishList> wishList = new ArrayList<WishList>();


/********************************************************************************************************
 *
 * Public interface of the item fetcher.
 *
 *********************************************************************************************************/

    /**
     * Builds a new query.
     *
     * @return ItemQueryBuilder
     */
    public ItemQueryBuilder query() {
        return new ItemQueryBuilder(this);
    }


    /**
     * Gets an item by its unique id.
     *
     * @return Item
     * @throws IOException           An IOException
     * @throws ItemNotFoundException An exception indicating this item id doest not exist.
     */
    public Item findById(int itemId) throws IOException, ItemNotFoundException {
        List<Item> items = gateway.get(Collections.singletonList(itemId), Item.class);
        if (items.isEmpty()) throw new ItemNotFoundException();
        return items.get(0);
    }


    /**
     * Returns a list of item ids.
     *
     * @param query ItemQueryBuilder
     * @return Returns a list of item ids.
     * @throws IOException IOException
     */
    public List<Integer> fetchIds(ItemQueryBuilder query) throws IOException {
        this.fetchObjects(query);
        List<Integer> ids = new ArrayList<Integer>();
        for (Item item : this.currentList) {
            ids.add(item.getKey());
        }
        return ids;
    }


    /**
     * Returns a list of item names.
     *
     * @param query ItemQueryBuilder
     * @return Returns a list of item ids.
     * @throws IOException IOException
     */
    public List<String> fetchNames(ItemQueryBuilder query) throws IOException {
        this.fetchObjects(query);
        List<String> names = new ArrayList<String>();
        for (Item item : this.currentList) {
            names.add(item.getName());
        }
        return names;
    }


    /**
     * Returns a list of item objects.
     *
     * @param query ItemQueryBuilder
     * @return Returns a list of item objects.
     * @throws IOException IOException
     */
    public List<Item> fetchObjects(ItemQueryBuilder query) throws IOException {
        this.fetch(query);
        return this.currentList;
    }


/********************************************************************************************************
 *
 * Common queries (syntactic sugar)
 *
 *********************************************************************************************************/

    /**
     * Filter the suggested items belonging to this user which are in the other user's wishlist
     *
     * @param thisUser  The user who the items belong to
     * @param otherUser The user whose wishlist we are looking through
     * @return the query include all suggested items
     */
    public ItemQueryBuilder recommendedItems(int thisUser, int otherUser) {
        return this.query().onlyApproved().notDeleted()
                .onlyOwnedBy(thisUser).inWishlistOf(otherUser)
                .heldByOwner().ownedByUnfrozenUser().ownedByUnVacationUser().unreserved();
    }


    /**
     * Filter the suggested items belonging to this user which are not in the other user's wishlist
     *
     * @param thisUser  The user who the items belong to
     * @param otherUser The user whose wishlist we are looking through
     * @return the query include all suggested items
     */
    public ItemQueryBuilder notRecommendedItems(int thisUser, int otherUser) {
        return this.query().onlyApproved().notDeleted()
                .onlyOwnedBy(thisUser).notInWishlistOf(otherUser)
                .heldByOwner().ownedByUnfrozenUser().ownedByUnVacationUser().unreserved();
    }

    /**
     * Filter browsable items
     *
     * @return the query includes all browsable items
     */
    public ItemQueryBuilder browsableItems() {
        return this.query().onlyApproved().notDeleted().heldByOwner().ownedByUnfrozenUser().ownedByUnVacationUser().unreserved();
    }

    /**
     * Filter items that should be shown in my wishlist
     *
     * @param userId the userId of the user whose wishlist we are examining
     * @return the query include all user's wish lists
     */
    public ItemQueryBuilder viewMyWishlist(int userId) {
        return this.query().onlyApproved().notDeleted().exceptOwnedBy(userId).inWishlistOf(userId)
                .heldByOwner().ownedByUnfrozenUser().ownedByUnVacationUser().unreserved();
    }

    /**
     * filter the item that belongs to the user
     *
     * @param userId the userId of the user whose wishlist we are examining
     * @return the query includes all user's items
     */
    public ItemQueryBuilder allMyItems(int userId) {
        return this.query().notDeleted().onlyOwnedBy(userId).heldByOwner();
    }


/********************************************************************************************************
 *
 * All the List<String> Filters Available
 *
 *********************************************************************************************************/

    /**
     * Filter the list of items and leave the items with the Tags associated with them
     *
     * @param tagNames A list of tag names.
     * @throws IOException An IOException
     */
    public void isTaggedWithAll(List<String> tagNames) throws IOException {
        for (String tagName : tagNames) {
            this.isTaggedWith(tagName);
        }
    }

    /**
     * Filters based on one tag.
     *
     * @param tagName The string representation of a tag
     * @throws IOException An IOException
     */
    public void isTaggedWith(String tagName) throws IOException {
        List<Item> filtered = new ArrayList<>();
        for (Item item : this.currentList) {
            List<Tag> tags = item.relation(this.mapsRelation, "tags", Tag.class);
            boolean include = false;
            for (Tag tag : tags) {
                if (tag.getTagName().equals(tagName)) include = true;
            }
            if (include) filtered.add(item);
        }
        this.currentList = filtered;
    }


/********************************************************************************************************
 *
 * All the Integer Filters Available
 *
 *********************************************************************************************************/
    /**
     * Get the item by item id
     *
     * @param itemId The unique id of the item
     */
    public void findById(Integer itemId) {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getKey() == itemId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get the item that belongs to user from the same home city
     *
     * @param userId The unique id of the user
     * @throws IOException An IOException
     */
    public void findByHomeCity(Integer userId) throws IOException {
        List<Item> filtered = new ArrayList<>();
        String homeCiy = getOwner(userId).getHomeCity();
        for (Item item : this.currentList) {
            if (getOwner(item.getOwnerId()).getHomeCity().equals(homeCiy)) {
                filtered.add(item);
            }
        }
        this.currentList = filtered;
    }

    /**
     * Get the item not owned by the user
     *
     * @param userId The unique id of the user
     */
    public void exceptOwnedBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getOwnerId() != userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get the item only owned by the user
     *
     * @param userId The unique id of the user
     */
    public void onlyOwnedBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getOwnerId() == userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get the item that are not held by the user
     *
     * @param userId The unique id of the user
     */
    public void exceptHeldBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getHolderId() != userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get the item held by the user
     *
     * @param userId The unique id of the user
     */
    public void onlyHeldBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getHolderId() == userId) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get the item sold by the user
     *
     * @param userId The unique id of the user
     */
    public void sellBy(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getOwnerId() == userId && item.getHolderId() == userId && item.isForSale()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get the items in the wishlist of the user
     *
     * @param userId The unique id of the user
     */
    public void inWishlistOf(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        WishList wishList = this.getWishlistOf(userId);
        if (wishList != null) {
            for (Item item : this.currentList) {
                if (wishList.getWishList().contains(item.getKey())) filtered.add(item);
            }
        }
        this.currentList = filtered;
    }

    /**
     * Get the item that are not in the wishlist of the user
     *
     * @param userId The unique id of the user
     */
    public void notInWishlistOf(Integer userId) {
        List<Item> filtered = new ArrayList<Item>();
        WishList wishList = this.getWishlistOf(userId);
        //If there is a wishlist, we must only add items not in the wishlist
        if (wishList != null) {
            for (Item item : this.currentList) {
                if (!wishList.getWishList().contains(item.getKey())) filtered.add(item);
            }
            this.currentList = filtered;
        }
        //Otherwise, we do nothing and no filtering has taken place
    }


/********************************************************************************************************
 *
 * All the Boolean Filters Available
 *
 *********************************************************************************************************/

    /**
     * Get the items that are held by their owner
     */
    public void heldByOwner() {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.getOwnerId() == item.getHolderId()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are approved
     */
    public void onlyApproved() {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (item.isVisible()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are not visible to users
     */
    public void exceptApproved() {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            if (!item.isVisible()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are owned by unfrozen user
     *
     * @throws IOException An IOException
     */
    public void ownedByUnfrozenUser() throws IOException {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            User user = this.getOwner(item.getOwnerId());
            if (user != null && !user.getStatus().equals("frozen")) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are owned by user not on vacation
     *
     * @throws IOException An IOException
     */
    public void ownedByUnVacationUser() throws IOException {
        List<Item> filtered = new ArrayList<Item>();
        for (Item item : this.currentList) {
            User user = this.getOwner(item.getOwnerId());
            if (user != null && !user.getStatus().equals("vacation")) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are deleted
     */
    public void onlyDeleted() {
        List<Item> filtered = new ArrayList<>();
        for (Item item : this.currentList) {
            if (item.isSoftDeleted()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are not deleted
     */
    public void notDeleted() {
        List<Item> filtered = new ArrayList<>();
        for (Item item : this.currentList) {
            if (!item.isSoftDeleted()) filtered.add(item);
        }
        this.currentList = filtered;
    }

    /**
     * Get only the items that are for sale
     */
    public void forSell() {
        List<Item> filtered = new ArrayList<>();
        for (Item item : this.currentList) {
            if (item.isForSale()) filtered.add(item);
        }
        this.currentList = filtered;
    }


    /**
     * Get only the items that are not reserved
     */
    public void unreserved() {
        List<Item> filtered = new ArrayList<>();
        for (Item item : this.currentList) {
            if (!item.isReserved()) filtered.add(item);
        }
        this.currentList = filtered;
    }


/********************************************************************************************************
 *
 * Helper Methods
 *
 *********************************************************************************************************/

    /**
     * reset the all lists this class saved
     */
    protected void reset() {
        this.allItems = new ArrayList<Item>();
        this.currentList = new ArrayList<Item>();
        this.wishList = new ArrayList<WishList>();
    }

    /**
     * Get all Item Objects and save them into the lists this class had
     */
    protected void all() throws IOException {
        this.allItems = gateway.all(Item.class);
        this.currentList.addAll(this.allItems);
        this.wishList = gateway.all(WishList.class);
    }

    /**
     * Get the owner by inputting the owner's it
     *
     * @param userId id of user
     * @return User Object whose id is userId
     * @throws IOException An IOException
     */
    private User getOwner(int userId) throws IOException {
        List<User> users = gateway.get(new ArrayList<>(Collections.singletonList(userId)), User.class);
        if (!users.isEmpty()) return users.get(0);
        return null;
    }

    /**
     * Get the wishlist by inputting userId
     *
     * @param userId id of user
     * @return Wishlist Object whose userId is userId
     */
    private WishList getWishlistOf(Integer userId) {
        for (WishList wishList : this.wishList) {
            if (wishList.getOwnerId() == userId) return wishList;
        }
        return null;
    }


}
