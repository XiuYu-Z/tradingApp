package usecases.alerts;

import entities.Item;
import persistence.PersistenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents an alert to admin when a user requests to add an Item to Inventory
 */
public class AddInventoryAlert extends SystemAlert {

    private final PersistenceInterface gateway;


    /**
     * To create an instance of AddInventoryAlert
     *
     * @param gateway which gives access to the stored data
     */
    public AddInventoryAlert(PersistenceInterface gateway) {
        this.gateway = gateway;
    }

    /**
     * To check if the item with the input id needs to be alerted to the admin
     *
     * @param itemId of the item
     * @return true iff the item is waiting for admin to confirm to be added to inventory
     * @throws IOException if there is a IO error
     */
    @Override
    public boolean needAlert(int itemId) throws IOException {
        List<Integer> items = new ArrayList<>();
        items.add(itemId);
        return !gateway.get(items, Item.class).get(0).isVisible();
    }


    /**
     * To get the name of the alert
     *
     * @return the name of this alert
     */
    @Override
    public String getAlert() {
        return "AddInventoryAlert";
    }

}
