package usecases.trade;

import entities.*;
import persistence.PersistenceInterface;
import persistence.relations.MapsRelations;
import usecases.query.AbstractFetcher;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TransactionFetcher extends AbstractFetcher {

    /**
     * Class dependencies
     */
    PersistenceInterface gateway;

    /**
     * A concrete instance of relation mapper.
     */
    MapsRelations relationMapper;

    /**
     * Initializes this class.
     *
     * @param gateway        PersistenceInterface
     * @param relationMapper RelationMapper
     */
    public TransactionFetcher(PersistenceInterface gateway, MapsRelations relationMapper) {
        this.gateway = gateway;
        this.relationMapper = relationMapper;
    }


/********************************************************************************************************
 *
 * Public interface of the transaction fetcher.
 *
 *********************************************************************************************************/

    /**
     * Builds a new query.
     *
     * @return TransactionQueryBuilder
     */
    public TransactionQueryBuilder query() {
        return new TransactionQueryBuilder(this);
    }


    /**
     * Returns a Transaction Object if exists
     *
     * @param query TransactionQueryBuilder
     * @return Returns a list of transaction objects.
     * @throws IOException IOException
     */
    public Transaction getTransaction(TransactionQueryBuilder query) throws IOException {
        if (this.getTransactions(query).get(0) != null) {
            return this.getTransactions(query).get(0);
        }
        return null;
    }

    /**
     * Returns a list of Transactions
     *
     * @param query TransactionQueryBuilder
     * @return Returns a list of transaction objects.
     * @throws IOException IOException
     */
    public List<Transaction> getTransactions(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        return this.currentList;
    }

    /**
     * Returns a map with the transaction id as key and the meeting objects it contains as value
     *
     * @param query TransactionQueryBuilder
     * @return A map with the transaction id as key and the ids of the meetings it contains as value
     * @throws IOException IOException
     */
    public Map<Integer, List<Meeting>> getMeetings(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        Map<Integer, List<Meeting>> meetings = new HashMap<>();
        for (Transaction transaction : this.currentList) {
            meetings.put(transaction.getKey(), transaction.relation(relationMapper, "meetings", Meeting.class));
        }
        return meetings;
    }

    /**
     * Returns a list of meeting objects associated with this transaction.
     *
     * @param query TransactionQueryBuilder
     * @return Returns a list of meeting objects associated with this transaction.
     * @throws IOException IOException
     */
    public List<Meeting> getMeetingsList(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        List<Meeting> meetings = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            meetings.addAll(transaction.relation(relationMapper, "meetings", Meeting.class));
        }
        return meetings;
    }

    /**
     * Returns map saves a transaction id and the ids of trades it contains
     *
     * @param query TransactionQueryBuilder
     * @return Returns map saves a transaction id and the ids of trades it contains
     * @throws IOException IOException
     */
    public Map<Integer, List<Trade>> getTrades(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        Map<Integer, List<Trade>> trades = new HashMap<>();
        for (Transaction transaction : this.currentList) {
            trades.put(transaction.getKey(), transaction.relation(relationMapper, "trades", Trade.class));
        }
        return trades;
    }

    /**
     * Returns a list of trades associated with this query
     *
     * @param query TransactionQueryBuilder
     * @return Returns a list of trades
     * @throws IOException IOException
     */
    public List<Trade> getTradesList(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        List<Trade> trades = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            trades.addAll(transaction.relation(relationMapper, "trades", Trade.class));
        }
        return trades;
    }

    /**
     * Returns a map with the trade id as key and borrowers as value.
     *
     * @param query TransactionQueryBuilder
     * @return Returns a map with the trade id as key and borrowers as value.
     * @throws IOException IOException
     */
    public Map<Integer, User> getBorrowers(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        Map<Integer, User> users = new HashMap<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
            for (Trade trade : trades) {
                User user = gateway.get(trade.getBorrowerId(), User.class);
                users.put(trade.getKey(), user);
            }
        }
        return users;
    }

    /**
     * Returns a map with the trade id as key and borrower ids as value.
     *
     * @param query TransactionQueryBuilder
     * @return Returns a map with the trade id as key and borrower ids as value.
     * @throws IOException IOException
     */
    public Map<Integer, Integer> getBorrowerIds(TransactionQueryBuilder query) throws IOException {
        Map<Integer, Integer> borrowerIds = new HashMap<>();
        Map<Integer, User> borrowers = this.getBorrowers(query);
        for (Map.Entry<Integer, User> entry : borrowers.entrySet()) {
            borrowerIds.put(entry.getKey(), entry.getValue().getKey());
        }
        return borrowerIds;
    }

    /**
     * Returns a map with the trade id as key and lenders as value.
     *
     * @param query TransactionQueryBuilder
     * @return Returns a map with the trade id as key and lenders as value.
     * @throws IOException IOException
     */
    public Map<Integer, User> getLenders(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        Map<Integer, User> users = new HashMap<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
            for (Trade trade : trades) {
                User user = gateway.get(trade.getLenderId(), User.class);
                users.put(trade.getKey(), user);
            }
        }
        return users;
    }

    /**
     * Returns a map with the trade id as key and lender ids as value.
     *
     * @param query TransactionQueryBuilder
     * @return Returns a map with the trade id as key and lender ids as value.
     * @throws IOException IOException
     */
    public Map<Integer, Integer> getLenderIds(TransactionQueryBuilder query) throws IOException {
        Map<Integer, Integer> lenderIds = new HashMap<>();
        Map<Integer, User> lenders = this.getLenders(query);
        for (Map.Entry<Integer, User> entry : lenders.entrySet()) {
            lenderIds.put(entry.getKey(), entry.getValue().getKey());
        }
        return lenderIds;
    }

    /**
     * Returns a map with the trade id as key and items as value.
     *
     * @param query TransactionQueryBuilder
     * @return Returns a map with the trade id as key and items as value.
     * @throws IOException IOException
     */
    public Map<Integer, List<Item>> getItems(TransactionQueryBuilder query) throws IOException {
        this.fetch(query);
        Map<Integer, List<Item>> items = new HashMap<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
            for (Trade trade : trades) {
                items.put(trade.getKey(), trade.relation(relationMapper, "items", Item.class));
            }
        }
        return items;
    }


