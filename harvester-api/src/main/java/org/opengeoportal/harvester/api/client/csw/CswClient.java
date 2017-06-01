package org.opengeoportal.harvester.api.client.csw;

import java.net.URL;

import org.jdom.Element;
import org.opengeoportal.harvester.api.client.csw.request.CatalogRequest;
import org.opengeoportal.harvester.api.client.csw.request.GetCapabilitiesParser;
import org.opengeoportal.harvester.api.client.csw.request.GetCapabilitiesRequest;
import org.opengeoportal.harvester.api.client.csw.request.GetRecordsRequest;
import org.opengeoportal.harvester.api.client.csw.request.GetRecordsResponseParser;
import org.opengeoportal.harvester.api.client.csw.response.GetCapabilitiesResponse;
import org.opengeoportal.harvester.api.client.csw.response.GetRecordsResponse;
import org.opengeoportal.harvester.api.exception.CswClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CswClient {
    private static final String PREFERRED_HTTP_METHOD = CatalogRequest.Method.POST
            .toString();

    private static String CONSTRAINT_LANGUAGE_VERSION = "1.1.0";
    public static final int GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE = 40;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** CSW server url **/
    private final String serverUrl;

    public CswClient(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public CswClient(final URL serverUrl) {
        this(serverUrl.toString());
    }

    /**
     * Configs a {@code GetRecordsRequest} with the preferred method if
     * possible.
     *
     * @param request
     * @param oper
     * @param preferredMethod
     * @throws Exception
     */
    private void configGetRecordsRequest(final GetRecordsRequest request,
            final CswOperation oper, final String preferredMethod,
            final String cql, final String filter) throws Exception {

        URL url = oper.postUrl;
        ConstraintLanguage constraintLanguage = ConstraintLanguage.FILTER;
        String constraint = filter;
        CatalogRequest.Method method = CatalogRequest.Method.POST;

        // Use the preferred HTTP method and check one exist.
        final boolean isGetPreferredAndAvailable = (oper.getUrl != null)
                && preferredMethod.equals("GET")
                && oper.constraintLanguage.contains("cql_text");

        final boolean isPostPreferredAndAvailable = (oper.postUrl != null)
                && preferredMethod.equals("POST")
                && oper.constraintLanguage.contains("filter");

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
        request.setConstraintLangVersion(CswClient.CONSTRAINT_LANGUAGE_VERSION);
        request.setConstraint(constraint);
        request.setMethod(method);
        for (final String typeName : oper.typeNamesList) {
            request.addTypeName(TypeName.getTypeName(typeName));
        }
        request.setOutputFormat(oper.preferredOutputFormat);
    }

    public GetCapabilitiesResponse getCapabilities() throws Exception {
        final URL url = new URL(this.serverUrl);
        final GetCapabilitiesRequest cswGetCapabilities = new GetCapabilitiesRequest(
                url);
        cswGetCapabilities.setUrl(url);

        return new GetCapabilitiesParser().parse(cswGetCapabilities.execute());
    }

    public GetRecordsResponse getRecords(final GetRecordsRequest request,
            final int start, final int max) throws Exception {
        try {
            this.logger.info("Searching on : " + " (" + start + ", "
                    + (start + max) + ")");
            final Element response = request.execute();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Sent request " + request.getSentData());
                // logger.debug("Search results:\n" + Xml.getString(response));
            }

            return new GetRecordsResponseParser().parse(response);

        } catch (final Exception e) {
            this.logger.warn("Raised exception when searching : " + e);
            throw new CswClientException(
                    "Raised exception when searching: " + e.getMessage(), e);
        }
    }

    /**
     * Configures a {@code GetRecordsRequest}.
     *
     * @return {@code GetRecordsRequest}
     * @throws Exception
     */
    public GetRecordsRequest setupGetRecordsRequest(final String cql,
            final String filter) throws Exception {
        final GetCapabilitiesResponse capabilitiesResponse = this
                .getCapabilities();
        final CswOperation getRecordsOper = capabilitiesResponse
                .getOperation(GetCapabilitiesResponse.GET_RECORDS);

        final URL url = new URL(this.serverUrl);
        final GetRecordsRequest request = new GetRecordsRequest(url);
        request.setUrl(url);

        request.setResultType(ResultType.RESULTS);
        request.setElementSetName(ElementSetName.FULL);
        request.setMaxRecords(
                CswClient.GETRECORDS_NUMBER_OF_RESULTS_PER_PAGE + "");

        // Use the preferred HTTP method and check one exist.
        this.configGetRecordsRequest(request, getRecordsOper,
                CswClient.PREFERRED_HTTP_METHOD, cql, filter);

        // Simple fallback mechanism. Try search with PREFERRED_HTTP_METHOD
        // method, if fails change it
        try {
            request.setStartPosition(1 + "");
            this.getRecords(request, 1, 1);
        } catch (final Exception ex) {
            this.configGetRecordsRequest(request, getRecordsOper,
                    CswClient.PREFERRED_HTTP_METHOD.equals("GET") ? "POST"
                            : "GET",
                    cql, filter);
        }

        return request;
    }

}
