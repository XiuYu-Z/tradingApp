package entities;

import persistence.relations.HasRelations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that represents a Transaction, including a list of trades
 * All transaction can be consider as oneWay transaction or twoWay transaction
 * The different is that oneWay Transaction only has one lender and one borrower, but twoWay Transaction has two people
 * exchange there items
 *
 * @version July 2, 2020
 */
public class Transaction extends AbstractBaseEntity implements HasRelations {
    /**
     * The id of this Transaction
     */
    private int transId;

    /**
     * Whether this transaction is one way or two way
     */
    private boolean oneWay;

    /**
     * the list of trade id in this transaction
     */
    private List<Integer> tradeList;


    /**
     * the list of meeting id in this transaction
     */
    private List<Integer> meetingList;


    /**
     * Default constructor without input id
     * Create a new transaction with trade list, user list, and whether it is one way or two way
     * set the default id to 0
     *
     * @param tradeList   the list of trade
     * @param meetingList the list of meeting
     */
    public Transaction(List<Integer> tradeList, List<Integer> meetingList) {
        this.transId = 0;
        this.tradeList = tradeList;
        this.meetingList = meetingList;
        if (tradeList.size() == 1) {
            this.oneWay = true;
        } else {
            this.oneWay = false;
        }
    }


    /**
     * Get the list of all trades in this transaction
     *
     * @return the list of trades
     */
    public List<Integer> getTradeList() {
        return tradeList;
    }

    /**
     * Get the list of all user id in this transaction
     *
     * @return the list of meeting
     */
    public List<Integer> getMeetingList() {
        return meetingList;
    }

    /**
     * A method to get the key to this Transaction, which is the transId
     *
     * @return the id of this transaction
     */
    @Override
    public int getKey() {
        return this.transId;
    }

    /**
     * set a new transId
     *
     * @param id the new id of transaction
     */
    @Override
    public void setKey(int id) {
        this.transId = id;
    }


    /**
     * return all names of attributes in this class with the values
     *
     * @return the names of attributes in this class with the values
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "transId=" + transId +
                ", oneWay=" + oneWay +
                ", tradeList=" + tradeList +
                ", meetingList=" + meetingList +
                '}';
    }


    /**
     * get a many to many relation between Transaction to trade and meetings
     *
     * @return A map defining the relations
     */
    @Override
    public Map<String, List<Integer>> getDefinedRelations() {
        Map<String, List<Integer>> relationMap = new HashMap<>();
        relationMap.put("trades", this.tradeList);
        relationMap.put("meetings", this.meetingList);
        return relationMap;
    }

}
