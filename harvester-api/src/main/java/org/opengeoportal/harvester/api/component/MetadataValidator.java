package org.opengeoportal.harvester.api.component;

import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

public class MetadataValidator {
    private Ingest ingest;

    public MetadataValidator(Ingest ingest) {
        this.ingest = ingest;
    }

    public boolean validate(Metadata metadata, IngestReport report) {
        //TODO: Use the ingest required fields to verify the metadata record and fill the report with related errors and warnings.

        return true;
    }
}
