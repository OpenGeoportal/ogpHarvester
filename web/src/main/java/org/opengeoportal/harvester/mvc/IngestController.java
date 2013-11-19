package org.opengeoportal.harvester.mvc;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestCsw;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.IngestWebDav;
import org.opengeoportal.harvester.mvc.bean.BoundingBox;
import org.opengeoportal.harvester.mvc.bean.IngestFormBean;
import org.opengeoportal.harvester.mvc.exception.InvalidParameterValue;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes(types = { IngestFormBean.class })
public class IngestController {

	@RequestMapping(value = "/rest/ingests/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String createIngest(@RequestBody IngestFormBean ingestFormBean) {
		Ingest ingest;

		switch (ingestFormBean.getTypeOfInstance()) {
		case SOLR:
			IngestOGP ingestOGP = new IngestOGP();
			if (ingestFormBean.getExtent() != null) {
				BoundingBox bbox = ingestFormBean.getExtent();
				ingestOGP.setBboxWest(bbox.getMinx());
				ingestOGP.setBboxEast(bbox.getMaxx());
				ingestOGP.setBboxSouth(bbox.getMiny());
				ingestOGP.setBboxNorth(bbox.getMaxy());

			}

			ingest = ingestOGP;
			break;
		case GEONETWORK:
			ingest = new IngestGeonetwork();
			break;
		case CSW:
			ingest = new IngestCsw();
			break;
		case WEBDAV:
			ingest = new IngestWebDav();
			break;
		default:
			throw new InvalidParameterValue(ingestFormBean.getTypeOfInstance()
					.name()
					+ " is not a valid instance type. Please add a new "
					+ "case to the switch instruction");
		}

		// common fields
		ingest.setBeginDate(ingestFormBean.getBeginDate());
		ingest.setName(ingestFormBean.getIngestName());
		ingest.setUrl(ingestFormBean.getUrl());

		return "success";
	}

	@RequestMapping("/")
	public String angular() {
		return "ngView";
	}

}
