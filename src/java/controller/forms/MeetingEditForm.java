package controller.forms;

import javax.validation.constraints.NotBlank;

public class MeetingEditForm {

    private int meetingId;

    @NotBlank(message = "Please enter a meeting location before proceeding.")
    private String meetingLocation;

    @NotBlank(message = "Please enter a meeting date before proceeding.")
    private String meetingDate;

    /**
     * Gets the id of the meeting we are about to edit.
     *
     * @return the id of the meeting we are about to edit.
     */
    public int getMeetingId() {
        return this.meetingId;
    }

    /**
     * Sets the id of the meeting we are about to edit.
     *
     * @param meetingId the id of the meeting we are about to edit.
     */
    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    /**
     * Gets the new meeting location.
     *
     * @return the new meeting location.
     */
    public String getMeetingLocation() {
        return this.meetingLocation;
    }

    /**
     * Sets the new meeting location.
     *
     * @param meetingLocation the new meeting location.
     */
    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    /**
     * Gets the new meeting date.
     *
     * @return the new meeting date.
     */
    public String getMeetingDate() {
        return this.meetingDate;
    }

    /**
     * Sets the new meeting date.
     *
     * @param meetingDate the new meeting date.
     */
    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

}
