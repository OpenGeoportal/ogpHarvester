package org.opengeoportal.harvester.api.metadata.parser;

import org.opengeoportal.harvester.api.metadata.model.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;
import java.util.*;

public abstract class BaseXmlMetadataParser implements MetadataParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Document document;
    protected XPath xPath;

    protected int numberOfParseWarnings = 0;
    protected Vector<String> missingParseTags = new Vector<String>();
    protected MetadataParserResponse metadataParserResponse;

    public interface Tag {
        String getTagName();
        String getXPathName();
    };

    @Override
    public MetadataParserResponse parse(Document document) {
        if (document == null){
            logger.error("document is null");
        }

        XPathFactory factory=XPathFactory.newInstance();
        xPath=factory.newXPath();

        HashMap<String, String> prefMap = new HashMap<String, String>() {{
            put("gmd", "http://www.isotc211.org/2005/gmd");
            put("gco", "http://www.isotc211.org/2005/gco");
        }};

        SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
        xPath.setNamespaceContext(namespaces);

        this.document = document;
        this.metadataParserResponse = new MetadataParserResponse();
        handleTitle();
        handleAbstract();
        handleLayerName();
        handlePublisher();
        handleOriginator();
        handleBounds();
        handleKeywords();
        handleAccess();
        handleDataType();
        handleFullText();
        handleDate();
        metadataParserResponse.setMetadataParsed(true);
        return metadataParserResponse;
    }

    abstract void handleOriginator();

    abstract void handlePublisher();

    abstract void handleLayerName();

    abstract void handleAbstract();

    abstract void handleTitle();

    abstract void handleDate();

    abstract void handleDataType();

    abstract void handleAccess();

    abstract void handleKeywords();

    abstract void handleBounds();

    abstract void handleFullText();


    public String getDocumentValue(Tag tag) throws Exception {
        return (String) xPath.evaluate(tag.getXPathName(), document, XPathConstants.STRING);
    }

    protected String getFullText()
    {
        try
        {
            Source xmlSource = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlSource, streamResult);
            String fileContents = stringWriter.toString();

            return fileContents;

        } catch (TransformerConfigurationException e) {
            // TODO Auto-generated catch block
            logger.error("transformer configuration error");
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            logger.error("transformer error");

            e.printStackTrace();
        } catch (Exception e){
            logger.error("Problem processing full text: " + e.getMessage());
        }
        return null;
    }

    protected Boolean validateBounds(String minX, String minY, String maxX, String maxY){
        BoundingBox bounds = new BoundingBox(minX, minY, maxX, maxY);
        if (bounds.isValid()){
            return true;
        } else {
            return false;
        }
    }


    private class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> PREF_MAP = new HashMap<String, String>();

        public SimpleNamespaceContext(final Map<String, String> prefMap) {
            PREF_MAP.putAll(prefMap);
        }

        public String getNamespaceURI(String prefix) {
            return PREF_MAP.get(prefix);
        }

        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        public Iterator getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }

    }
}
