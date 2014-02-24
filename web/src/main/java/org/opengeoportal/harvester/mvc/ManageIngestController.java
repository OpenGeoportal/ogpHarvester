/*
 * ManageIngestController.java
 * 
 * Copyright (C) 2013
 * 
 * This file is part of Open Geoportal Harvester
 * 
 * This software is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * As a special exception, if you link this library with other files to produce
 * an executable, this library does not by itself cause the resulting executable
 * to be covered by the GNU General Public License. This exception does not
 * however invalidate any other reasons why the executable file might be covered
 * by the GNU General Public License.
 * 
 * Authors:: Juan Luis Rodriguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestJobStatus;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;
import org.opengeoportal.harvester.api.service.IngestService;
import org.opengeoportal.harvester.mvc.bean.IngestListItem;
import org.opengeoportal.harvester.mvc.bean.JsonResponse;
import org.opengeoportal.harvester.mvc.bean.JsonResponse.STATUS;
import org.opengeoportal.harvester.mvc.bean.PageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 * 
 */
@Controller
public class ManageIngestController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private IngestService ingestService;
	@Resource
	private IngestJobStatusService jobStatusService;
	@Resource
	private IngestReportService reportService;
	@Resource
	IngestReportErrorService errorService;

	@RequestMapping("/manageIngests")
	public String test(ModelMap model) {
		return "ngView";
	}

	@RequestMapping("/rest/ingests")
	@ResponseBody
	public Map<String, Object> getAllIngests(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int pageSize, ModelMap model) {
		if (page < 1) {
			page = 1;
		}

		// Pages are zero base. We need to subtract 1 to the received page
		page = page - 1;
		Pageable pageable = new PageRequest(page, pageSize);
		Page<Ingest> resultPage = ingestService.findAll(pageable);
		PageWrapper pageDetails = new PageWrapper(resultPage);

		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("pageDetails", pageDetails);

		List<IngestListItem> resultList = Lists
				.newArrayListWithCapacity(resultPage.getNumberOfElements());
		Set<Long> executingJobs = ingestService.getCurrentlyExecutingJobs();
		for (Ingest ingest : resultPage) {
			IngestListItem ingestListItem = new IngestListItem(ingest);
			Date nextRun = ingestService.getNextRun(ingest);
			ingestListItem.setNextRun(nextRun);
			ingestListItem
					.setInProgress(executingJobs.contains(ingest.getId()));

			resultList.add(ingestListItem);
		}
		resultMap.put("elements", resultList);

		return resultMap;
	}

	/**
	 * Return the last executed status for the ingest with identifier
	 * <code>id</code>.
	 * 
	 * @param id
	 *            ingest identifier.
	 * @return
	 */
	@RequestMapping("/rest/ingests/{id}")
	@ResponseBody
	public JsonResponse ingestDetails(@PathVariable Long id) {
		JsonResponse response = new JsonResponse();

		Ingest ingest = ingestService.findById(id);
		if (ingest == null) {
			response.setStatus(STATUS.FAIL);
			Map<String, String> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "INGEST_NOT_FOUND");
			errorMap.put("ingestId", id.toString());
			response.setResult(errorMap);
			return response;
		}

		Map<String, Object> ingestMap = Maps.newHashMap();
		ingestMap.put("id", id);
		ingestMap.put("name", ingest.getName());
		ingestMap.put("lastRun", ingest.getLastRun());

		IngestJobStatus lastJobStatus = jobStatusService
				.findLastStatusForIngest(id);
		if (lastJobStatus == null) {
			response.setStatus(STATUS.FAIL);
			Map<String, Object> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "INGEST_WITHOUT_PREVIOUS_EXECUTIONS");
			errorMap.put("ingestId", id.toString());
			response.setResult(errorMap);
			if (logger.isInfoEnabled()) {
				logger.info(String
						.format("A finished IngestJobStatus could not be found for ingest with id=%d",
								id));
			}
			return response;
		}

		IngestReport report = reportService
				.findReportByJobStatusId(lastJobStatus.getId());
		if (report == null) {
			response.setStatus(STATUS.FAIL);
			Map<String, Object> errorMap = Maps.newHashMap();
			errorMap.put("errorCode", "INGEST_WITHOUT_AVAILABLE_REPORTS");
			errorMap.put("ingestId", id.toString());
			errorMap.put("ingestJobStatusId", lastJobStatus.getId());
			response.setResult(errorMap);
			if (logger.isWarnEnabled()) {
				logger.warn(String
						.format("Report not found when getting ingestDetails. [ingestId=%d, ingestJobStatusId=%d]",
								id, lastJobStatus.getId()));
			}

			return response;
		}

		Long reportId = report.getId();
		ingestMap.put("reportId", reportId);

		Map<String, Object> passed = new HashMap<String, Object>();
		passed.put("restrictedRecords", report.getRestrictedRecords());
		passed.put("publicRecords", report.getPublicRecords());
		passed.put("vectorRecords", report.getVectorRecords());
		passed.put("rasterRecords", report.getRasterRecords());
		ingestMap.put("passed", passed);

		Map<String, Object> warning = new HashMap<String, Object>();
		warning.put("unrequiredFields", report.getUnrequiredFieldWarnings());
		warning.put("webserviceWarnings", report.getWebServiceWarnings());
		ingestMap.put("warning", warning);

		Map<IngestReportErrorType, Long> errorMap = errorService
				.getCountErrorTypesByReportId(reportId);
		// Summarize error count by category
		Map<String, Object> errorsMap = new HashMap<String, Object>();
		errorsMap.put("requiredFields",
				errorMap.get(IngestReportErrorType.REQUIRED_FIELD_ERROR));
		errorsMap.put("webServiceErrors",
				errorMap.get(IngestReportErrorType.WEB_SERVICE_ERROR));
		errorsMap.put("systemErrors",
				errorMap.get(IngestReportErrorType.SYSTEM_ERROR));

		// Detail of required field errors
		List<SimpleEntry<String, Long>> requiredFieldSubcat = handleDetailErrorCount(
				reportId, IngestReportErrorType.REQUIRED_FIELD_ERROR);
		errorsMap.put("requiredFieldsList", requiredFieldSubcat);

		// Detail of web service errors
		List<SimpleEntry<String, Long>> webServiceErrorList = handleDetailErrorCount(
				reportId, IngestReportErrorType.WEB_SERVICE_ERROR);
		errorsMap.put("webServiceErrorList", webServiceErrorList);

		// Detail of system errors
		List<SimpleEntry<String, Long>> systemErrorList = handleDetailErrorCount(
				reportId, IngestReportErrorType.SYSTEM_ERROR);
		errorsMap.put("systemErrorList", systemErrorList);

		ingestMap.put("error", errorsMap);

		response.setStatus(STATUS.SUCCESS);
		response.setResult(ingestMap);

		return response;
	}

	/**
	 * Return a list of SimpleEntry with the subcategory of the error as key and
	 * the count of that subcategory for the ingest report passed as parameter.
	 * 
	 * @param reportId
	 *            the ingest report identifier.
	 * @param errorType
	 *            the main category of errors.
	 * @return a list of SimpleEntry with the subcategory of the error as key
	 *         and the count of that subcategory for the ingest report passed as
	 *         parameter.
	 */
	private List<SimpleEntry<String, Long>> handleDetailErrorCount(
			Long reportId, IngestReportErrorType errorType) {
		List<SimpleEntry<String, Long>> errorSubcateroryList = Lists
				.newArrayList();
		Map<String, Long> errorMap = errorService.getCountErrorsByReportId(
				reportId, errorType);
		for (Entry<String, Long> entry : errorMap.entrySet()) {
			errorSubcateroryList.add(new SimpleEntry<String, Long>(entry
					.getKey(), entry.getValue()));
		}
		return errorSubcateroryList;
	}

	@RequestMapping("/rest/ingests/{id}/metadata/{reportId}")
	public void downloadMetadata(@PathVariable String id,
			@PathVariable Long reportId,
			@RequestParam(defaultValue = "") String[] requiredField,
			@RequestParam(defaultValue = "") String[] webserviceError,
			@RequestParam(defaultValue = "") String[] systemError,
			OutputStream out, HttpServletResponse response) {

		// response.setHeader("Content-Type", "text/plain; charset=utf-8");
		response.setHeader("Content-Type:", "application/octet-stream");
		response.setHeader("Content-Disposition",
				"attachment; filename=metadata_" + id + ".zip");

		ZipOutputStream zipOutputStream = null;
		try {
			zipOutputStream = new ZipOutputStream(out);
			zipOutputStream.setLevel(9);
			errorService.writeErrorZipForIngest(reportId, zipOutputStream,
					requiredField, webserviceError, systemError);
			zipOutputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
