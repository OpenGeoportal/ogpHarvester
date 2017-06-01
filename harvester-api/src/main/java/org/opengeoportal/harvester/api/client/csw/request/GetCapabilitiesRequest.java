package org.opengeoportal.harvester.api.client.csw.request;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.Section;

/**
 * Params: - sections (0..n) - updateSequence (0..1) - acceptFormats (0..n) -
 * acceptVersions (0..n)
 */

public class GetCapabilitiesRequest extends CatalogRequest {
    private String sequence;

    private final List<String> alVersions = new ArrayList<String>();
    private final List<String> alFormats = new ArrayList<String>();
    private final Set<Section> hsSections = new HashSet<Section>();

    public GetCapabilitiesRequest(final URL cswServerUrl) {
        super(cswServerUrl);
    }

    public void addOutputFormat(final String format) {
        this.alFormats.add(format);
    }

    public void addSection(final Section section) {
        this.hsSections.add(section);
    }

    public void addVersion(final String version) {
        this.alVersions.add(version);
    }

    @Override
    protected Element getPostParams() {
        final Element params = new Element(this.getRequestName(),
                Csw.NAMESPACE_CSW);

        params.setAttribute("service", Csw.SERVICE);

        if (this.sequence != null) {
            params.setAttribute("updateSequence", this.sequence);
        }

        this.fill(params, "AcceptVersions", "Version", this.alVersions,
                Csw.NAMESPACE_OWS);
        this.fill(params, "Sections", "Section", this.hsSections,
                Csw.NAMESPACE_OWS);
        this.fill(params, "AcceptFormats", "OutputFormat", this.alFormats,
                Csw.NAMESPACE_OWS);

        return params;
    }

    @Override
    protected String getRequestName() {
        return "GetCapabilities";
    }

    public void setUpdateSequence(final String sequence) {
        this.sequence = sequence;
    }

    @Override
    protected void setupGetParams() {
        this.addParam("request", this.getRequestName());
        this.addParam("service", Csw.SERVICE);

        if (this.sequence != null) {
            this.addParam("updateSequence", this.sequence);
        }

        this.fill("acceptVersions", this.alVersions);
        this.fill("sections", this.hsSections);
        this.fill("acceptFormats", this.alFormats);
    }
}