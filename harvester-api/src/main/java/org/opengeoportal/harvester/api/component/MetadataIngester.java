package org.opengeoportal.harvester.api.component;

import java.util.List;

import org.opengeoportal.harvester.api.domain.IngestReport;
import org.opengeoportal.harvester.api.metadata.model.Metadata;

/**
 * Ingest metadata into a destination server.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public interface MetadataIngester {
    /**
     * Ingest a list of metadata using batch.
     * 
     * @param metadataList
     *            metadata list to ingest
     * @param ingestReport
     */
    void ingest(List<Metadata> metadataList, IngestReport ingestReport);

    /**
     * Ingest one metadata into a destination server.
     * 
     * @param metadata
     *            metadata to ingest.
     */
    void ingest(Metadata metadata);
}
