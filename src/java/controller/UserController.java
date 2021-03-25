package controller;

import controller.forms.LoginForm;
import controller.forms.RegisterForm;
import controller.forms.UpdateHomeCityForm;
import controller.forms.UpdatePasswordForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import presenter.UserPresenter;
import usecases.SystemFacade;
import usecases.TradingFacade;
import usecases.users.exceptions.DuplicatedUserNameException;
import usecases.users.exceptions.InvalidLoginCredentialsException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;


@Controller
public class UserController extends AbstractBaseController {

    /**
     * Class dependencies
     */
    private final SystemFacade systemFacade;
    private final UserPresenter userPresenter;

    /**
     * create new user controller
     *
     * @param systemFacade  A facade that contains system related functionality.
     * @param userPresenter A presenter for all informations related to a user.
     */
    public UserController(SystemFacade systemFacade, UserPresenter userPresenter) {
        super(systemFacade);
        this.systemFacade = systemFacade;
        this.userPresenter = userPresenter;
    }

/********************************************************************************************************
 *
 * Registration & Authentication Methods
 *
 *********************************************************************************************************/

    /**
     * Shows login view.
     *
     * @param model Holds data to send to the view.
     * @return A string indicating which view to display.
     */
    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false) boolean registerSuccess) {
        return userPresenter.loginPresenter(model, registerSuccess);
    }


    /**
     * Handles a login submission.
     *
     * @param login   A login form that holds the user input.
     * @param request An object holding the HTTP request.
     * @return A url to which we should redirect.
     * @throws IOException
     * @throws InvalidLoginCredentialsException when the input username and password cannot be matched in database
     */
    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute LoginForm login,
                              Errors errors, HttpServletRequest request,
                              Model model) throws IOException {
        String email = login.getEmail();
        String password = login.getPassword();
        if (errors.hasErrors()) {
            return "users/login";
        }
        if (this.systemFacade.auth().authenticate(email, password) == -1) {
            return userPresenter.invalidUser(model);
        }

        int userId = this.systemFacade.auth().authenticate(email, password);

        String homeCity = this.systemFacade.auth().getUserHomeCity(userId);
        request.getSession().setAttribute("userId", userId);
        request.getSession().setAttribute("username", email);
        request.getSession().setAttribute("homeCity", homeCity);

        return "redirect:/items/add";
    }

    /**
     * show register view
     *
     * @param model Holds data to send to the view.
     * @return A string indicate the register site
     */
    @GetMapping("/register")
    public String register(Model model) {
        return userPresenter.registerPresenter(model);
    }

    /**
     * handel registration of new user
     *
     * @param register
     * @param errors
     * @param model Holds data to send to the view.
     * @return A string indicate the login site
     * @throws DuplicatedUserNameException
     * @throws IOException
     */
    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute RegisterForm register, Errors errors, Model model)
            throws DuplicatedUserNameException, IOException {

        if (errors.hasErrors()) {
            return "users/register";
        }
        if (systemFacade.auth().emailDuplicate(register.getEmail())) {
            return userPresenter.emailDuplicate(model);
        }
        this.systemFacade.auth().register(register.getEmail(), register.getPassword(), register.getHomeCity());
        return "redirect:/login?registerSuccess=true";
    }

    /**
     * show logout view
     *
     * @param request An object holding the HTTP request.
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("userId");
        return "redirect:/";
    }


/********************************************************************************************************
 *
 * User Account Methods
 *
 *********************************************************************************************************/

    /**
     * show settings view
     *
     * @param model Holds data to send to the view.
     * @param request An object holding the HTTP request.
     * @return A String indicates the setting site
     * @throws IOException
     */
    @GetMapping("/settings")
    public String settings(Model model, HttpServletRequest request) throws IOException {
        int userId = this.getLoggedInUserId(request);
        return userPresenter.settingsPresenter(model, userId);
    }

    /**
     * show update password view
     *
     * @param model Holds data to send to the view.
     * @return A String indicates the update password site
     */
    @GetMapping("/updatepassword")
    public String showPasswordUpdate(Model model) {
        return userPresenter.showPasswordUpdatePresenter(model);
    }

    /**
     * perform update password action
     *
     * @param request An object holding the HTTP request.
     * @param form The form holding the front-end input.
     * @param errors in the input data
     * @return A String indicates the setting site
     * @throws IOException
     */
    @PostMapping("/updatepassword")
    public String handlePasswordUpdate(HttpServletRequest request,
                                       @Valid @ModelAttribute UpdatePasswordForm form,
                                       Errors errors) throws IOException {
        if (this.isDemo(request)) return "redirect:/settings";

        if (errors.hasErrors()) {
            return "users/updatePassword";
        }
        int userId = this.getLoggedInUserId(request);
        this.systemFacade.users().changePassword(form.getNewPassword(), userId);
        return "redirect:/settings";
    }

    /**
     * show update home city view
     *
     * @param model Holds data to send to the view.
     * @return A String indicates the update home city site
     */
    @GetMapping("/updateHomeCity")
    public String showHomeCityUpdate(Model model) {
        return userPresenter.showHomeCityUpdatePresenter(model);
    }

    /**
     * perform update home city action
     *
     * @param request An object holding the HTTP request.
     * @param form The form holding the front-end input.
     * @param errors in the input data
     * @return A String indicates the setting site
     * @throws IOException
     */
    @PostMapping("/updateHomeCity")
    public String handleHomeCityUpdate(HttpServletRequest request,
                                       @Valid @ModelAttribute UpdateHomeCityForm form,
                                       Errors errors) throws IOException {
        if (this.isDemo(request)) return "redirect:/settings";

        if (errors.hasErrors()) {
            return "users/updateHomeCity";
        }
        int userId = this.getLoggedInUserId(request);
        this.systemFacade.users().changeHomeCity(form.getNewHomeCity(), userId);
        request.getSession().removeAttribute("homeCity");
        request.getSession().setAttribute("homeCity", form.getNewHomeCity());
        return "redirect:/settings";
    }

    /**
     * show set vacation view
     *
     * @param request An object holding the HTTP request.
     * @return A String indicates the setting site
     * @throws IOException
     */
    @PostMapping("/setvacation")
    public String setVacation(HttpServletRequest request) throws IOException {
        if (this.isDemo(request)) return "redirect:/settings";
        int userId = this.getLoggedInUserId(request);
        this.systemFacade.users().setVacation(userId);
        return "redirect:/settings";
    }

    /**
     * perform undo vacation
     *
     * @param request An object holding the HTTP request.
     * @return A String indicates the setting site
     * @throws IOException
     */
    @PostMapping("/undovacation")
    public String undoVacation(HttpServletRequest request) throws IOException {
        if (this.isDemo(request)) return "redirect:/settings";
        int userId = this.getLoggedInUserId(request);
        this.systemFacade.users().unVacation(userId);
        return "redirect:/settings";
    }

    /**
     * perform unfreeze request
     *
     * @param request An object holding the HTTP request.
     * @return A String indicates the setting site
     * @throws IOException
     */
    @PostMapping("/unfreeze/request/handle")
    public String requestUnfreeze(HttpServletRequest request) throws IOException {
        systemFacade.users().requestUnfreeze(this.getLoggedInUserId(request));
        return "redirect:/settings";
    }
}
