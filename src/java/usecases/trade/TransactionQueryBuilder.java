package usecases.trade;

import entities.*;
import usecases.query.AbstractQueryBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Builds a transaction query. This query builder allows the user to chain filters together easily.
 */
public class TransactionQueryBuilder extends AbstractQueryBuilder {

    /**
     * Fetcher used to Executes a TransactionQuery
     */
    private final TransactionFetcher fetcher;

    /**
     * Initializes this class by placing all known filters into existing categories.
     *
     * @param fetcher TransactionFetcher
     */
    public TransactionQueryBuilder(TransactionFetcher fetcher) {
        this.fetcher = fetcher;
        this.integerFilters.put("findById", null);
        this.integerFilters.put("involvesUser", null);
        this.integerFilters.put("involvesItem", null);

        this.booleanFilters.put("onGoing", null);
        this.booleanFilters.put("isComplete", null);
        this.booleanFilters.put("isIncomplete", null);
        this.booleanFilters.put("isExpected", null);
        this.dateFilters.put("after", null);
    }


/********************************************************************************************************
 *
 * All the Integer Filters
 *
 *********************************************************************************************************/

    /**
     * Set "findById" in integerFilters to transactionId
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder findById(int transactionId) {
        this.integerFilters.put("findById", transactionId);
        return this;
    }

    /**
     * Set "involvesUser" in integerFilters to userId
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder involvesUser(int userId) {
        this.integerFilters.put("involvesUser", userId);
        return this;
    }

    /**
     * Set "involvesUserAsBorrower" in integerFilters to userId
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder involvesUserAsBorrower(int userId) {
        this.integerFilters.put("involvesUserAsBorrower", userId);
        return this;
    }

    /**
     * Set "involvesUserAsLender" in integerFilters to userId
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder involvesUserAsLender(int userId) {
        this.integerFilters.put("involvesUserAsLender", userId);
        return this;
    }


    /**
     * Set "involvesItem" in integerFilters to tradeId
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder involvesItem(int itemId) {
        this.integerFilters.put("involvesItem", itemId);
        return this;
    }


/********************************************************************************************************
 *
 * All the Boolean Filters
 *
 *********************************************************************************************************/

    /**
     * Set "onGoing" in booleanFilters to true and set "isComplete" to false
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder isOpen() {
        this.booleanFilters.put("onGoing", true);
        return this;
    }

    /**
     * Set "isComplete" in booleanFilters to true
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder isComplete() {
        this.booleanFilters.put("isComplete", true);
        return this;
    }

    /**
     * Set "isIncomplete" in booleanFilters to true
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder isIncomplete() {
        this.booleanFilters.put("isIncomplete", true);
        return this;
    }

    /**
     * Set "isExpected" in booleanFilters to true
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder isExpected() {
        this.booleanFilters.put("isExpected", true);
        return this;
    }



/********************************************************************************************************
 *
 * All the Date Filters
 *
 ********************************************************************************************************/

    /**
     * Set "after" in dateFilter date
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder after(LocalDate date) {
        this.dateFilters.put("after", date);
        return this;
    }


/********************************************************************************************************
 *
 * Gets relations
 *
 *********************************************************************************************************/

    /**
     * Get a transaction
     *
     * @return Transaction
     * @throws IOException An IOException
     */
    public Transaction getTransaction() throws IOException {
        return this.fetcher.getTransaction(this);
    }

    /**
     * Get a list of transactions
     *
     * @return list of transaction
     * @throws IOException An IOException
     */
    public List<Transaction> getTransactions() throws IOException {
        return this.fetcher.getTransactions(this);
    }

    /**
     * Get a map with transaction ids and a list of its meetings
     *
     * @return a map with transaction id as key a list of its meeting as value
     * @throws IOException An IOException
     */
    public Map<Integer, List<Meeting>> getMeetings() throws IOException {
        return this.fetcher.getMeetings(this);
    }

    /**
     * Get a list of meetings
     *
     * @return A list of meetings
     * @throws IOException An IOException
     */
    public List<Meeting> getMeetingsList() throws IOException {
        return this.fetcher.getMeetingsList(this);
    }

    /**
     * Get a map with transaction id with a list of its trades
     *
     * @return a map with transaction id as key and a list of its trades as value
     * @throws IOException An IOException
     */
    public Map<Integer, List<Trade>> getTrades() throws IOException {
        return this.fetcher.getTrades(this);
    }

    /**
     * Get a list of trades.
     *
     * @return A list of trades
     * @throws IOException An IOException
     */
    public List<Trade> getTradesList() throws IOException {
        return this.fetcher.getTradesList(this);
    }

    /**
     * Get a map with trade id as key and borrower as value
     *
     * @return a map with trade id as key and borrower as value
     * @throws IOException An IOException
     */
    public Map<Integer, User> getBorrowers() throws IOException {
        return this.fetcher.getBorrowers(this);
    }

    /**
     * Get a map with trade id as key and borrower id as value
     *
     * @return a map with trade id as key and borrower id as value
     * @throws IOException An IOException
     */
    public Map<Integer, Integer> getBorrowerIds() throws IOException {
        return this.fetcher.getBorrowerIds(this);
    }

    /**
     * Get a map with trade id as key and lender as value
     *
     * @return a map with trade id as key and lender as value
     * @throws IOException An IOException
     */
    public Map<Integer, User> getLenders() throws IOException {
        return this.fetcher.getLenders(this);
    }

    /**
     * Get a map with trade id as key and lender id as value
     *
     * @return a map with trade id as key and lender id as value
     * @throws IOException An IOException
     */
    public Map<Integer, Integer> getLenderIds() throws IOException {
        return this.fetcher.getLenderIds(this);
    }

    /**
     * Get a map with trade id as key and its list of items as value
     *
     * @return a map with trade id as key and list of items as value
     * @throws IOException An IOException
     */
    public Map<Integer, List<Item>> getItems() throws IOException {
        return this.fetcher.getItems(this);
    }


}
