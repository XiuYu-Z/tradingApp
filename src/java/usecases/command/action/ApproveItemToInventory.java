package usecases.command.action;

import entities.History;
import persistence.PersistenceInterface;
import usecases.command.exceptions.CommandExecutionException;
import usecases.items.ItemEditor;
import usecases.items.ItemFetcher;
import usecases.trade.TransactionFetcher;

import java.io.IOException;

/**
 * An undoable action of approving item to inventory for admin
 */
public class ApproveItemToInventory implements Undoable {

    /**
     * The editor of items
     */
    private final ItemEditor itemEditor;

    /**
     * Class dependencies
     */
    private final PersistenceInterface gateway;

    /**
     * The fetcher of items
     */
    private final ItemFetcher itemFetcher;

    /**
     * The fetcher of transaction
     */
    private final TransactionFetcher transactionFetcher;


    /**
     * Initializes this class.
     *
     * @param itemEditor         the editor of items
     * @param itemFetcher        the fetcher of items.
     * @param transactionFetcher the fetcher of transaction
     * @param gateway            persistence of Object which is used to read and edit information of entities
     */
    public ApproveItemToInventory(ItemEditor itemEditor, ItemFetcher itemFetcher, TransactionFetcher transactionFetcher,
                                  PersistenceInterface gateway) {
        this.itemEditor = itemEditor;
        this.gateway = gateway;
        this.itemFetcher = itemFetcher;
        this.transactionFetcher = transactionFetcher;
    }

    /**
     * Approving an item and save this action as history for admin to read
     *
     * @param itemId id of item
     * @throws IOException An IOException
     */
    public void execute(int itemId) throws IOException {
        itemEditor.approveItem(itemId);
        History history = new History();
        history.addData("itemId", itemId);
        history.setActionName(this.getClass().getName());
        history.setDisplayString("Approve item with id " + itemId);
        gateway.create(history, History.class);
    }

    /**
     * Undo an action by recalling a history
     *
     * @param history History that save the undoable action to undo it
     * @throws CommandExecutionException A wrapper class wrapping an underlying exception.
     */
    @Override
    public void undo(History history) throws CommandExecutionException {
        try {
            itemEditor.disapproveItem((Integer) history.getData("itemId"));
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
     * Return whether this history can be recalled or the action can be undone
     *
     * @param history History that save the undoable action to undo it
     * @return whether this history can be recalled or the action can be undo
     */
    @Override
    public boolean canUndo(History history) {
        try {
            int itemId = (Integer) history.getData("itemId");
            if (itemFetcher.query().onlyApproved()
                    .notDeleted().heldByOwner().ownedByUnfrozenUser().getIds().
                            contains(itemId)) {
                return transactionFetcher.query().involvesItem(itemId).getTransactions().isEmpty();
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

}
