package org.opengeoportal.harvester.api.component;

import java.util.Calendar;
import java.util.UUID;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestJobStatus;
import org.opengeoportal.harvester.api.domain.IngestJobStatusValue;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserProvider;
import org.opengeoportal.harvester.api.metadata.parser.XmlMetadataParserProvider;

/**
 * Base class for ingest jobs.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * @author <a href="mailto:jose.garcia@geocat.net">Jose García</a>.
 * 
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
	 * Create a new instance.
	 */
	public BaseIngestJob() {
		parserProvider = new XmlMetadataParserProvider();
		report = new IngestReport();
	}

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
	public void init(UUID jobId, Ingest ingest,
			MetadataIngester metadataIngester) {
		this.jobId = jobId;
		this.ingest = ingest;
		this.metadataIngester = metadataIngester;
		this.metadataValidator = new MetadataValidator(ingest);
		this.jobStatus = new IngestJobStatus();
		jobStatus.setStatus(IngestJobStatusValue.NOT_STARTED_YET);
		jobStatus.setJobExecutionIdentifier(jobId);
		ingest.addJobStatus(jobStatus);
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
	public void run() {

		if (jobStatus == null) {
			throw new IllegalStateException(
					"init method must be called before a run can be performed");
		}
		try {
			jobStatus.setStartTime(Calendar.getInstance().getTime());
			jobStatus.setStatus(IngestJobStatusValue.PROCESSING);
			ingest();
			jobStatus.setStatus(IngestJobStatusValue.SUCCESSED);
		} catch (Exception e) {
			jobStatus.setStatus(IngestJobStatusValue.FAILED);
		} finally {
			jobStatus.setEndTime(Calendar.getInstance().getTime());

		}
	}

	/**
	 * Method to be implemented by the child implementations and do the actual
	 * ingest work.
	 */
	protected abstract void ingest();

}
