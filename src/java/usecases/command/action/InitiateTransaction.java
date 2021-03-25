package usecases.command.action;

import entities.History;
import entities.Meeting;
import persistence.PersistenceInterface;
import usecases.command.exceptions.CommandExecutionException;
import usecases.meeting.exceptions.MeetingException;
import usecases.trade.TransactionFetcher;
import usecases.trade.TransactionManager;
import usecases.trade.exceptions.TooManyItemListsException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * An undoable action of initiating a transaction
 */
public class InitiateTransaction implements Undoable {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * Fetcher of transactions
     */
    private final TransactionFetcher transactionFetcher;

    /**
     * Manager of transactions
     */
    private final TransactionManager transactionManager;

    /**
     * Initializes this class.
     *
     * @param gateway            persistence of Object which is used to read and edit information of entities
     * @param transactionManager manager of transactions
     * @param transactionFetcher fetcher of transactions
     */
    public InitiateTransaction(PersistenceInterface gateway, TransactionManager transactionManager,
                               TransactionFetcher transactionFetcher) {
        this.gateway = gateway;
        this.transactionFetcher = transactionFetcher;
        this.transactionManager = transactionManager;
    }


    /**
     * Create a new transaction and create a history of it for admin to read
     *
     * @param borrowerId       the borrower's id in this transaction
     * @param lenderId         the lender's id in this transaction
     * @param borrowItemId     the id of borrow item
     * @param lendItemId       the id of lend item
     * @param tradeType        the type of trade which should be permanent or temporary
     * @param tradeDuration    the duration of this trade
     * @param meetingDate      the date of first meeting
     * @param meetingLocation  the first location of meeting
     * @param meetingLocation2 the second location of meeting
     * @throws TooManyItemListsException Represents the exception that there is too many item lists added to a trade
     * @throws MeetingException          An exception related to meetings
     * @throws IOException               An IOException
     */
    public void execute(int borrowerId, int lenderId, int borrowItemId, int lendItemId, String tradeType,
                        String tradeDuration, LocalDate meetingDate,
                        String meetingLocation, String meetingLocation2) throws
            TooManyItemListsException, MeetingException, IOException {

        int transactionId = transactionManager.buildTransaction(borrowerId, lenderId, borrowItemId, lendItemId,
                tradeType, tradeDuration, meetingDate, meetingLocation, meetingLocation2);

        History history = new History();
        history.addData("transactionId", transactionId);
        history.addData("borrowerId", borrowerId);
        history.addData("lenderId", lenderId);
        history.addData("borrowItemId", borrowItemId);
        history.addData("lendItemId", lendItemId);
        history.addData("tradeType", tradeType);
        history.addData("tradeDuration", tradeDuration);
        history.addData("meetingDate", meetingDate);
        history.addData("meetingLocation", meetingLocation);
        history.addData("meetingLocation2", meetingLocation2);
        history.setActionName(this.getClass().getName());
        history.setDisplayString("Borrower with id " + borrowerId + " initiates a transaction with lender with id "
                + lenderId + " involving borrow item with id " + borrowItemId + " and lend item with id " + lendItemId);
        gateway.create(history, History.class);
    }

    /**
     * Undo an action by recalling a history
     *
     * @param history History that save the undoable action to undo it
     * @throws CommandExecutionException A wrapper class wrapping an underlying exception.
     */
    @Override
    public void undo(History history) throws CommandExecutionException {
        try {
            transactionManager.deleteTransaction((Integer) history.getData("transactionId"));
            history.setUndone(true);
            String oldMessage = history.getDisplayString();
            String newMessage = oldMessage + " has been undone";
            history.setDisplayString(newMessage);
            gateway.update(history, History.class);
        } catch (Exception e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Return whether this history can be recalled or the action can be undo
     *
     * @param history History that save the undoable action to undo it
     * @return whether this history can be recalled or the action can be undo
     */
    @Override
    public boolean canUndo(History history) {
        try {
            List<Meeting> meetings = transactionFetcher.query().getMeetings().
                    get((Integer) history.getData("transactionId"));
            for (Meeting meeting : meetings) {
                if (meeting.isAgreedTo()) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
