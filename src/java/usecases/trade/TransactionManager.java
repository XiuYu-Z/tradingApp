package usecases.trade;

import entities.Item;
import entities.Meeting;
import entities.Trade;
import entities.Transaction;
import persistence.PersistenceInterface;
import persistence.relations.MapsRelations;
import usecases.meeting.MeetingFactory;
import usecases.meeting.exceptions.TooManyLocationsException;
import usecases.meeting.exceptions.TooManyTimesException;
import usecases.trade.exceptions.TooManyItemListsException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * A use case for Transaction, which deals with all operations relating to Transaction including instantiation.
 */
public class TransactionManager {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final MapsRelations relationMapper;

    /**
     * Fetcher of transaction
     */
    private final TransactionFetcher transactionFetcher;

    /**
     * Factory of trade
     */
    private final TradeFactory tradeFactory;

    /**
     * Factory of meeting
     */
    private final MeetingFactory meetingFactory;

    /**
     * instantiate an instance of TransactionManager
     *
     * @param gateway to access data stored in file
     */
    public TransactionManager(PersistenceInterface gateway, TransactionFetcher transactionFetcher,
                              MapsRelations relationMapper, TradeFactory tradeFactory, MeetingFactory meetingFactory) {

        this.gateway = gateway;
        this.transactionFetcher = transactionFetcher;
        this.relationMapper = relationMapper;
        this.tradeFactory = tradeFactory;
        this.meetingFactory = meetingFactory;
    }

    /**
     * Return a list of frequent partners of a user.
     *
     * @param userId Unique id of a user.
     * @return a list of frequent Partner of a user with userId
     * @throws IOException An IOException
     */
    public List<List<Integer>> frequentPartners(int userId) throws IOException {

        Collection<Integer> lenderIds = this.transactionFetcher.query().involvesUser(userId).getLenderIds().values();
        Collection<Integer> borrowerIds = this.transactionFetcher.query().involvesUser(userId).getBorrowerIds().values();
        List<Integer> results = new ArrayList<>();
        results.addAll(lenderIds);
        results.addAll(borrowerIds);

        Map<Integer, Integer> tradeCount = new HashMap<>();

        for (Integer counterPartyId : results) {
            if (counterPartyId != userId) {
                tradeCount.put(counterPartyId, Collections.frequency(results, counterPartyId));
            }
        }
        return this.sort(tradeCount);
    }

    /**
     * Return a list of most traded items
     *
     * @return a list of most traded items
     * @throws IOException An IOException
     */
    public List<List<Integer>> mostTradedItems() throws IOException {
        List<Trade> trades = this.transactionFetcher.query().isComplete().getTradesList();
        Map<Integer, Integer> itemsCount = new HashMap<>();
        List<Integer> items = new ArrayList<>();
        for (Trade trade : trades) {
            items.addAll(trade.getItemList());
        }
        for (Integer itemId : items) {
            itemsCount.put(itemId, Collections.frequency(items, itemId));
        }
        return this.sort(itemsCount);
    }


    /**
     * Build a transaction with input information
     *
     * @param borrowerId       id of borrower
     * @param lenderId         id of lender
     * @param borrowedItemId   id of borrower's item
     * @param lendItemId       id of lender's item
     * @param tradeType        type of trade
     * @param tradeDuration    the duration of trade
     * @param meetingDate      the date of first meeting
     * @param meetingLocation1 first location of meeting
     * @param meetingLocation2 second location of meeting
     */
    public int buildTransaction(int borrowerId, int lenderId, int borrowedItemId, int lendItemId, String tradeType,
                                String tradeDuration, LocalDate meetingDate,
                                String meetingLocation1, String meetingLocation2)
            throws TooManyItemListsException, IOException, TooManyLocationsException, TooManyTimesException {

        this.setupTrades(borrowerId, lenderId, borrowedItemId, lendItemId, tradeType);
        this.setupMeetings(tradeDuration, meetingDate, meetingLocation1, meetingLocation2, borrowerId);

        return initiateTransaction(tradeFactory.getTradeId(), meetingFactory.getMeetingId());
    }


