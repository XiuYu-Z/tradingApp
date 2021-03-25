package usecases.meeting;

import entities.Meeting;
import entities.Trade;
import entities.Transaction;
import persistence.PersistenceInterface;
import persistence.relations.MapsRelations;
import usecases.config.ListensForConfig;
import usecases.meeting.exceptions.EditAgreedMeetingException;
import usecases.meeting.exceptions.MeetingException;
import usecases.meeting.exceptions.TooManyEditsException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * A use case for Meeting, which deals with all operations relating to Meeting except instantiation.
 */
public class MeetingManager implements ListensForConfig {

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;
    private final MapsRelations relationMapper;


    /**
     * Instance variables
     */
    private int editThreshold; // set by configManager, see updateConfig


    /**
     * To create an instance of MeetingManager
     *
     * @param gateway to access the stored data
     */
    public MeetingManager(PersistenceInterface gateway, MapsRelations relationMapper) {
        this.gateway = gateway;
        this.relationMapper = relationMapper;
    }

    /**
     * Returns a meeting with the given meeting id.
     *
     * @param meetingId meetingId
     * @return A Meeting object
     * @throws IOException IOException
     */
    public Meeting getMeeting(int meetingId) throws IOException {
        List<Integer> ids = new ArrayList<>();
        ids.add(meetingId);
        return gateway.get(ids, Meeting.class).get(0);
    }

    /**
     * Returns all meeting objects.
     *
     * @return All meeting objects
     * @throws IOException IOException
     */
    public List<Meeting> getAll() throws IOException {
        return this.gateway.all(Meeting.class);
    }


    /**
     * To allow a user to edit a meeting
     *
     * @param userId    of the user who wants to make an edition
     * @param meetingId of the intended meeting
     * @param location  which the user wants to change to
     * @param time      which the user wants to change to
     * @return true iff the user wants to change the first meeting
     * @throws MeetingException Exception related to manipulating meetings
     * @throws IOException      if there is an IO error
     */
    public boolean editMeeting(int userId, int meetingId, String location, LocalDate time) throws
            MeetingException, IOException {

        Meeting meeting = this.getMeeting(meetingId);

        // To check if the user has reached the edit limits
        if (!userCanEdit(userId, meeting)) {
            throw new TooManyEditsException();
        }

        // To check if the user is trying to edit an agreed meeting
        if (meeting.isAgreedTo()) throw new EditAgreedMeetingException();

        // Starts making edits
        meeting.editLocation(location);

        // a user cannot change the time for the second meeting, as it is given by the system
        if (!meeting.hasSecondMeeting()) {
            meeting.editTime(time);
        }

        meeting.setLastEditor(userId);
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);
        gateway.update(meetings, Meeting.class);

        return !meeting.hasSecondMeeting();
    }




    /**
     * To let a user to agree to the meeting, instead of making further change
     *
     * @param meetingId of the meeting
     * @throws IOException if there is an IO error
     */
    public void agreeToMeeting(int meetingId) throws IOException {
        Meeting meeting = this.getMeeting(meetingId);
        meeting.markAgreed();
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);
        gateway.update(meetings, Meeting.class);
    }


    /**
     * To let a user to confirm that the meeting took place in real life
     *
     * @param meetingId of the meeting
     * @param userId    of the user
     * @throws IOException if there is an IO error
     */
    public void markConducted(int meetingId, int userId) throws IOException {
        Meeting meeting = getMeeting(meetingId);
        meeting.markConfirmed(userId);
        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);
        gateway.update(meetings, Meeting.class);
    }


    /**
     * A list of meetings ids where its this user's turn to edit.
     *
     * @param meetings A list of meetings
     * @param userId   The user id
     * @return A list of meeting ids where it's this users to edit.
     */
    public List<Integer> usersEditTurn(List<Meeting> meetings, int userId) {
        List<Integer> editTurn = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (!meeting.isLastEditor(userId)) {
                editTurn.add(meeting.getKey());
            }
        }
        return editTurn;
    }


    /**
     * Returns a list of meetings this userId is able to edit
     *
     * @param meetings A list of Meeting objects
     * @param userId   A unique user id
     * @return A list of Meeting ids
     */
    public List<Integer> getEditPermissions(List<Meeting> meetings, int userId) {
        List<Integer> editPermissions = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (meeting.getLastEditor() != userId && !meeting.isAgreedTo() && !meeting.isComplete()) {
                editPermissions.add(meeting.getKey());
            }
        }
        return editPermissions;
    }

    /**
     * Returns a list of meetings this userId is unable to edit due to too many editions
     *
     * @param meetings A list of Meeting objects
     * @param userId   A unique user id
     * @return A list of Meeting ids
     */
    public List<Integer> getUserEditTooMany(List<Meeting> meetings, int userId) {
        List<Integer> violation = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if (!userCanEdit(userId, meeting)) violation.add(meeting.getKey());
        }
        return violation;
    }


    /**
     * Returns a list of meeting confirmation permissions.
     *
     * @param meetings A list of meetings
     * @return A map with the meeting id as key and the list of users who can confirm this meeting as value
     */
    public Map<Integer, List<Integer>> getConfirmPermissions(List<Meeting> meetings) {
        Map<Integer, List<Integer>> confirmPermissions = new HashMap<>();
        for (Meeting meeting : meetings) {
            List<Integer> users = getUsers(meeting);
            if (meeting.isAgreedTo() && !meeting.isComplete() && meeting.hasPassed()) {
                confirmPermissions.put(meeting.getKey(), new ArrayList<>());
                for (int user : users) {
                    if (!meeting.isConfirmBy(user)) {
                        List<Integer> current = confirmPermissions.get(meeting.getKey());
                        current.add(user);
                        confirmPermissions.put(meeting.getKey(), current);
                    }
                }
            }
        }
        return confirmPermissions;
    }


    /**
     * Updates the maximum number of editions allowed
     *
     * @param config A map with the key value pairs of all the configurable options.
     */
    @Override
    public void updateConfig(Map<String, String> config) {
        this.editThreshold = parseInt(config.get("maxMeetingEdits"));
    }


    /********************************************************************************************************
     *
     * Helper Methods
     *
     *********************************************************************************************************/

    private List<Integer> getUsers(Meeting meeting) {
        List<Integer> users = new ArrayList<>();
        try {
            Transaction transaction = meeting.relation(relationMapper, "transactions", Transaction.class).get(0);
            Trade trade = transaction.relation(relationMapper, "trades", Trade.class).get(0);
            users.add(trade.getLenderId());
            users.add(trade.getBorrowerId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private boolean userCanEdit(int userId, Meeting meeting) {
        return meeting.getNumOfEditsByUser(userId) < editThreshold;
    }


}
