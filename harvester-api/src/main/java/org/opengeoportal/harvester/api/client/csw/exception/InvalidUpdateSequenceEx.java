package org.opengeoportal.harvester.api.client.csw.exception;

public class InvalidUpdateSequenceEx extends CatalogException {
    private static final long serialVersionUID = 8899240805483632480L;

    public InvalidUpdateSequenceEx(final String value) {
        super(CatalogException.INVALID_UPDATE_SEQUENCE, value, null);
    }

    public InvalidUpdateSequenceEx(final String value,
            final CatalogException prev) {
        super(CatalogException.INVALID_UPDATE_SEQUENCE, value, null, prev);
    }
}