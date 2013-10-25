package org.opengeoportal.harvester.mvc;

import org.opengeoportal.harvester.mvc.exception.FormNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IngestController {
	@RequestMapping(value = "/ingest", method = RequestMethod.GET)
	public String ingest(ModelMap model) {

		return "welcome";

	}

	@RequestMapping(value = "/fragments")
	public String partialIngestForm(String instanceType) {
		String partialForm = "partial/solr";

		if (instanceType == null) {
			partialForm = "partial/solr";
		} else if (instanceType.equals("solr")) {
			partialForm = "partial/solr";
		} else if (instanceType.equals("geonetwork")) {
			partialForm = "partial/geonetwork";
		} else if (instanceType.equals("csw")) {
			partialForm = "partial/csw";
		} else if (instanceType.equals("webdav")) {
			partialForm = "partial/webdav";
		} else {
			throw new FormNotFoundException("Partial form " + instanceType
					+ " is not valid or cannot be found");
		}
		return partialForm;
	}

}
