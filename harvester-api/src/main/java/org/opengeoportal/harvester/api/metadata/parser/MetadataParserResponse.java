package org.opengeoportal.harvester.api.metadata.parser;

import java.util.ArrayList;

import org.opengeoportal.harvester.api.metadata.model.Metadata;

public class MetadataParserResponse extends IngestResponse {
    private final Metadata metadata;

    private boolean metadataParsed = false;

    public MetadataParserResponse() {
        this.metadata = new Metadata();
        this.ingestErrors = new ArrayList<IngestInfo>();
        this.ingestWarnings = new ArrayList<IngestInfo>();
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    public boolean isMetadataParsed() {
        return this.metadataParsed;
    }

    public void setMetadataParsed(final boolean metadataParsed) {
        this.metadataParsed = metadataParsed;
    }

}