/********************************************************************************************************
 *
 * All the Integer Filters
 *
 *********************************************************************************************************/

    /**
     * Filter transactions by transaction id
     *
     * @param transactionId unique id of transactions
     */
    public void findById(Integer transactionId) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            if (transaction.getKey() == transactionId) filtered.add(transaction);
        }
        this.currentList = filtered;
    }

    /**
     * Filter transactions by userId
     *
     * @param userId id of user
     * @throws IOException IOException
     */
    public void involvesUser(Integer userId) throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(this.relationMapper, "trades", Trade.class);
            if (this.tradesInvolvesUser(trades, userId)) filtered.add(transaction);
        }
        this.currentList = filtered;
    }

    /**
     * Filter transactions by what items it involves
     *
     * @param itemId id of item
     * @throws IOException IOException.
     */
    public void involvesItem(Integer itemId) throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            boolean involves = false;
            List<Trade> trades = transaction.relation(this.relationMapper, "trades", Trade.class);
            for (Trade trade: trades) {
                List<Item> items = trade.relation(this.relationMapper, "items", Item.class);
                for (Item item : items) {
                    if (item.getKey() == itemId) {
                        involves = true;
                        break;
                    }
                }
            }
            if(involves) filtered.add(transaction);
        }
        this.currentList = filtered;
    }

    /**
     * Filters based on whether the user is a borrower.
     *
     * @param userId id of user
     * @throws IOException IOException
     */
    public void involvesUserAsBorrower(Integer userId) throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(this.relationMapper, "trades", Trade.class);
            if (this.tradesInvolvesUserAsBorrower(trades, userId)) filtered.add(transaction);
        }
        this.currentList = filtered;
    }

    /**
     * Filters based on whether the user is a lender.
     *
     * @param userId id of user
     * @throws IOException IOException
     */
    public void involvesUserAsLender(Integer userId) throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(this.relationMapper, "trades", Trade.class);
            if (this.tradesInvolvesUserAsLender(trades, userId)) filtered.add(transaction);
        }
        this.currentList = filtered;
    }


