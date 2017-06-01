package org.opengeoportal.harvester.api.client.csw.exception;

public class MissingParameterValueEx extends CatalogException {
    private static final long serialVersionUID = -5797750194252680005L;

    public MissingParameterValueEx(final String name) {
        super(CatalogException.MISSING_PARAMETER_VALUE, null, name);
    }

    public MissingParameterValueEx(final String name,
            final CatalogException prev) {
        super(CatalogException.MISSING_PARAMETER_VALUE, null, name, prev);
    }
}