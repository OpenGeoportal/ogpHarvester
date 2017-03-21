package org.opengeoportal.harvester.mvc.utils;

import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;
import org.opengeoportal.harvester.api.service.IngestReportWarningsService;
import org.opengeoportal.harvester.api.service.IngestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created by joana on 21/03/17.
 */

@Controller
public class ManageLayerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    IngestReportErrorService errorService;
    @Resource
    IngestReportWarningsService warningService;
    @Resource
    private IngestService ingestService;
    @Resource
    private IngestJobStatusService jobStatusService;
    @Resource
    private IngestReportService reportService;

    @RequestMapping("/manageLayers")
    public String indexRedirection() {
        return "ngView";
    }

}
