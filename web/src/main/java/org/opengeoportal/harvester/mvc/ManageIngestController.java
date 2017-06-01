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
import org.opengeoportal.harvester.api.domain.IngestReportWarningType;
import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;
import org.opengeoportal.harvester.api.service.IngestReportWarningsService;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The Class ManageIngestController.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>
 */
@Controller
public class ManageIngestController {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /** The error service. */
    @Resource
    IngestReportErrorService errorService;
    
    /** The warning service. */
    @Resource
    IngestReportWarningsService warningService;
    
    /** The ingest service. */
    @Resource
    private IngestService ingestService;
    
    /** The job status service. */
    @Resource
    private IngestJobStatusService jobStatusService;
    
    /** The report service. */
    @Resource
    private IngestReportService reportService;

    /**
     * Download metadata.
     *
     * @param id the id
     * @param reportId the report id
     * @param requiredField the required field
     * @param webserviceError the webservice error
     * @param systemError the system error
     * @param out the out
     * @param response the response
     */
    @RequestMapping("/rest/ingests/{id}/metadata/{reportId}")
    public void downloadMetadata(@PathVariable final String id,
            @PathVariable final Long reportId,
            @RequestParam(defaultValue = "") final String[] requiredField,
            @RequestParam(defaultValue = "") final String[] webserviceError,
            @RequestParam(defaultValue = "") final String[] systemError,
            final OutputStream out, final HttpServletResponse response) {

        // response.setHeader("Content-Type", "text/plain; charset=utf-8");
        response.setHeader("Content-Type:", "application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=metadata_" + id + ".zip");

        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(out);
            zipOutputStream.setLevel(9);
            this.errorService.writeErrorZipForIngest(reportId, zipOutputStream,
                    requiredField, webserviceError, systemError);
            zipOutputStream.close();

        } catch (final IOException e) {
            this.logger
                    .warn("Error generating zip report detail for ingest report "
                            + reportId);
        } finally {
            try {
                response.flushBuffer();
            } catch (final IOException e) {
                this.logger
                        .warn("Error generating zip report detail for ingest report "
                                + reportId);
            }
        }
    }

    /**
     * Gets the all ingests.
     *
     * @param page the page
     * @param pageSize the page size
     * @param model the model
     * @return the all ingests
     */
    @RequestMapping("/rest/ingests")
    @ResponseBody
    public Map<String, Object> getAllIngests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") final int pageSize,
            final ModelMap model) {
        if (page < 1) {
            page = 1;
        }

        // Pages are zero base. We need to subtract 1 to the received page
        page = page - 1;
        // Sort by most recent execution
        final Sort sort = new Sort(
                new Sort.Order(Sort.Direction.DESC, "lastRun"));
        final Pageable pageable = new PageRequest(page, pageSize, sort);
        final Page<Ingest> resultPage = this.ingestService.findAll(pageable);
        final PageWrapper pageDetails = new PageWrapper(resultPage);

        final Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("pageDetails", pageDetails);

        final List<IngestListItem> resultList = Lists
                .newArrayListWithCapacity(resultPage.getNumberOfElements());
        final Set<Long> executingJobs = this.ingestService
                .getCurrentlyExecutingJobs();
        for (final Ingest ingest : resultPage) {
            final IngestJobStatus lastStatus = this.jobStatusService
                    .findLastStatusForIngest(ingest.getId());

            final IngestListItem ingestListItem = new IngestListItem(ingest,
                    lastStatus);
            final Date nextRun = this.ingestService.getNextRun(ingest);
            ingestListItem.setNextRun(nextRun);
            ingestListItem
                    .setInProgress(executingJobs.contains(ingest.getId()));

            resultList.add(ingestListItem);
        }
        resultMap.put("elements", resultList);

        return resultMap;
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
            final Long reportId, final IngestReportErrorType errorType) {
        final List<SimpleEntry<String, Long>> errorSubcategoryList = Lists
                .newArrayList();
        final Map<String, Long> errorMap = this.errorService
                .getCountErrorsByReportId(reportId, errorType);
        for (final Entry<String, Long> entry : errorMap.entrySet()) {
            errorSubcategoryList.add(new SimpleEntry<String, Long>(
                    entry.getKey(), entry.getValue()));
        }
        return errorSubcategoryList;
    }

    /**
     * Return a list of SimpleEntry with the subcategory of the error as key and
     * the count of that subcategory for the ingest report passed as parameter.
     *
     * @param reportId            the ingest report identifier.
     * @return a list of SimpleEntry with the subcategory of the error as key
     *         and the count of that subcategory for the ingest report passed as
     *         parameter.
     */
    private List<SimpleEntry<String, Long>> handleDetailWarningCount(
            final Long reportId) {
        final List<SimpleEntry<String, Long>> warningSubcategoryList = Lists
                .newArrayList();
        final Map<String, Long> warningMap = this.warningService
                .getCountWarningsByReportId(reportId);
        for (final Entry<String, Long> entry : warningMap.entrySet()) {
            warningSubcategoryList.add(new SimpleEntry<String, Long>(
                    entry.getKey(), entry.getValue()));
        }
        return warningSubcategoryList;
    }

    /**
     * Index redirection.
     *
     * @return the string
     */
    @RequestMapping("/manageIngests")
    public String indexRedirection() {
        return "ngView";
    }

    /**
     * Return the last executed status for the ingest with identifier
     * <code>id</code>.
     *
     * @param id
     *            ingest identifier.
     * @return a JsonResponse
     */
    @RequestMapping("/rest/ingests/{id}")
    @ResponseBody
    public JsonResponse ingestDetails(@PathVariable final Long id) {
        final JsonResponse response = new JsonResponse();

        final Ingest ingest = this.ingestService.findById(id);
        if (ingest == null) {
            response.setStatus(STATUS.FAIL);
            final Map<String, String> errorMap = Maps.newHashMap();
            errorMap.put("errorCode", "INGEST_NOT_FOUND");
            errorMap.put("ingestId", id.toString());
            response.setResult(errorMap);
            return response;
        }

        final Map<String, Object> ingestMap = Maps.newHashMap();
        ingestMap.put("id", id);
        ingestMap.put("name", ingest.getName());
        ingestMap.put("lastRun", ingest.getLastRun());

        final IngestJobStatus lastJobStatus = this.jobStatusService
                .findLastStatusForIngest(id);
        if (lastJobStatus == null) {
            response.setStatus(STATUS.FAIL);
            final Map<String, Object> errorMap = Maps.newHashMap();
            errorMap.put("errorCode", "INGEST_WITHOUT_PREVIOUS_EXECUTIONS");
            errorMap.put("ingestId", id.toString());
            response.setResult(errorMap);
            if (this.logger.isInfoEnabled()) {
                this.logger.info(String.format(
                        "A finished IngestJobStatus could not be found for ingest with id=%d",
                        id));
            }
            return response;
        }

        final IngestReport report = this.reportService
                .findReportByJobStatusId(lastJobStatus.getId());
        if (report == null) {
            response.setStatus(STATUS.FAIL);
            final Map<String, Object> errorMap = Maps.newHashMap();
            errorMap.put("errorCode", "INGEST_WITHOUT_AVAILABLE_REPORTS");
            errorMap.put("ingestId", id.toString());
            errorMap.put("ingestJobStatusId", lastJobStatus.getId());
            response.setResult(errorMap);
            if (this.logger.isWarnEnabled()) {
                this.logger.warn(String.format(
                        "Report not found when getting ingestDetails. [ingestId=%d, ingestJobStatusId=%d]",
                        id, lastJobStatus.getId()));
            }

            return response;
        }

        final Long reportId = report.getId();
        ingestMap.put("reportId", reportId);

        final Map<String, Object> passed = new HashMap<String, Object>();
        passed.put("restrictedRecords", report.getRestrictedRecords());
        passed.put("publicRecords", report.getPublicRecords());
        passed.put("vectorRecords", report.getVectorRecords());
        passed.put("rasterRecords", report.getRasterRecords());
        ingestMap.put("passed", passed);

        /*
         * Map<String, Object> warning = new HashMap<String, Object>();
         * warning.put("unrequiredFields", report.getUnrequiredFieldWarnings());
         * warning.put("webserviceWarnings", report.getWebServiceWarnings());
         * ingestMap.put("warning", warning);
         */

        // Summarize error count by category
        final Map<String, Object> warningsMap = new HashMap<String, Object>();

        final Map<IngestReportWarningType, Long> unrequiredFieldMap = this.warningService
                .getCountWarningTypesByReportId(reportId);

        warningsMap.put("unrequiredFields", unrequiredFieldMap
                .get(IngestReportWarningType.UNREQUIRED_FIELD_WARNING));

        // Detail of required field errors
        final List<SimpleEntry<String, Long>> unrequiredFieldSubcat = this
                .handleDetailWarningCount(reportId);
        warningsMap.put("unrequiredFieldsList", unrequiredFieldSubcat);

        ingestMap.put("warning", warningsMap);

        final Map<IngestReportErrorType, Long> errorMap = this.errorService
                .getCountErrorTypesByReportId(reportId);
        // Summarize error count by category
        final Map<String, Object> errorsMap = new HashMap<String, Object>();
        errorsMap.put("requiredFields",
                errorMap.get(IngestReportErrorType.REQUIRED_FIELD_ERROR));
        errorsMap.put("webServiceErrors",
                errorMap.get(IngestReportErrorType.WEB_SERVICE_ERROR));
        errorsMap.put("systemErrors",
                errorMap.get(IngestReportErrorType.SYSTEM_ERROR));
        errorsMap.put("failedrecordscount", report.getFailedRecordsCount());

        // Detail of required field errors
        final List<SimpleEntry<String, Long>> requiredFieldSubcat = this
                .handleDetailErrorCount(reportId,
                        IngestReportErrorType.REQUIRED_FIELD_ERROR);
        errorsMap.put("requiredFieldsList", requiredFieldSubcat);

        // Detail of web service errors
        final List<SimpleEntry<String, Long>> webServiceErrorList = this
                .handleDetailErrorCount(reportId,
                        IngestReportErrorType.WEB_SERVICE_ERROR);
        errorsMap.put("webServiceErrorList", webServiceErrorList);

        // Detail of system errors
        final List<SimpleEntry<String, Long>> systemErrorList = this
                .handleDetailErrorCount(reportId,
                        IngestReportErrorType.SYSTEM_ERROR);
        errorsMap.put("systemErrorList", systemErrorList);

        ingestMap.put("error", errorsMap);

        response.setStatus(STATUS.SUCCESS);
        response.setResult(ingestMap);

        return response;
    }

    /**
     * Interrupt the Ingest's job if it is running.
     *
     * @param id            the ingest identifier.
     * @return response indicating success or fail and the cause.
     */
    @RequestMapping("/rest/ingests/{id}/interrupt")
    @ResponseBody
    public JsonResponse interruptIngest(@PathVariable final Long id) {
        final JsonResponse response = new JsonResponse();
        try {
            final boolean interrupted = this.ingestService.interruptIngest(id);
            if (interrupted) {
                response.setStatus(STATUS.SUCCESS);
                final Map<String, Object> data = Maps.newHashMap();
                data.put("ingestId", id);
                response.setResult(data);

            } else {
                response.setStatus(STATUS.FAIL);
                final Map<String, Object> errorMap = Maps.newHashMap();
                errorMap.put("errorCode", "ERROR_INTERRUPTING_INGEST");
                errorMap.put("ingestId", id);
                response.setResult(errorMap);
            }

        } catch (final Exception e) {
            response.setStatus(STATUS.FAIL);
            final Map<String, Object> errorMap = Maps.newHashMap();
            errorMap.put("errorCode", "EXCEPTION_INTERRUPTING_INGEST");
            errorMap.put("ingestId", id);
            response.setResult(errorMap);
        }
        return response;
    }

}
