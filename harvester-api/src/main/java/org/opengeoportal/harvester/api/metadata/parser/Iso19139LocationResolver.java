package org.opengeoportal.harvester.api.metadata.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Iso19139LocationResolver extends AbstractLocationResolver
		implements LocationResolver {
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Multimap<LocationType, LocationLink> resolveLocation(
			Document xmlDocument) {
		/*
		 * <gmd:CI_OnlineResource> <gmd:linkage>
		 * <gmd:URL>http://www.fao.org/figis
		 * /geoserver/species/ows?SERVICE=WMS</gmd:URL> </gmd:linkage>
		 * <gmd:protocol>
		 * <gco:CharacterString>OGC:WMS-1.3.0-http-get-map</gco:CharacterString>
		 * </gmd:protocol> <gmd:name>
		 * <gco:CharacterString>SPECIES_DIST_AAO</gco:CharacterString>
		 * </gmd:name> <gmd:description> <gco:CharacterString>FAO aquatic
		 * species distribution map of Acipenser
		 * oxyrinchus</gco:CharacterString> </gmd:description>
		 * </gmd:CI_OnlineResource> </gmd:onLine>
		 */

		/*
		 * <gmd:graphicOverview><gmd:MD_BrowseGraphic><gmd:fileName><gco:
		 * CharacterString
		 * >thumbnail_s.gif</gco:CharacterString></gmd:fileName><gmd
		 * :fileDescription
		 * ><gco:CharacterString>thumbnail</gco:CharacterString><
		 * /gmd:fileDescription
		 * ><gmd:fileType><gco:CharacterString>gif</gco:CharacterString
		 * ></gmd:fileType
		 * ></gmd:MD_BrowseGraphic></gmd:graphicOverview><gmd:graphicOverview
		 * ><gmd
		 * :MD_BrowseGraphic><gmd:fileName><gco:CharacterString>thumbnail.gif
		 * </gco
		 * :CharacterString></gmd:fileName><gmd:fileDescription><gco:CharacterString
		 * >
		 * large_thumbnail</gco:CharacterString></gmd:fileDescription><gmd:fileType
		 * ><gco:CharacterString>gif</gco:CharacterString></gmd:fileType></gmd:
		 * MD_BrowseGraphic></gmd:graphicOverview>
		 */
		Multimap<LocationType, LocationLink> linksMultimap = ArrayListMultimap
				.create();

		linksMultimap.putAll(getLinksFromCI_OnlineResource(xmlDocument));
		// look at the links, determine if it's an ows, zip, other filetype

		try {
			LocationLink browseLink = getBrowseLink(xmlDocument);
			linksMultimap.put(browseLink.getLocationType(), browseLink);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error parsing location browse link", e);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("areLinks:"
					+ Boolean.toString(!linksMultimap.isEmpty()));
		}
		return linksMultimap;
	}

	/*
	 * 
	 * <gmd:graphicOverview> <gmd:MD_BrowseGraphic> <gmd:fileName>
	 * <gco:CharacterString
	 * >http://www.fao.org/figis/geoserver/wms?service=WMS&amp
	 * ;version=1.1.0&amp;
	 * request=GetMap&amp;layers=fifao:UN_CONTINENT,SPECIES_DIST_HXC
	 * &amp;bbox=-178.480041504
	 * ,-56.531620026,179.576858521,74.20552063&amp;width
	 * =600&amp;height=219&amp;
	 * srs=EPSG:4326&amp;format=image%2Fpng</gco:CharacterString>
	 * </gmd:fileName> <gmd:fileDescription> <gco:CharacterString>FAO aquatic
	 * species distribution map of Chlamydoselachus
	 * anguineus</gco:CharacterString> </gmd:fileDescription> <gmd:fileType>
	 * <gco:CharacterString>image/png</gco:CharacterString> </gmd:fileType>
	 * </gmd:MD_BrowseGraphic> </gmd:graphicOverview>
	 */
	public LocationLink getBrowseLink(Document xmlDocument) throws Exception {
		Set<LocationLink> linkValueSet = new HashSet<LocationLink>();
		NodeList linkNodes = xmlDocument.getElementsByTagNameNS("*",
				"MD_BrowseGraphic");
		for (int j = 0; j < linkNodes.getLength(); j++) {
			NodeList children = linkNodes.item(j).getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {

				Node currentNode = children.item(i);
				String nodeName = currentNode.getNodeName();
				if (nodeName.toLowerCase().contains("fileName")) {
					URL url = new URL(currentNode.getTextContent().trim());
					return new LocationLink(LocationType.browseGraphic, url);
				}
			}
		}
		throw new Exception("No browse graphic.");
	}

	public Multimap<LocationType, LocationLink> getLinksFromCI_OnlineResource(
			Document xmlDocument) {
		Multimap<LocationType, LocationLink> linksMultimap = ArrayListMultimap
				.create();
		NodeList linkNodes = xmlDocument.getElementsByTagNameNS("*",
				"CI_OnlineResource");
		// protocol
		// linkage
		// name
		for (int j = 0; j < linkNodes.getLength(); j++) {
			NodeList children = linkNodes.item(j).getChildNodes();
			LocationType locType = null;
			URL url = null;
			String name = null;
			for (int i = 0; i < children.getLength(); i++) {
				Node currentNode = children.item(i);
				String nodeName = currentNode.getNodeName();

				if (nodeName.toLowerCase().contains("protocol")) {
					String protocol = currentNode.getTextContent().trim();
					// try to parse service type from protocol
					if (logger.isDebugEnabled()) {
						logger.debug("Protocol: " + protocol);
					}
					try {
						locType = parseServiceLocationType(protocol);
						if (logger.isDebugEnabled()) {
							logger.debug("Location type: " + locType.toString());
						}
					} catch (Exception e) {
						if (logger.isWarnEnabled()) {
							logger.warn(
									"Unable to determine LocationType protocol",
									e);
						}
					}
				} else if (nodeName.toLowerCase().contains("linkage")) {
					String url$ = currentNode.getTextContent().trim();
					if (logger.isDebugEnabled()) {
						logger.debug("URL: " + url$);
					}

					if (url$.startsWith("http") || url$.startsWith("ftp")) {
						try {
							url = new URL(url$);
						} catch (MalformedURLException e) {
						}
					}
				} else if (nodeName.toLowerCase().contains("name")) {
					name = currentNode.getTextContent().trim();
				}
			}

			if ((locType != null) && (url != null)) {
				LocationLink link = new LocationLink(locType, url);
				if (name != null) {
					link.setResourceName(name);
				}
				linksMultimap.put(link.getLocationType(), link);
			}
		}

		return linksMultimap;
	}
}
