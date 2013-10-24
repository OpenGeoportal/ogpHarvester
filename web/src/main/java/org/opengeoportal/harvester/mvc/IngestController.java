package org.opengeoportal.harvester.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IngestController {
    @RequestMapping(value="/ingest", method = RequestMethod.GET)
    public String login(ModelMap model) {

        return "welcome";

    }

}
