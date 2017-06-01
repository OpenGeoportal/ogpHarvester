package org.opengeoportal.harvester.api.component.geonetwork;

import java.net.URL;
import java.util.List;

import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkClient;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchParams;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResponse;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResult;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;
import org.opengeoportal.harvester.api.domain.IngestReportErrorType;
import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

/**
 * IngestJob that read from a remote Geonetwork.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class GeonetworkIngestJob extends BaseIngestJob {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
     * (non-Javadoc)
     *
     * @see org.opengeoportal.harvester.api.component.BaseIngestJob#ingest()
     */
    @Override
    public void ingest() {
        try {
            boolean processFinished = false;
            int page = 0;
            long failedRecordsCount = 0;

            final URL geonetworkURL = new URL(this.ingest.getActualUrl());

            final GeoNetworkClient gnClient = new GeoNetworkClient(
                    geonetworkURL);

            final GeoNetworkSearchParams searchParameters = new GeoNetworkSearchParams(
                    (IngestGeonetwork) this.ingest);
            this.logger.info("GeonetworkIngestJob: search parameters "
                    + searchParameters.toString());

            while (!(this.isInterruptRequested() || processFinished)) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info(String.format(
                            "Ingest %d: requesting page=%d,"
                                    + " pageSize=%d, from=%d, to=%d to GN",
                            this.ingest.getId(), page,
                            searchParameters.getPageSize(),
                            searchParameters.getFrom(),
                            searchParameters.getTo()));
                }
                searchParameters.setPage(page++);
                final GeoNetworkSearchResponse searchResponse = gnClient
                        .search(searchParameters);

                final List<Metadata> metadataList = Lists
                        .newArrayListWithCapacity(searchResponse
                                .getMetadataSearchResults().size());

                for (final GeoNetworkSearchResult record : searchResponse
                        .getMetadataSearchResults()) {
                    Document document = null;
                    try {
                        document = gnClient.retrieveMetadata(record.getId());

                        final MetadataParser parser = this.parserProvider
                                .getMetadataParser(document);
                        final MetadataParserResponse parserResult = parser
                                .parse(document);

                        final Metadata metadata = parserResult.getMetadata();
                        metadata.setInstitution(
                                this.ingest.getNameOgpRepository());

                        final boolean valid = this.metadataValidator
                                .validate(metadata, this.report);
                        if (valid) {
                            metadataList.add(metadata);
                        } else {
                            failedRecordsCount++;
                        }

                    } catch (final Exception ex) {
                        failedRecordsCount++;
                        this.saveException(ex,
                                IngestReportErrorType.SYSTEM_ERROR, document);

                    }
                }
                this.report.setFailedRecordsCount(failedRecordsCount);
                this.metadataIngester.ingest(metadataList, this.report);

                // --- check to see if we have to perform additional searches
                processFinished = ((searchParameters.getFrom()
                        + searchParameters.getPageSize()) > searchResponse
                                .getTotal());

            }

        } catch (final Exception e) {
            this.logger.error(
                    "Error in Geonetwork Ingest: " + this.ingest.getName(), e);
            this.saveException(e, IngestReportErrorType.SYSTEM_ERROR);
        }

    }
}
