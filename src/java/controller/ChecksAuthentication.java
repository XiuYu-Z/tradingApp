package controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

public interface ChecksAuthentication {

    /**
     * Check if a a user is logged in
     *
     * @param request An object holding the HTTP request.
     * @return whether a user is logged in
     */
    @ModelAttribute("loggedIn")
    default boolean isLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("userId") != null;
    }

    /**
     * Get id of the logged in user
     *
     * @param request An object holding the HTTP request.
     * @return the id of the logged in user
     */
    default int getLoggedInUserId(HttpServletRequest request) {
        return (Integer) request.getSession().getAttribute("userId");
    }

}
