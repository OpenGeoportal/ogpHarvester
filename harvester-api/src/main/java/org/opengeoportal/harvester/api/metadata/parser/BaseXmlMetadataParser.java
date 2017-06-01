package org.opengeoportal.harvester.api.metadata.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.time.DateUtils;
import org.opengeoportal.harvester.api.metadata.model.LocationLink;
import org.opengeoportal.harvester.api.metadata.model.LocationLink.LocationType;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Multimap;

public abstract class BaseXmlMetadataParser extends BaseMetadataParser
        implements MetadataParser {

    private class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> PREF_MAP = new HashMap<String, String>();

        public SimpleNamespaceContext(final Map<String, String> prefMap) {
            this.PREF_MAP.putAll(prefMap);
        }

        @Override
        public String getNamespaceURI(final String prefix) {
            return this.PREF_MAP.get(prefix);
        }

        @Override
        public String getPrefix(final String uri) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator getPrefixes(final String uri) {
            throw new UnsupportedOperationException();
        }

    }

    public interface Tag {

        String getTagName();

        String getXPathName();
    }

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Medadata XML document.
     */
    protected Document document;

    protected XPath xPath;;

    protected MetadataParserResponse metadataParserResponse;

    public String getDocumentValue(final Tag tag) throws Exception {
        return (String) this.xPath.evaluate(tag.getXPathName(), this.document,
                XPathConstants.STRING);
    }

    protected String getFullText() {
        return XmlUtil.getFullText(this.document);
    }

    protected abstract LocationResolver getLocationResolver();

    protected abstract HashMap<String, String> getNamespaces();

    protected abstract void handleAbstract();

    protected abstract void handleAccess();

    protected abstract void handleBounds();

    protected abstract void handleDataType();

    protected abstract void handleDate();

    protected abstract void handleFullText();

    protected abstract void handleId();

    protected abstract void handleKeywords();

    protected abstract void handleLayerName();

    protected void handleLocation() {
        final Multimap<LocationType, LocationLink> locationMap = this
                .getLocationResolver().resolveLocation(this.document);
        final String locationJson = this
                .buildLocationJsonFromLocationLinks(locationMap);
        this.metadataParserResponse.getMetadata().setLocation(locationJson);

    }

    protected abstract void handleOriginator();

    protected abstract void handlePublisher();

    protected abstract void handleTitle();

    @Override
    public MetadataParserResponse parse(final Document document) {
        if (document == null) {
            this.logger.error("document is null");
        }

        final XPathFactory factory = XPathFactory.newInstance();
        this.xPath = factory.newXPath();

        final HashMap<String, String> prefMap = this.getNamespaces();
        final SimpleNamespaceContext namespaces = new SimpleNamespaceContext(
                prefMap);
        this.xPath.setNamespaceContext(namespaces);

        this.document = document;
        this.metadataParserResponse = new MetadataParserResponse();
        this.handleId();
        this.handleTitle();
        this.handleAbstract();
        this.handleLayerName();
        this.handlePublisher();
        this.handleOriginator();
        this.handleBounds();
        this.handleKeywords();
        this.handleAccess();
        this.handleDataType();
        this.handleFullText();
        this.handleDate();
        this.handleLocation();
        this.metadataParserResponse.setMetadataParsed(true);
        return this.metadataParserResponse;
    }

    protected Date processDateString(String passedDate) throws ParseException {
        // can't do anything if there's no value passed
        if ((passedDate == null) || (passedDate.equalsIgnoreCase("unknown"))) {
            return null;
        }
        final List<String> formatsList = new ArrayList<String>();
        // add likely formats in order of likelihood

        formatsList.add("yyyyMMdd");
        formatsList.add("yyyyMM");
        formatsList.add("MM/yyyy");
        formatsList.add("MM/dd/yyyy");
        formatsList.add("MM/dd/yy");
        formatsList.add("MM-dd-yyyy");
        formatsList.add("MMMM yyyy");
        formatsList.add("MMM yyyy");
        formatsList.add("dd MMMM yyyy");
        formatsList.add("dd MMM yyyy");
        formatsList.add("yyyy");

        final String[] parsePatterns = formatsList
                .toArray(new String[formatsList.size()]);
        // String returnYear = null;

        passedDate = passedDate.trim();
        final Date date = DateUtils.parseDate(passedDate, parsePatterns);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        this.logger.debug("Document date: " + passedDate + ", Parsed date: "
                + calendar.get(Calendar.YEAR));
        // returnYear = Integer.toString(calendar.get(Calendar.YEAR));

        return date;
    }
}
