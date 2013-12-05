package org.opengeoportal.harvester.api.component;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestReport;

import java.util.UUID;

public abstract class BaseIngestJob implements Runnable {
    protected Ingest ingest;
    protected UUID jobId;
    protected MetadataIngester metadataIngester;
    protected MetadataValidator metadataValidator;
    protected IngestReport report = new IngestReport();

    public void init(UUID jobId, Ingest ingest, MetadataIngester metadataIngester) {
        this.jobId = jobId;
        this.ingest = ingest;
        this.metadataIngester = metadataIngester;
        this.metadataValidator = new MetadataValidator(ingest);
    }

    public IngestReport getIngestReport() {
        return report;
    }
}
