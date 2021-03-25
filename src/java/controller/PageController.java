package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import usecases.SystemFacade;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PageController extends AbstractBaseController {

    /**
     * create new page controller
     *
     * @param systemFacade
     */
    public PageController(SystemFacade systemFacade) {
        super(systemFacade);
    }

    /**
     * Shows the home page
     *
     * @param request An object holding the HTTP request.
     * @return A url to which we redirect.
     */
    @GetMapping("/")
    public String index(HttpServletRequest request) {
        return "main";
    }

    /**
     * Switches colour
     * @param request An object holding the HTTP request.
     * @return A url to which we redirect.
     */
    @GetMapping("/colour")
    public String colour(HttpServletRequest request) {
        if (request.getSession().getAttribute("isDarkTheme") != null) {
            boolean opposite = (boolean) request.getSession().getAttribute("isDarkTheme");
            request.getSession().setAttribute("isDarkTheme", !opposite);
        }
        else request.getSession().setAttribute("isDarkTheme", true);
        return "redirect:" + request.getHeader("Referer");
    }

}
