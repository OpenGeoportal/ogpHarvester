package org.opengeoportal.harvester.api.component;

import java.util.Calendar;
import java.util.UUID;

import org.opengeoportal.harvester.api.client.solr.SolrRecord;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestJobStatus;
import org.opengeoportal.harvester.api.domain.IngestJobStatusValue;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.domain.IngestReportError;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserProvider;
import org.opengeoportal.harvester.api.metadata.parser.XmlMetadataParserProvider;
import org.opengeoportal.harvester.api.service.ExceptionTranslator;
import org.opengeoportal.harvester.api.service.IngestJobStatusService;
import org.opengeoportal.harvester.api.service.IngestReportErrorService;
import org.opengeoportal.harvester.api.service.IngestReportService;
import org.opengeoportal.harvester.api.service.IngestReportWarningsService;
import org.w3c.dom.Document;

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

    private IngestReportWarningsService warningService;
    private ExceptionTranslator exceptionTranslatorService;

    /**
     * If <code>true</code>, job must be interrupted when possible.
     */
    private boolean interruptRequest;

    /**
     * Create a new instance.
     */
    public BaseIngestJob() {
        this.interruptRequest = false;
        this.parserProvider = new XmlMetadataParserProvider();
        this.report = new IngestReport();
    }

    /**
     * @return the errorService
     */
    public IngestReportErrorService getErrorService() {
        return this.errorService;
    }

    public ExceptionTranslator getExceptionTranslatorService() {
        return this.exceptionTranslatorService;
    }

    /**
     * @return the ingest report.
     */
    public IngestReport getIngestReport() {
        return this.report;
    }

    /**
     * @return the jobId
     */
    public UUID getJobId() {
        return this.jobId;
    }

    /**
     * @return the jobStatusService
     */
    public IngestJobStatusService getJobStatusService() {
        return this.jobStatusService;
    }

    /**
     * @return the reportService
     */
    public IngestReportService getReportService() {
        return this.reportService;
    }

    /**
     * @return the warningService
     */
    public IngestReportWarningsService getWarningService() {
        return this.warningService;
    }

    /**
     * Method to be implemented by the child implementations and do the actual
     * ingest work.
     *
     * @return true if job has been interrupted, false otherwise.
     */
    protected abstract void ingest();

    /**
     * Prepare the IngestJob to be run.
     *
     * @param jobId
     *            job identifier.
     * @param ingest
     *            ingest to be executed.
     * @param metadataIngester
     *            the ingester in charge to store the metadata.
     */
    public void init(final UUID jobId, final Ingest ingest,
            final MetadataIngester metadataIngester) {
        this.jobId = jobId;
        this.ingest = ingest;
        this.metadataIngester = metadataIngester;
        this.metadataValidator = new MetadataValidator(ingest,
                this.errorService, this.warningService);
        this.jobStatus = new IngestJobStatus();
        this.jobStatus.setStatus(IngestJobStatusValue.NOT_STARTED_YET);
        this.jobStatus.setJobExecutionIdentifier(jobId);
        this.jobStatus.setIngest(ingest);
        this.jobStatus = this.jobStatusService.save(this.jobStatus);
        this.report.setJobStatus(this.jobStatus);
        this.reportService.save(this.report);
        this.jobStatus.setIngestReport(this.report);
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
        return this.interruptRequest;
    }

    /**
     * Launch the process.
     */
    @Override
    public void run() {

        if (this.jobStatus == null) {
            throw new IllegalStateException(
                    "init method must be called before a run can be performed");
        }
        try {
            this.jobStatus.setStartTime(Calendar.getInstance().getTime());
            this.jobStatus.setStatus(IngestJobStatusValue.PROCESSING);
            this.jobStatus = this.jobStatusService.save(this.jobStatus);
            this.ingest();
            if (!this.isInterruptRequested()) {
                this.jobStatus.setStatus(IngestJobStatusValue.SUCCESSED);
            } else {
                this.jobStatus.setStatus(IngestJobStatusValue.CANCELLED);
            }
        } catch (final Exception e) {
            this.jobStatus.setStatus(IngestJobStatusValue.FAILED);
        } finally {
            this.jobStatus.setEndTime(Calendar.getInstance().getTime());
            this.jobStatus = this.jobStatusService.save(this.jobStatus);
            this.report = this.reportService.save(this.report);

        }
    }

    protected void saveException(final Exception e,
            final IngestReportErrorType errorType) {
        final IngestReportError error = this.exceptionTranslatorService
                .translateException(e, errorType);
        error.setReport(this.report);
        this.getErrorService().save(error);
        this.report.addError(error);
    }

    /**
     * Create a new IngestError and store it; always for system errors
     *
     * @param e
     *            Exception to be logged.
     * @param errorType
     *            type of error.
     */
    protected void saveException(final Exception e,
            final IngestReportErrorType errorType, final Document document) {
        final IngestReportError error = this.exceptionTranslatorService
                .translateException(e, errorType, document);
        error.setReport(this.report);
        this.getErrorService().save(error);
        this.report.addError(error);
    }

    protected void saveException(final Exception e,
            final IngestReportErrorType errorType, final SolrRecord record) {
        final IngestReportError error = this.exceptionTranslatorService
                .translateException(e, errorType, record);
        error.setReport(this.report);
        this.getErrorService().save(error);
        this.report.addError(error);
    }

    /**
     * @param errorService
     *            the errorService to set
     */
    public void setErrorService(final IngestReportErrorService errorService) {
        this.errorService = errorService;
    }

    public void setExceptionTranslatorService(
            final ExceptionTranslator exceptionTranslatorService) {
        this.exceptionTranslatorService = exceptionTranslatorService;
    }

    /**
     * @param jobStatusService
     *            the jobStatusService to set
     */
    public void setJobStatusService(
            final IngestJobStatusService jobStatusService) {
        this.jobStatusService = jobStatusService;
    }

    /**
     * @param reportService
     *            the reportService to set
     */
    public void setReportService(final IngestReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * @param warningService
     *            the warningService to set
     */
    public void setWarningService(
            final IngestReportWarningsService warningService) {
        this.warningService = warningService;
    }
}
