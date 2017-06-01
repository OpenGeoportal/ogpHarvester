package org.opengeoportal.harvester.api.client.csw;

import org.opengeoportal.harvester.api.client.csw.exception.InvalidParameterValueEx;

/**
 * OutputSchema enum.
 *
 */
public enum OutputSchema {
    OGC_CORE("Record"), ISO_PROFILE("IsoRecord");

    /**
     * Check that outputSchema is known by local catalogue instance.
     *
     * @param schema
     *            requested outputSchema
     * @return either Record or IsoRecord (GN internal representation)
     * @throws InvalidParameterValueEx
     *             hmm
     */
    public static OutputSchema parse(final String schema)
            throws InvalidParameterValueEx {
        if (schema == null) {
            return OGC_CORE;
        }

        if (schema.equals("csw:Record")) {
            return OGC_CORE;
        }

        if (schema.equals("csw:IsoRecord")) {
            return ISO_PROFILE;
        }

        if (schema.equals(Csw.NAMESPACE_CSW.getURI())) {
            return OGC_CORE;
        }

        if (schema.equals(Csw.NAMESPACE_GMD.getURI())) {
            return ISO_PROFILE;
        }

        throw new InvalidParameterValueEx("outputSchema", schema);
    }

    private String schema;

    private OutputSchema(final String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return this.schema;
    }
}