package controller.forms;

public class MeetingApproveForm {

    private int meetingId;

    /**
     * Gets the meeting id that is to be approved.
     *
     * @return the meeting id that is to be approved.
     */
    public int getMeetingId() {
        return this.meetingId;
    }

    /**
     * Sets the meeting id that is to be approved.
     *
     * @param meetingId the meeting id that is to be approved.
     */
    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

}
