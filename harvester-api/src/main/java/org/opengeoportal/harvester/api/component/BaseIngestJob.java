package org.opengeoportal.harvester.api.component;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestJobStatus;
import org.opengeoportal.harvester.api.domain.IngestJobStatusValue;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserProvider;
import org.opengeoportal.harvester.api.metadata.parser.XmlMetadataParserProvider;
import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;

import java.util.Calendar;
import java.util.UUID;

/**
 * Base class for ingest jobs.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * @author <a href="mailto:jose.garcia@geocat.net">Jose García</a>.
 */
public abstract class BaseIngestJob implements Runnable {
    /**
     * The ingest to be performed.
     */
    protected Ingest ingest;
    /**
     * Execution identifier.
     */
    protected UUID jobId;
    /**
     * Metadata ingester. Will ingest retrieved medatata into a concrete
     * instance.
     */
    protected MetadataIngester metadataIngester;
    /**
     * Metadata validator.
     */
    protected MetadataValidator metadataValidator;
    /**
     * Ingest execution report.
     */
    protected IngestReport report;
    /**
     * Metadata parsers factory.
     */
    protected MetadataParserProvider parserProvider;
    /**
     * Job execution status and statistics.
     */
    private IngestJobStatus jobStatus;

    /**
     * Job status service.
     */
    private IngestJobStatusService jobStatusService;

    /**
     * Ingest Report service.
     */
    private IngestReportService reportService;

    private IngestReportErrorService errorService;
    /**
     * If <code>true</code>, job must be interrupted when possible.
     */
    private boolean interruptRequest;

    /**
     * Create a new instance.
     */
    public BaseIngestJob() {
        this.interruptRequest = false;
        parserProvider = new XmlMetadataParserProvider();
        report = new IngestReport();
    }

    /**
     * Prepare the IngestJob to be run.
     *
     * @param jobId            job identifier.
     * @param ingest           ingest to be executed.
     * @param metadataIngester the ingester in charge to store the metadata.
     */
    public void init(UUID jobId, Ingest ingest,
                     MetadataIngester metadataIngester) {
        this.jobId = jobId;
        this.ingest = ingest;
        this.metadataIngester = metadataIngester;
        this.metadataValidator = new MetadataValidator(ingest, errorService);
        this.jobStatus = new IngestJobStatus();
        jobStatus.setStatus(IngestJobStatusValue.NOT_STARTED_YET);
        jobStatus.setJobExecutionIdentifier(jobId);
        jobStatus.setIngest(ingest);
        jobStatus = jobStatusService.save(jobStatus);
        report.setJobStatus(jobStatus);
        reportService.save(report);
        jobStatus.setIngestReport(report);
    }

    /**
     * @return the ingest report.
     */
    public IngestReport getIngestReport() {
        return report;
    }

    /**
     * @return the jobId
     */
    public UUID getJobId() {
        return jobId;
    }

    /**
     * Launch the process.
     */
    @Override
    public void run() {

        if (jobStatus == null) {
            throw new IllegalStateException(
                    "init method must be called before a run can be performed");
        }
        try {
            jobStatus.setStartTime(Calendar.getInstance().getTime());
            jobStatus.setStatus(IngestJobStatusValue.PROCESSING);
            jobStatus = jobStatusService.save(jobStatus);
            ingest();
            if (!isInterruptRequested()) {
                jobStatus.setStatus(IngestJobStatusValue.SUCCESSED);
            } else {
                jobStatus.setStatus(IngestJobStatusValue.CANCELLED);
            }
        } catch (Exception e) {
            jobStatus.setStatus(IngestJobStatusValue.FAILED);
        } finally {
            jobStatus.setEndTime(Calendar.getInstance().getTime());
            jobStatus = jobStatusService.save(jobStatus);
            report = reportService.save(report);

        }
    }

    /**
     * Method to be implemented by the child implementations and do the actual
     * ingest work.
     * @return true if job has been interrupted, false otherwise.
     */
    protected abstract void ingest();

    /**
     * @return the jobStatusService
     */
    public IngestJobStatusService getJobStatusService() {
        return jobStatusService;
    }

    /**
     * @param jobStatusService the jobStatusService to set
     */
    public void setJobStatusService(IngestJobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    /**
     * @return the reportService
     */
    public IngestReportService getReportService() {
        return reportService;
    }

    /**
     * @param reportService the reportService to set
     */
    public void setReportService(IngestReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * @return the errorService
     */
    public IngestReportErrorService getErrorService() {
        return errorService;
    }

    /**
     * @param errorService the errorService to set
     */
    public void setErrorService(IngestReportErrorService errorService) {
        this.errorService = errorService;
    }

    /**
     * Set a flag that indicates job must be interrupted.
     */
    public void interrupt() {
        this.interruptRequest = true;
    }

    /**
     * Check if job must be interrupted.
     *
     * @return
     */
    public boolean isInterruptRequested() {
        return interruptRequest;
    }
}
