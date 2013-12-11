package org.opengeoportal.harvester.mvc;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestCsw;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.IngestWebDav;
import org.opengeoportal.harvester.api.service.IngestService;
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

import com.google.common.collect.Maps;

@Controller
@SessionAttributes(types = { IngestFormBean.class })
public class IngestController {
	@Resource
	private IngestService ingestService;

	@RequestMapping(value = "/rest/ingests/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> createIngest(
			@RequestBody IngestFormBean ingestFormBean) {
		boolean usesCustomRepo = ingestFormBean.getCatalogOfServices() != null;

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
			ingestOGP.setCustomSolrQuery(ingestFormBean.getSolrCustomQuery());
			ingestOGP.setDataRepositories(Arrays.asList(ingestFormBean
					.getDataRepositories()));
			ingestOGP
					.setDataTypes(Arrays.asList(ingestFormBean.getDataTypes()));
			ingestOGP.setDateFrom(ingestFormBean.getContentRangeFrom());
			ingestOGP.setDateTo(ingestFormBean.getContentRangeTo());
			ingestOGP.setExcludeRestrictedData(ingestFormBean
					.isExcludeRestricted());
			ingestOGP.setFromSolrTimestamp(ingestFormBean.getRangeSolrFrom());
			ingestOGP.setToSolrTimestamp(ingestFormBean.getRangeSolrTo());
			ingestOGP.setOriginator(ingestFormBean.getOriginator());
			ingestOGP.setPlaceKeyword(ingestFormBean.getPlaceKeyword());
			ingestOGP.setThemeKeyword(ingestFormBean.getThemeKeyword());
			ingestOGP.setTopicCategory(ingestFormBean.getTopic());

			ingest = ingestOGP;
			break;
		case GEONETWORK:
			IngestGeonetwork ingestGN = new IngestGeonetwork();

			ingestGN.setAbstractText(ingestFormBean.getGnAbstractText());
			ingestGN.setFreeText(ingestFormBean.getGnFreeText());
			ingestGN.setGeonetworkSource(Arrays.asList(ingestFormBean
					.getGnSources()));
			ingestGN.setKeyword(ingestFormBean.getGnKeyword());
			ingestGN.setTitle(ingestFormBean.getGnTitle());

			ingest = ingestGN;
			break;
		case CSW:
			IngestCsw ingestCsw = new IngestCsw();

			if (ingestFormBean.getExtent() != null) {
				BoundingBox bbox = ingestFormBean.getExtent();
				ingestCsw.setBboxWest(bbox.getMinx());
				ingestCsw.setBboxEast(bbox.getMaxx());
				ingestCsw.setBboxSouth(bbox.getMiny());
				ingestCsw.setBboxNorth(bbox.getMaxy());
			}
			ingestCsw.setCustomCswQuery(ingestFormBean.getCswCustomQuery());
			ingestCsw.setDateFrom(ingestFormBean.getCswRangeFrom());
			ingestCsw.setDateTo(ingestFormBean.getCswRangeTo());
			ingestCsw.setFreeText(ingestFormBean.getCswFreeText());
			ingestCsw.setSubject(ingestFormBean.getCswSubject());
			ingestCsw.setTitle(ingestFormBean.getCswTitle());

			ingest = ingestCsw;
			break;
		case WEBDAV:
			IngestWebDav ingestWebDav = new IngestWebDav();
			ingestWebDav
					.setDateFrom(ingestFormBean.getWebdavFromLastModified());
			ingestWebDav.setDateTo(ingestFormBean.getWebdavToLastModified());

			ingest = ingestWebDav;
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
		ingest.setNameOgpRepository(ingestFormBean.getNameOgpRepository());
		ingest.setScheduled(true);
		if (!usesCustomRepo) {
			ingest.setUrl(ingestFormBean.getUrl());
		}
		for (Entry<String, Boolean> requiredField : ingestFormBean
				.getRequiredFields().entrySet()) {
			if (requiredField.getValue()) {
				ingest.addRequiredField(requiredField.getKey());
			}
		}

		if (usesCustomRepo) {
			ingest = ingestService.save(ingest,
					ingestFormBean.getCatalogOfServices(),
					ingestFormBean.getTypeOfInstance());
		} else {
			ingest = ingestService.save(ingest);
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("success", true);
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", ingest.getId());
		data.put("name", ingest.getName());
		result.put("data", data);

		return result;
	}

	@RequestMapping("/")
	public String angular() {
		return "ngView";
	}

}
