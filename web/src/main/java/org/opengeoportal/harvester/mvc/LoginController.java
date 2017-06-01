package org.opengeoportal.harvester.mvc;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(ModelMap model) {

       return "login";

    }
    
    @RequestMapping(value="/loginsuccess", method = RequestMethod.GET)
    public String loginSuccess(ModelMap model) {

       return "login";

    }

    @RequestMapping(value="/loginfailed", method = RequestMethod.GET)
    public String loginerror(ModelMap model) {

        model.addAttribute("error", "true");
        return "login";

    }

    @RequestMapping(value="/accessDenied", method = RequestMethod.GET)
    public String accessDenied(ModelMap model) {

        return "accessDenied";

    }

    @RequestMapping(value="/getDataIngestToken", method = RequestMethod.GET)
    @ResponseBody
    public String getToken(HttpServletRequest req) {

        return (String) req.getSession().getAttribute("dataIngest_token");

    }
}
