package controller;

import controller.forms.CancelTransactionForm;
import controller.forms.InitiateTradeForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import presenter.TransactionPresenter;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.command.action.InitiateTransaction;
import usecases.items.exceptions.ItemNotFoundException;
import usecases.meeting.exceptions.MeetingException;
import usecases.trade.exceptions.TooManyItemListsException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;


@Controller
public class TransactionController extends AbstractBaseController {


    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final InitiateTransaction initiateTransaction;
    private final TransactionPresenter transactionPresenter;

    /**
     * create new transaction controller
     *
     * @param tradingFacade        A facade that contains trading related functionality.
     * @param systemFacade         A facade that contains system related functionality.
     * @param initiateTransaction  A command to execute the action of initiate a transaction
     * @param transactionPresenter A presenter for all information related to transactions
     */
    public TransactionController(TradingFacade tradingFacade, SystemFacade systemFacade, InitiateTransaction initiateTransaction, TransactionPresenter transactionPresenter) {
        super(systemFacade);
        this.tradingFacade = tradingFacade;
        this.initiateTransaction = initiateTransaction;
        this.transactionPresenter = transactionPresenter;
    }


    /**
     * Shows a list of the user's items that can be exchanged for another user's item.
     *
     * @param request       An object holding the HTTP request.
     * @param model         Holds data to send to the view.
     * @param tradeDuration A parameter indicating whether the trade is temporary or permanent.
     * @param borrowItemId  The id of the item to be borrowed
     * @return A string indicating which view to display.
     * @throws IOException           An IOException
     * @throws ItemNotFoundException An exception indicating that the item was not found in persistence.
     */
    @GetMapping("/trade")
    public String trade(HttpServletRequest request, Model model,
                        @RequestParam String tradeDuration,
                        @RequestParam String borrowItemId) throws IOException, ItemNotFoundException {

        int userId = this.getLoggedInUserId(request);
        return transactionPresenter.tradePresenter(model, tradeDuration, borrowItemId, userId);
    }


    /**
     * Shows the form to set up the meeting time and location.
     *
     * @param request       An object holding the HTTP request.
     * @param model         Holds data to send to the view.
     * @param tradeDuration A parameter indicating whether the trade is temporary or permanent.
     * @param tradeType     A parameter indicating whether the trade is one way or two way.
     * @param borrowItemId  The id of the item to be borrowed.
     * @param lendItemId    The id of the item to be lent.
     * @return A string indicating which view to display.
     */
    @GetMapping("/trade/meeting")
    public String setMeetings(HttpServletRequest request, Model model,
                              @RequestParam String tradeDuration,
                              @RequestParam String tradeType,
                              @RequestParam String borrowItemId,
                              @RequestParam(required = false) String lendItemId,
                              @RequestParam(required = false) boolean validationRedirect) {


        if (!validationRedirect) {
            transactionPresenter.initiateTradeForm(model);
        }
        if (request.getParameterMap().containsKey("lendItemId")) {
            transactionPresenter.lendItemId(model, lendItemId);
        }
        //Bind our dynamic variables to the model, for HTML display
        return transactionPresenter.setMeetingsPresenter(model, tradeDuration, tradeType, borrowItemId);
    }

    /**
     * Handles form request to initiate a new trade.
     *
     * @param request An object holding the HTTP request.
     * @param form    The form holding the front-end input.
     * @param errors  An object holding validation errors
     * @param model   A model holding data.
     * @return A url to which we redirect.
     * @throws MeetingException          An exception related to meetings
     * @throws IOException               An IOException
     * @throws ItemNotFoundException     An exception indicating an item was not found
     * @throws TooManyItemListsException An exception indicating that too many items lists were attached to a trade.
     */
    @PostMapping("trade/initiate")
    public String initiate(HttpServletRequest request, @Valid @ModelAttribute InitiateTradeForm form,
                           Errors errors, Model model)
            throws MeetingException, IOException, ItemNotFoundException, TooManyItemListsException {

        int borrowerId = this.getLoggedInUserId(request);
        int borrowItemId = Integer.parseInt(form.getBorrowItemId());
        int lenderId = tradingFacade.fetchItems().findById(Integer.parseInt(form.getBorrowItemId())).getOwnerId();

        int lendItemId;
        if (!form.getLendItemId().equals("")) {
            lendItemId = Integer.parseInt(form.getLendItemId());
        } else lendItemId = 0;

        String tradeType = form.getTradeType();
        String tradeDuration = form.getTradeDuration();

        //Validate meetings
        if (errors.hasErrors()) {
            return setMeetings(request, model, tradeDuration, tradeType, Integer.toString(borrowItemId), Integer.toString(lendItemId), true);
        }

        //Validate second meeting
        if (this.needSecondMeeting(form)) {
            transactionPresenter.secondMeetingInfoMissing(model);
        }

        //Validate meeting date
        LocalDate meetingDate = LocalDate.parse(form.getMeetingDate());
        if (this.isPastDate(meetingDate)) {
            transactionPresenter.dateFromPast(model);
        }

        if (this.needSecondMeeting(form) || this.isPastDate(meetingDate)) {
            return setMeetings(request, model, tradeDuration, tradeType, Integer.toString(borrowItemId), Integer.toString(lendItemId), true);
        }

        //Run this initiation through the "command" package so that the admin can choose to undo it.
        this.initiateTransaction.execute(borrowerId, lenderId, borrowItemId, lendItemId, tradeType,
                tradeDuration, meetingDate, form.getMeetingLocation(), form.getMeetingLocation2());

        return "redirect:/trade/history?newTradeSuccess=true";
    }

    /**
     * Checks if a date is in the past
     *
     * @param date A LocalDate
     * @return True iff a date was in the past
     */
    private boolean isPastDate(LocalDate date) {
        return date.compareTo(LocalDate.now()) <= 0;
    }

    /**
     * Checks if we need the user to input a second date
     *
     * @param form A form that holds input values
     * @return True iff we require second meeting info
     */
    private boolean needSecondMeeting(InitiateTradeForm form) {
        return form.getTradeDuration().equals("temporary") && (form.getMeetingLocation2().isEmpty());
    }

    /**
     * Cancel a transaction only if the transaction has not been agreed yet.
     *
     * @param form Cancel Transaction Form
     * @return A url to which we redirect.
     * @throws IOException An IOException.
     */
    @PostMapping("/trade/cancel")
    public String cancelTransaction(CancelTransactionForm form) throws IOException {
        int transactionId = form.getTransactionId();
        if (!tradingFacade.manageTransactions().checkAgree(transactionId)) {
            tradingFacade.manageTransactions().deleteTransaction(transactionId);
            return "redirect:/trade/history?cancelTransactionSuccess=true";
        }
        return "redirect:/trade/history?cancelTransactionSuccess=false";
    }


    /**
     * Shows trade history of a user.
     *
     * @param request         An object holding the HTTP request.
     * @param model           Holds data to send to the view.
     * @param newTradeSuccess A parameter indicating whether a newly created trade was a success
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/trade/history")
    public String transHistory(HttpServletRequest request, Model model,
                               @RequestParam(required = false) String newTradeSuccess,
                               @RequestParam(required = false) Boolean cancelTransactionSuccess) throws IOException {
        if (newTradeSuccess != null && newTradeSuccess.equals("true")) {
            transactionPresenter.newTradeSuccess(model);
        }

        model.addAttribute("cancelTransactionSuccess", cancelTransactionSuccess);
        model.addAttribute("cancelTransactionForm", new CancelTransactionForm());
        int userId = this.getLoggedInUserId(request);

        return transactionPresenter.transHistoryPresenter(model, userId);
    }

}
