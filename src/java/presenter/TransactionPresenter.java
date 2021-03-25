package presenter;

import controller.forms.InitiateTradeForm;
import entities.*;
import org.springframework.ui.Model;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.items.exceptions.ItemNotFoundException;
import usecases.trade.TransactionQueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class TransactionPresenter {

    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;

    /**
     * Instantiates this presenter.
     *
     * @param tradingFacade The facade holding trading related functionality.
     * @param systemFacade  The facade holding system related functionality.
     */
    public TransactionPresenter(TradingFacade tradingFacade, SystemFacade systemFacade) {
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
    }

    /**
     * Prepares the page to let the user choose what items to exchange.
     *
     * @param model         A model that holds dynamic data.
     * @param tradeDuration temporary or permanent
     * @param borrowItemId  the item id of the item that is being borrowed
     * @param userId        The user id
     * @return The view
     * @throws IOException           An IOException
     * @throws ItemNotFoundException An exception that indicates that no item was found
     */
    public String tradePresenter(Model model, String tradeDuration, String borrowItemId, int userId)
            throws IOException, ItemNotFoundException {

        Item item = this.tradingFacade.fetchItems().findById(Integer.parseInt(borrowItemId));
        List<Item> recommendedItems = tradingFacade.fetchItems().recommendedItems(userId, item.getOwnerId()).getObjects();
        List<Item> otherItems = tradingFacade.fetchItems().notRecommendedItems(userId, item.getOwnerId()).getObjects();

        //Bind our dynamic variables to the model, for HTML display
        model.addAttribute("tradeDuration", tradeDuration);
        model.addAttribute("recommendedItems", recommendedItems);
        model.addAttribute("otherItems", otherItems);
        model.addAttribute("borrowItem", item);

        return "trade/chooseExchange";
    }

    /**
     * Handles binding the trade form to the model.
     *
     * @param model A model that holds dynamic data.
     */
    public void initiateTradeForm(Model model) {
        model.addAttribute("initiateTradeForm", new InitiateTradeForm());
    }

    /**
     * Handles binding the lent item id
     *
     * @param model      A model that holds dynamic data.
     * @param lendItemId The item id of the thing we are lending.
     */
    public void lendItemId(Model model, String lendItemId) {
        model.addAttribute("lendItemId", lendItemId);
    }

    /**
     * Shows the form to set up the meeting time and location.
     *
     * @param model         A model that holds dynamic data.
     * @param tradeDuration temporary or permanent
     * @param tradeType     one way or two way
     * @param borrowItemId  borrow item id
     * @return The view
     */
    public String setMeetingsPresenter(Model model, String tradeDuration, String tradeType, String borrowItemId) {
        model.addAttribute("borrowItemId", borrowItemId);
        model.addAttribute("tradeDuration", tradeDuration);
        model.addAttribute("tradeType", tradeType);
        return "meetings/meetingForm";
    }

    /**
     * Handles if we are missing second meeting info.
     *
     * @param model A model that holds dynamic data.
     */
    public void secondMeetingInfoMissing(Model model) {
        model.addAttribute("secondMeetingInfoMissing", true);
    }


    /**
     * Handles the model binding if the user input a date from the past
     *
     * @param model A model that holds dynamic data.
     */
    public void dateFromPast(Model model) {
        model.addAttribute("dateFromPast", true);
    }

    /**
     * Handles the model binding if the new trade succeeded.
     *
     * @param model A model that holds dynamic data.
     */
    public void newTradeSuccess(Model model) {
        model.addAttribute("newTradeSuccess", true);
    }


    /**
     * Handles showing transaction history.
     *
     * @param model  A model that holds dynamic data.
     * @param userId The user id
     * @return The view
     * @throws IOException An IOException
     */
    public String transHistoryPresenter(Model model, int userId) throws IOException {
        TransactionQueryBuilder query = tradingFacade.fetchTransactions().query().involvesUser(userId);

        //Retrieves the transactions, trades, items, meetings, and users related to this user's transactions.
        List<Transaction> transactions = query.getTransactions();
        Map<Integer, List<Trade>> trades = query.getTrades();
        Map<Integer, List<Item>> items = query.getItems();
        Map<Integer, List<Meeting>> meetings = query.getMeetings();
        Map<Integer, User> borrowers = query.getBorrowers();
        Map<Integer, User> lenders = query.getLenders();

        //Bind our dynamic variables to the model, for HTML display
        model.addAttribute("transactions", transactions);
        model.addAttribute("trades", trades);
        model.addAttribute("meetings", meetings);
        model.addAttribute("items", items);
        model.addAttribute("borrowers", borrowers);
        model.addAttribute("lenders", lenders);
        model.addAttribute("users", systemFacade.users().allById());

        return "trade/transactions";
    }

}