/********************************************************************************************************
 *
 * All the Boolean Filters
 *
 *********************************************************************************************************/

    /**
     * Filters only ongoing transactions
     *
     * @throws IOException IOException
     */
    public void onGoing() throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Meeting> meetings = transaction.relation(this.relationMapper, "meetings", Meeting.class);
            Meeting lastMeeting = this.getLastMeeting(meetings);
            if (!lastMeeting.hasPassed()) {
                filtered.add(transaction);
            }
        }
        this.currentList = filtered;
    }

    /**
     * Filters only completed transactions
     *
     * @throws IOException IOException
     */
    public void isComplete() throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
            List<Meeting> meetings = transaction.relation(relationMapper, "meetings", Meeting.class);
            if (this.tradesComplete(trades) && this.meetingsComplete(meetings)) filtered.add(transaction);
        }
        this.currentList = filtered;
    }

    /**
     * Filters transactions that are not completed
     *
     * @throws IOException
     */
    public void isIncomplete() throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Trade> trades = transaction.relation(relationMapper, "trades", Trade.class);
            List<Meeting> meetings = transaction.relation(relationMapper, "meetings", Meeting.class);
            if ((!this.tradesComplete(trades) | !this.meetingsComplete(meetings)) && !onGoing(transaction))
                filtered.add(transaction);
        }
        this.currentList = filtered;
    }

    /**
     * Filters transactions that are expected to take place, but not yet so
     *
     * @throws IOException
     */
    public void isExpected() throws IOException {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction transaction : this.currentList) {
            List<Meeting> meetings = transaction.relation(relationMapper, "meetings", Meeting.class);
            if ((this.meetingAgree(meetings)))
                filtered.add(transaction);
        }
        this.currentList = filtered;
    }


    /********************************************************************************************************
     *
     * All the Date Filters
     *
     *********************************************************************************************************/

    /**
     * Filters the transactions which is expected to take place after the given date
     * @param date a date to compare to the date of the first meeting in a transaction
     */
    public void after(LocalDate date) {
        List<Transaction> filtered = new ArrayList<>();

        try {
            for (Transaction transaction : this.currentList) {
                List<Meeting> meetings = gateway.get(transaction.getMeetingList(), Meeting.class);
                if (getFirstMeeting(meetings).getTime().isAfter(date)) filtered.add(transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.currentList = filtered;
    }


    /********************************************************************************************************
     *
     * Helper Methods
     *
     *********************************************************************************************************/

    /**
     * Get whether the trades involve user with userId
     *
     * @param trades list of trades
     * @param userId id of user
     */
    private boolean tradesInvolvesUser(List<Trade> trades, int userId) {
        for (Trade trade : trades) {
            boolean involvesUser = trade.getBorrowerId() == userId || trade.getLenderId() == userId;
            if (involvesUser) return true;
        }
        return false;
    }

    /**
     * Get whether the trades involve user with userId as borrower
     *
     * @param trades list of trades
     * @param userId id of user
     */
    private boolean tradesInvolvesUserAsBorrower(List<Trade> trades, int userId) {
        for (Trade trade : trades) {
            boolean involvesUser = trade.getBorrowerId() == userId;
            if (involvesUser) return true;
        }
        return false;
    }

    /**
     * Get whether the trades involve user with userId as borrower
     *
     * @param trades list of trades
     * @param userId id of user
     */
    private boolean tradesInvolvesUserAsLender(List<Trade> trades, int userId) {
        for (Trade trade : trades) {
            boolean involvesUser = trade.getLenderId() == userId;
            if (involvesUser) return true;
        }
        return false;
    }

    /**
     * Get whether the trades are complete
     *
     * @param trades list of trades
     */
    private boolean tradesComplete(List<Trade> trades) {
        for (Trade trade : trades) {
            if (!trade.isComplete()) return false;
        }
        return true;
    }

    /**
     * Get whether the meeting are completed or not
     *
     * @param meetings a list of meeting
     */
    private boolean meetingsComplete(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            if (!meeting.isComplete()) return false;
        }
        return true;
    }

    /**
     * Get whether the meeting are completed or not
     *
     * @param meetings a list of meeting
     */
    private boolean meetingAgree(List<Meeting> meetings) {
        for (Meeting meeting : meetings) {
            if (meeting.isAgreedTo()) return true;
        }
        return false;
    }

    /**
     * Get the latter meeting in a transaction
     *
     * @param meetings a list of meeting
     * @return return the latter meeting object
     */
    private Meeting getLastMeeting(List<Meeting> meetings) {
        Meeting lastMeeting = meetings.get(0);
        for (Meeting meeting : meetings) {
            if (meeting.getTime().isAfter(lastMeeting.getTime())) {
                lastMeeting = meeting;
            }
        }
        return lastMeeting;
    }

    /**
     * Get the first meeting in a transaction
     *
     * @param meetings a list of meeting
     * @return return the first meeting object
     */
    private Meeting getFirstMeeting(List<Meeting> meetings) {
        Meeting firstMeeting = meetings.get(0);
        for (Meeting meeting : meetings) {
            if (firstMeeting.getTime().isAfter(meeting.getTime())) {
                firstMeeting = meeting;
            }
        }
        return firstMeeting;
    }


    /**
     * Get whether this transaction if ongoing or not
     *
     * @param transaction transaction Object
     * @throws IOException An IOException
     */
    private boolean onGoing(Transaction transaction) throws IOException {
        List<Meeting> meetings = transaction.relation(this.relationMapper, "meetings", Meeting.class);
        Meeting lastMeeting = this.getLastMeeting(meetings);
        return !lastMeeting.hasPassed();
    }


    /**
     * Stores the result of the current fetch.
     */
    private List<Transaction> allTransactions = new ArrayList<>();
    private List<Transaction> currentList = new ArrayList<>();


    /**
     * Reset the all lists this class saved
     */
    protected void reset() {
        this.allTransactions = new ArrayList<>();
        this.currentList = new ArrayList<>();
    }


    /**
     * Get all Item Objects and save them into the lists this class had
     */
    protected void all() throws IOException {
        this.allTransactions = gateway.all(Transaction.class);
        this.currentList.addAll(this.allTransactions);
    }


}
