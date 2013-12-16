package org.opengeoportal.harvester.api.client.csw.response;

import org.opengeoportal.harvester.api.client.csw.Csw;
import org.opengeoportal.harvester.api.client.csw.CswOperation;

import java.util.HashMap;
import java.util.Map;

public class GetCapabilitiesResponse {
    public static final String GET_RECORDS      = "GetRecords";
    public static final String GET_RECORD_BY_ID = "GetRecordById";

    private String preferredServerVersion = Csw.CSW_VERSION;


    private Map<String, CswOperation> operations = new HashMap<String, CswOperation>();

    public GetCapabilitiesResponse(Map<String, CswOperation> operations, String preferredServerVersion) {
        this.operations = operations;
        this.preferredServerVersion = preferredServerVersion;
    }

    public CswOperation getOperation(String name) {
        return operations.get(name);
    }

    public String getPreferredServerVersion() {
        return preferredServerVersion;
    }

}
