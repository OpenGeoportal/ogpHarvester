package org.opengeoportal.harvester.api.client.csw.exception;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.geonetwork.Xml;

public class CatalogException extends Exception {
    private static final long serialVersionUID = -2483411203445474288L;

    protected static final String INVALID_PARAMETER_VALUE = "InvalidParameterValue";
    protected static final String INVALID_UPDATE_SEQUENCE = "InvalidUpdateSequence";
    protected static final String MISSING_PARAMETER_VALUE = "MissingParameterValue";
    protected static final String NO_APPLICABLE_CODE = "NoApplicableCode";
    protected static final String OPERATION_NOT_SUPPORTED = "OperationNotSupported";
    protected static final String VERSION_NEGOTIATION_FAILED = "VersionNegotiationFailed";

    // ---------------------------------------------------------------------------

    private static CatalogException createException(final Element ex,
            final Element response, final CatalogException prev)
            throws Exception {
        final Namespace ns = response.getNamespace();

        final String code = ex.getAttributeValue("exceptionCode");

        if (code == null) {
            throw new Exception("Bad exception (no 'exceptionCode' attr) : \n"
                    + Xml.getString(response));
        }

        final Element text = ex.getChild("ExceptionText", ns);

        final String locator = ex.getAttributeValue("locator");
        final String message = (text == null) ? null : text.getText();

        CatalogException e = null;

        if (code.equals(CatalogException.INVALID_PARAMETER_VALUE)) {
            e = new InvalidParameterValueEx(null, message, prev);
        }

        if (code.equals(CatalogException.INVALID_UPDATE_SEQUENCE)) {
            e = new InvalidUpdateSequenceEx(message, prev);
        }

        if (code.equals(CatalogException.MISSING_PARAMETER_VALUE)) {
            e = new MissingParameterValueEx(null, prev);
        }

        if (code.equals(CatalogException.NO_APPLICABLE_CODE)) {
            e = new NoApplicableCodeEx(message, prev);
        }

        if (code.equals(CatalogException.OPERATION_NOT_SUPPORTED)) {
            e = new OperationNotSupportedEx(null, prev);
        }

        if (code.equals(CatalogException.VERSION_NEGOTIATION_FAILED)) {
            e = new VersionNegotiationFailedEx(message, prev);
        }

        if (e == null) {
            throw new Exception("Bad exception (unknown 'exceptionCode') : \n"
                    + Xml.getString(response));
        }

        e.locator = locator;

        return e;
    }

    public static Element marshal(CatalogException e) {
        // --- setup root node

        final Element root = new Element("ExceptionReport", Csw.NAMESPACE_OWS);
        root.setAttribute("version", Csw.OWS_VERSION);
        root.addNamespaceDeclaration(Csw.NAMESPACE_XSI);
        root.setAttribute("schemaLocation",
                Csw.NAMESPACE_OWS.getURI() + " " + Csw.OWS_SCHEMA_LOCATIONS
                        + "/ows/1.0.0/owsExceptionReport.xsd",
                Csw.NAMESPACE_XSI);

        while (e != null) {
            final Element exc = new Element("Exception", Csw.NAMESPACE_OWS);
            exc.setAttribute("exceptionCode", e.getCode());

            if (e.getMessage() != null) {
                exc.addContent(new Element("ExceptionText", Csw.NAMESPACE_OWS)
                        .setText(e.getMessage()));
            }

            if (e.getLocator() != null) {
                exc.setAttribute("locator", e.getLocator());
            }

            root.addContent(exc);

            e = (CatalogException) e.getCause();
        }

        return root;
    }

    // ---------------------------------------------------------------------------
    // ---
    // --- Constructor
    // ---
    // ---------------------------------------------------------------------------

    public static void unmarshal(final Element response) throws Exception {
        if (!response.getName().equals("ExceptionReport")) {
            return;
        }

        final Namespace ns = response.getNamespace();

        final List exceptions = response.getChildren("Exception", ns);

        if (exceptions.size() == 0) {
            throw new Exception("Bad exception (no 'Exception' elem) : \n"
                    + Xml.getString(response));
        }

        CatalogException e = null;

        for (int i = exceptions.size() - 1; i >= 0; i--) {
            final Element ex = (Element) exceptions.get(i);

            e = CatalogException.createException(ex, response, e);
        }

        throw e;
    }

    // ---------------------------------------------------------------------------

    private final String code;

    // ---------------------------------------------------------------------------
    // ---
    // --- API methods
    // ---
    // ---------------------------------------------------------------------------

    private String locator;

    public CatalogException(final String code, final String message,
            final String locator) {
        super(message);

        this.code = code;
        this.locator = locator;
    }

    // ---------------------------------------------------------------------------

    public CatalogException(final String code, final String message,
            final String locator, final CatalogException cause) {
        super(message, cause);

        this.code = code;
        this.locator = locator;
    }

    // ---------------------------------------------------------------------------

    public String getCode() {
        return this.code;
    }

    // ---------------------------------------------------------------------------

    public String getLocator() {
        return this.locator;
    }

    // ---------------------------------------------------------------------------

    @Override
    public String toString() {
        final String clazz = this.getClass().getName();
        return clazz + ": code=" + this.code + ", locator=" + this.locator
                + ", message=" + this.getMessage();
    }
}

// =============================================================================
/*
 * 
 * il Content-Type deve essere "application/soap+xml; charset="utf-8" " il
 * Content-Length deve essere settato
 * 
 * <?xml version="1.0" ?>
 * 
 * <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope"> <env:Body>
 * <env:Fault> <env:Code> <env:Value>env:Sender|env:Receiver</env:Value> [
 * <env:Subcode> <env:Value>gn:xxx</env:Value> [ <env:Subcode>...</env:Subcode>
 * ] </env:Subcode> ] </env:Code>
 * 
 * <env:Reason> <env:Text xml:lang="en">...human readable...</env:Text> ...
 * </env:Reason>
 * 
 * <env:Detail> <... env:encodingStyle="geonet.org/encoding/error"> ...altri
 * figli con pi� info </...> </env:Detail> </env:Fault> </env:Body>
 * </env:Envelope>
 * 
 * 
 * 
 * <env:Code> <env:Value>env:Sender|env:Receiver</env:Value> [ <env:Subcode>
 * <env:Value>gn:xxx</env:Value> [ <env:Subcode>...</env:Subcode> ]
 * </env:Subcode> ] </env:Code>
 * 
 * <env:Reason> <env:Text xml:lang="en">...human readable...</env:Text> ...
 * </env:Reason>
 * 
 * <env:Detail> <... env:encodingStyle="geonet.org/encoding/error"> ...altri
 * figli con pi� info </...> </env:Detail>
 * 
 * 
 * 
 * 
 * <env:faultcode>env:Server</env:faultcode> <env:faultstring>...human
 * readable...</env:faultstring>
 * 
 * <env:detail> <ows:ExceptionReport>... </env:detail>
 * 
 */
