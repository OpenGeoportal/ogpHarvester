package org.opengeoportal.harvester.api.client.geonetwork;

import org.jdom.Element;
import org.opengeoportal.harvester.api.domain.IngestGeonetwork;

/**
 * Page is 0 based.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class GeoNetworkSearchParams {
    private static final int NUMBER_OF_RESULTS_PER_PAGE = 40;

    private final String freeText;
    private final String title;
    private final String abstractText;
    private final String keyword;
    private final String siteId;
    private int page = 0;

    private int pageSize = GeoNetworkSearchParams.NUMBER_OF_RESULTS_PER_PAGE;

    public GeoNetworkSearchParams(final IngestGeonetwork ingest) {
        this(ingest, GeoNetworkSearchParams.NUMBER_OF_RESULTS_PER_PAGE);

    }

    public GeoNetworkSearchParams(final IngestGeonetwork ingest,
            final int pageSize) {
        this.freeText = ingest.getFreeText();
        this.title = ingest.getTitle();
        this.abstractText = ingest.getAbstractText();
        this.keyword = ingest.getKeyword();
        // TODO
        this.siteId = "";

        this.pageSize = pageSize;
    }

    private void add(final Element req, final String name, final String value) {
        if (value.length() != 0) {
            req.addContent(new Element(name).setText(value));
        }
    }

    public int getFrom() {
        return (this.page * this.pageSize) + 1;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return this.page;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTo() {
        return (this.page + 1) * this.pageSize;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(final int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        final String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append("any: " + this.freeText + NEW_LINE);
        result.append("title: " + this.title + NEW_LINE);
        result.append("abstract: " + this.abstractText + NEW_LINE);
        result.append("themekey: " + this.keyword + NEW_LINE);
        result.append("siteId: " + this.siteId + NEW_LINE);
        result.append("from: " + this.getFrom() + NEW_LINE);
        result.append("to: " + this.getTo() + NEW_LINE);
        result.append("}");

        return result.toString();
    }

    public Element toXml() {
        final Element req = new Element("request");

        this.add(req, "any", (this.freeText != null) ? this.freeText : "");
        this.add(req, "title", (this.title != null) ? this.title : "");
        this.add(req, "abstract",
                (this.abstractText != null) ? this.abstractText : "");
        this.add(req, "themekey", (this.keyword != null) ? this.keyword : "");
        this.add(req, "siteId", this.siteId);
        this.add(req, "from", this.getFrom() + "");
        this.add(req, "to", this.getTo() + "");

        return req;
    }
}