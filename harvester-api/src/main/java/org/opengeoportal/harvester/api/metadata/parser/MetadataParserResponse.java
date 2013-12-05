package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.metadata.model.Metadata;

import java.util.ArrayList;

public class MetadataParserResponse extends IngestResponse {
    private Metadata metadata;

    private boolean metadataParsed = false;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadataParsed(boolean metadataParsed) {
        this.metadataParsed = metadataParsed;
    }

    public boolean isMetadataParsed() {
        return metadataParsed;
    }

    public MetadataParserResponse(){
		metadata = new Metadata();
		ingestErrors = new ArrayList<IngestInfo>();
		ingestWarnings = new ArrayList<IngestInfo>();
	}
	
}