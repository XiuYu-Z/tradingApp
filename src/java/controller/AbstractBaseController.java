package controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import usecases.SystemFacade;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

abstract public class AbstractBaseController implements ChecksAuthentication {

    /**
     * Class dependencies
     */
    SystemFacade systemFacade;

    /**
     * Constructor of this class
     *
     * @param systemFacade The facade holding system related functionality.
     */
    public AbstractBaseController(SystemFacade systemFacade) {
        this.systemFacade = systemFacade;
    }

    /**
     * Prints the exception to application-errors.log
     *
     * @param e The exception
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e) {
        e.printStackTrace();
    }


    /**
     * Check if the logged in user is an admin
     *
     * @param request An object holding the HTTP request.
     * @return whether the user is admin
     */
    @ModelAttribute("isAdmin")
    protected boolean isAdmin(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().isAdmin(userId);
        }
        return false;
    }



    /**
     * Check if the logged in user is a normal user
     *
     * @param request An object holding the HTTP request
     * @return whether the user is normal
     */
    @ModelAttribute("isNormal")
    protected boolean isNormal(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().isNormal(userId);
        }
        return false;
    }

    /**
     * Check if the logged in user has requested to be unfrozen
     *
     * @param request An object holding the HTTP request.
     * @return whether the user has requested to be unfrozen
     */
    @ModelAttribute("isRequestedUnfreeze")
    protected boolean isRequestedUnfreeze(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().isRequestedUnfreeze(userId);
        }
        return false;
    }

    /**
     * Check if the logged in user has paused account
     *
     * @param request An object holding the HTTP request.
     * @return whether the user has paused account
     */
    @ModelAttribute("isVacation")
    protected boolean isVacation(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().isVacation(userId);
        }
        return false;
    }

    /**
     * Check if the logged in user can pause account
     *
     * @param request An object holding the HTTP request.
     * @return whether the user can pause account
     */
    @ModelAttribute("canVacation")
    protected boolean canVacation(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().canVacation(userId);
        }
        return false;
    }

    /**
     * Check if the logged in user is a demo user
     *
     * @param request An object holding the HTTP request.
     * @return whether the user is a demo user
     */
    @ModelAttribute("isDemo")
    protected boolean isDemo(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().isDemo(userId);
        }
        return false;
    }

    /**
     * Check if the logged in user can borrow items
     *
     * @param request An object holding the HTTP request.
     * @return whether the user can borrow items
     */
    @ModelAttribute("canBorrow")
    protected boolean canBorrow(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().canBorrow(userId);
        }
        return false;
    }

    /**
     * Check if the logged in user is frozen
     *
     * @param request An object holding the HTTP request.
     * @return whether the user is frozen
     */
    @ModelAttribute("isFrozen")
    protected boolean isFrozen(HttpServletRequest request) throws IOException {
        if (this.isLoggedIn(request)) {
            int userId = this.getLoggedInUserId(request);
            return this.systemFacade.permissionsManager().isFrozen(userId);
        }
        return false;
    }

    /**
     * Get username of the logged in user
     *
     * @param request An object holding the HTTP request.
     * @return the username of the logged in user
     */
    @ModelAttribute("username")
    protected String username(HttpServletRequest request) {
        if (this.isLoggedIn(request)) {
            return (String) request.getSession().getAttribute("username");
        }
        return null;
    }

}
