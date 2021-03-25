package usecases.command.action;

import entities.History;

/**
 * Interface about an actionable action, like adding inventory, creating new transaction, these actions
 * will be saved as the history for admin to read
 */
public interface Actionable {

    /**
     * The method to get whether this history is able to be undone
     *
     * @param history History of an action
     */
    boolean canUndo(History history);

}
