package presenter;

import controller.forms.*;
import entities.History;
import entities.Item;
import entities.User;
import org.springframework.ui.Model;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.command.CommandManager;
import usecases.rules.RuleDoesNotExistException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminPresenter {

    /**
     * Class dependencies
     */
    private final SystemFacade systemFacade;
    private final TradingFacade tradingFacade;
    private final CommandManager commandManager;


    /**
     * Instantiates this presenter.
     *
     * @param tradingFacade  The facade holding trading related functionality.
     * @param systemFacade   The facade holding system related functionality.
     * @param commandManager Manages actions
     */
    public AdminPresenter(SystemFacade systemFacade, TradingFacade tradingFacade, CommandManager commandManager) {
        this.systemFacade = systemFacade;
        this.tradingFacade = tradingFacade;
        this.commandManager = commandManager;
    }

    /**
     * Shows freeze suggestions page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     * @throws IOException               An IOException
     * @throws RuleDoesNotExistException An exception that a particular system rule doesn't exist.
     */
    public String viewFreezeSuggestionPresenter(Model model) throws IOException, RuleDoesNotExistException {
        List<User> users = systemFacade.alert().getFreezeSuggestions();
        model.addAttribute("freezeSuggestions", users);
        model.addAttribute("freezeUserForm", new FreezeUserForm());
        return "admin/freezeSuggestions";
    }

    /**
     * Shows unfreeze suggestions page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     * @throws IOException               An IOException
     * @throws RuleDoesNotExistException An exception that a particular system rule doesn't exist.
     */
    public String viewUnfreezeRequestsPresenter(Model model) throws IOException, RuleDoesNotExistException {
        List<User> unfreezeRequests = systemFacade.alert().getUnfreezeRequests();
        model.addAttribute("unfreezeRequests", unfreezeRequests);
        model.addAttribute("unfreezeUserForm", new UnfreezeUserForm());
        return "admin/unfreezeRequests";
    }

    /**
     * Shows approve items page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     * @throws IOException An IOException
     */
    public String viewUnapprovedItemsPresenter(Model model) throws IOException {
        List<Item> items = tradingFacade.fetchItems().query().notDeleted().exceptApproved()
                .ownedByUnfrozenUser().getObjects();
        model.addAttribute("unapprovedItems", items);
        model.addAttribute("itemApproveForm", new ItemApproveForm());
        model.addAttribute("itemTagMap", systemFacade.tagManager().getItemTagMap());
        return "admin/approveItems";
    }

    /**
     * Shows the page to promote users.
     *
     * @param model          A model that holds dynamic data.
     * @param promoteSuccess Whether promoting was successful.
     * @return The view
     * @throws IOException An IOException
     */
    public String showUsersPresenter(Model model, Boolean promoteSuccess) throws IOException {
        model.addAttribute("promoteSuccess", promoteSuccess);
        List<User> users = systemFacade.users().all();
        model.addAttribute("users", users);
        return "admin/users";
    }

    /**
     * Shows the config page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String configPresenter(Model model) {
        Map<String, String> config = this.systemFacade.config().all();
        model.addAttribute("config", config);
        model.addAttribute("configForm", new ConfigForm());
        return "admin/config";
    }

    /**
     * Shows the undo page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     * @throws IOException An IOException
     */
    public String undoPresenter(Model model) throws IOException {
        List<History> actions = this.commandManager.allActions();
        Map<Integer, Boolean> canUndo = this.commandManager.getUndoPermissions();

        model.addAttribute("actions", actions);
        model.addAttribute("undoPermissions", canUndo);
        model.addAttribute("undoForm", new UndoForm());
        return "admin/all";
    }

    /**
     * Shows the reporting page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     * @throws IOException An IOException
     */
    public String handleReportingPresenter(Model model) throws IOException {
        List<List<Integer>> mostFrequentTradedItem = this.tradingFacade.manageTransactions().mostTradedItems();
        List<List<Integer>> userCredit = this.systemFacade.users().userHighCredit();
        model.addAttribute("allUsers", this.systemFacade.users().allById());
        model.addAttribute("items", tradingFacade.editItems().get(mostFrequentTradedItem.get(0)));
        model.addAttribute("itemFrequency", mostFrequentTradedItem.get(1));
        model.addAttribute("userCredit", userCredit.get(0));
        model.addAttribute("credit", userCredit.get(1));
        return "admin/reporting";
    }

    /**
     * Shows the demo page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     * @throws IOException An IOException
     */
    public String demoPresenter(Model model) throws IOException {
        model.addAttribute("demoForm", new MakeDemoForm());
        model.addAttribute("users", systemFacade.users().all());
        return "admin/demo";
    }

    /**
     * Binds makingDemoSuccess variable.
     *
     * @param model A model that holds dynamic data.
     */
    public void makeDemoSuccess(Model model) {
        model.addAttribute("makeDemoSuccess", true);
    }


}
