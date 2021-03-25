package controller;

import controller.forms.MeetingApproveForm;
import controller.forms.MeetingEditForm;
import entities.Meeting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import presenter.MeetingPresenter;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.meeting.exceptions.MeetingException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;


@Controller
public class MeetingController extends AbstractBaseController {

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final MeetingPresenter meetingPresenter;

    /**
     * create new meeting controller
     *
     * @param tradingFacade    A facade that contains trading related functionality.
     * @param systemFacade     A facade that contains system related functionality.
     * @param meetingPresenter The meeting presenter
     */
    public MeetingController(TradingFacade tradingFacade, SystemFacade systemFacade, MeetingPresenter meetingPresenter) {
        super(systemFacade);
        this.tradingFacade = tradingFacade;
        this.meetingPresenter = meetingPresenter;
    }

    /**
     * Shows the all the meetings related to this user.
     *
     * @param model   Holds data to send to the view.
     * @param request An object holding the HTTP request.
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/meetings/view")
    public String viewMeetings(Model model, HttpServletRequest request) throws IOException {
        int userId = this.getLoggedInUserId(request);
        return meetingPresenter.viewMeetingsPresenter(model, userId);
    }

    /**
     * Shows the form to edit a meeting.
     *
     * @param model     Holds data to send to the view.
     * @param meetingId The unique key for a meeting.
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/meetings/edit")
    public String editMeetings(Model model, @RequestParam int meetingId, @RequestParam(required = false) boolean validationRedirect)
            throws IOException {

        if (!validationRedirect) {
            meetingPresenter.meetingEditForm(model);
        }
        return meetingPresenter.editMeetingsPresenter(model, meetingId);

    }


    /**
     * Handles the form submission for editing a meeting.
     *
     * @param form    The form holding the front-end input.
     * @param request An object holding the HTTP request.
     * @return A url to which we redirect.
     * @throws MeetingException An exception related to manipulating a meeting
     * @throws IOException      An IOException
     */
    @PostMapping("/meetings/edit")
    public String editMeetings(@Valid @ModelAttribute MeetingEditForm form,
                               Errors errors, HttpServletRequest request, Model model)
            throws MeetingException, IOException {

        int meetingId = form.getMeetingId();

        if (errors.hasErrors()) {
            meetingPresenter.invalidInput(model);
            return editMeetings(model, meetingId, true);
        }

        LocalDate meetingDate = LocalDate.parse(form.getMeetingDate());
        if (meetingDate.compareTo(LocalDate.now()) <= 0) {
            meetingPresenter.dateFromPast(model);
            return editMeetings(model, meetingId, true);
        }

        int userId = this.getLoggedInUserId(request);
        String meetingLocation = form.getMeetingLocation();

        tradingFacade.manageMeetings().editMeeting(userId, meetingId, meetingLocation, meetingDate);

        return "redirect:/meetings/view";
    }


    /**
     * Handles approving a meeting.
     *
     * @param form    The form holding the front-end input.
     * @param request An object holding the HTTP request.
     * @return A url to which we redirect.
     * @throws IOException An IOException
     */
    @PostMapping("/meetings/approve")
    public String agreeMeetings(@ModelAttribute MeetingApproveForm form, HttpServletRequest request) throws IOException {

        int meetingId = form.getMeetingId();
        tradingFacade.manageMeetings().agreeToMeeting(meetingId);

        return "redirect:/meetings/view";
    }

    /**
     * Handles confirming a meeting.
     *
     * @param form    The form holding the front-end input.
     * @param request An object holding the HTTP request.
     * @return A url to which we redirect.
     * @throws IOException An IOException
     */
    @PostMapping("/meetings/confirm")
    public String confirmMeetings(@ModelAttribute MeetingApproveForm form, HttpServletRequest request) throws IOException {

        int userId = this.getLoggedInUserId(request);
        int meetingId = form.getMeetingId();
        tradingFacade.manageMeetings().markConducted(meetingId, userId);
        Meeting meeting = tradingFacade.manageMeetings().getMeeting(meetingId);
        if (meeting.isComplete()) tradingFacade.manageTransactions().performMeeting(meetingId);


        return "redirect:/meetings/view";
    }


}
