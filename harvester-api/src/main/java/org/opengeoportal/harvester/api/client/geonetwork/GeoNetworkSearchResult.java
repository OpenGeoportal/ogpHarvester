package org.opengeoportal.harvester.api.client.geonetwork;

public class GeoNetworkSearchResult {
    private int id;
    private String uuid;
    private String schema;
    private String changeDate;

    public String getChangeDate() {
        return this.changeDate;
    }

    public int getId() {
        return this.id;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setChangeDate(final String changeDate) {
        this.changeDate = changeDate;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setSchema(final String schema) {
        this.schema = schema;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }
}
