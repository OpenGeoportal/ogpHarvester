package org.opengeoportal.harvester.api.util;

import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;


public class XmlUtil {
    public static Document load(InputStream inputStream)  throws Exception {
        return getDocumentBuilder().parse(inputStream);

    }

    public static Document load(String uri)  throws Exception {
        return getDocumentBuilder().parse(uri);
    }

    public static MetadataType getMetadataType(Document document) throws Exception {
        MetadataType metadataType = null;
        try {
            //<metstdn>FGDC Content Standards for Digital Geospatial Metadata
            //<metstdv>FGDC-STD-001-1998
            if (document.getElementsByTagName("metstdn").item(0).getTextContent().toLowerCase().contains("fgdc")){
                metadataType = MetadataType.FGDC;
            }
        } catch (Exception e){/*ignore*/
            //document.getElementsByTagName("metstdn").item(0).getTextContent().toLowerCase();
        }

        try {
            //  <gmd:metadataStandardName>
            //  <gmd:spatialRepresentationInfo>
            //<gmd:metadataStandardName>
            //  <gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString>
            //</gmd:metadataStandardName>
            //existence of these two tags (ignoring namespace) should be good enough
            NodeList standardNodes = document.getElementsByTagNameNS("*", "metadataStandardName");
            if (standardNodes.getLength() > 0){
                if (standardNodes.item(0).getTextContent().contains("19139")){
                    metadataType = MetadataType.ISO_19139;
                }
            }
        } catch (Exception e){/*ignore*/}

        if (metadataType == null){
            //throw an exception...metadata type is not supported
            throw new Exception("Metadata Type is not supported.");
        }
        return metadataType;
    }


    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder documentBuilder;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        documentBuilderFactory.setValidating(false);  // dtd isn't available; would be nice to attempt to validate
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder;
    }
}
