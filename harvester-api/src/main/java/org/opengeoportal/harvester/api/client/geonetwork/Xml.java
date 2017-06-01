package org.opengeoportal.harvester.api.client.geonetwork;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import javax.xml.XMLConstants;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Utility class used in {@link XmlRequest} to manage {@link java.lang.String}
 * and {@link org.jdom.Element} conversions.
 *
 * Code from GeoNetwork opensource project.
 *
 */
public class Xml {
    private static SAXBuilder getSAXBuilderWithoutXMLResolver(
            final boolean validate) {
        final SAXBuilder builder = new SAXBuilder(validate);
        builder.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return builder;
    }

    /**
     * Converts an xml element to a string.
     *
     * @param data
     * @return
     */
    public static String getString(final Document data) {
        final XMLOutputter outputter = new XMLOutputter(
                Format.getPrettyFormat());

        return outputter.outputString(data);
    }

    /**
     * Converts an xml element to a string.
     *
     * @param data
     * @return
     */
    public static String getString(final Element data) {
        final XMLOutputter outputter = new XMLOutputter(
                Format.getPrettyFormat());

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
    public static Element loadFile(final URL url)
            throws IOException, JDOMException {
        final SAXBuilder builder = Xml.getSAXBuilderWithoutXMLResolver(false);// new
                                                                              // SAXBuilder();
        final Document jdoc = builder.build(url);

        return (Element) jdoc.getRootElement().detach();
    }

    /**
     * Loads an xml stream and returns its root node (validates the xml with a
     * dtd)
     *
     * @param input
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public static Element loadStream(final InputStream input)
            throws IOException, JDOMException {
        final SAXBuilder builder = new SAXBuilder();
        builder.setFeature(
                "http://apache.org/xml/features/allow-java-encodings", true);
        final Document jdoc = builder.build(input);

        return (Element) jdoc.getRootElement().detach();
    }

    /**
     * Loads an xml string and returns its root node (validates the xml with a
     * dtd)
     *
     * @param data
     * @param validate
     * @throws IOException
     * @throws JDOMException
     */
    public static Element loadString(final String data, final boolean validate)
            throws IOException, JDOMException {
        final SAXBuilder builder = new SAXBuilder(validate);
        builder.setFeature(
                "http://apache.org/xml/features/allow-java-encodings", true);

        final Document jdoc = builder.build(new StringReader(data));

        return (Element) jdoc.getRootElement().detach();
    }
}