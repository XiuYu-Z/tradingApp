package controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import usecases.SystemFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Runs methods before/after controller methods are executed
 */
public class AuthInterceptor implements HandlerInterceptor, ChecksAuthentication {

    private List<String> nonAuth = new ArrayList<>();

    SystemFacade systemFacade;

    /**
     * Instantiates this class
     */
    public AuthInterceptor(SystemFacade systemFacade) {
        this.systemFacade = systemFacade;
        nonAuth.add("/");
        nonAuth.add("/login");
        nonAuth.add("/register");
        nonAuth.add("/logout");
        nonAuth.add("/colour");
        nonAuth.add("/data/seed");
        nonAuth.add("/data/delete");
    }

    /**
     * Handles requests before hitting controller methods.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param handler A handler object
     * @return A boolean indicating whether to further handle this request
     * @throws IOException An IOException
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        String uri = request.getRequestURI();

        //If one of the above url's is where the request is headed to, then we continue
        if(this.nonAuth.contains(uri)) {
            return true;
        }

        //Else we need to check auth
        if(!this.isLoggedIn(request)) {
            response.sendRedirect("/login");
            return false;
        }

        //We next need to check admin permissions
        if(uri.matches("^(/admin).*")) {
            int userId = this.getLoggedInUserId(request);
            if(!this.systemFacade.permissionsManager().isAdmin(userId)) {
                response.sendRedirect("/login");
                return false;
            }
        }

        return true;
    }

}
