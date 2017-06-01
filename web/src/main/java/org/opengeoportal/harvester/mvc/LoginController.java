package org.opengeoportal.harvester.mvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The Class LoginController.
 */
@Controller
public class LoginController {

    /**
     * Access denied.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
    public String accessDenied(final ModelMap model) {

        return "accessDenied";

    }

    /**
     * Gets the token.
     *
     * @param req the req
     * @return the token
     */
    @RequestMapping(value = "/getDataIngestToken", method = RequestMethod.GET)
    @ResponseBody
    public String getToken(final HttpServletRequest req) {

        return (String) req.getSession().getAttribute("dataIngest_token");

    }

    /**
     * Login.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(final ModelMap model) {

        return "login";

    }

    /**
     * Loginerror.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
    public String loginerror(final ModelMap model) {

        model.addAttribute("error", "true");
        return "login";

    }

    /**
     * Login success.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping(value = "/loginsuccess", method = RequestMethod.GET)
    public String loginSuccess(final ModelMap model) {

        return "login";

    }
}
