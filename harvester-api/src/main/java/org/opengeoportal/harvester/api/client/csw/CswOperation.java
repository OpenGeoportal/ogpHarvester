package org.opengeoportal.harvester.api.client.csw;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Representation of a CSW operation as it was described in a GetCapabilities
 * response.
 *
 */
public class CswOperation {

    public String name;
    public URL getUrl;
    public URL postUrl;

    /**
     * The OutputSchemas as advertised in the CSW server's GetCapabilities
     * response.
     */
    public List<String> outputSchemaList = new ArrayList<String>();

    /**
     * The OutputFormats as advertised in the CSW server's GetCapabilities
     * response.
     */
    public List<String> outputFormatList = new ArrayList<String>();

    /**
     * The constraintLanguage as advertised in the CSW server's GetCapabilities
     * response.
     */
    public List<String> constraintLanguage = new ArrayList<String>();

    /**
     * The preferred OutputSchema from the above.
     */
    public String preferredOutputSchema;

    /**
     * The preferred OutputFormat from the above.
     */
    public String preferredOutputFormat;

    public String preferredServerVersion;

    public List<String> typeNamesList = new ArrayList<String>();

    public void choosePreferredOutputFormat() {
        final OutputFormatPreference preference = new OutputFormatPreference();
        for (final Iterator<String> i = preference.iterator(); i.hasNext();) {
            final String nextBest = i.next();
            if (this.outputFormatList.contains(nextBest)) {
                this.preferredOutputFormat = nextBest;
                break;
            }
        }

        if (this.preferredOutputFormat == null) {
            this.preferredOutputFormat = Csw.OUTPUT_FORMAT_APPLICATION_XML;
        }
    }

    public void choosePreferredOutputSchema() {
        final OutputSchemaPreference preference = new OutputSchemaPreference();
        for (final Iterator<String> i = preference.iterator(); i.hasNext();) {
            final String nextBest = i.next();
            if (this.outputSchemaList.contains(nextBest)) {
                this.preferredOutputSchema = nextBest;
                break;
            }
        }
    }

}
