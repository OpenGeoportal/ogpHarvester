package org.opengeoportal.harvester.api.client.csw.request;

import org.jdom.Element;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.Section;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/** Params:
  *  - sections       (0..n)
  *  - updateSequence (0..1)
  *  - acceptFormats  (0..n)
  *  - acceptVersions (0..n)
  */

public class GetCapabilitiesRequest extends CatalogRequest
{
	private String sequence;

	private List<String> alVersions = new ArrayList<String>();
	private List<String> alFormats  = new ArrayList<String>();
	private Set<Section>  hsSections = new HashSet<Section>();

    public GetCapabilitiesRequest(URL cswServerUrl) {
        super(cswServerUrl);
    }

	public void addVersion(String version) {
		alVersions.add(version);
	}

	public void addSection(Section section) {
		hsSections.add(section);
	}

	public void addOutputFormat(String format) {
		alFormats.add(format);
	}


	public void setUpdateSequence(String sequence) {
		this.sequence = sequence;
	}

	protected String getRequestName() {
        return "GetCapabilities";
    }

	protected void setupGetParams() {
		addParam("request", getRequestName());
		addParam("service", Csw.SERVICE);

		if (sequence != null)
			addParam("updateSequence", sequence);

		fill("acceptVersions", alVersions);
		fill("sections",       hsSections);
		fill("acceptFormats",  alFormats);
	}

	protected Element getPostParams() {
		Element params  = new Element(getRequestName(), Csw.NAMESPACE_CSW);

		params.setAttribute("service", Csw.SERVICE);

		if (sequence != null)
			params.setAttribute("updateSequence", sequence);

		fill(params, "AcceptVersions", "Version",      alVersions, Csw.NAMESPACE_OWS);
		fill(params, "Sections",       "Section",      hsSections, Csw.NAMESPACE_OWS);
		fill(params, "AcceptFormats",  "OutputFormat", alFormats,  Csw.NAMESPACE_OWS);

		return params;
	}
}