    /**
     * To update the system that an exchange of item has taken place in real life as planned in a meeting
     *
     * @param meetingId of the meeting
     * @throws IOException if there is an IO error
     */
    public void performMeeting(int meetingId) throws IOException {

        // Find the transaction that the meeting of the meeting id is in
        Meeting meeting = gateway.get(meetingId, Meeting.class);
        Transaction transaction = meeting.relation(relationMapper, "transactions", Transaction.class).get(0);
        List<Meeting> meetings = gateway.get(transaction.getMeetingList(), Meeting.class);

        //handle permanent transaction, which only has one meeting
        if (meetings.size() < 2 && meetingClear(meetings.get(0))) {
            finishTransaction(transaction.getKey());
        }
        //handle temporary transaction which has two meetings
        else if ((meetingClear(meetings.get(0)) && !meetingClear(meetings.get(1))) ||
                (meetingClear(meetings.get(1)) && !meetingClear(meetings.get(0)))) {
            startTempTransaction(transaction.getKey());
        }
        //Complete Meeting
        else if (meetingClear(meetings.get(0)) && meetingClear(meetings.get(1))) {
            finishTransaction(transaction.getKey());
        }
    }


    /**
     * Delete a transaction with transactionId
     *
     * @param transactionId The ID of a transaction
     * @throws IOException if the data cannot be read from file
     */
    public void deleteTransaction(int transactionId) throws IOException {
        Transaction transaction = gateway.get(transactionId, Transaction.class);
        //Make items unreserved
        Trade trade = gateway.get(transaction.getTradeList().get(0), Trade.class);
        List<Item> items = gateway.get(trade.getItemList(), Item.class);
        for (Item item : items) item.setReserved(false);
        gateway.update(items, Item.class);

        gateway.delete(transaction.getTradeList(), Trade.class);
        gateway.delete(transaction.getMeetingList(), Meeting.class);
        gateway.delete(Collections.singletonList(transactionId), Transaction.class);

    }


    /**
     * Check if the transaction is agreed to
     *
     * @param transactionId The ID of a transaction.
     * @return true if and only if the transaction is agreed, aka all the meetings in the transaction are agreed.
     * @throws IOException An IOException.
     */
    public boolean checkAgree(int transactionId) throws IOException {
        Transaction transaction = gateway.get(Collections.singletonList(transactionId), Transaction.class).get(0);
        List<Meeting> meetings = transaction.relation(relationMapper, "meetings", Meeting.class);
        for (Meeting meeting : meetings) {
            if (!meeting.isAgreedTo()) {
                return false;
            }
        }
        return true;
    }


/********************************************************************************************************
 *
 * Helper methods
 *
 *********************************************************************************************************/


