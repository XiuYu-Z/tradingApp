package usecases.command.action;

import entities.History;
import usecases.command.exceptions.CommandExecutionException;

/**
 * Interface about an undoable action
 */

public interface Undoable extends Actionable {

    /**
     * The method to undo an action by recalling its history
     *
     * @param history History of an action
     * @throws CommandExecutionException A wrapper class wrapping an underlying exception.
     */
    void undo(History history) throws CommandExecutionException;

}