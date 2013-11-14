package org.opengeoportal.harvester.mvc;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.opengeoportal.harvester.mvc.bean.IngestFormBean;
import org.opengeoportal.harvester.mvc.exception.FormNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@SessionAttributes(types = { IngestFormBean.class })
public class IngestController {
//	@RequestMapping(value = "/")
//	public String mainPage(ModelMap model) {
//		return ingest(model);
//	}
	
	@RequestMapping(value = "/ingest")
	public String ingest(ModelMap model) {
		IngestFormBean formBean = new IngestFormBean();
		ingestStep1(formBean, model);

		return "welcome";
	}

	@RequestMapping(value = "/ingest/step1")
	public String ingestStep1(IngestFormBean ingestFormBean, ModelMap model) {
		model.put("ingestFormBean", ingestFormBean);

		List<SimpleEntry<String, String>> catalogOfServices = new ArrayList<SimpleEntry<String, String>>();
		catalogOfServices.add(new SimpleEntry<String, String>("catalog1",
				"Example catalog 1"));
		catalogOfServices.add(new SimpleEntry<String, String>("catalog2",
				"Example catalog 2"));
		catalogOfServices.add(new SimpleEntry<String, String>("catalog3",
				"Example catalog 3"));
		catalogOfServices.add(new SimpleEntry<String, String>("catalog4",
				"Example catalog 4"));
		model.put("catalogOfServicesList", catalogOfServices);

		List<SimpleEntry<Integer, String>> dataRepository = new ArrayList<SimpleEntry<Integer, String>>();
		for (int i = 1; i <= 10; i++) {
			dataRepository.add(new SimpleEntry<Integer, String>(i,
					"Data Repository " + i));
		}
		model.put("dataRepositoryList", dataRepository);

		List<SimpleEntry<String, String>> geonetworkSourcesList = new ArrayList<SimpleEntry<String, String>>();
		for (int i = 1; i <= 10; i++) {
			geonetworkSourcesList.add(new SimpleEntry<String, String>(Integer
					.toString(i), "Geonetwork source " + i));
		}
		model.put("geonetworkSourcesList", geonetworkSourcesList);

		return "welcome";
	}

	@RequestMapping(value = "/ingest/schedule")
	public String ingestStep2(IngestFormBean ingestFormBean, ModelMap model) {
		model.put("ingestFormBean", ingestFormBean);

		return "scheduleStep2";

	}

	@RequestMapping(value = "/ingest/startIngest")
	public String ingestFinalStep(IngestFormBean ingestFormBean,
			ModelMap model, SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		model.put("nextRunDate", new Date());
		return "scheduleStepFinal";
	}

	@RequestMapping(value = "/fragments")
	public String partialIngestForm(@RequestParam String instanceType,
			ModelMap model) {
		List<SimpleEntry<String, String>> catalogOfServices = new ArrayList<SimpleEntry<String, String>>();
		catalogOfServices.add(new SimpleEntry<String, String>("catalog1",
				"Example catalog 1"));
		catalogOfServices.add(new SimpleEntry<String, String>("catalog2",
				"Example catalog 2"));
		catalogOfServices.add(new SimpleEntry<String, String>("catalog3",
				"Example catalog 3"));
		catalogOfServices.add(new SimpleEntry<String, String>("catalog4",
				"Example catalog 4"));
		model.put("catalogOfServicesList", catalogOfServices);
		model.put("catalogOfServices", "");
		model.put("cswUrl", "");
		model.put("extent", "");
		model.put("themeKeyword", "");
		model.put("placeKeyword", "");
		model.put("topic", "");
		model.put("rangeFrom", "");
		model.put("rangeTo", "");
		model.put("originator", "");
		model.put("dataType", "");
		model.put("dataRepository", "");
		model.put("excludeRestricted", Boolean.FALSE);
		model.put("rangeSolrFrom", "");
		model.put("rangeSolrTo", "");
		model.put("customSolrQuery", "");
		model.put("geonetworkUrl", "");
		model.put("ogpRepository", "");
		model.put("title", "");
		model.put("keyword", "");
		model.put("abstractText", "");
		model.put("freeText", "");
		model.put("geonetworkSources", "");
		model.put("location", "");
		model.put("subject", "");
		model.put("customCswQuery", "");
		model.put("url", "");
		model.put("webDavUrl", "");
		model.put("lastModifiedFrom", "");
		model.put("lastModifiedTo", "");

		String partialForm = "partial/solr";

		if (instanceType == null) {
			partialForm = "partial/solr";
		} else if (instanceType.equals("solr")) {
			partialForm = "partial/solr";
		} else if (instanceType.equals("geonetwork")) {
			List<SimpleEntry<String, String>> geonetworkSourcesList = new ArrayList<SimpleEntry<String, String>>();
			for (int i = 1; i <= 10; i++) {
				geonetworkSourcesList.add(new SimpleEntry<String, String>(
						Integer.toString(i), "Geonetwork source " + i));
			}
			model.put("geonetworkSourcesList", geonetworkSourcesList);

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
	
	@RequestMapping ("/")
	public String angular() {
		return "ngView";
	}

}
