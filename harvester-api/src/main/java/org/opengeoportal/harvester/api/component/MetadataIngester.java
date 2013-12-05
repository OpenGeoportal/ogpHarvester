package org.opengeoportal.harvester.api.component;

import org.opengeoportal.harvester.api.metadata.model.Metadata;

public interface MetadataIngester {
    public void ingest(Metadata metadata);
}
