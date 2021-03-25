package usecases;

import usecases.items.ItemEditor;
import usecases.items.ItemFetcher;
import usecases.meeting.MeetingFactory;
import usecases.meeting.MeetingManager;
import usecases.tags.TagManager;
import usecases.trade.TradeFactory;
import usecases.trade.TransactionFetcher;
import usecases.trade.TransactionManager;

/**
 * A facade for trading-related use case classes.
 */
public class TradingFacade {

    /**
     * Class dependencies
     */
    private final ItemEditor itemEditor;
    private final ItemFetcher itemFetcher;
    private final MeetingFactory meetingFactory;
    private final MeetingManager meetingManager;
    private final TradeFactory makeTrades;
    private final TransactionManager manageTransactions;
    private final TransactionFetcher fetchTransactions;
    private final TagManager tagManager;


    /**
     * Initializes this class.
     *
     * @param itemEditor         ItemEditor
     * @param itemFetcher        ItemFetcher
     * @param meetingFactory     MeetingFactory
     * @param meetingManager     MeetingManager
     * @param makeTrades         ItemEditor
     * @param manageTransactions TransactionManager
     */
    public TradingFacade(ItemEditor itemEditor, ItemFetcher itemFetcher, MeetingFactory meetingFactory, MeetingManager meetingManager,
                         TradeFactory makeTrades, TransactionManager manageTransactions, TransactionFetcher fetchTransactions, TagManager tagManager) {
        this.itemEditor = itemEditor;
        this.itemFetcher = itemFetcher;
        this.meetingFactory = meetingFactory;
        this.meetingManager = meetingManager;
        this.makeTrades = makeTrades;
        this.manageTransactions = manageTransactions;
        this.fetchTransactions = fetchTransactions;
        this.tagManager = tagManager;
    }

    /**
     * Returns an instance of ItemFetcher.
     *
     * @return Returns an instance of ItemFetcher.
     */
    public ItemFetcher fetchItems() {
        return this.itemFetcher;
    }

    /**
     * Returns an instance of ItemEditor.
     *
     * @return Returns an instance of ItemEditor.
     */
    public ItemEditor editItems() {
        return this.itemEditor;
    }

    /**
     * Returns an instance of MeetingFactory.
     *
     * @return Returns an instance of MeetingFactory.
     */
    public MeetingFactory makeMeetings() {
        return this.meetingFactory;
    }

    /**
     * Returns an instance of MeetingManager.
     *
     * @return Returns an instance of MeetingManager.
     */
    public MeetingManager manageMeetings() {
        return this.meetingManager;
    }

    /**
     * Returns an instance of TradeFactory.
     *
     * @return Returns an instance of TradeFactory.
     */
    public TradeFactory makeTrades() {
        return this.makeTrades;
    }


    public TagManager manageTags() {
        return this.tagManager;
    }

    /**
     * Returns an instance of TransactionManager.
     *
     * @return Returns an instance of TransactionManager.
     */
    public TransactionManager manageTransactions() {
        return this.manageTransactions;
    }

    /**
     * Returns an instance of TransactionFetcher
     *
     * @return Returns an instance of TransactionFetcher
     */
    public TransactionFetcher fetchTransactions() {
        return this.fetchTransactions;
    }


}
