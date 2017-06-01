package org.opengeoportal.harvester.api.client.csw.request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.CswOperation;
import org.opengeoportal.harvester.api.client.csw.TypeName;
import org.opengeoportal.harvester.api.client.csw.response.GetCapabilitiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to parse GetCapabilities document.
 */
public class GetCapabilitiesParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Search for valid POST or GET URL and check that service is available
     * using GET method or POST/XML.
     *
     * SOAP services are not supported (TODO ?).
     * 
     * @param dcps
     * @param op
     */
    private void evaluateUrl(final List<Element> dcps, final CswOperation op) {
        if (dcps == null) {
            this.logger.warn("Missing 'ows:DCP' element in operation");
            return;
        }

        final Namespace ns = Namespace
                .getNamespace("http://www.w3.org/1999/xlink");

        for (final Element dcp : dcps) {
            final Element http = dcp.getChild("HTTP", Csw.NAMESPACE_OWS);

            if (http == null) {
                this.logger.warn("Missing 'ows:HTTP' element in operation/DCP");
                continue;
            }

            // GET method
            final Element getUrl = http.getChild("Get", Csw.NAMESPACE_OWS);

            if (getUrl == null) {
                this.logger.warn(
                        "No GET url found in current DCP. Checking POST ...");
            } else {
                final String tmpGetUrl = getUrl.getAttributeValue("href", ns);

                if ((tmpGetUrl != null) && (op.getUrl == null)) {
                    try {
                        op.getUrl = new URL(tmpGetUrl);
                        this.logger
                                .debug("Found URL (GET method): " + tmpGetUrl);
                    } catch (final MalformedURLException e) {
                        this.logger.error(
                                "Malformed 'xlink:href' attribute in operation's http method");
                    }
                }
            }

            // POST method
            @SuppressWarnings(value = "unchecked")
            final List<Element> postUrlList = http.getChildren("Post",
                    Csw.NAMESPACE_OWS);

            for (final Element postUrl : postUrlList) {
                if (postUrl == null) {
                    this.logger.warn("No POST url found in current DCP.");
                } else {
                    final String tmpPostUrl = postUrl.getAttributeValue("href",
                            ns);

                    if (tmpPostUrl == null) {
                        this.logger.warn(
                                "Missing 'xlink:href' attribute in operation's http method");
                    } else {
                        if (op.postUrl == null) {
                            // PostEncoding could return a SOAP service address.
                            // Not supported
                            final Element methodConstraint = postUrl
                                    .getChild("Constraint", Csw.NAMESPACE_OWS);

                            if (methodConstraint != null) {
                                final Element value = methodConstraint
                                        .getChild("Value", Csw.NAMESPACE_OWS);
                                if ((value != null)
                                        && value.getText().equals("SOAP")) {
                                    this.logger.warn("The URL " + tmpPostUrl
                                            + " using POST/SOAP method is not supported for harvesting.");
                                    continue;
                                }
                            }

                            try {
                                op.postUrl = new URL(tmpPostUrl);
                                this.logger.debug("Found URL (POST method):"
                                        + tmpPostUrl);
                                break;
                            } catch (final MalformedURLException e) {
                                this.logger.error(
                                        "Malformed 'xlink:href' attribute in operation's http method");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get operations name and properties needed for futur operation calls.
     *
     * @param oper
     * @return
     */
    @SuppressWarnings("unchecked")
    private CswOperation extractOperation(final Element oper,
            final String preferredServerVersion) {
        final String name = oper.getAttributeValue("name");

        if (name == null) {
            this.logger.warn("Operation has no 'name' attribute");
            return null;
        }

        final CswOperation op = new CswOperation();
        op.name = name;

        final List<Element> dcp = oper.getChildren("DCP", Csw.NAMESPACE_OWS);
        this.evaluateUrl(dcp, op);

        final List<Element> parameters = oper.getChildren("Parameter",
                Csw.NAMESPACE_OWS);
        this.logger.debug("Found " + parameters.size()
                + " parameters for operation: " + name);
        List<Element> outputSchemas = null;
        List<Element> typeNames = null;
        List<Element> outputFormats = null;
        List<Element> constraintLanguages = null;

        for (final Element parameter : parameters) {
            final String parameterName = parameter.getAttributeValue("name");
            this.logger.debug("Processing parameter: " + parameterName);
            if ((parameterName != null)
                    && parameterName.equalsIgnoreCase("outputSchema")) {
                outputSchemas = parameter.getChildren("Value",
                        Csw.NAMESPACE_OWS);
                this.logger.debug("Found " + outputSchemas.size()
                        + " outputSchemas for operation: " + name);
            }

            // CSW 07-045 spec sometime use typenames or typename for the
            // GetRecord type name parameter:
            // * With 's' in Table 29
            // * Without 's' p114 in GetCapabiltiies examples and in figure 10
            // 'getRecords service="CSW", typeName="gmd:MD_Metadata"'
            // Type name is used in both GetRecords (probably with typenames)
            // and DescribeRecord (problaby with typename) operation
            // so check for both parameters
            if ((parameterName != null)
                    && (parameterName.equalsIgnoreCase("typeNames")
                            || parameterName.equalsIgnoreCase("typeName"))) {
                typeNames = parameter.getChildren("Value", Csw.NAMESPACE_OWS);
                this.logger.debug("Found " + typeNames.size()
                        + " typeNames for operation: " + name);
            }

            if ((parameterName != null)
                    && parameterName.equalsIgnoreCase("outputFormat")) {
                outputFormats = parameter.getChildren("Value",
                        Csw.NAMESPACE_OWS);
                this.logger.debug("Found " + outputFormats.size()
                        + " outputFormats for operation: " + name);
            }

            if ((parameterName != null)
                    && parameterName.equalsIgnoreCase("CONSTRAINTLANGUAGE")) {
                constraintLanguages = parameter.getChildren("Value",
                        Csw.NAMESPACE_OWS);
                this.logger.debug("Found " + constraintLanguages.size()
                        + " constraintLanguage for operation: " + name);
            }
        }

        if (outputSchemas != null) {
            for (final Element outputSchema : outputSchemas) {
                final String outputSchemaValue = outputSchema.getValue();
                this.logger.debug("Adding outputSchema: " + outputSchemaValue
                        + " to operation: " + name);
                op.outputSchemaList.add(outputSchemaValue);
            }
            op.choosePreferredOutputSchema();
        } else {
            this.logger.warn("No outputSchema for operation: " + name);
        }

        if (constraintLanguages != null) {
            for (final Element constraintLanguage : constraintLanguages) {
                String constraintLanguageValue = constraintLanguage.getValue()
                        .toLowerCase();
                this.logger.debug("Adding constraintLanguage : "
                        + constraintLanguageValue + " to operation: " + name);
                if ("cql".equals(constraintLanguageValue)) {
                    this.logger.debug(
                            " Some implementation use CQL instead of CQL_TEXT for the CQL constraint language value.");
                    constraintLanguageValue = "cql_text";
                }
                op.constraintLanguage.add(constraintLanguageValue);
            }
        } else {
            this.logger.warn("No constraintLanguage for operation: " + name);
        }

        if (typeNames != null) {
            for (final Element typeName : typeNames) {
                final String typeNameValue = typeName.getValue();
                this.logger.debug("Adding typeName: " + typeNameValue
                        + " to operation: " + name);
                final TypeName tn = TypeName.getTypeName(typeNameValue);
                if (tn != null) {
                    op.typeNamesList.add(typeNameValue);
                } else {
                    this.logger.warn("  Unsupported typeName found: "
                            + typeNameValue + ".");
                }
            }
        } else {
            this.logger.warn("No typeNames for operation: " + name);
        }

        if (outputFormats != null) {
            for (final Element outputFormat : outputFormats) {
                final String outputFormatValue = outputFormat.getValue();
                this.logger.debug("Adding outputFormat: " + outputFormatValue
                        + " to operation: " + name);
                op.outputFormatList.add(outputFormatValue);
            }
            op.choosePreferredOutputFormat();
        } else {
            op.preferredOutputFormat = Csw.OUTPUT_FORMAT_APPLICATION_XML;
            this.logger.warn("No outputFormat for operation: " + name);
        }

        op.preferredServerVersion = preferredServerVersion;

        return op;
    }

    public GetCapabilitiesResponse parse(final Element capab) {
        final String preferredServerVersion = this.parseVersions(capab);
        final Map<String, CswOperation> operations = this.parseOperations(capab,
                preferredServerVersion);

        return new GetCapabilitiesResponse(operations, preferredServerVersion);
    }

    /**
     * Get available operations in the GetCapabilities document.
     *
     * @param capabil
     */
    private Map<String, CswOperation> parseOperations(final Element capabil,
            final String preferredServerVersion) {
        final Map<String, CswOperation> operations = new HashMap<String, CswOperation>();

        final Element operMd = capabil.getChild("OperationsMetadata",
                Csw.NAMESPACE_OWS);

        if (operMd == null) {
            // log("Missing 'ows:OperationsMetadata' element");
        } else {
            for (final Object e : operMd.getChildren()) {
                final Element elem = (Element) e;

                if ("Operation".equals(elem.getName())) {
                    final CswOperation oper = this.extractOperation(elem,
                            preferredServerVersion);

                    if (oper != null) {
                        operations.put(oper.name, oper);
                    }
                }
            }
        }

        return operations;
    }

    /**
     * Gets server supported versions.
     *
     * @param capabil
     */
    private String parseVersions(final Element capabil) {
        String preferredServerVersion = Csw.CSW_VERSION;
        final List<String> serverVersions = new ArrayList<String>();
        final Element serviceIdentificationMd = capabil
                .getChild("ServiceIdentification", Csw.NAMESPACE_OWS);

        if (serviceIdentificationMd == null) {
            this.logger.warn("Missing 'ows:ServiceTypeVersion' element");
        } else {
            @SuppressWarnings(value = "unchecked")
            final List<Element> serviceIdentificationMdElems = serviceIdentificationMd
                    .getChildren();
            for (final Element value : serviceIdentificationMdElems) {
                final String valueName = value.getName();
                this.logger.debug("Processing value: " + valueName);
                if ((valueName != null)
                        && valueName.equalsIgnoreCase("ServiceTypeVersion")) {
                    serverVersions.add(value.getValue());
                }
            }
        }

        // Select default CSW supported version
        if (serverVersions.isEmpty()) {
            serverVersions.add(Csw.CSW_VERSION);
        }

        final List<String> preferenceVersions = new ArrayList<String>();
        preferenceVersions.add(Csw.CSW_VERSION);
        preferenceVersions.add("2.0.1");
        preferenceVersions.add("2.0.0");

        for (final String nextBest : preferenceVersions) {
            if (serverVersions.contains(nextBest)) {
                preferredServerVersion = nextBest;
                break;
            }
        }

        return preferredServerVersion;
    }
}
