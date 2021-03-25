package controller.forms;

public class UndoForm {

    private int historyId;

    /**
     * Gets the id of the historical action we will undo.
     *
     * @return the id of the historical action we will undo.
     */
    public int getHistoryId() {
        return this.historyId;
    }

    /**
     * Sets the id of the historical action we will undo.
     *
     * @param id the id of the historical action we will undo.
     */
    public void setHistoryId(int id) {
        this.historyId = id;
    }


}
