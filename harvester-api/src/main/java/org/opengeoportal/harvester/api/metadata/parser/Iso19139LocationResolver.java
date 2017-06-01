package org.opengeoportal.harvester.api.metadata.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

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
    public LocationLink getBrowseLink(final Document xmlDocument)
            throws Exception {
        new HashSet<LocationLink>();
        final NodeList linkNodes = xmlDocument.getElementsByTagNameNS("*",
                "MD_BrowseGraphic");
        for (int j = 0; j < linkNodes.getLength(); j++) {
            final NodeList children = linkNodes.item(j).getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {

                final Node currentNode = children.item(i);
                final String nodeName = currentNode.getNodeName();
                if (nodeName.toLowerCase().contains("fileName")) {
                    final URL url = new URL(
                            currentNode.getTextContent().trim());
                    return new LocationLink(LocationType.browseGraphic, url);
                }
            }
        }
        throw new Exception("No browse graphic.");
    }

    public Multimap<LocationType, LocationLink> getLinksFromCI_OnlineResource(
            final Document xmlDocument) {
        final Multimap<LocationType, LocationLink> linksMultimap = ArrayListMultimap
                .create();
        final NodeList linkNodes = xmlDocument.getElementsByTagNameNS("*",
                "CI_OnlineResource");
        // protocol
        // linkage
        // name
        for (int j = 0; j < linkNodes.getLength(); j++) {
            final NodeList children = linkNodes.item(j).getChildNodes();
            LocationType locType = null;
            URL url = null;
            String name = null;
            for (int i = 0; i < children.getLength(); i++) {
                final Node currentNode = children.item(i);
                final String nodeName = currentNode.getNodeName();

                if (nodeName.toLowerCase().contains("protocol")) {
                    final String protocol = currentNode.getTextContent().trim();
                    // try to parse service type from protocol
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Protocol: " + protocol);
                    }
                    try {
                        locType = this.parseServiceLocationType(protocol);
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug(
                                    "Location type: " + locType.toString());
                        }
                    } catch (final Exception e) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger
                                    .debug("Unable to determine LocationType protocol: "
                                            + e.getMessage());
                        }
                    }
                } else if (nodeName.toLowerCase().contains("linkage")) {
                    final String url$ = currentNode.getTextContent().trim();
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("URL: " + url$);
                    }

                    if (url$.startsWith("http") || url$.startsWith("ftp")) {
                        try {
                            url = new URL(url$);
                        } catch (final MalformedURLException e) {
                        }
                    }
                } else if (nodeName.toLowerCase().contains("name")) {
                    name = currentNode.getTextContent().trim();
                }
            }

            if ((locType != null) && (url != null)) {
                final LocationLink link = new LocationLink(locType, url);
                if (name != null) {
                    link.setResourceName(name);
                }
                linksMultimap.put(link.getLocationType(), link);
            }
        }

        return linksMultimap;
    }

    @Override
    public Multimap<LocationType, LocationLink> resolveLocation(
            final Document xmlDocument) {
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
         * </gco :CharacterString></gmd:fileName><gmd:fileDescription><gco:
         * CharacterString >
         * large_thumbnail</gco:CharacterString></gmd:fileDescription><gmd:
         * fileType
         * ><gco:CharacterString>gif</gco:CharacterString></gmd:fileType></gmd:
         * MD_BrowseGraphic></gmd:graphicOverview>
         */
        final Multimap<LocationType, LocationLink> linksMultimap = ArrayListMultimap
                .create();

        linksMultimap.putAll(this.getLinksFromCI_OnlineResource(xmlDocument));
        // look at the links, determine if it's an ows, zip, other filetype

        try {
            final LocationLink browseLink = this.getBrowseLink(xmlDocument);
            linksMultimap.put(browseLink.getLocationType(), browseLink);
        } catch (final Exception e) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Error parsing location browse link");
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(
                    "areLinks:" + Boolean.toString(!linksMultimap.isEmpty()));
        }
        return linksMultimap;
    }
}
