package controller;

import controller.forms.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import presenter.AdminPresenter;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.command.CommandManager;
import usecases.command.action.ApproveItemToInventory;
import usecases.command.exceptions.CommandExecutionException;
import usecases.rules.RuleDoesNotExistException;

import java.io.IOException;
import java.util.Map;


@Controller
public class AdminController extends AbstractBaseController {

    /**
     * Class dependencies
     */
    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;
    private final CommandManager commandManager;
    private final ApproveItemToInventory approveItemToInventory;
    private final AdminPresenter adminPresenter;


    /**
     * Creates a new admin controller
     *
     * @param tradingFacade          A facade that contains trading related functionality.
     * @param systemFacade           A facade that contains system related functionality.
     * @param commandManager         Manages commands
     * @param approveItemToInventory An action to approve items.
     * @param adminPresenter         The admin presenter
     */
    public AdminController(TradingFacade tradingFacade, SystemFacade systemFacade, CommandManager commandManager,
                           ApproveItemToInventory approveItemToInventory, AdminPresenter adminPresenter) {
        super(systemFacade);
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
        this.commandManager = commandManager;
        this.approveItemToInventory = approveItemToInventory;
        this.adminPresenter = adminPresenter;
    }


    /**
     * Shows admin panel.
     *
     * @return A string indicating which view to display.
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin/admin";
    }


/********************************************************************************************************
 *
 * Freeze a user
 *
 *********************************************************************************************************/

    /**
     * Shows the system generated freeze suggestions.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     * @throws IOException               AnIOException
     * @throws RuleDoesNotExistException If the input system rule does not exist
     */
    @GetMapping("/admin/freeze/view")
    public String viewFreezeSuggestions(Model model) throws IOException, RuleDoesNotExistException {
        return adminPresenter.viewFreezeSuggestionPresenter(model);
    }


    /**
     * Handles the freezing a user.
     *
     * @param form The form holding the front-end input.
     * @return A url to which we redirect.
     * @throws IOException An IOException.
     */
    @PostMapping("/admin/freeze/handle")
    public String handleFreeze(@ModelAttribute FreezeUserForm form) throws IOException {
        systemFacade.users().freezeUser(form.getUserId());
        return "redirect:/admin/freeze/view?freezeSuccess=true";
    }


/********************************************************************************************************
 *
 * Unfreeze a user
 *
 *********************************************************************************************************/


    /**
     * Shows the system generated freeze suggestions.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     * @throws IOException               AnIOException
     * @throws RuleDoesNotExistException If the input system rule does not exist
     */
    @GetMapping("/admin/unfreeze/view")
    public String viewUnfreezeRequests(Model model) throws IOException, RuleDoesNotExistException {
        return adminPresenter.viewUnfreezeRequestsPresenter(model);
    }


    /**
     * Handles unfreezing a user.
     *
     * @return A url to which to redirect.
     */
    @PostMapping("/admin/unfreeze/handle")
    public String handleUnfreeze(UnfreezeUserForm form) throws IOException {
        systemFacade.users().unFreezeUser(form.getUserId());
        return "redirect:/admin/unfreeze/view";
    }


/********************************************************************************************************
 *
 * Approve items
 *
 *********************************************************************************************************/

    /**
     * Shows items awaiting approval.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     * @throws IOException AnIOException
     */
    @GetMapping("/admin/items")
    public String viewUnapprovedItems(Model model) throws IOException {
        return adminPresenter.viewUnapprovedItemsPresenter(model);
    }

    /**
     * Handles the approval of an item.
     *
     * @param form The form holding the front-end input.
     * @return A url to which we redirect.
     * @throws IOException AnIOException
     */
    @PostMapping("/admin/items")
    public String handleApprovals(@ModelAttribute ItemApproveForm form) throws IOException {
        for (int itemId : form.getItems()) {
            approveItemToInventory.execute(itemId);
        }
        return "redirect:/admin";
    }


/********************************************************************************************************
 *
 * Promote Users
 *
 *********************************************************************************************************/

    /**
     * Shows all the users in the system.
     *
     * @param model          Holds data to send to the view.
     * @param promoteSuccess A parameter indicating whether the promotion succeeded or not.
     * @return A string indicating which view to display.
     * @throws IOException An IOException.
     */
    @GetMapping("/admin/users")
    public String showUsers(Model model, @RequestParam(required = false) boolean promoteSuccess) throws IOException {
        return adminPresenter.showUsersPresenter(model, promoteSuccess);
    }

