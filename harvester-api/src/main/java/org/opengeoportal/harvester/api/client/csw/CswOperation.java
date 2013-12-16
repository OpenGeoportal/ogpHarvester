package org.opengeoportal.harvester.api.client.csw;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Representation of a CSW operation as it was described in a GetCapabilities response.
 *
 */
public class CswOperation {
	
	public String name;
	public URL    getUrl;
	public URL    postUrl;
	
	/**
	 * The OutputSchemas as advertised in the CSW server's GetCapabilities response.
	 */
	public List<String> outputSchemaList = new ArrayList<String>();

    /**
	 * The OutputFormats as advertised in the CSW server's GetCapabilities response.
	 */
    public List<String> outputFormatList = new ArrayList<String>();

    /**
     * The constraintLanguage as advertised in the CSW server's GetCapabilities response.
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

    public void choosePreferredOutputSchema() {
		OutputSchemaPreference preference = new OutputSchemaPreference();
		for(Iterator<String> i = preference.iterator(); i.hasNext();){
			String nextBest = i.next();
			if(outputSchemaList.contains(nextBest)) {
				preferredOutputSchema = nextBest;
				break;
			}
		}
	}

    public void choosePreferredOutputFormat() {
		OutputFormatPreference preference = new OutputFormatPreference();
		for(Iterator<String> i = preference.iterator(); i.hasNext();){
			String nextBest = i.next();
			if(outputFormatList.contains(nextBest)) {
				preferredOutputFormat = nextBest;
				break;
			}
		}

        if (preferredOutputFormat == null) preferredOutputFormat = Csw.OUTPUT_FORMAT_APPLICATION_XML;
	}

}
