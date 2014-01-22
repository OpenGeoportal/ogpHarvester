package org.opengeoportal.harvester.api.client.csw.request;


import org.jdom.Element;
import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.response.GetRecordsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Parses a {@code org.jdom.Element} with the GetRecords response, returning a {@code GetRecordsResponse} object.
 *
 */
public class GetRecordsResponseParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	public GetRecordsResponse parse(Element response) {
        GetRecordsResponse getRecordsResponse = new GetRecordsResponse();

        Element results  = response.getChild("SearchResults", Csw.NAMESPACE_CSW);

        // Some providers forget to update their CSW namespace to the CSW 2.0.2 specification
        if(results == null) {
            // in that case, try to accommodate them anyway:
            results = response.getChild("SearchResults", Csw.NAMESPACE_CSW_OLD);
            if (results == null) {
                //throw new OperationAbortedEx("Missing 'SearchResults'", response);
            } else {
                logger.warn("Received GetRecords response with incorrect namespace: " + Csw.NAMESPACE_CSW_OLD);
            }
        }

        getRecordsResponse.setNumberOfRecordsMatched(Integer.parseInt(results.getAttributeValue("numberOfRecordsMatched")));
        getRecordsResponse.setNumberOfRecordsReturned(Integer.parseInt(results.getAttributeValue("numberOfRecordsReturned")));
        getRecordsResponse.setNextRecord(Integer.parseInt(results.getAttributeValue("nextRecord")));
        getRecordsResponse.setElementSet(results.getAttributeValue("elementSet"));

        getRecordsResponse.setResults(results.getChildren());

        return getRecordsResponse;
    }

}
