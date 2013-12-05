package org.opengeoportal.harvester.api.client.geonetwork;


import org.opengeoportal.harvester.api.client.geonetwork.GeoNetworkSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeoNetworkSearchResponse {
    private int total;
    private List<GeoNetworkSearchResult> metadataSearchResults = new ArrayList<GeoNetworkSearchResult>();

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<GeoNetworkSearchResult> getMetadataSearchResults() {
        return Collections.unmodifiableList(metadataSearchResults);
    }

    public void addMetadataSearchResult(GeoNetworkSearchResult metadataSearchResult) {
        metadataSearchResults.add(metadataSearchResult);
    }
}
