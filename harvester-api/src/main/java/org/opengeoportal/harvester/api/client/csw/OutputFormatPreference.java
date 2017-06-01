package org.opengeoportal.harvester.api.client.csw;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * OutputFormat in order of preference.
 *
 * TODO This should be made configurable by a system administrator.
 *
 */
public class OutputFormatPreference {

    /**
     * List of OutputSchema in order of preference.
     */
    private static List<String> outputFormats = new ArrayList<String>();

    /**
     * Populate list of OutputFormats in order of preference.
     */
    static {
        OutputFormatPreference.outputFormats
                .add(Csw.OUTPUT_FORMAT_APPLICATION_XML);
    }

    public Iterator<String> iterator() {
        return OutputFormatPreference.outputFormats.iterator();
    }

}