    /**
     * Promotes a user.
     *
     * @param model Holds data to send to the view.
     * @param form  The form holding the front-end input.
     * @return A url to which we redirect.
     * @throws IOException An IOException.
     */
    @PostMapping("/admin/promote")
    public String handlePromote(Model model, @ModelAttribute PromoteForm form) throws IOException {
        systemFacade.users().promote(form.getUserId());
        return "redirect:/admin/users?promoteSuccess=true";
    }


/********************************************************************************************************
 *
 * Change config
 *
 *********************************************************************************************************/

    /**
     * Shows the configuration panel.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     */
    @GetMapping("/admin/config/show")
    public String config(Model model, @RequestParam(required = false) boolean validationError) {
        if(validationError) model.addAttribute("validationError", true);
        return adminPresenter.configPresenter(model);
    }

    /**
     * Handles updating the configuration.
     *
     * @param form The form holding the front-end input.
     * @param redirectAttributes Redirection attributes.
     * @return A url to which we redirect.
     * @throws IOException An IOException.
     */
    @PostMapping("/admin/config/update")
    public String updateConfig(ConfigForm form, RedirectAttributes redirectAttributes) throws IOException {
        if(this.validateConfig(form)) {
            for (Map.Entry<String, String> entry : form.getConfigurations().entrySet()) {
                systemFacade.config().edit(entry.getKey(), Integer.parseInt(entry.getValue()));
            }
            return "redirect:/admin";
        }
        else {
            redirectAttributes.addAttribute("validationError", true);
            return "redirect:/admin/config/show";
        }

    }

    private boolean validateConfig(ConfigForm form) {
        try {
            for (Map.Entry<String, String> entry : form.getConfigurations().entrySet()) {
                int value = Integer.parseInt(entry.getValue());
                if(value < 0) return false;
            }
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }


/********************************************************************************************************
 *
 * Undo panel
 *
 *********************************************************************************************************/

    /**
     * Shows all the undoable actions.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     * @throws IOException An IOException.
     */
    @GetMapping("/admin/undo/all")
    public String undo(Model model) throws IOException {
        return adminPresenter.undoPresenter(model);
    }


    /**
     * Handles undoing a particular action.
     *
     * @param undoForm The form holding the front-end input.
     * @return A url to which we redirect.
     * @throws IOException               An IOException.
     * @throws CommandExecutionException An exception indicating there was an exception.
     */
    @PostMapping("/admin/undo/handle")
    public String handle(@ModelAttribute UndoForm undoForm) throws IOException, CommandExecutionException {
        this.commandManager.undo(undoForm.getHistoryId());
        return "redirect:/admin/undo/all";
    }


/********************************************************************************************************
 *
 * A reporting feature. Shows the most popular items, the users with the highest credit. top 10
 *
 *********************************************************************************************************/

    /**
     * Shows analytics page.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     * @throws IOException An IOException
     */
    @GetMapping("/admin/reporting")
    public String handleReporting(Model model) throws IOException {
        return adminPresenter.handleReportingPresenter(model);
    }


/********************************************************************************************************
 *
 * Create demo account
 *
 *********************************************************************************************************/

    /**
     * Shows the page to allow the admin to make accounts into demo accounts.
     *
     * @param model           Holds data to send to the view.
     * @param makeDemoSuccess Whether the user was succesfully made into a demo account.
     * @return A string indicating which view to display.
     * @throws IOException An IOException.
     */
    @GetMapping("/admin/demo")
    public String demo(Model model, @RequestParam(required = false) boolean makeDemoSuccess) throws IOException {
        if (makeDemoSuccess) adminPresenter.makeDemoSuccess(model);
        return adminPresenter.demoPresenter(model);

    }


    /**
     * Makes an account into a demo.
     *
     * @param form The form holding the front-end input.
     * @return The url to which we redirect.
     * @throws IOException An IOException.
     */
    @PostMapping("/admin/demo")
    public String handleMakeDemo(MakeDemoForm form) throws IOException {
        this.systemFacade.users().setAccountToDemo(form.getUserId());
        return "redirect:/admin/demo?makeDemoSuccess=true";
    }


}