    /**
     * Sorts a key value pair
     *
     * @param count the map
     * @return a sorted map
     */
    private List<List<Integer>> sort(Map<Integer, Integer> count) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        List<Integer> keys = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            int position = 0;
            for (int frequency : frequencies) {
                if (entry.getValue() < frequency) position++;
            }
            frequencies.add(position, entry.getValue());
            keys.add(position, entry.getKey());
        }
        result.add(keys);
        result.add(frequencies);
        return result;
    }


    /**
     * Set up the trades for a transaction
     *
     * @param borrowerId     id of borrower
     * @param lenderId       id of lender
     * @param borrowedItemId id of borrower's item
     * @param lendItemId     id of lender's item
     * @param tradeType      type of trade
     */
    private void setupTrades(int borrowerId, int lenderId, int borrowedItemId, int lendItemId, String tradeType)
            throws TooManyItemListsException {

        tradeFactory.reset().fillLenderId(lenderId).fillBorrowId(borrowerId);

        List<Integer> itemIds = new ArrayList<>();
        itemIds.add(borrowedItemId);

        if (tradeType.equals("oneWay")) {
            tradeFactory.oneWay().fillItems(itemIds);
        } else if (tradeType.equals("sell")) {
            tradeFactory.sell().fillItems(itemIds);
        } else {
            List<Integer> exchangedItems = new ArrayList<>();
            exchangedItems.add(lendItemId);
            tradeFactory.twoWay().fillItems(itemIds).fillItems(exchangedItems);
        }

        //Make items reserved
        try {
            List<Item> items = gateway.get(itemIds, Item.class);
            for (Item item : items) item.setReserved(true);
            gateway.update(items, Item.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Set up the meetings for a transaction
     *
     * @param tradeDuration    the duration of trade
     * @param date             date of first meeting
     * @param meetingLocation1 first location of meeting
     * @param meetingLocation2 second location of meeting
     * @param userId           id of user
     * @throws TooManyTimesException     Represents the exception that a user is trying to give more meeting times than needed
     * @throws TooManyLocationsException Represents the exception that a user is trying to give more locations than needed
     */
    private void setupMeetings(String tradeDuration, LocalDate date, String meetingLocation1, String meetingLocation2, int userId)
            throws TooManyTimesException, TooManyLocationsException {

        meetingFactory.reset().fillTime(date)
                .fillLocation(meetingLocation1)
                .setCurrentSuggestionMaker(userId);

        if (!tradeDuration.equals("permanent")) {
            meetingFactory.temporary().fillLocation(meetingLocation2);
        } else meetingFactory.permanent();
    }


    /**
     * To instantiate a transaction
     *
     * @param tradeIds   of the trade involved in the transaction (can get from TradeFactory)
     * @param meetingIds of the meetings involved in the transaction (can get from meetingFactory)
     * @return the id of the created transaction
     * @throws IOException if there is an IO error
     */
    private int initiateTransaction(List<Integer> tradeIds, List<Integer> meetingIds)
            throws IOException {

        Transaction transaction = new Transaction(tradeIds, meetingIds);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        List<Transaction> obtainedTransactions = gateway.create(transactions, Transaction.class);
        return obtainedTransactions.get(0).getKey();

    }


    /**
     * to indicate that a temporary transaction has been started in real-life by users
     *
     * @param transactionId of the transaction
     * @throws IOException if the data cannot be read from file
     */
    private void startTempTransaction(int transactionId)
            throws IOException {

        Transaction transaction = this.transactionFetcher.query().findById(transactionId).getTransaction();
        List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
        for (Trade trade : trades) {
            List<Item> items = gateway.get(trade.getItemList(), Item.class);
            for (Item item : items) {
                // change holder of the item
                item.changeHolderId(trade.getBorrowerId(), trade.getLenderId());
            }
            gateway.update(items, Item.class);
        }
    }


    /**
     * To indicate that the transaction has been finished in real-life by users
     *
     * @param transactionId of the transaction
     * @throws IOException if the data cannot be read from file
     */
    private void finishTransaction(int transactionId)
            throws IOException {

        Transaction transaction = gateway.get(Collections.singletonList(transactionId), Transaction.class).get(0);
        List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
        boolean isPermanent = transaction.getMeetingList().size() < 2;
        for (Trade trade : trades) {
            List<Item> items = gateway.get(trade.getItemList(), Item.class);
            trade.setComplete(true);
            for (Item item : items) {
                this.handleItems(trade, item, isPermanent);
            }
            gateway.update(items, Item.class);
        }
        gateway.update(trades, Trade.class);
    }


    /**
     * Handle item process during making transaction
     *
     * @param trade       Trade Object
     * @param item        Item Object
     * @param isPermanent whether this transaction is permanent or not
     */
    private void handleItems(Trade trade, Item item, boolean isPermanent) {
        //unreserved the item
        item.setReserved(false);
        // soft-delete the item if the transaction is permanent
        item.setSoftDelete(isPermanent);
        // change holder of the item
        item.changeHolderId(trade.getBorrowerId(), trade.getLenderId());
        // change owner of the item if the transaction is permanent
        if (isPermanent) item.changeOwnerId(trade.getBorrowerId(), trade.getLenderId());
    }

    /**
     * Return true iff the meeting is agreed and completed
     *
     * @param meeting Meeting
     * @return true iff the meeting is agreed and completed
     */
    private boolean meetingClear(Meeting meeting) {
        return meeting.isComplete() && meeting.isAgreedTo();
    }


}



