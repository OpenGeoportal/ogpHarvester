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
import com.google.common.collect.Multimaps;

/**
 * Determine the Location field content of a FGDC Metadata document.
 * 
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * @author Chris S. Barnett.
 * 
 */
public class FgdcLocationResolver extends AbstractLocationResolver implements
		LocationResolver {
	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Multimap<LocationLink.LocationType, LocationLink> resolveLocation(Document xmlDocument) {
		/*
		 * <onlink>ftp://congo.iluci.org/CARPE_data_explorer/Products/DFCM_products
		 * /ltlt/ltlt_457_time2.tif.gz</onlink>
		 * 
		 * 
		 * <browse>
		 *    <browsen>ftp://congo.iluci.org/CARPE_data_explorer/Products/DFCM_products
		 *       /ltlt/ltlt_457_t2.jpg</browsen> 
		 *    <browsed>jpg</browsed>
		 *    <browset>thumbnail</browset> 
		 * </browse>
		 * <browse>
		 *    <browsen>ftp://congo.iluci
		 *       .org/CARPE_data_explorer/Products/DFCM_products
		 *        /ltlt/ltlt_457_t2.jpg</browsen> 
		 *    <browsed>jpg</browsed>
		 *    <browset>large_thumbnail</browset> </browse>
		 */
		Multimap<LocationLink.LocationType, LocationLink> linksMap = ArrayListMultimap.create();
		Set<String> serviceLinks = getLinksFromTagType("onlink", xmlDocument);
		// look at the links, determine if it's an ows, zip, other filetype
		Iterator<String> serviceIterator = serviceLinks.iterator();
		if (serviceIterator.hasNext()) {
			String serviceLink = serviceIterator.next();
			URL serviceURL = null;
			try {
				serviceURL = new URL(serviceLink);
				
				// determine locationType
				LocationLink link = new LocationLink(
						parseServiceLocationType(serviceLink), serviceURL);
				linksMap.put(link.getLocationType(), link);
			} catch (MalformedURLException e) {
				if (logger.isWarnEnabled()) {
					logger.warn("Invalid URL parsing location:" + serviceLink);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		Set<String> browseLinks = getLinksFromTagType("browsen", xmlDocument);
		Iterator<String> browseIterator = browseLinks.iterator();
		if (browseIterator.hasNext()) {
			// we just need one
			String browseLink = browseIterator.next();
			URL browseURL = null;
			try {
				browseURL = new URL(browseLink);
				LocationLink link = new LocationLink(LocationType.browseGraphic,
						browseURL);
				linksMap.put(link.getLocationType(), link);
			} catch (MalformedURLException e) {
				logger.error("Invalid URL:" + browseLink);
			}
		}
		return linksMap;
	}

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
	private Set<String> getLinksFromTagType(String tagName, Document xmlDocument) {
		Set<String> linkValueSet = new HashSet<String>();
		NodeList linkNodes = xmlDocument.getElementsByTagName(tagName);
		for (int j = 0; j < linkNodes.getLength(); j++) {
			String nodeValue = linkNodes.item(j).getTextContent().trim();
			if (nodeValue.startsWith("http") || nodeValue.startsWith("ftp")) {
				linkValueSet.add(nodeValue);
			}
		}
		return linkValueSet;
	}
}
