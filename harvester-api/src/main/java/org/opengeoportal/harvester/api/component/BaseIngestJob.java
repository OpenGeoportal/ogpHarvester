package org.opengeoportal.harvester.api.component;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserProvider;
import org.opengeoportal.harvester.api.metadata.parser.XmlMetadataParserProvider;

import java.util.UUID;

public abstract class BaseIngestJob implements Runnable {
    protected Ingest ingest;
    protected UUID jobId;
    protected MetadataIngester metadataIngester;
    protected MetadataValidator metadataValidator;
    protected IngestReport report = new IngestReport();
    protected MetadataParserProvider parserProvider;


    public BaseIngestJob() {
        parserProvider = new XmlMetadataParserProvider();
    }

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
