package org.opengeoportal.harvester.api.client.csw;

import org.opengeoportal.harvester.api.client.csw.exception.InvalidParameterValueEx;

/**
 * OutputSchema enum.
 *
 */
public enum OutputSchema
{
	OGC_CORE("Record"), ISO_PROFILE("IsoRecord");


    private String schema;

	private OutputSchema(String schema) { this.schema = schema;}

	public String toString() { return schema; }

	/**
	 * Check that outputSchema is known by local catalogue instance.
	 *
     * @param schema requested outputSchema
     * @return either Record or IsoRecord (GN internal representation)
     * @throws InvalidParameterValueEx hmm
     */
	public static OutputSchema parse(String schema) throws InvalidParameterValueEx
	{
		if (schema == null)
            return OGC_CORE;

		if (schema.equals("csw:Record"))
            return OGC_CORE;

		if (schema.equals("csw:IsoRecord"))
            return ISO_PROFILE;
		
		if (schema.equals(Csw.NAMESPACE_CSW.getURI()))
            return OGC_CORE;

		if (schema.equals(Csw.NAMESPACE_GMD.getURI()))
            return ISO_PROFILE;
		
		throw new InvalidParameterValueEx("outputSchema", schema);
	}
}