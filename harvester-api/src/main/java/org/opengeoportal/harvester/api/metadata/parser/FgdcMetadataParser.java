package org.opengeoportal.harvester.api.metadata.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.apache.commons.lang3.StringUtils;
import org.opengeoportal.harvester.api.metadata.model.AccessLevel;
import org.opengeoportal.harvester.api.metadata.model.GeometryType;
import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.opengeoportal.harvester.api.metadata.model.PlaceKeywords;
import org.opengeoportal.harvester.api.metadata.model.ThemeKeywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Multimap;

/**
 * Metadata parser capable of process FGDC metadata.
 *
 * @author <a href="mailto:jose.garcia@geocat.net">José García</a>.
 * @author <a href="mailto:juanluisrp@geocat.net">Juan Luis Rodríguez</a>.
 *
 */
public class FgdcMetadataParser extends BaseXmlMetadataParser {

    public static enum FgdcTag implements Tag {

        Title("title", "/metadata/idinfo/citation/citeinfo/title"), Abstract(
                "abstract",
                "/metadata/idinfo/descript/abstract"), LayerName("ftname",
                        "/metadata/idinfo/citation/citeinfo/ftname"), Publisher(
                                "publish",
                                "/metadata/idinfo/citation/citeinfo/pubinfo/publish"), Originator(
                                        "origin",
                                        "/metadata/idinfo/citation/citeinfo/origin"), WestBc(
                                                "westbc",
                                                "/metadata/idinfo/spdom/bounding/westbc"), EastBc(
                                                        "eastbc",
                                                        "/metadata/idinfo/spdom/bounding/eastbc"), NorthBc(
                                                                "northbc",
                                                                "/metadata/idinfo/spdom/bounding/northbc"), SouthBc(
                                                                        "southbc",
                                                                        "/metadata/idinfo/spdom/bounding/southbc"), Access(
                                                                                "accconst",
                                                                                "/metadata/idinfo/accconst"),
        // Keywords
        PlaceKeywordsHeader("place",
                "/metadata/idinfo/keywords/place"), ThemeKeywordsHeader("theme",
                        "/metadata/idinfo/keywords/theme"), PlaceKeywordsThesaurus(
                                "placekt",
                                "/metadata/idinfo/keywords/place/placekt"), ThemeKeywordsThesaurus(
                                        "themekt",
                                        "/metadata/idinfo/keywords/theme/placekt"), PlaceKeywords(
                                                "placekey", "placekey"), // relative
                                                                         // xpath
        ThemeKeywords("themekey", "themekey"), // relative xpath
        // Data type
        DataType_Srccitea("srccitea", "/metadata/spdoinfo/srccitea"), // TODO:
        // Verify
        // xpath
        DataType_Direct("direct",
                "/metadata/spdoinfo/direct"), DataType_Sdtstype("sdtstype",
                        "/metadata/spdoinfo/ptvctinf/sdtsterm/sdtstype"), Date_Caldate(
                                "caldate",
                                "/metadata/idinfo/timeperd/timeinfo/sngdate/caldate"), Date_Begdate(
                                        "begdate",
                                        "/metadata/idinfo/timeperd/timeinfo/rngdates/begdate"), Date_DateStamp(
                                                "dateStamp",
                                                "/metadata/idinfo/timeperd/timeinfo/dateStamp"); // TODO:
        // Verify
        // xpath;

        private final String tagName;
        private final String xPath; // XML xpath

        FgdcTag(final String tagName, final String xPath) {
            this.tagName = tagName;
            this.xPath = xPath;
        }

        @Override
        public String getTagName() {
            return this.tagName;
        }

        @Override
        public String getXPathName() {
            return this.xPath;
        }
    }

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Location resolver.
     */
    private final LocationResolver locationResolver = new FgdcLocationResolver();

    private final IsoTopicResolver isoTopicResolver = new IsoTopicResolver();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.opengeoportal.harvester.api.metadata.parser.BaseXmlMetadataParser#
     * getLocationResolver()
     */
    @Override
    protected LocationResolver getLocationResolver() {
        return this.locationResolver;
    }

