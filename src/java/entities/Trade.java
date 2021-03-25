package entities;

import persistence.relations.HasRelations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents a trade
 * The trade include a list of items, the id of lender and borrower, and whether it is completed or not
 * this trade can be considered as temporary or permanent
 *
 * @version July 02, 2020
 */

public class Trade extends AbstractBaseEntity implements HasRelations {
    /**
     * The id of lender
     */
    private int lenderId;

    /**
     * The id of borrower
     */
    private int borrowerId;

    /**
     * The list of items in this trade
     */
    private List<Integer> itemList;

    /**
     * Whether this trade complete or not
     */
    private boolean complete;

    /**
     * The id of this trade
     */
    private int tradeId;

    /**
     * Whether this is a cash sale
     */
    private boolean sell;


    /**
     * Constructor for trade without input trade id
     * Create a trade with input information with duration
     *
     * @param lenderId   the id of lender
     * @param borrowerId the id of the borrower
     * @param itemList   the item list of this trade
     */
    public Trade(int lenderId, int borrowerId, List<Integer> itemList) {
        this.tradeId = 0;
        this.lenderId = lenderId;
        this.borrowerId = borrowerId;
        this.itemList = itemList;
        this.complete = false;
        this.sell = false;
    }


    /**
     * get the id of this trade
     *
     * @return the id of this trade
     */
    public int getTradeId() {
        return tradeId;
    }

    /**
     * Whether it is complete or not
     *
     * @return whether this trade is complete or not
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Get borrower's id
     *
     * @return the id of the borrower
     */
    public int getBorrowerId() {
        return borrowerId;
    }

    /**
     * Get lender's id
     *
     * @return the id of the lender
     */
    public int getLenderId() {
        return lenderId;
    }

    /**
     * Get the list of items
     *
     * @return the list of all items in this trade
     */
    public List<Integer> getItemList() {
        return itemList;
    }

    /**
     * change the complete status
     *
     * @param status set the status true or boolean
     */
    public void setComplete(boolean status) {
        this.complete = status;
    }


    /**
     * Set whether this is a cash sale
     *
     * @param sell whether this is a cash sale
     */
    public void setSell(boolean sell) {
        this.sell = sell;
    }

    /**
     * Whether this is a cash sale
     *
     * @return
     */
    public boolean getSell() {
        return this.sell;
    }


    /**
     * A method to get the key to this trade, which is the tradeId
     *
     * @return the id of this trade
     */
    @Override
    public int getKey() {
        return tradeId;
    }


    /**
     * set a new tradeId
     *
     * @param id the new id of trade
     */
    @Override
    public void setKey(int id) {
        this.tradeId = id;
    }


    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "Trade{" +
                "lenderId=" + lenderId +
                ", borrowerId=" + borrowerId +
                ", itemList=" + itemList +
                ", complete=" + complete +
                ", tradeId=" + tradeId +
                '}';
    }


    /**
     * Get a many to many relationship between items and trade
     *
     * @return A map defining the relations
     */
    @Override
    public Map<String, List<Integer>> getDefinedRelations() {
        Map<String, List<Integer>> relationMap = new HashMap<>();
        relationMap.put("items", this.itemList);
        return relationMap;
    }

}

