package org.opengeoportal.harvester.api.client.geonetwork;

import org.jdom.Element;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;

public class GeoNetworkSearchParams {
    private static final int NUMBER_OF_RESULTS_PER_PAGE = 40;

    private String freeText;
    private String title;
    private String abstractText;
    private String keyword;
    private String siteId;

    private int from = 1;
    private int pageSize = NUMBER_OF_RESULTS_PER_PAGE;

    public int getPageSize() {
        return pageSize;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public GeoNetworkSearchParams(IngestGeonetwork ingest) {
       this(ingest, NUMBER_OF_RESULTS_PER_PAGE);

    }

    public GeoNetworkSearchParams(IngestGeonetwork ingest, int pageSize) {
        this.freeText = ingest.getFreeText();
        this.title = ingest.getTitle();
        this.abstractText = ingest.getAbstractText();
        this.keyword = ingest.getKeyword();
        // TODO
        this.siteId = "";

        this.pageSize = pageSize;
    }

    public Element toXml() {
        Element req = new Element("request");

        add(req, "any", this.freeText);
        add(req, "title", this.title);
        add(req, "abstract", this.abstractText);
        add(req, "themekey", this.keyword);
        add(req, "siteId", this.siteId);
        add(req, "from", this.from + "");
        add(req, "to", (this.from + this.pageSize) + "");


        return req;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append("any: " + this.freeText + NEW_LINE);
        result.append("title: " + this.title + NEW_LINE);
        result.append("abstract: " + this.abstractText + NEW_LINE);
        result.append("themekey: " + this.keyword + NEW_LINE);
        result.append("siteId: " + this.siteId + NEW_LINE);
        result.append("from: " + this.from + NEW_LINE);
        result.append("to: " + this.from + this.pageSize + NEW_LINE);
        result.append("}");

        return result.toString();
    }

    private void add(Element req, String name, String value) {
        if (value.length() != 0)
            req.addContent(new Element(name).setText(value));
    }
}