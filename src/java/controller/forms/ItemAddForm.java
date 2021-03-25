package controller.forms;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

public class ItemAddForm {

    @NotBlank(message = "Item name should not be empty")
    private String itemName;

    @NotBlank(message = "Item description should not be empty")
    private String itemDescription;

    @Range(min = 0, message = "Please enter a positive integer")
    private int price;

    private boolean forSale;

    @NotBlank(message = "Tagging your item is non-optional. Please include at least one tag.")
    private String tags;

    /**
     * Returns the item name.
     *
     * @return the item name.
     */
    public String getItemName() {
        return this.itemName;
    }

    /**
     * Sets the item name.
     *
     * @param itemName the item name
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Gets the item description.
     *
     * @return the item description.
     */
    public String getItemDescription() {
        return this.itemDescription;
    }

    /**
     * Sets the item description.
     *
     * @param itemDescription the item description.
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    /**
     * Gets the tags associated with this item.
     *
     * @return the tags associated with this item.
     */
    public String getTags() {
        return this.tags;
    }

    /**
     * Sets the tags associated with this item.
     *
     * @param tags the tags associated with this item.
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Gets the price of this item.
     *
     * @return The price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of this item.
     *
     * @param price The price
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets whether this item is for sale.
     *
     * @return whether this item is for sale.
     */
    public boolean getForSale() {
        return forSale;
    }

    /**
     * Sets whether this item is for sale.
     *
     * @param forSale whether this item is for sale.
     */
    public void setForSale(boolean forSale) {
        this.forSale = forSale;
    }


}
