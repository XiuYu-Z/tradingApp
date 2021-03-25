package presenter;

import controller.forms.MeetingApproveForm;
import controller.forms.MeetingEditForm;
import entities.Meeting;
import org.springframework.ui.Model;
import usecases.TradingFacade;
import usecases.trade.TransactionQueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MeetingPresenter {

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;

    /**
     * Instanties this class.
     *
     * @param tradingFacade The facade holding trading related functionality.
     */
    public MeetingPresenter(TradingFacade tradingFacade) {
        this.tradingFacade = tradingFacade;
    }

    /**
     * Handles showing meetings.
     *
     * @param model  A model that holds dynamic data.
     * @param userId The user id's whose meetings we are examining
     * @return The view
     * @throws IOException An IOException
     */
    public String viewMeetingsPresenter(Model model, int userId) throws IOException {
        TransactionQueryBuilder query = tradingFacade.fetchTransactions().query().involvesUser(userId);
        Map<Integer, List<Meeting>> meetings = query.getMeetings();
        List<Meeting> meetingList = query.getMeetingsList();

        //Bind our dynamic variables to the model, for HTML display
        model.addAttribute("approveMeetingForm", new MeetingApproveForm());
        model.addAttribute("confirmMeetingForm", new MeetingApproveForm());
        model.addAttribute("myMeetings", meetings);
        model.addAttribute("usersEditTurn", this.tradingFacade.manageMeetings().usersEditTurn(meetingList, userId));
        model.addAttribute("editPermissions", this.tradingFacade.manageMeetings().getEditPermissions(meetingList, userId));
        model.addAttribute("confirmPermissions", this.tradingFacade.manageMeetings().getConfirmPermissions(meetingList));
        model.addAttribute("tooManyEditions", this.tradingFacade.manageMeetings().getUserEditTooMany(meetingList, userId));
        model.addAttribute("userId", userId);

        return "meetings/viewMeetings";
    }

    /**
     * Shows the edit meetings page.
     *
     * @param model     A model that holds dynamic data.
     * @param meetingId The meeting id that we are editing
     * @return The view
     * @throws IOException An IOException
     */
    public String editMeetingsPresenter(Model model, int meetingId) throws IOException {
        Meeting meeting = tradingFacade.manageMeetings().getMeeting(meetingId);

        //Bind our dynamic variables to the model, for HTML display
        model.addAttribute("meeting", meeting);

        return "meetings/editMeeting";
    }

    /**
     * Binds the meeting edit form.
     *
     * @param model A model that holds dynamic data.
     */
    public void meetingEditForm(Model model) {
        model.addAttribute("meetingEditForm", new MeetingEditForm());
    }

    /**
     * Binds that the input was invalid.
     *
     * @param model A model that holds dynamic data.
     */
    public void invalidInput(Model model) {
        model.addAttribute("invalidInput", true);
    }

    /**
     * Binds that the meeting date was in the past.
     *
     * @param model A model that holds dynamic data.
     */
    public void dateFromPast(Model model) {
        model.addAttribute("dateFromPast", true);
    }
}
