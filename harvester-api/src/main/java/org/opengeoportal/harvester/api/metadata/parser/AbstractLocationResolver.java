package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.w3c.dom.Document;

import com.google.common.collect.Multimap;

/**
 * Implement commons methods to the concrete LocationResolvers.
 *
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 * @author Chris S. Barnett.
 *
 */
public abstract class AbstractLocationResolver implements LocationResolver {

    /*
     * <gmd:CI_OnlineResource> <gmd:linkage>
     * <gmd:URL>http://www.fao.org/figis/geoserver
     * /species/ows?SERVICE=WMS</gmd:URL> </gmd:linkage> <gmd:protocol>
     * <gco:CharacterString>OGC:WMS-1.3.0-http-get-map</gco:CharacterString>
     * </gmd:protocol> <gmd:name>
     * <gco:CharacterString>SPECIES_DIST_AAO</gco:CharacterString> </gmd:name>
     * <gmd:description> <gco:CharacterString>FAO aquatic species distribution
     * map of Acipenser oxyrinchus</gco:CharacterString> </gmd:description>
     * </gmd:CI_OnlineResource> </gmd:onLine>
     */
    // wms
    // wfs
    // wcs
    // tilecache
    // arcGISRest
    // browseGraphic
    // non-georeferenced
    /*
     * Here is what we want:
     * http://linuxdev.lib.berkeley.edu:8080/geoserver/UCB/
     * wms?service=WMS&version
     * =1.1.0&request=GetMap&layers=UCB:images&CQL_FILTER=
     * PATH=%27furtwangler_sid/
     * 17076013_01_001a_s.sid%27&styles=&bbox=0.0,-65536.0,65536.0,0.0&width=512
     * &height=512&srs=EPSG:404000&format=application/openlayers
     * 
     * Here is what we get: fileName: 17076013_07_072a.tif location:
     * {"imageCollection": [{"collection": "UCB:images", "path": "furtwangler",
     * "url": "http://linuxdev.lib.berkeley.edu:8080/geoserver/UCB/wms",
     * collectionurl:
     * "http://www.lib.berkeley.edu/EART/mapviewer/collections/histoposf/"}]}
     */
    // serviceStart
    // zipFile
    // download

    /**
     * 
     * @param link
     *            link URL.
     * @return <code>true</code> if link is an ArcGIS REST resource,
     *         <code>false</code> otherwise.
     */
    public Boolean isArcGISRest(final String link) {
        final boolean result = link.toLowerCase().contains("arcgis/rest");
        return result;

    }

    /**
     * 
     * @param link
     *            link URL.
     * @return <code>true</code> if the resource is a file, <code>false</code>
     *         otherwise.
     */
    public Boolean isFile(final String link) {
        if (link.startsWith("ftp")) {
            // must be zipFile, download
            return true;
        }
        if (link.endsWith(".zip")) {
            return true;
        }
        if (link.endsWith(".gz")) {
            return true;
        }
        if (link.toLowerCase().contains("download")) {
            return true;
        }
        return false;
    }

    /**
     * @param link
     *            link URL.
     * @return <code>true</code> if link is a WCS resource, <code>false</code>
     *         in other case.
     */
    public Boolean isWCS(final String link) {
        final boolean result = link.toLowerCase().contains("wcs");
        return result;
    }

    /**
     * 
     * @param link
     *            link URL.
     * @return <code>true</code> if link is a WFS resource, <code>false</code>.
     *         in other case.
     */
    public Boolean isWFS(final String link) {
        final boolean result = link.toLowerCase().contains("wfs");
        return result;

    }

    /**
     * 
     * @param link
     *            link URL.
     * @return <code>true</code> if link is a WMS resource, <code>false</code>.
     *         otherwise.
     */
    public Boolean isWMS(final String link) {
        final boolean result = link.toLowerCase().contains("wms");
        return result;
    }

    /**
     * Discover and return the location type of the passed link string.
     * 
     * @param link
     *            the link string.
     * @return the location type.
     * @throws Exception
     *             thrown if the location type can not be determined.
     */
    public LocationType parseServiceLocationType(String link) throws Exception {
        link = link.trim();
        /*
         * wms, wfs, wcs, //tilecache, imageCollection, ArcGISRest,
         * //serviceStart, //zipFile,? download;
         */
        if (this.isWMS(link)) {
            return LocationType.wms;
        } else if (this.isWFS(link)) {
            return LocationType.wfs;
        } else if (this.isWCS(link)) {
            return LocationType.wcs;
        } else if (this.isArcGISRest(link)) {
            return LocationType.ArcGISRest;
        } else if (this.isFile(link)) {
            return LocationType.fileDownload;
        } else {
            throw new Exception("link type not supported.");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.opengeoportal.harvester.api.
     * metadata.parser.LocationResolver#resolveLocation(org.w3c.dom.Document)
     */
    @Override
    public abstract Multimap<LocationType, LocationLink> resolveLocation(
            Document xmlDocument);

}
