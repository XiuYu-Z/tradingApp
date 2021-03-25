package usecases.command.action;

import entities.History;
import persistence.PersistenceInterface;
import usecases.command.exceptions.CommandExecutionException;
import usecases.items.ItemEditor;
import usecases.items.ItemFetcher;

import java.io.IOException;

/**
 * An undoable action of adding wish list
 */
public class AddToWishlist implements Undoable {

    /**
     * Class dependencies
     */
    PersistenceInterface gateway;

    /**
     * The editor of items
     */
    ItemEditor itemEditor;

    /**
     * The fetcher of items
     */
    ItemFetcher itemFetcher;

    /**
     * Initializes this class.
     *
     * @param itemEditor  the editor of items
     * @param itemFetcher the fetcher of items.
     * @param gateway     persistence of Object which is used to read and edit information of entities
     */
    public AddToWishlist(ItemEditor itemEditor, ItemFetcher itemFetcher, PersistenceInterface gateway) {
        this.gateway = gateway;
        this.itemEditor = itemEditor;
        this.itemFetcher = itemFetcher;
    }


    /**
     * Add new item to wish list and create a history of it for admin to read
     *
     * @param itemId id of item
     * @param userId id of user
     * @throws IOException An IOException
     */
    public void execute(int itemId, int userId) throws IOException {
        itemEditor.addItemToWishlist(itemId, userId);
        History history = new History();
        history.addData("itemId", itemId);
        history.addData("userId", userId);
        history.setActionName(this.getClass().getName());
        history.setDisplayString("Added item id" + itemId + " to wishlist of user with id " + userId);
        gateway.create(history, History.class);
    }

    /**
     * Undo an action by recalling a history
     *
     * @param history History that save the undoable action to undo it
     * @throws CommandExecutionException A wrapper class wrapping an underlying exception.
     */
    public void undo(History history) throws CommandExecutionException {
        try {
            itemEditor.removeItemFromWishlist((Integer) history.
                    getData("itemId"), (Integer) history.getData("userId"));
            history.setUndone(true);
            String oldMessage = history.getDisplayString();
            String newMessage = oldMessage + " has been undone";
            history.setDisplayString(newMessage);
            gateway.update(history, History.class);
        } catch (Exception e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Return whether this history can be recalled or the action can be undo
     *
     * @param history History that save the undoable action to undo it
     * @return whether this history can be recalled or the action can be undo
     */
    public boolean canUndo(History history) {

        if (history.isUndone()) return false;

        int itemId = (int) history.getData("itemId");
        int userId = (int) history.getData("userId");

        try {
            return itemFetcher.query().inWishlistOf(userId).getIds().contains(itemId);

        } catch (IOException e) {
            return false;
        }
    }

}
