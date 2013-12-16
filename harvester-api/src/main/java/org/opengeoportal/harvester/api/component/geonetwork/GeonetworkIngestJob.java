package org.opengeoportal.harvester.api.component.geonetwork;

import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkClient;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchParams;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResult;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResponse;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;

import org.opengeoportal.harvester.api.metadata.model.Metadata;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


public class GeonetworkIngestJob extends BaseIngestJob {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        try {
            boolean processFinished = false;
            int start = 1;

            GeoNetworkClient gnClient = new GeoNetworkClient(ingest.getUrl());
            GeoNetworkSearchParams searchParameters = new GeoNetworkSearchParams((IngestGeonetwork) ingest);
            logger.info("GeonetworkIngestJob: search parameters " + searchParameters.toString());

            while (!processFinished) {
                searchParameters.setFrom(start);
                GeoNetworkSearchResponse searchResponse = gnClient.search(searchParameters);

                for(GeoNetworkSearchResult record : searchResponse.getMetadataSearchResults()) {
                    Document document = gnClient.retrieveMetadata(record.getId());

                    MetadataParser parser = new Iso19139MetadataParser();
                    MetadataParserResponse parserResult = parser.parse(document);

                    Metadata metadata = parserResult.getMetadata();
                    metadata.setInstitution(ingest.getNameOgpRepository());

                    boolean valid = metadataValidator.validate(metadata, report);
                    if (valid) {
                        metadataIngester.ingest(metadata);
                    }

                }

                //--- check to see if we have to perform additional searches
                processFinished =  (start+searchParameters.getPageSize() > searchResponse.getTotal());

                start += searchParameters.getPageSize();

            }
        } catch (Exception ex) {
            //TODO: Log exception
        }

    }
}