    @Override
    protected HashMap<String, String> getNamespaces() {
        return new HashMap<String, String>();
    }

    @Override
    protected void handleAbstract() {
        final Tag tag = FgdcTag.Abstract;
        try {
            this.metadataParserResponse.getMetadata()
                    .setDescription(this.getDocumentValue(tag));
        } catch (final Exception e) {
            this.logger.error("handleAbstract: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void handleAccess() {
        final Tag tag = FgdcTag.Access;
        try {
            String accessValueMd = "";
            try {
                accessValueMd = this.getDocumentValue(tag);
            } catch (final Exception e) {
                final AccessLevel nullAccess = null;
                this.metadataParserResponse.getMetadata()
                        .setAccessLevel(nullAccess);
                return;
            }

            AccessLevel accessValue = AccessLevel.Public;
            accessValueMd = accessValueMd.toLowerCase();
            if (accessValueMd.startsWith("restricted")) {
                accessValue = AccessLevel.Restricted;
            }
            this.metadataParserResponse.getMetadata()
                    .setAccessLevel(accessValue);
        } catch (final Exception e) {
            this.logger.error("handleAccess: " + e.getMessage());
            this.metadataParserResponse.addError(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void handleBounds() {
        Tag tag = FgdcTag.NorthBc;
        try {
            final String maxY = this.getDocumentValue(tag);
            tag = FgdcTag.EastBc;
            final String maxX = this.getDocumentValue(tag);
            tag = FgdcTag.SouthBc;
            final String minY = this.getDocumentValue(tag);
            tag = FgdcTag.WestBc;
            final String minX = this.getDocumentValue(tag);
            // should validate bounds here
            if (this.validateBounds(minX, minY, maxX, maxY)) {
                this.metadataParserResponse.getMetadata().setBounds(minX, minY,
                        maxX, maxY);
            } else {
                throw new Exception("Invalid Bounds: " + minX + "," + minY + ","
                        + maxX + "," + maxY);
            }
        } catch (final Exception e) {
            this.logger.error("handleBounds: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void handleDataType() {
        String direct = null; // raster?
        String sdtsType = null; // vector type
        String srcCiteA = null; // scanned map

        try {
            srcCiteA = this.getDocumentValue(FgdcTag.DataType_Srccitea);
            if (srcCiteA.equalsIgnoreCase("Paper Map")) {
                this.metadataParserResponse.getMetadata()
                        .setGeometryType(GeometryType.ScannedMap);
                return;
            }
        } catch (final Exception e) {
            // just continue to next block
        }

        try {
            direct = this.getDocumentValue(FgdcTag.DataType_Direct);
            if (direct.equalsIgnoreCase("raster")) {
                this.metadataParserResponse.getMetadata()
                        .setGeometryType(GeometryType.Raster);
                return;
            }
        } catch (final Exception e) {
            // again, move to the next block
        }

        try {
            sdtsType = this.getDocumentValue(FgdcTag.DataType_Sdtstype);
            GeometryType solrType;
            if (sdtsType.equals("G-polygon") || sdtsType.contains("olygon")
                    || sdtsType.contains("chain")) {
                solrType = GeometryType.Polygon;

            } else if (sdtsType.equals("Composite")
                    || sdtsType.contains("omposite")
                    || sdtsType.equals("Entity point")) {
                solrType = GeometryType.Point;

            } else if (sdtsType.equals("String")) {
                solrType = GeometryType.Line;

            } else {
                solrType = GeometryType.Undefined;
            }
            this.metadataParserResponse.getMetadata().setGeometryType(solrType);
        } catch (final Exception e) {
            this.logger.error("null geometry type");
            final GeometryType solrType = GeometryType.Undefined;
            this.metadataParserResponse.getMetadata().setGeometryType(solrType);
            // we should make a note if the geometry type is undefined
        }
    }

    @Override
    protected void handleDate() {
        String dateString = null;
        Date dateValue = null;
        try {
            dateString = this.getDocumentValue(FgdcTag.Date_Caldate);
        } catch (final Exception e) {
            dateString = null;
        }

        if (StringUtils.isEmpty(dateString)) {
            try {
                dateString = this.getDocumentValue(FgdcTag.Date_Begdate);
            } catch (final Exception e) {
                dateString = null;
            }
        }

        if (StringUtils.isEmpty(dateString)) {
            try {
                dateString = this.getDocumentValue(FgdcTag.Date_DateStamp);
            } catch (final Exception e) {
                this.logger.warn(
                        "No valid Content Date could be found in the document.");
                this.metadataParserResponse.getMetadata().setContentDate(null);
                return;
            }
        }

        try {
            this.logger.debug("DATE VALUE#######:" + dateString);
            dateValue = this.processDateString(dateString);
            this.metadataParserResponse.getMetadata().setContentDate(dateValue);
        } catch (final Exception e) {
            try {
                dateString = dateString.substring(0, 3);
                final int dateValueInt = Integer.parseInt(dateString);
                dateString = Integer.toString(dateValueInt);
                if (dateString.length() == 4) {
                    this.metadataParserResponse.getMetadata()
                            .setContentDate(this.processDateString(dateString));
                }
            } catch (final Exception e1) {
                this.logger.warn(
                        "No valid Content Date could be found in the document.");
                this.metadataParserResponse.getMetadata().setContentDate(null);
            }
        }
    }

    @Override
    protected void handleFullText() {
        final String fullText = this.getFullText();
        this.metadataParserResponse.getMetadata().setFullText(fullText);
        this.metadataParserResponse.getMetadata().setOriginalMetadata(fullText);
    }

    @Override
    protected void handleId() {

    }

    @Override
    protected void handleKeywords() {
        final List<ThemeKeywords> themeKeywordList = new ArrayList<ThemeKeywords>();
        final List<PlaceKeywords> placeKeywordList = new ArrayList<PlaceKeywords>();

        try {
            // Theme keywords
            // if there is an ISO topic keyword set it the value on the Metadata
            // object
            String isoTopic = "";
            final NodeList themeKeywordNodes = (NodeList) this.xPath.evaluate(
                    FgdcTag.ThemeKeywordsHeader.getXPathName(), this.document,
                    XPathConstants.NODESET);

            for (int i = 0; i < themeKeywordNodes.getLength(); i++) {
                final Node keyword = themeKeywordNodes.item(i);

                final NodeList keywordValueNodes = (NodeList) this.xPath
                        .evaluate(FgdcTag.ThemeKeywords.getXPathName(), keyword,
                                XPathConstants.NODESET);
                if (keywordValueNodes.getLength() > 0) {
                    final String keywordThesaurus = (String) this.xPath
                            .evaluate(
                                    FgdcTag.ThemeKeywordsThesaurus
                                            .getXPathName(),
                                    keyword, XPathConstants.STRING);

                    final ThemeKeywords themeKeyword = new ThemeKeywords();
                    themeKeyword.setThesaurus(keywordThesaurus);

                    for (int j = 0; j < keywordValueNodes.getLength(); j++) {
                        final String keywordValue = keywordValueNodes.item(j)
                                .getTextContent();
                        themeKeyword.addKeyword(keywordValue);
                        isoTopic = this.isoTopicResolver
                                .getIsoTopicKeyword(keywordValue);
                    }

                    themeKeywordList.add(themeKeyword);
                }
            }

            // place keywords
            final NodeList placeKeywordNodes = (NodeList) this.xPath.evaluate(
                    FgdcTag.PlaceKeywordsHeader.getXPathName(), this.document,
                    XPathConstants.NODESET);

            for (int i = 0; i < placeKeywordNodes.getLength(); i++) {
                final Node keyword = placeKeywordNodes.item(i);

                final NodeList keywordValueNodes = (NodeList) this.xPath
                        .evaluate(FgdcTag.PlaceKeywords.getXPathName(), keyword,
                                XPathConstants.NODESET);
                if (keywordValueNodes.getLength() > 0) {
                    final String keywordThesaurus = (String) this.xPath
                            .evaluate(
                                    FgdcTag.PlaceKeywordsThesaurus
                                            .getXPathName(),
                                    keyword, XPathConstants.STRING);

                    final PlaceKeywords placeKeyword = new PlaceKeywords();
                    placeKeyword.setThesaurus(keywordThesaurus);

                    for (int j = 0; j < keywordValueNodes.getLength(); j++) {
                        final String keywordValue = keywordValueNodes.item(j)
                                .getTextContent();
                        placeKeyword.addKeyword(keywordValue);
                    }

                    placeKeywordList.add(placeKeyword);
                }
            }

            if (!isoTopic.isEmpty()) {
                this.metadataParserResponse.getMetadata().setTopic(isoTopic);
            }
            this.metadataParserResponse.getMetadata()
                    .setThemeKeywords(themeKeywordList);
            this.metadataParserResponse.getMetadata()
                    .setPlaceKeywords(placeKeywordList);
        } catch (final Exception e) {
            this.logger.error("handleKeywords: " + e.getMessage());
        }
    }

    @Override
    protected void handleLayerName() {
        // LayerName calculated from the location links
        final Multimap<LocationLink.LocationType, LocationLink> locationMap = this
                .getLocationResolver().resolveLocation(this.document);

        final Tag tag = FgdcTag.LayerName;

        try {
            final Collection<LocationLink> wmsLinks = locationMap
                    .get(LocationLink.LocationType.wms);

            if ((wmsLinks != null) && (!wmsLinks.isEmpty())) {
                final Iterator<LocationLink> it = wmsLinks.iterator();
                while (it.hasNext()) {
                    final String resourceName = it.next().getResourceName();

                    if (StringUtils.isNotEmpty(resourceName)) {
                        this.metadataParserResponse.getMetadata()
                                .setOwsName(resourceName);
                        break;
                    }
                }
            }

            // If empty, use ftName property
            if (StringUtils.isEmpty(
                    this.metadataParserResponse.getMetadata().getOwsName())) {
                this.metadataParserResponse.getMetadata()
                        .setOwsName(this.getDocumentValue(tag));
            }

        } catch (final Exception e) {
            this.logger.error("handleLayerName: " + e.getMessage());
            this.metadataParserResponse.addError(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opengeoportal.harvester.api.metadata.parser.BaseXmlMetadataParser
     * #handleLocation()
     */
    @Override
    protected void handleLocation() {
        final Multimap<LocationType, LocationLink> locationMap = this.locationResolver
                .resolveLocation(this.document);
        final String locationJson = this
                .buildLocationJsonFromLocationLinks(locationMap);
        this.metadataParserResponse.getMetadata().setLocation(locationJson);

    }

    @Override
    protected void handleOriginator() {
        final Tag tag = FgdcTag.Originator;
        try {
            this.metadataParserResponse.getMetadata()
                    .setOriginator(this.getDocumentValue(tag));
        } catch (final Exception e) {
            this.logger.error("handleOriginator: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void handlePublisher() {
        final Tag tag = FgdcTag.Publisher;
        try {
            this.metadataParserResponse.getMetadata()
                    .setPublisher(this.getDocumentValue(tag));
        } catch (final Exception e) {
            this.logger.error("handlePublisher: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    protected void handleTitle() {
        final Tag tag = FgdcTag.Title;
        try {
            this.metadataParserResponse.getMetadata()
                    .setTitle(this.getDocumentValue(tag));
        } catch (final Exception e) {
            this.logger.error("handleTitle: " + e.getMessage());
            this.metadataParserResponse.addWarning(tag.toString(),
                    tag.getTagName(), e.getClass().getName(), e.getMessage());
        }
    }
}
