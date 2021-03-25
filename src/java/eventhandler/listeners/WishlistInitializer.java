package eventhandler.listeners;

import eventhandler.events.UserRegisteredEvent;
import persistence.exceptions.PersistenceException;
import usecases.items.ItemEditor;

import java.io.IOException;


/**
 * Initializes a wish list when a user registers.
 */
public class WishlistInitializer implements HandlesUserRegistered {


    private final ItemEditor itemEditor;

    /**
     * Initializes this class.
     *
     * @param itemEditor The item editor dependency.
     */
    public WishlistInitializer(ItemEditor itemEditor) {
        this.itemEditor = itemEditor;
    }


    /**
     * We use the itemEditor to create a new wish list for this user.
     *
     * @param event
     */
    @Override
    public void handle(UserRegisteredEvent event) {
        try {
            this.itemEditor.initializeWishlist(event.getUserId());
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
