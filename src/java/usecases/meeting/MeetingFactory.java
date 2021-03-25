package usecases.meeting;

import entities.Meeting;
import persistence.PersistenceInterface;
import persistence.exceptions.PersistenceException;
import usecases.meeting.exceptions.TooManyLocationsException;
import usecases.meeting.exceptions.TooManyTimesException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A use case for producing Meeting object only, which makes use of the decoration pattern.
 *
 * @author XiuYu Zhang
 * @version 2 July 2020
 */
public class MeetingFactory {


    /**
     * Class dependencies
     */
    private PersistenceInterface gateway;


    /**
     * Instance variables
     */
    private boolean temporary;
    private List<LocalDate> timeArray;
    private List<String> locationArray;
    private int duration;
    private int currentSuggestionMaker;


    /**
     * To create an instance of MeetingFactory for temporary transaction of duration 1 mouth.
     *
     * @param gateway to access the stored data
     */
    public MeetingFactory(PersistenceInterface gateway) {
        this.temporary = true;
        this.timeArray = new ArrayList<>();
        this.locationArray = new ArrayList<>();
        this.duration = 1; // 1 month
        this.gateway = gateway;

    }


    /**
     * To get the ids of the instantiated meetings, then the MeetingFactory resets.
     *
     * @return a list of ids of created meetings
     * @throws IOException          if there is an IO error
     * @throws PersistenceException if there is an exception thrown during persisting to database.
     */
    public List<Integer> getMeetingId() throws IOException, PersistenceException {
        List<Integer> ids = new ArrayList<>();
        for (Meeting meeting : init()) {
            ids.add(meeting.getKey());
        }
        reset();
        return ids;
    }


    /**
     * To get the instantiated meetings, then the MeetingFactory resets.
     *
     * @return a list of meeting objects created
     * @throws IOException          if there is an IO error
     * @throws PersistenceException if there is an exception thrown during persisting to database.
     */
    public List<Meeting> getMeeting() throws IOException, PersistenceException {
        List<Meeting> meetings = init();
        reset();
        return meetings;
    }


    /**
     * Set the MeetingFactory to produce meetings for permanent transaction
     *
     * @return the MeetingFactory itself
     */
    public MeetingFactory permanent() {
        this.temporary = false;
        return this;
    }

    /**
     * Change the duration between meetings for temporary transaction
     *
     * @param duration the duration between meetings
     * @return the MeetingFactory itself
     */
    public MeetingFactory setDuration(int duration) {
        this.duration = duration;
        return this;
    }


    /**
     * Set the MeetingFactory to produce meetings for temporary transaction
     *
     * @return the MeetingFactory itself
     */
    public MeetingFactory temporary() {
        this.temporary = true;
        return this;
    }


    /**
     * Fill location into the first meeting in the list which does not have a location
     *
     * @param location of the meeting
     * @return the MeetingFactory itself
     * @throws TooManyLocationsException when trying to add extra location
     */
    public MeetingFactory fillLocation(String location) throws TooManyLocationsException {
        if (this.locationArray.size() >= 1 && !this.temporary) {
            throw new TooManyLocationsException();
        }
        this.locationArray.add(location);
        return this;
    }


    /**
     * Fill time into the first meeting in the list which does not have a location
     *
     * @param time of the meeting
     * @return the MeetingFactory itself
     * @throws TooManyTimesException when trying to add extra time
     */
    public MeetingFactory fillTime(LocalDate time) throws TooManyTimesException {
        if (!this.timeArray.isEmpty()) {
            throw new TooManyTimesException();
        }
        this.timeArray.add(time);
        return this;
    }


    /**
     * Allows other classes to reset the factory configuration.
     *
     * @return the MeetingFactory itself
     */
    public MeetingFactory reset() {
        this.temporary = true;
        this.timeArray = new ArrayList<>();
        this.locationArray = new ArrayList<>();
        this.duration = 1; // 1 mouth
        return this;
    }


    /**
     * Set the user who is the last one who made changes to the meeting
     *
     * @param userId of the user
     * @return the MeetingFactory itself
     */
    public MeetingFactory setCurrentSuggestionMaker(int userId) {
        this.currentSuggestionMaker = userId;
        return this;
    }


/********************************************************************************************************
 *
 * Helper Methods
 *
 *********************************************************************************************************/


    /**
     * A private method used as helper function, which creates meetings according
     * to the attributes stored in the Meeting factory.
     *
     * @return a list of meeting
     * @throws IOException An IOException
     */
    private List<Meeting> init() throws IOException {
        List<Meeting> arr = new ArrayList<>();
        Meeting firstMeeting = new Meeting(this.timeArray.get(0), this.locationArray.get(0),
                this.currentSuggestionMaker);

        arr.add(firstMeeting);

        if (this.temporary) {
            Meeting secondMeeting = new Meeting(this.timeArray.get(0).plusMonths(duration), this.locationArray.get(1),
                    this.currentSuggestionMaker);
            secondMeeting.setSecondMeeting(true);
            arr.add(secondMeeting);
        }
        return gateway.create(arr, Meeting.class);
    }


}
