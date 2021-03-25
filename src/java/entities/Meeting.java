package entities;

import persistence.relations.HasRelations;

import java.time.LocalDate;
import java.util.*;


/**
 * Represents an Meeting.
 */

public class Meeting extends AbstractBaseEntity implements HasRelations {

    /**
     * The key of this class
     */
    private int meetingId;

    /**
     * A list of meeting edits
     */
    private final List<LocalDate> times = new ArrayList<>();

    /**
     * The location of this meeting hold
     */
    private final List<String> locations = new ArrayList<>();

    /**
     * List of editors
     */
    private final List<Integer> editorIds = new ArrayList<>();

    /**
     * List of parties who agree to this meeting
     */
    private boolean agreedTo;


    /**
     * Save the userIds of the users who have confirmed this meeting has occured in real life.
     */
    private final Set<Integer> confirmedBy = new HashSet<>();


    /**
     * Whether another meeting exists
     */
    private boolean hasSecondMeeting;


    /**
     * constructor with out input id
     * Create a meeting with input information and set the number of two users's choices to 0,
     *
     * @param time     the date when this meeting begin
     * @param location the location of this meeting made
     * @param editorId the id of person who suggest this meeting
     */
    public Meeting(LocalDate time, String location, int editorId) {
        this.meetingId = 0; // set the default id to 0
        this.locations.add(location);
        this.times.add(time);
        this.editorIds.add(editorId);
        this.agreedTo = false;
        this.hasSecondMeeting = false;
    }

    /**
     * get the time when this meeting hold
     *
     * @return the date of this meeting
     */
    public LocalDate getTime() {
        return this.times.get(times.size() - 1);
    }

    /**
     * change the time of this meeting
     *
     * @param time set new time to this meeting
     */
    public void editTime(LocalDate time) {
        this.times.add(time);
    }

    /**
     * Return true if the time has passed.
     *
     * @return
     */
    public boolean hasPassed() {

        return LocalDate.now().isAfter(this.getTime());
    }

    /**
     * get the location where this meeting hold
     *
     * @return the location of this meeting
     */
    public String getLocation() {
        return this.locations.get(locations.size() - 1);
    }

    /**
     * change the location of this meeting
     *
     * @param location set new location to this meeting
     */
    public void editLocation(String location) {
        this.locations.add(location);
    }

    /**
     * get suggestion maker's od
     *
     * @return the id of one who make suggestion
     */
    public int getLastEditor() {
        return this.editorIds.get(editorIds.size() - 1);
    }

    /**
     * set new suggestion maker's od
     *
     * @param userId suggestion maker's od
     */
    public void setLastEditor(int userId) {
        this.editorIds.add(userId);
    }

    /**
     * Checks that the last editor is the userId
     *
     * @param userId The user id of the editor
     * @return True iff this user was the last editor of the meeting
     */
    public boolean isLastEditor(int userId) {
        return this.getLastEditor() == userId;
    }

    /**
     * get how many times did two users make choices
     *
     * @param userId the user id
     * @return the user's id who make choices
     */
    public int getNumOfEditsByUser(int userId) {
        return Collections.frequency(this.editorIds, userId);
    }

    /**
     * Mark that it's been agreed to.
     */
    public void markAgreed() {
        this.agreedTo = true;
    }

    /**
     * Checks if the meeting has been agreed to
     *
     * @return Whether the meeting has been agreed to
     */
    public boolean isAgreedTo() {
        return this.agreedTo;
    }

    /**
     * Mark that the meeting has been confirmed by a particular user.
     *
     * @param userId The user id of the person who is marking this meeting
     */
    public void markConfirmed(int userId) {
        this.confirmedBy.add(userId);
    }

    /**
     * get whether this meeting is completed or not
     *
     * @return whether this meeting is complete or not
     */
    public boolean isComplete() {
        for (int userId : editorIds) {
            if (!this.confirmedBy.contains(userId)) return false;
        }
        return true;
    }

    /**
     * get whether this meeting is confirm by the user
     *
     * @param userId of the user
     * @return whether this meeting is confirm by the user
     */
    public boolean isConfirmBy(int userId) {
        return this.confirmedBy.contains(userId);
    }

    /**
     * Whether there is another meeting after this.
     *
     * @return True iff there is another meeting after this.
     */
    public boolean hasSecondMeeting() {
        return hasSecondMeeting;
    }

    /**
     * Set whether there is another meeting after this.
     *
     * @param hasSecondMeeting Whether there is another meeting after this.
     */
    public void setSecondMeeting(boolean hasSecondMeeting) {
        this.hasSecondMeeting = hasSecondMeeting;
    }

    /**
     * A method to get the key to this item, which is the id
     *
     * @return the id of meeting
     */
    @Override
    public int getKey() {
        return meetingId;
    }

    /**
     * Set a new id to meetingId
     *
     * @param id set new id to this meeting
     */
    @Override
    public void setKey(int id) {
        this.meetingId = id;
    }

    /**
     * get the relation defined currently it's not being used
     *
     * @return
     */
    @Override
    public Map<String, List<Integer>> getDefinedRelations() {
        Map<String, List<Integer>> relationMap = new HashMap<>();
        return relationMap;
    }

}
