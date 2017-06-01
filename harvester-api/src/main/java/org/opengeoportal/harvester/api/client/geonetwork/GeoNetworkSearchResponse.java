package org.opengeoportal.harvester.api.client.geonetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeoNetworkSearchResponse {
    private int total;
    private final List<GeoNetworkSearchResult> metadataSearchResults = new ArrayList<GeoNetworkSearchResult>();

    public void addMetadataSearchResult(
            final GeoNetworkSearchResult metadataSearchResult) {
        this.metadataSearchResults.add(metadataSearchResult);
    }

    public List<GeoNetworkSearchResult> getMetadataSearchResults() {
        return Collections.unmodifiableList(this.metadataSearchResults);
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }
}
