package org.opengeoportal.harvester.api.client.geonetwork;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

/**
 * Utility class used in {@link XmlRequest} to manage {@link java.lang.String} and {@link org.jdom.Element} conversions.
 *
 * Code from GeoNetwork opensource project.
 *
 */
public class Xml{
    /**
     * Loads an xml string and returns its root node (validates the xml with a dtd)
     *
     * @param data
     * @param validate
     * @throws IOException
     * @throws JDOMException
     */
    public static Element loadString(String data, boolean validate)
            throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder(validate);
        builder.setFeature("http://apache.org/xml/features/allow-java-encodings", true);


        Document jdoc    = builder.build(new StringReader(data));

        return (Element) jdoc.getRootElement().detach();
    }

    /**
     * Loads an xml stream and returns its root node (validates the xml with a dtd)
     *
     * @param input
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public static Element loadStream(InputStream input) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
        Document   jdoc    = builder.build(input);

        return (Element) jdoc.getRootElement().detach();
    }


    /**
     * Converts an xml element to a string.
     *
     * @param data
     * @return
     */
    public static String getString(Element data) {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

        return outputter.outputString(data);
    }

    /**
     * Converts an xml element to a string.
     *
     * @param data
     * @return
     */
    public static String getString(Document data) {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

        return outputter.outputString(data);
    }

    /**
     * Loads an xml file from a URL and returns its root node.
     *
     * @param url
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public static Element loadFile(URL url) throws IOException, JDOMException {
        SAXBuilder builder = getSAXBuilderWithoutXMLResolver(false);//new SAXBuilder();
        Document   jdoc    = builder.build(url);

        return (Element) jdoc.getRootElement().detach();
    }


    private static SAXBuilder getSAXBuilderWithoutXMLResolver(boolean validate) {
        SAXBuilder builder = new SAXBuilder(validate);
        builder.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return builder;
    }
}