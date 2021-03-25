package controller.forms;

import javax.validation.constraints.NotBlank;

public class InitiateTradeForm {

    private String tradeDuration;
    private String tradeType;
    private String borrowItemId;
    private String lendItemId;

    @NotBlank(message = "Please enter a location before proceeding.")
    private String meetingLocation;

    @NotBlank(message = "Please enter a meeting date before proceeding.")

    private String meetingDate;
    private String meetingLocation2;
    private String meetingDate2;

    /**
     * Gets whether this trade is permanent or temporary
     *
     * @return whether this trade is permanent or temporary
     */
    public String getTradeDuration() {
        return this.tradeDuration;
    }

    /**
     * Sets whether this trade is permanent or temporary.
     *
     * @param tradeDuration whether this trade is permanent or temporary
     */
    public void setTradeDuration(String tradeDuration) {
        this.tradeDuration = tradeDuration;
    }

    /**
     * Gets whether the trade is one way or two way.
     *
     * @return whether this trade is permanent or temporary
     */
    public String getTradeType() {
        return this.tradeType;
    }

    /**
     * Sets whether the trade is one way or two way.
     *
     * @param tradeType whether this trade is permanent or temporary
     */
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    /**
     * Gets the item id of the item being borrowed.
     *
     * @return the item id of the item being borrowed.
     */
    public String getBorrowItemId() {
        return this.borrowItemId;
    }

    /**
     * Sets the item id of the item being borrowed.
     *
     * @param borrowItemId sets the item id of the item being borrowed.
     */
    public void setBorrowItemId(String borrowItemId) {
        this.borrowItemId = borrowItemId;
    }

    /**
     * Gets the item id of the item being lent.
     *
     * @return the item id of the item being lent.
     */
    public String getLendItemId() {
        return this.lendItemId;
    }

    /**
     * Sets the item id of the item being lent.
     *
     * @param lendItemId the item id of the item being lent.
     */
    public void setLendItemId(String lendItemId) {
        this.lendItemId = lendItemId;
    }

    /**
     * Gets the meeting location.
     *
     * @return the meeting location.
     */
    public String getMeetingLocation() {
        return this.meetingLocation;
    }

    /**
     * Sets the meeting location.
     *
     * @param meetingLocation the meeting location.
     */
    public void setMeetingLocation(String meetingLocation) {
        this.meetingLocation = meetingLocation;
    }

    /**
     * Gets the meeting date.
     *
     * @return the meeting date.
     */
    public String getMeetingDate() {
        return this.meetingDate;
    }

    /**
     * Sets the meeting date.
     *
     * @param meetingDate the meeting date.
     */
    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    /**
     * Gets the second meeting location.
     *
     * @return the second meeting location.
     */
    public String getMeetingLocation2() {
        return this.meetingLocation2;
    }

    /**
     * Sets the second meeting location.
     *
     * @param meetingLocation2 sets the second meeting location.
     */
    public void setMeetingLocation2(String meetingLocation2) {
        this.meetingLocation2 = meetingLocation2;
    }

    /**
     * Gets the second meeting date.
     *
     * @return the second meeting date.
     */
    public String getMeetingDate2() {
        return this.meetingDate2;
    }

    /**
     * Sets the second meeting date.
     *
     * @param meetingDate2 the second meeting date.
     */
    public void setMeetingDate2(String meetingDate2) {
        this.meetingDate2 = meetingDate2;
    }


}
