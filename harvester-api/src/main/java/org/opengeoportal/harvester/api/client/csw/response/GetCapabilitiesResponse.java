package org.opengeoportal.harvester.api.client.csw.response;

import java.util.HashMap;
import java.util.Map;

import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.CswOperation;

public class GetCapabilitiesResponse {
    public static final String GET_RECORDS = "GetRecords";
    public static final String GET_RECORD_BY_ID = "GetRecordById";

    private String preferredServerVersion = Csw.CSW_VERSION;

    private Map<String, CswOperation> operations = new HashMap<String, CswOperation>();

    public GetCapabilitiesResponse(final Map<String, CswOperation> operations,
            final String preferredServerVersion) {
        this.operations = operations;
        this.preferredServerVersion = preferredServerVersion;
    }

    public CswOperation getOperation(final String name) {
        return this.operations.get(name);
    }

    public String getPreferredServerVersion() {
        return this.preferredServerVersion;
    }

}
