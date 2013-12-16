package org.opengeoportal.harvester.api.client.csw;

import org.jdom.Namespace;

import javax.xml.XMLConstants;

public final class Csw {
    //---------------------------------------------------------------------------
    //---
    //--- Namespaces
    //---
    //---------------------------------------------------------------------------

    public static final Namespace NAMESPACE_CSW = Namespace.getNamespace("csw", "http://www.opengis.net/cat/csw/2.0.2");
    public static final Namespace NAMESPACE_CSW_OLD = Namespace.getNamespace("csw", "http://www.opengis.net/cat/csw");
    public static final Namespace NAMESPACE_OGC = Namespace.getNamespace("ogc", "http://www.opengis.net/ogc");
    public static final Namespace NAMESPACE_OWS = Namespace.getNamespace("ows", "http://www.opengis.net/ows");
    public static final Namespace NAMESPACE_ENV = Namespace.getNamespace("env", "http://www.w3.org/2003/05/soap-envelope");
    public static final Namespace NAMESPACE_GMD = Namespace.getNamespace("gmd", "http://www.isotc211.org/2005/gmd");
    public static final Namespace NAMESPACE_DC = Namespace.getNamespace("dc","http://purl.org/dc/elements/1.1/");
    public static final Namespace NAMESPACE_XSI = Namespace.getNamespace("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

    //---------------------------------------------------------------------------
    //---
    //--- Strings
    //---
    //---------------------------------------------------------------------------
    public static final String OWS_SCHEMA_LOCATIONS = "http://schemas.opengis.net";
    public static final String SCHEMA_LANGUAGE = "http://www.w3.org/XML/Schema";
    public static final String SERVICE         = "CSW";

    public static final String CSW_VERSION    = "2.0.2";
    public static final String OWS_VERSION    = "1.2.0";
    public static final String FILTER_VERSION_1_1 = "1.1.0";
    public static final String FILTER_VERSION_1_0 = "1.0.0";

    // Queryables
    public static final String ISO_QUERYABLES = "SupportedISOQueryables";
    public static final String ADDITIONAL_QUERYABLES = "AdditionalQueryables";

    // Sections
    public static final String SECTION_SI = "ServiceIdentification";
    public static final String SECTION_SP = "ServiceProvider";
    public static final String SECTION_OM = "OperationsMetadata";
    public static final String SECTION_FC = "Filter_Capabilities";

    public static final String OPERATION = "Operation";

    public static final String OUTPUT_FORMAT_APPLICATION_XML  = "application/xml";

}
