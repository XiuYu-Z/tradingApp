package usecases.trade;

import entities.Trade;
import persistence.PersistenceInterface;
import persistence.exceptions.PersistenceException;
import usecases.trade.exceptions.TooManyItemListsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A use case for producing Trade object only, which makes use of the decoration pattern.
 */
public class TradeFactory {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * a list including lists of integers
     */
    private List<List<Integer>> items;

    /**
     * id of lender
     */
    private int lenderId;

    /**
     * id of borrower
     */
    private int borrowerId;

    /**
     * Whether this trade is two-way
     */
    private boolean twoWay;

    /**
     * Whether this trade is for sale or not
     */
    private boolean sell;


    /**
     * To create an instance of TradeFactory
     *
     * @param gateway to acess the stored data
     */
    public TradeFactory(PersistenceInterface gateway) {
        lenderId = -1;
        borrowerId = -1;
        items = new ArrayList<>();
        twoWay = false;
        this.gateway = gateway;
        sell = false;
    }


    /**
     * To get the ids of the instantiated trades, then the TradeFactory resets.
     *
     * @return a list of ids of created trades
     * @throws IOException          if there is an IO error
     * @throws PersistenceException if there is an exception thrown during persisting to database.
     */
    public List<Integer> getTradeId() throws IOException, PersistenceException {
        List<Integer> ids = new ArrayList<>();
        for (Trade trade : init()) {
            ids.add(trade.getTradeId());
        }
        reset();
        return ids;
    }

    /**
     * To get the instantiated trades, then the TradeFactory resets.
     *
     * @return a list of created meetings
     * @throws IOException          if there is an IO error
     * @throws PersistenceException if there is an exception thrown during persisting to database.
     */
    public List<Trade> getTrade() throws IOException, PersistenceException {
        List<Trade> trades = init();
        reset();
        return trades;
    }


    /**
     * Change the transaction to a two way transaction
     *
     * @return the TradeFactory itself
     */
    public TradeFactory twoWay() {
        this.twoWay = true;
        return this;
    }


    /**
     * Change the transaction to a one way transaction
     *
     * @return the TradeFactory itself
     */
    public TradeFactory oneWay() {
        this.twoWay = false;
        return this;
    }

    /**
     * Change the transaction to a sale
     *
     * @return the TradeFactory itself
     */
    public TradeFactory sell() {
        this.sell = true;
        this.oneWay();
        return this;
    }


    /**
     * Fill id of lender into the first meeting in the list which does not have a location
     *
     * @param lenderId of the trade
     * @return the TradeFactory itself
     */
    public TradeFactory fillLenderId(int lenderId) {
        this.lenderId = lenderId;
        return this;
    }


    /**
     * Fill id of borrower into the first meeting in the list which does not have a location
     *
     * @param borrowerId of the trade
     * @return the TradeFactory itself
     */
    public TradeFactory fillBorrowId(int borrowerId) {
        this.borrowerId = borrowerId;
        return this;
    }

    /**
     * Fill list of items into the first trade in the list which does not have a list of items
     *
     * @param itemList of the trade
     * @return the TradeFactory itself
     * @throws TooManyItemListsException when trying to add extra location
     */
    public TradeFactory fillItems(List<Integer> itemList) throws TooManyItemListsException {
        if (this.items.size() >= 1 && !this.twoWay) {
            throw new TooManyItemListsException();
        }
        this.items.add(itemList);
        return this;
    }


    /**
     * Allows other classes to reset the trade factory.
     *
     * @return the TradeFactory itself
     */
    public TradeFactory reset() {
        lenderId = -1;
        borrowerId = -1;
        items = new ArrayList<List<Integer>>();
        return this;
    }


/********************************************************************************************************
 *
 * Helper Methods
 *
 *********************************************************************************************************/

    /**
     * A private method used as helper function, which creates trades according to the attributes stored in the trade
     * factory.
     *
     * @return list of trades
     * @throws IOException An IOException
     */
    private List<Trade> init() throws IOException {
        List<Trade> arr = new ArrayList<>();
        Trade firstTrade = new Trade(lenderId, borrowerId, items.get(0));
        firstTrade.setSell(this.sell);
        arr.add(firstTrade);
        if (twoWay) {
            Trade secondTrade = new Trade(borrowerId, lenderId, items.get(1));
            arr.add(secondTrade);
        }
        return gateway.create(arr, Trade.class);
    }

}
