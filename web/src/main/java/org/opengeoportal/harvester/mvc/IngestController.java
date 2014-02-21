package org.opengeoportal.harvester.mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javassist.expr.NewArray;

import javax.annotation.Resource;

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.DataType;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestCsw;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;
import org.opengeoportal.harvester.api.domain.IngestOGP;
import org.opengeoportal.harvester.api.domain.IngestWebDav;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.opengeoportal.harvester.api.service.IngestService;
import org.opengeoportal.harvester.mvc.bean.BoundingBox;
import org.opengeoportal.harvester.mvc.bean.IngestFormBean;
import org.opengeoportal.harvester.mvc.bean.JsonResponse;
import org.opengeoportal.harvester.mvc.bean.JsonResponse.STATUS;
import org.opengeoportal.harvester.mvc.exception.InvalidParameterValue;
import org.opengeoportal.harvester.mvc.exception.ItemNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.common.collect.Maps;

/**
 * Web controller that manage the Ingests.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * 
 */
@Controller
@SessionAttributes(types = { IngestFormBean.class })
public class IngestController {
	@Resource
	private IngestService ingestService;

	@RequestMapping(value = "/rest/ingests/new", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Object> createIngest(
			@RequestBody IngestFormBean ingestFormBean) {
		Ingest ingest = null;
		boolean updating = ingestFormBean.getId() != null;
		InstanceType instanceType = ingestFormBean.getTypeOfInstance();
		if (updating) {
			ingest = ingestService.findById(ingestFormBean.getId());
			if (ingest == null) {
				throw new ItemNotFoundException(
						"Cannot find an Ingest with id "
								+ ingestFormBean.getId());
			}
		} else {
			switch (instanceType) {
			case SOLR:
				ingest = new IngestOGP();
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
				throw new InvalidParameterValue(ingestFormBean
						.getTypeOfInstance().name()
						+ " is not a valid instance type. Please add a new "
						+ "case to the switch instruction");
			}

		}

		boolean usesCustomRepo = ingestFormBean.getCatalogOfServices() != null;

		switch (ingestFormBean.getTypeOfInstance()) {
		case SOLR:
			IngestOGP ingestOGP = (IngestOGP) ingest;
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

			break;
		case GEONETWORK:
			IngestGeonetwork ingestGN = (IngestGeonetwork) ingest;

			ingestGN.setAbstractText(ingestFormBean.getGnAbstractText());
			ingestGN.setFreeText(ingestFormBean.getGnFreeText());
			ingestGN.setGeonetworkSource(Arrays.asList(ingestFormBean
					.getGnSources()));
			ingestGN.setKeyword(ingestFormBean.getGnKeyword());
			ingestGN.setTitle(ingestFormBean.getGnTitle());
			;
			break;
		case CSW:
			IngestCsw ingestCsw = (IngestCsw) ingest;

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

			break;
		case WEBDAV:
			IngestWebDav ingestWebDav = (IngestWebDav) ingest;
			ingestWebDav
					.setDateFrom(ingestFormBean.getWebdavFromLastModified());
			ingestWebDav.setDateTo(ingestFormBean.getWebdavToLastModified());

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
		ingest.setFrequency(ingestFormBean.getFrequency());
		ingest.setScheduled(true);
		if (!usesCustomRepo) {
			ingest.setUrl(ingestFormBean.getUrl());
			ingest.setRepository(null);
		}
		
		// remove old required fields and add the new ones
		ingest.getRequiredFields().clear();
		for (Entry<String, Boolean> requiredField : ingestFormBean
				.getRequiredFields().entrySet()) {
			if (requiredField.getValue()) {
				ingest.addRequiredField(requiredField.getKey());
			}
		}

		if (usesCustomRepo) {
			ingest = ingestService.saveAndSchedule(ingest,
					ingestFormBean.getCatalogOfServices(),
					ingestFormBean.getTypeOfInstance());
		} else {
			ingest = ingestService.saveAndSchedule(ingest);
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("success", true);
		Map<String, Object> data = Maps.newHashMap();
		data.put("id", ingest.getId());
		data.put("name", ingest.getName());
		result.put("data", data);

		return result;
	}

	/**
	 * Return details about an ingest.
	 * 
	 * @param id
	 *            ingest identifier.
	 * @return JSON with ingest details.
	 */
	@RequestMapping(value = "/rest/ingests/{id}/details")
	@ResponseBody
	public IngestFormBean getDetails(@PathVariable("id") Long id) {
		IngestFormBean result = new IngestFormBean();

		// Check if ingest exist
		Ingest ingest = ingestService.findById(id);
		if (ingest == null) {
			throw new ItemNotFoundException("Cannot find an ingest with id "
					+ id);
		}

		if (ingest instanceof IngestOGP) {
			IngestOGP ingestOGP = (IngestOGP) ingest;
			result.setTypeOfInstance(InstanceType.SOLR);

			BoundingBox bbox = new BoundingBox();
			if (ingestOGP.getBboxEast() != null
					&& ingestOGP.getBboxNorth() != null
					&& ingestOGP.getBboxSouth() != null
					&& ingestOGP.getBboxWest() != null) {
				bbox.setMinx(ingestOGP.getBboxWest());
				bbox.setMiny(ingestOGP.getBboxSouth());
				bbox.setMaxx(ingestOGP.getBboxEast());
				bbox.setMaxy(ingestOGP.getBboxNorth());
			}
			result.setExtent(bbox);
			result.setSolrCustomQuery(ingestOGP.getCustomSolrQuery());
			result.setDataRepositories(ingestOGP.getDataRepositories().toArray(
					new String[] {}));
			result.setDataTypes(ingestOGP.getDataTypes().toArray(
					new DataType[] {}));
			result.setContentRangeFrom(ingestOGP.getDateFrom());
			result.setContentRangeTo(ingestOGP.getDateTo());
			result.setExcludeRestricted(ingestOGP.isExcludeRestrictedData());
			result.setRangeSolrFrom(ingestOGP.getFromSolrTimestamp());
			result.setRangeSolrTo(ingestOGP.getToSolrTimestamp());
			result.setOriginator(ingestOGP.getOriginator());
			result.setPlaceKeyword(ingestOGP.getPlaceKeyword());
			result.setThemeKeyword(ingestOGP.getThemeKeyword());
			result.setTopic(ingestOGP.getTopicCategory());
		} else if (ingest instanceof IngestGeonetwork) {
			IngestGeonetwork ingestGN = (IngestGeonetwork) ingest;
			result.setTypeOfInstance(InstanceType.GEONETWORK);

			result.setGnAbstractText(ingestGN.getAbstractText());
			result.setGnFreeText(ingestGN.getFreeText());
			result.setGnSources(ingestGN.getGeonetworkSources().toArray(
					new String[] {}));
			result.setGnKeyword(ingestGN.getKeyword());
			result.setGnTitle(ingestGN.getTitle());
		} else if (ingest instanceof IngestCsw) {
			IngestCsw ingestCsw = (IngestCsw) ingest;
			result.setTypeOfInstance(InstanceType.CSW);

			BoundingBox bbox = new BoundingBox();
			if (ingestCsw.getBboxEast() != null
					&& ingestCsw.getBboxNorth() != null
					&& ingestCsw.getBboxSouth() != null
					&& ingestCsw.getBboxWest() != null) {
				bbox.setMinx(ingestCsw.getBboxWest());
				bbox.setMiny(ingestCsw.getBboxSouth());
				bbox.setMaxx(ingestCsw.getBboxEast());
				bbox.setMaxy(ingestCsw.getBboxNorth());
			}
			result.setExtent(bbox);
			result.setCswCustomQuery(ingestCsw.getCustomCswQuery());
			result.setCswRangeFrom(ingestCsw.getDateFrom());
			result.setCswRangeTo(ingestCsw.getDateTo());
			result.setCswFreeText(ingestCsw.getFreeText());
			result.setCswSubject(ingestCsw.getSubject());
			result.setCswTitle(ingestCsw.getTitle());
		} else if (ingest instanceof IngestWebDav) {
			IngestWebDav ingestWebDav = (IngestWebDav) ingest;
			result.setTypeOfInstance(InstanceType.WEBDAV);

			result.setWebdavFromLastModified(ingestWebDav.getDateFrom());
			result.setWebdavToLastModified(ingestWebDav.getDateTo());
		} else {
			throw new InvalidParameterValue(ingest.getClass().getName()
					+ " is not a valid recognized Ingest subclass.");
		}

		// common fields
		result.setBeginDate(ingest.getBeginDate());
		result.setIngestName(ingest.getName());
		result.setFrequency(ingest.getFrequency());
		result.setNameOgpRepository(ingest.getNameOgpRepository());
		result.setScheduled(ingest.isScheduled());
		result.setId(ingest.getId());
		result.setUrl(ingest.getUrl());
		CustomRepository repository = ingest.getRepository();
		if (repository != null) {
			if (!repository.isDeleted()) {
				result.setCatalogOfServices(repository.getId());
			}
			result.setCustomRepoName(repository.getName());
			result.setCustomRepoDeleted(repository.isDeleted());
		}

		Map<String, Boolean> requiredFields = result.getRequiredFields();
		if (requiredFields == null) {
			requiredFields = Maps.newHashMap();
			result.setRequiredFields(requiredFields);
		}
		for (String requiredField : ingest.getRequiredFields()) {
			requiredFields.put(requiredField, Boolean.TRUE);
		}

		return result;
	}

	/**
	 * Unschedule the passed ingest.
	 * 
	 * @param id
	 *            ingest identifier.
	 * @return a {@link JsonResponse} object with the result of the operation.
	 */
	@RequestMapping(value = "/rest/ingests/{id}/unschedule")
	@ResponseBody
	public JsonResponse unscheduleIngest(@PathVariable("id") Long id) {
		JsonResponse response = new JsonResponse();
		Ingest ingest = ingestService.findById(id);
		if (ingest == null) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "INGEST_NOT_FOUND");
			errorMap.put("ingestId", id.toString());
			response.setResult(errorMap);
		}

		boolean unscheduled = ingestService.unscheduleIngest(id);
		if (unscheduled) {
			response.setStatus(STATUS.SUCCESS);
			Map<String, Object> resultMap = Maps.newHashMap();
			resultMap.put("ingestId", id);
			response.setResult(resultMap);
		} else {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "INGEST_NOT_FOUND");
			errorMap.put("ingestId", id.toString());
			response.setResult(errorMap);
		}

		return response;

	}

	@RequestMapping("/")
	public String angular() {
		return "ngView";
	}

}
