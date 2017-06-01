package org.opengeoportal.harvester.api.metadata.parser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Determine the Location field content of a FGDC Metadata document.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * @author Chris S. Barnett.
 *
 */
public class FgdcLocationResolver extends AbstractLocationResolver
        implements LocationResolver {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Retrieve tagName nodes which content starts by <i>http</i> or <i>ftp</i>.
     * 
     * @param tagName
     *            node tag name
     * @param xmlDocument
     *            XML document.
     * @return a set with the nodes with tagName and which content looks like an
     *         URL.
     */
    private Set<String> getLinksFromTagType(final String tagName,
            final Document xmlDocument) {
        final Set<String> linkValueSet = new HashSet<String>();
        final NodeList linkNodes = xmlDocument.getElementsByTagName(tagName);
        for (int j = 0; j < linkNodes.getLength(); j++) {
            final String nodeValue = linkNodes.item(j).getTextContent().trim();
            if (nodeValue.startsWith("http") || nodeValue.startsWith("ftp")) {
                linkValueSet.add(nodeValue);
            }
        }
        return linkValueSet;
    }

    @Override
    public Multimap<LocationLink.LocationType, LocationLink> resolveLocation(
            final Document xmlDocument) {
        /*
         * <onlink>ftp://congo.iluci.org/CARPE_data_explorer/Products/
         * DFCM_products /ltlt/ltlt_457_time2.tif.gz</onlink>
         * 
         * 
         * <browse> <browsen>ftp://congo.iluci.org/CARPE_data_explorer/Products/
         * DFCM_products /ltlt/ltlt_457_t2.jpg</browsen> <browsed>jpg</browsed>
         * <browset>thumbnail</browset> </browse> <browse>
         * <browsen>ftp://congo.iluci
         * .org/CARPE_data_explorer/Products/DFCM_products
         * /ltlt/ltlt_457_t2.jpg</browsen> <browsed>jpg</browsed>
         * <browset>large_thumbnail</browset> </browse>
         */
        final Multimap<LocationLink.LocationType, LocationLink> linksMap = ArrayListMultimap
                .create();
        final Set<String> serviceLinks = this.getLinksFromTagType("onlink",
                xmlDocument);
        // look at the links, determine if it's an ows, zip, other filetype
        final Iterator<String> serviceIterator = serviceLinks.iterator();
        if (serviceIterator.hasNext()) {
            final String serviceLink = serviceIterator.next();
            URL serviceURL = null;
            try {
                serviceURL = new URL(serviceLink);

                // determine locationType
                final LocationLink link = new LocationLink(
                        this.parseServiceLocationType(serviceLink), serviceURL);
                linksMap.put(link.getLocationType(), link);
            } catch (final MalformedURLException e) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn(
                            "Invalid URL parsing location:" + serviceLink);
                }
            } catch (final Exception e) {
                this.logger.error(e.getMessage());
            }
        }

        final Set<String> browseLinks = this.getLinksFromTagType("browsen",
                xmlDocument);
        final Iterator<String> browseIterator = browseLinks.iterator();
        if (browseIterator.hasNext()) {
            // we just need one
            final String browseLink = browseIterator.next();
            URL browseURL = null;
            try {
                browseURL = new URL(browseLink);
                final LocationLink link = new LocationLink(
                        LocationType.browseGraphic, browseURL);
                linksMap.put(link.getLocationType(), link);
            } catch (final MalformedURLException e) {
                this.logger.error("Invalid URL:" + browseLink);
            }
        }
        return linksMap;
    }
}
