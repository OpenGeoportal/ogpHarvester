package org.opengeoportal.harvester.api.client.csw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * OutputSchema in order of preference.
 *
 * TODO This should be made configurable by a system administrator.
 * 
 */
public class OutputSchemaPreference {

    /**
     * List of OutputSchema in order of preference.
     */
    private static List<String> outputSchemas = new ArrayList<String>();

    /**
     * Populate list of OutputSchemas in order of preference.
     */
    static {
        OutputSchemaPreference.outputSchemas.add(Csw.NAMESPACE_GMD.getURI());
        OutputSchemaPreference.outputSchemas.add(Csw.NAMESPACE_CSW.getURI());
    }

    public Iterator<String> iterator() {
        return OutputSchemaPreference.outputSchemas.iterator();
    }

}
