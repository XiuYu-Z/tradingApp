package entities;

import persistence.relations.HasRelations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents an item
 */

public class Item extends AbstractBaseEntity implements HasRelations {

    /**
     * The id of this item which is unique
     */
    private int id;

    /**
     * The name of this item
     */
    private String name;

    /**
     * Its owner's id
     */
    private int ownerId;

    /**
     * The id of person who hold it now (do not mean he own this item)
     */
    private int holderId;

    /**
     * The description of this item
     */
    private String description;

    /**
     * Whether the item is visible
     */
    private boolean visibility;

    /**
     * Whether the item is soft deleted or not
     */
    private boolean softDelete;

    /**
     * Whether the item is reserved or not
     */
    private boolean isReserved;

    /**
     * The price/value of the item.
     */
    private int price;

    /**
     * Whether the item is for sale
     */
    private boolean forSale;


    /**
     * Constructor without input id
     * Create an item with input information,
     * and set visibility to false at beginning
     * the default id is 0
     *
     * @param name        the input String of item's name
     * @param description the description of this item
     * @param owner       the integer as owner id
     */
    public Item(String name, String description, int owner, int price, boolean forSale) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.ownerId = owner;
        this.holderId = owner;
        this.visibility = false;
        this.softDelete = false;
        this.isReserved = false;
        this.price = price;
        this.forSale = forSale;
    }


    /**
     * The methods to get the holder's id
     *
     * @return the holder's id
     */
    public int getHolderId() {
        return holderId;
    }


    /**
     * The methods to get the owner's id
     *
     * @return the owner's id
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * The methods to get whether this item is visible or not
     *
     * @return boolean whether this item is visible or not
     */
    public boolean isVisible() {
        return visibility;
    }

    /**
     * The methods to get this item's description
     *
     * @return the item's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * The methods to get this item's name
     *
     * @return the item's name
     */
    public String getName() {
        return name;
    }

    /**
     * The methods to set new description to this item
     *
     * @param description the description for item
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The methods to set new id to holder
     * if first input attribute is equal to the holder's id then save as second input attribute
     *
     * @param user1 the first input id
     * @param user2 the second input id
     */
    public void changeHolderId(int user1, int user2) {
        if (user1 == holderId) {
            this.holderId = user2;
        } else {
            this.holderId = user1;
        }

    }

    /**
     * The methods to set new id to holder directly
     *
     * @param holder the first input id
     */
    public void setHolderId(int holder) {
        this.holderId = holder;
    }

    /**
     * The methods to set new id to holder directly
     *
     * @param owner the first input id
     */
    public void setOwnerId(int owner) {
        this.ownerId = owner;
    }

    /**
     * The methods to set new id to owner
     * if first input attribute is equal to the owner's id then save as second input attribute
     *
     * @param user1 the first input id
     * @param user2 the second input id
     */
    public void changeOwnerId(int user1, int user2) {
        if (user1 == ownerId) {
            this.ownerId = user2;
        } else {
            this.ownerId = user1;
        }
    }

    /**
     * The methods to set new name to item
     *
     * @param name new name for item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The methods to set new visibility to item
     *
     * @param visibility boolean for item
     */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * set the attribute to soft delete
     *
     * @param softDelete set new boolean to softDelete
     */
    public void setSoftDelete(boolean softDelete) {
        this.softDelete = softDelete;
    }

    /**
     * get whether this item is soft deleted or not
     *
     * @return whether this item is soft deleted or not
     */
    public boolean isSoftDeleted() {
        return this.softDelete;
    }

    /**
     * Whether the item is reserved or not.
     *
     * @return Whether this item is reserved or not.
     */
    public boolean isReserved() {
        return isReserved;
    }

    /**
     * Set new boolean to Reserved
     *
     * @param isReserved set reserved to false or true
     */
    public void setReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    /**
     * Check if item is for sale.
     *
     * @return Whether the item is for sale
     */
    public boolean isForSale() {
        return forSale;
    }


    /**
     * Returns the price/value of this item
     *
     * @return The price
     */
    public int getPrice() {
        return price;
    }


    /**
     * A method to get the key to this item, which is the id
     *
     * @return the id of item
     */
    @Override
    public int getKey() {
        return id;
    }

    /**
     * A method to change the id of this item
     *
     * @param id the new id of item
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
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                ", holderId=" + holderId +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                ", softDelete=" + softDelete +
                ", isReserved=" + isReserved +
                ", price=" + price +
                ", forSell=" + forSale +
                '}';
    }


    /**
     * Get relation of item with other entities
     *
     * @return A hashmap indicating the number of relations defined in this entity.
     */
    @Override
    public Map<String, List<Integer>> getDefinedRelations() {
        return new HashMap<>();
    }


}
