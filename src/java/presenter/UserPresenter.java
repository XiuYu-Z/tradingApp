package presenter;

import controller.forms.LoginForm;
import controller.forms.RegisterForm;
import controller.forms.UpdateHomeCityForm;
import controller.forms.UpdatePasswordForm;
import entities.Transaction;
import org.springframework.ui.Model;
import usecases.SystemFacade;
import usecases.TradingFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPresenter {

    private final TradingFacade tradingFacade;
    private final SystemFacade systemFacade;

    /**
     * Instantiates this presenter.
     *
     * @param tradingFacade The facade holding trading related functionality.
     * @param systemFacade  The facade holding system related functionality.
     */
    public UserPresenter(TradingFacade tradingFacade, SystemFacade systemFacade) {
        this.tradingFacade = tradingFacade;
        this.systemFacade = systemFacade;
    }


    /**
     * Prepares data for the login page.
     *
     * @param model           A model that holds dynamic data.
     * @param registerSuccess Whether the registration succeeded
     * @return The view
     */
    public String loginPresenter(Model model, boolean registerSuccess) {
        model.addAttribute("registerSuccess", registerSuccess);
        model.addAttribute("loginForm", new LoginForm());
        return "users/login";
    }

    /**
     * Prepares data if the user is invalid.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String invalidUser(Model model) {
        model.addAttribute("invalidUser", "true");
        return "users/login";
    }

    /**
     * Prepares data if the email is duplicate.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String emailDuplicate(Model model) {
        model.addAttribute("emailDuplicate", true);
        return "users/register";
    }

    /**
     * Prepares data for the registration page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String registerPresenter(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "users/register";
    }

    /**
     * Prepares data for the settings page.
     *
     * @param model  A model that holds dynamic data.
     * @param userId The user id
     * @return The view
     * @throws IOException An IOException
     */
    public String settingsPresenter(Model model, int userId) throws IOException {
        List<List<Integer>> frequentPartners = this.tradingFacade.manageTransactions().frequentPartners(userId);
        List<Integer> viewers = new ArrayList<>();
        viewers.add(userId);
        model.addAttribute("hasPrivilege", checkPrivilege(userId));
        model.addAttribute("viewer", systemFacade.users().get(viewers));
        model.addAttribute("users", systemFacade.users().get(frequentPartners.get(0)));
        model.addAttribute("frequencies", frequentPartners.get(1));
        return "settings";
    }

    /**
     * Prepares data for the update password page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String showPasswordUpdatePresenter(Model model) {
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());
        return "users/updatePassword";
    }

    /**
     * Prepares data for the update home city page.
     *
     * @param model A model that holds dynamic data.
     * @return The view
     */
    public String showHomeCityUpdatePresenter(Model model) {
        model.addAttribute("updateHomeCityForm", new UpdateHomeCityForm());
        return "users/updateHomeCity";
    }


    /********************************************************************************************************
     *
     * Helper method
     *
     *********************************************************************************************************/
    private boolean checkPrivilege(Integer userId) throws IOException {
        List<Transaction> completeTransaction = this.tradingFacade.fetchTransactions().query().
                involvesUser(userId).isComplete().getTransactions();

        List<Transaction> failTransaction = this.tradingFacade.fetchTransactions().query().
                involvesUser(userId).isIncomplete().getTransactions();

        this.systemFacade.credit().updatePoint(userId, completeTransaction, failTransaction);

        return systemFacade.credit().getCredit(userId) >= 1200;
    }


}
