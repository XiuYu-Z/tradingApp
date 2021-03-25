package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents the wishlist
 * Every user has a wish list of items they want
 * and people can check the wishlist and make the trade with this user
 *
 * @version July 02, 2020
 */


public class WishList extends AbstractBaseEntity {
    /**
     * The list of item in this wishList, stored as item name
     */
    private List<Integer> wishList;

    /**
     * The id of this item
     */
    private int id;

    /**
     * the id of the owner of this item
     */
    private int ownerId;


    /**
     * Default constructor with input id
     * Create an user's wish list with an input id
     *
     * @param ownerId the id of the owner
     */
    public WishList(int ownerId) {
        this.ownerId = ownerId;
        this.wishList = new ArrayList<>();
        this.id = 0;
    }


    /**
     * Get the wish list
     *
     * @return the wish List
     */
    public List<Integer> getWishList() {
        return wishList;
    }

    /**
     * add a new item to wish list
     *
     * @param itemId add new item id into wishList
     */
    public void addWishList(int itemId) {
        wishList.add(itemId);
    }

    /**
     * remove an item from wish list
     *
     * @param itemId the id of the item
     */
    public void removeWishList(int itemId) {
        List<Integer> newList = new ArrayList<>();
        for (int i : wishList) {
            if (i != itemId) {
                newList.add(i);
            }
        }
        wishList = newList;
    }

    /**
     * return the id of owner
     *
     * @return the id of the owner
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Return the key of this wishlist, which is id
     *
     * @return the id of this wishlist
     */
    @Override
    public int getKey() {
        return id;
    }

    /**
     * Set a new id
     *
     * @param id the new id to this wishlist
     */
    @Override
    public void setKey(int id) {
        this.id = id;
    }


    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "WishList{" +
                "wishList=" + wishList +
                ", id=" + id +
                ", ownerId=" + ownerId +
                '}';
    }

}
