package org.opengeoportal.harvester.api.client.csw.exception;

public class NoApplicableCodeEx extends CatalogException {
    private static final long serialVersionUID = -2651752961299461115L;

    public NoApplicableCodeEx(final String message) {
        super(CatalogException.NO_APPLICABLE_CODE, message, null);
    }

    public NoApplicableCodeEx(final String message,
            final CatalogException prev) {
        super(CatalogException.NO_APPLICABLE_CODE, message, null, prev);
    }

    public NoApplicableCodeEx(final String message, final String locator) {
        super(CatalogException.NO_APPLICABLE_CODE, message, locator);
    }
}