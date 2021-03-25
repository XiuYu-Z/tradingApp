package usecases.command;

import entities.History;
import persistence.PersistenceInterface;
import usecases.command.action.Actionable;
import usecases.command.action.Undoable;
import usecases.command.exceptions.CommandExecutionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The manager of commands including all actionable and undoable actions
 */
public class CommandManager {

    /**
     * Class dependencies
     */
    PersistenceInterface gateway;

    /**
     * The methods to save all actionable action by maps
     */
    Map<String, Actionable> actionables = new HashMap<>();

    /**
     * Initializes this class.
     *
     * @param gateway     persistence of Object which is used to read and edit information of entities
     * @param actionables a list of all actionable actions
     */
    public CommandManager(PersistenceInterface gateway, List<Actionable> actionables) {
        this.gateway = gateway;
        for (Actionable actionable : actionables) {
            this.actionables.put(actionable.getClass().getName(), actionable);
        }
    }

    /**
     * Get all actionable actions's history
     *
     * @return a list of all histories
     * @throws IOException An IOException.
     */
    public List<History> allActions() throws IOException {
        return this.gateway.all(History.class);
    }

    /**
     * Returns whether the history can be undo'ed.
     *
     * @return A key value pair of the history id and whether it can be undone
     * @throws IOException An IOException.
     */
    public Map<Integer, Boolean> getUndoPermissions() throws IOException {

        List<History> allHistory = this.gateway.all(History.class);
        Map<Integer, Boolean> canUndo = new HashMap<>();

        for (History history : allHistory) {
            canUndo.put(history.getKey(), this.canUndo(history));
        }
        return canUndo;
    }

    /**
     * Undo an action by recalling a history
     *
     * @param historyId id of history
     * @throws CommandExecutionException A wrapper class wrapping an underlying exception.
     * @throws IOException               An IOException.
     */
    public void undo(int historyId) throws CommandExecutionException, IOException {
        History history = this.gateway.get(historyId, History.class);
        for (Map.Entry<String, Actionable> actionable : this.actionables.entrySet()) {
            String actionableName = actionable.getValue().getClass().getName();
            if (actionableName.equals(history.getActionName())) {
                if (actionable.getValue() instanceof Undoable) {
                    ((Undoable) actionable.getValue()).undo(history);
                }
            }
        }
    }


    /**
     * Return whether this history can be recalled or the action can be undo
     *
     * @param history History that save the undoable action to undo it
     * @return whether this history can be recalled or the action can be undo
     */
    public boolean canUndo(History history) {
        return this.actionables.get(history.getActionName()).canUndo(history);
    }


}
