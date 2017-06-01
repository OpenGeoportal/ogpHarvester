package org.opengeoportal.harvester.api.client.csw.exception;

public class InvalidParameterValueEx extends CatalogException {
    private static final long serialVersionUID = 2351490694044128159L;

    public InvalidParameterValueEx(final String name, final String value) {
        super(CatalogException.INVALID_PARAMETER_VALUE, value, name);
    }

    public InvalidParameterValueEx(final String name, final String value,
            final CatalogException prev) {
        super(CatalogException.INVALID_PARAMETER_VALUE, value, name, prev);
    }
}