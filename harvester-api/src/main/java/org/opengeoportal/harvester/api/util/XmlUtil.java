package org.opengeoportal.harvester.api.util;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

    private static DocumentBuilder getDocumentBuilder()
            throws ParserConfigurationException {
        DocumentBuilder documentBuilder;

        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();

        documentBuilderFactory.setValidating(false); // dtd isn't available;
                                                     // would be nice to attempt
                                                     // to validate
        documentBuilderFactory.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder;
    }

    /**
     * Transform document into a string.
     *
     * @param document
     * @return document string represantion or {@code null} if document is null
     *         or any exception raises while transforming document.
     */
    public static String getFullText(final Document document) {
        String fileContents = null;
        if (document == null) {
            return fileContents;
        }
        try {
            final Source xmlSource = new DOMSource(document);
            final StringWriter stringWriter = new StringWriter();
            final StreamResult streamResult = new StreamResult(stringWriter);
            final TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlSource, streamResult);
            fileContents = stringWriter.toString();
        } catch (final TransformerConfigurationException e) {
            XmlUtil.LOGGER.error("transformer configuration error", e);
        } catch (final TransformerException e) {
            XmlUtil.LOGGER.error("transformer error", e);
        } catch (final IllegalArgumentException e) {
            XmlUtil.LOGGER
                    .error("Problem processing full text: " + e.getMessage());
        }
        return fileContents;
    }

    /**
     *
     * @param document
     * @return
     * @throws UnsupportedMetadataType
     *             if metadata type is not found or is not supported.
     */
    public static MetadataType getMetadataType(final Document document)
            throws UnsupportedMetadataType {
        MetadataType metadataType = null;
        String metadataText = "";
        try {
            // <metstdn>FGDC Content Standards for Digital Geospatial Metadata
            // <metstdv>FGDC-STD-001-1998
            final String metadata = document.getElementsByTagName("metstdn")
                    .item(0).getTextContent();
            metadataText = metadata;
            if (metadata.toLowerCase().contains("fgdc")) {
                metadataType = MetadataType.FGDC;
            }
        } catch (final Exception e) {/* ignore */

            // document.getElementsByTagName("metstdn").item(0).getTextContent().toLowerCase();
        }

        try {
            // <gmd:metadataStandardName>
            // <gmd:spatialRepresentationInfo>
            // <gmd:metadataStandardName>
            // <gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString>
            // </gmd:metadataStandardName>
            // existence of these two tags (ignoring namespace) should be good
            // enough
            final NodeList standardNodes = document.getElementsByTagNameNS("*",
                    "metadataStandardName");
            if (standardNodes.getLength() > 0) {
                final String metadata = standardNodes.item(0).getTextContent();
                metadataText = metadata;
                if (metadata.contains("19139")) {
                    metadataType = MetadataType.ISO_19139;
                }
            }
        } catch (final Exception e) {/* ignore */

        }

        if (metadataType == null) {
            // throw an exception...metadata type is not supported
            throw new UnsupportedMetadataType(
                    "Metadata Type [" + metadataText + "] is not supported.");
        }
        return metadataType;
    }

    public static Document load(final InputStream inputStream)
            throws Exception {
        return XmlUtil.getDocumentBuilder().parse(inputStream);

    }

    public static Document load(final String uri) throws Exception {
        return XmlUtil.getDocumentBuilder().parse(uri);
    }
}
