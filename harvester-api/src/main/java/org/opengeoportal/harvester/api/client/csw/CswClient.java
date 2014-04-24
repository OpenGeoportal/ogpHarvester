package org.opengeoportal.harvester.api.client.csw;


import org.jdom.Element;
import org.opengeoportal.harvester.api.client.csw.request.*;
import org.opengeoportal.harvester.api.client.csw.response.GetCapabilitiesResponse;
import org.opengeoportal.harvester.api.client.csw.response.GetRecordsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import org.opengeoportal.harvester.api.exception.CswClientException;


public class CswClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PREFERRED_HTTP_METHOD = CatalogRequest.Method.POST.toString();
    private static String CONSTRAINT_LANGUAGE_VERSION = "1.1.0";

    public static final int GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE = 40;

    /** CSW server url **/
    private String serverUrl;

    public CswClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public CswClient(URL serverUrl) {
        this(serverUrl.toString());
    }


    public GetCapabilitiesResponse getCapabilities() throws Exception {
        URL url = new URL(this.serverUrl);
        GetCapabilitiesRequest cswGetCapabilities = new GetCapabilitiesRequest(url);
        cswGetCapabilities.setUrl(url);

       return new GetCapabilitiesParser().parse(cswGetCapabilities.execute());
    }

    public GetRecordsResponse getRecords(GetRecordsRequest request, int start, int max) throws Exception {
        try
        {
            logger.info("Searching on : " + " ("+ start + ", " + (start + max) +")");
            Element response = request.execute();
            if(logger.isDebugEnabled()) {
                logger.debug("Sent request " + request.getSentData());
                //logger.debug("Search results:\n" + Xml.getString(response));
            }


            return new GetRecordsResponseParser().parse(response);

        } catch(Exception e) {
            logger.warn("Raised exception when searching : "+ e);
            throw new CswClientException("Raised exception when searching: " + e.getMessage(), e);
        }
    }

    /**
     * Configures a {@code GetRecordsRequest}.
     *
     * @return  {@code GetRecordsRequest}
     * @throws Exception
     */
    public GetRecordsRequest setupGetRecordsRequest(String cql, String filter) throws Exception {
        GetCapabilitiesResponse capabilitiesResponse = getCapabilities();
        CswOperation getRecordsOper = capabilitiesResponse.getOperation(GetCapabilitiesResponse.GET_RECORDS);

        URL url = new URL(this.serverUrl);
        GetRecordsRequest request = new GetRecordsRequest(url);
        request.setUrl(url);

        request.setResultType(ResultType.RESULTS);
        request.setElementSetName(ElementSetName.FULL);
        request.setMaxRecords(GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE +"");

        // Use the preferred HTTP method and check one exist.
        configGetRecordsRequest(request, getRecordsOper, PREFERRED_HTTP_METHOD, cql, filter);

        // Simple fallback mechanism. Try search with PREFERRED_HTTP_METHOD method, if fails change it
        try {
            request.setStartPosition(1 +"");
            getRecords(request, 1, 1);
        } catch(Exception ex) {
            configGetRecordsRequest(request, getRecordsOper, PREFERRED_HTTP_METHOD.equals("GET") ? "POST" : "GET", cql, filter);
        }

        return request;
    }

    /**
     * Configs a {@code GetRecordsRequest} with the preferred method if possible.
     *
     * @param request
     * @param oper
     * @param preferredMethod
     * @throws Exception
     */
    private void configGetRecordsRequest(GetRecordsRequest request, CswOperation oper,
                                         String preferredMethod, String cql, String filter)
            throws Exception {

        URL url = oper.postUrl;
        ConstraintLanguage constraintLanguage = ConstraintLanguage.FILTER;
        String constraint = filter;
        CatalogRequest.Method method = CatalogRequest.Method.POST;

        // Use the preferred HTTP method and check one exist.
        boolean isGetPreferredAndAvailable = oper.getUrl != null &&
                preferredMethod.equals("GET") &&
                oper.constraintLanguage.contains("cql_text");

        boolean isPostPreferredAndAvailable = oper.postUrl != null &&
                preferredMethod.equals("POST") &&
                oper.constraintLanguage.contains("filter");

        // Try GET if it's preferred and available or if POST is not available
        if ((isGetPreferredAndAvailable) || (!isPostPreferredAndAvailable)) {
            url = oper.getUrl;
            constraintLanguage = ConstraintLanguage.CQL;
            constraint = cql;
            method = CatalogRequest.Method.GET;
        }

        request.setUrl(url);
        request.setServerVersion(oper.preferredServerVersion);
        request.setOutputSchema(oper.preferredOutputSchema);
        request.setConstraintLanguage(constraintLanguage);
        request.setConstraintLangVersion(CONSTRAINT_LANGUAGE_VERSION);
        request.setConstraint(constraint);
        request.setMethod(method);
        for(String typeName: oper.typeNamesList) {
            request.addTypeName(TypeName.getTypeName(typeName));
        }
        request.setOutputFormat(oper.preferredOutputFormat) ;
    }

}
