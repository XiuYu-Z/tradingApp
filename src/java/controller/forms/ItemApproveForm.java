package controller.forms;

import java.util.List;

public class ItemApproveForm {

    private List<Integer> items;

    /**
     * Gets the items that are being approved.
     *
     * @return A list of items.
     */
    public List<Integer> getItems() {
        return items;
    }

    /**
     * Sets the items that are being approved.
     *
     * @param items A list of items.
     */
    public void setItems(List<Integer> items) {
        this.items = items;
    }


}
