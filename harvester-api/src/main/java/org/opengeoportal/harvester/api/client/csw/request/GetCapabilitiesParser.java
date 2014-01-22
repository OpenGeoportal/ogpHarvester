package org.opengeoportal.harvester.api.client.csw.request;

import org.jdom.Element;
import org.jdom.Namespace;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.CswOperation;
import org.opengeoportal.harvester.api.client.csw.TypeName;
import org.opengeoportal.harvester.api.client.csw.response.GetCapabilitiesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to parse GetCapabilities document.
 */
public class GetCapabilitiesParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GetCapabilitiesResponse parse(Element capab) {
        String preferredServerVersion = parseVersions(capab);
        Map<String, CswOperation> operations = parseOperations(capab, preferredServerVersion);

        return new GetCapabilitiesResponse(operations, preferredServerVersion);
    }

    /**
     * Get available operations in the GetCapabilities document.
     *
     * @param capabil
     */
    private Map<String, CswOperation> parseOperations(Element capabil, String preferredServerVersion) {
        Map<String, CswOperation> operations = new HashMap<String, CswOperation>();

        Element operMd = capabil.getChild("OperationsMetadata", Csw.NAMESPACE_OWS);

        if (operMd == null) {
            //log("Missing 'ows:OperationsMetadata' element");
        } else {
            for(Object e : operMd.getChildren()) {
                Element elem = (Element) e;

                if ("Operation".equals(elem.getName())) {
                    CswOperation oper = extractOperation(elem, preferredServerVersion);

                    if (oper != null)
                        operations.put(oper.name, oper);
                }
            }
        }

        return operations;
    }

    /**
     * Get operations name and properties needed for futur operation calls.
     *
     * @param oper
     * @return
     */
    @SuppressWarnings("unchecked")
	private CswOperation extractOperation(Element oper, String preferredServerVersion) {
        String name = oper.getAttributeValue("name");

        if (name == null) {
            logger.warn("Operation has no 'name' attribute");
            return null;
        }

        CswOperation op = new CswOperation();
        op.name   = name;

        List<Element> dcp = oper.getChildren("DCP", Csw.NAMESPACE_OWS);
        evaluateUrl(dcp, op);

        List<Element> parameters = oper.getChildren("Parameter", Csw.NAMESPACE_OWS);
        logger.debug("Found " + parameters.size() + " parameters for operation: " + name);
        List<Element> outputSchemas = null;
        List<Element> typeNames = null;
        List<Element> outputFormats = null;
        List<Element> constraintLanguages = null;

        for (Element parameter : parameters) {
            String parameterName = parameter.getAttributeValue("name");
            logger.debug("Processing parameter: " + parameterName);
            if (parameterName != null && parameterName.equalsIgnoreCase("outputSchema")) {
                outputSchemas = parameter.getChildren("Value", Csw.NAMESPACE_OWS);
                logger.debug("Found " + outputSchemas.size() + " outputSchemas for operation: " + name);
            }

            // CSW 07-045 spec sometime use typenames or typename for the GetRecord type name parameter:
            // * With 's' in Table 29
            // * Without 's' p114 in GetCapabiltiies examples and in figure 10 'getRecords service="CSW", typeName="gmd:MD_Metadata"'
            // Type name is used in both GetRecords (probably with typenames) and DescribeRecord (problaby with typename) operation
            // so check for both parameters
            if (parameterName != null &&
                    (parameterName.equalsIgnoreCase("typeNames") || parameterName.equalsIgnoreCase("typeName"))
                    ) {
                typeNames = parameter.getChildren("Value", Csw.NAMESPACE_OWS);
                logger.debug("Found " + typeNames.size() + " typeNames for operation: " + name);
            }

            if (parameterName != null && parameterName.equalsIgnoreCase("outputFormat")) {
                outputFormats = parameter.getChildren("Value", Csw.NAMESPACE_OWS);
                logger.debug("Found " + outputFormats.size() + " outputFormats for operation: " + name);
            }

            if (parameterName != null &&
                    parameterName.equalsIgnoreCase("CONSTRAINTLANGUAGE")) {
                constraintLanguages = parameter.getChildren("Value", Csw.NAMESPACE_OWS);
                logger.debug("Found " + constraintLanguages.size() + " constraintLanguage for operation: " + name);
            }
        }

        if(outputSchemas != null) {
            for (Element outputSchema : outputSchemas) {
                String outputSchemaValue = outputSchema.getValue();
                logger.debug("Adding outputSchema: " + outputSchemaValue + " to operation: " + name);
                op.outputSchemaList.add(outputSchemaValue);
            }
            op.choosePreferredOutputSchema();
        } else {
            logger.warn("No outputSchema for operation: " + name);
        }

        if(constraintLanguages != null) {
            for (Element constraintLanguage : constraintLanguages) {
                String constraintLanguageValue = constraintLanguage.getValue().toLowerCase();
                logger.debug("Adding constraintLanguage : " + constraintLanguageValue + " to operation: " + name);
                if ("cql".equals(constraintLanguageValue)){
                    logger.debug(" Some implementation use CQL instead of CQL_TEXT for the CQL constraint language value.");
                    constraintLanguageValue = "cql_text";
                }
                op.constraintLanguage.add(constraintLanguageValue);
            }
        } else {
            logger.warn("No constraintLanguage for operation: " + name);
        }

        if(typeNames != null) {
            for (Element typeName : typeNames) {
                String typeNameValue = typeName.getValue();
                logger.debug("Adding typeName: " + typeNameValue + " to operation: " + name);
                TypeName tn = TypeName.getTypeName(typeNameValue);
                if (tn != null) {
                    op.typeNamesList.add(typeNameValue);
                } else {
                    logger.warn("  Unsupported typeName found: " + typeNameValue + ".");
                }
            }
        } else {
            logger.warn("No typeNames for operation: " + name);
        }

        if(outputFormats != null) {
            for (Element outputFormat : outputFormats) {
                String outputFormatValue = outputFormat.getValue();
                logger.debug("Adding outputFormat: " + outputFormatValue + " to operation: " + name);
                op.outputFormatList.add(outputFormatValue);
            }
            op.choosePreferredOutputFormat();
        } else {
            op.preferredOutputFormat = Csw.OUTPUT_FORMAT_APPLICATION_XML;
            logger.warn("No outputFormat for operation: " + name);
        }

        op.preferredServerVersion = preferredServerVersion;

        return op;
    }

    /**
     * Gets server supported versions.
     *
     * @param capabil
     */
    private String parseVersions(Element capabil) {
        String preferredServerVersion = Csw.CSW_VERSION;
        List<String> serverVersions = new ArrayList<String>();
        Element serviceIdentificationMd = capabil.getChild("ServiceIdentification", Csw.NAMESPACE_OWS);

        if (serviceIdentificationMd == null) {
            logger.warn("Missing 'ows:ServiceTypeVersion' element");
        } else {
            @SuppressWarnings(value = "unchecked")
            List<Element> serviceIdentificationMdElems = serviceIdentificationMd.getChildren();
            for (Element value : serviceIdentificationMdElems) {
                String valueName = value.getName();
                logger.debug("Processing value: " + valueName);
                if (valueName != null && valueName.equalsIgnoreCase("ServiceTypeVersion")) {
                    serverVersions.add(value.getValue());
                }
            }
        }

        // Select default CSW supported version
        if (serverVersions.isEmpty()) serverVersions.add(Csw.CSW_VERSION);

        List<String> preferenceVersions = new ArrayList<String>();
        preferenceVersions.add(Csw.CSW_VERSION);
        preferenceVersions.add("2.0.1");
        preferenceVersions.add("2.0.0");

        for (String nextBest : preferenceVersions) {
            if (serverVersions.contains(nextBest)) {
                preferredServerVersion = nextBest;
                break;
            }
        }

        return preferredServerVersion;
    }

    /**
     * Search for valid POST or GET URL and check that service is available using GET method or POST/XML.
     *
     * SOAP services are not supported (TODO ?).
     * @param dcps
     * @param op
     */
    private void evaluateUrl(List<Element> dcps, CswOperation op) {
        if (dcps == null) {
            logger.warn("Missing 'ows:DCP' element in operation");
            return;
        }

        Namespace ns = Namespace.getNamespace("http://www.w3.org/1999/xlink");

        for (Element dcp : dcps) {
            Element http = dcp.getChild("HTTP", Csw.NAMESPACE_OWS);

            if (http == null) {
                logger.warn("Missing 'ows:HTTP' element in operation/DCP");
                continue;
            }

            // GET method
            Element getUrl = http.getChild("Get",  Csw.NAMESPACE_OWS);

            if (getUrl == null) {
                logger.warn("No GET url found in current DCP. Checking POST ...");
            } else {
                String tmpGetUrl = getUrl.getAttributeValue("href", ns);

                if (tmpGetUrl != null && op.getUrl == null) {
                    try	{
                        op.getUrl = new URL(tmpGetUrl);
                        logger.debug("Found URL (GET method): " + tmpGetUrl);
                    } catch (MalformedURLException e) {
                        logger.error("Malformed 'xlink:href' attribute in operation's http method");
                    }
                }
            }

            // POST method
            @SuppressWarnings(value = "unchecked")
            List<Element> postUrlList = http.getChildren("Post", Csw.NAMESPACE_OWS);

            for(Element postUrl: postUrlList) {
                if (postUrl == null) {
                    logger.warn("No POST url found in current DCP.");
                } else {
                    String tmpPostUrl = postUrl.getAttributeValue("href", ns);

                    if (tmpPostUrl == null) {
                        logger.warn("Missing 'xlink:href' attribute in operation's http method");
                    } else {
                        if (op.postUrl == null) {
                            // PostEncoding could return a SOAP service address. Not supported
                            Element methodConstraint = postUrl.getChild("Constraint", Csw.NAMESPACE_OWS);

                            if (methodConstraint != null) {
                                Element value = methodConstraint.getChild("Value", Csw.NAMESPACE_OWS);
                                if (value != null && value.getText().equals("SOAP")) {
                                    logger.warn("The URL " + tmpPostUrl + " using POST/SOAP method is not supported for harvesting.");
                                    continue;
                                }
                            }

                            try	{
                                op.postUrl = new URL(tmpPostUrl);
                                logger.debug("Found URL (POST method):" + tmpPostUrl);
                                break;
                            } catch (MalformedURLException e) {
                                logger.error("Malformed 'xlink:href' attribute in operation's http method");
                            }
                        }
                    }
                }
            }
        }
    }
}
