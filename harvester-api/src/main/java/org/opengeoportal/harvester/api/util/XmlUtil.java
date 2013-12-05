package org.opengeoportal.harvester.api.util;

import org.w3c.dom.Document;

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
