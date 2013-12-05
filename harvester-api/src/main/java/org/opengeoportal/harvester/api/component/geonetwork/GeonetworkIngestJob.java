package org.opengeoportal.harvester.api.component.geonetwork;

import org.jdom.Element;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkClient;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchParams;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResult;
import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResponse;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
                    Element metadata = gnClient.retrieveMetadata(record.getId());

                    // TODO: Validate and ingest the metadata
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
